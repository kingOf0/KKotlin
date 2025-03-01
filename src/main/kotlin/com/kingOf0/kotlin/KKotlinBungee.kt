package com.kingOf0.kotlin

import com.kingOf0.kotlin.KKotlinManager.plugins
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ProxyReloadEvent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.config.Configuration
import net.md_5.bungee.config.ConfigurationProvider
import net.md_5.bungee.config.YamlConfiguration
import net.md_5.bungee.event.EventHandler
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.logging.Level

class KKotlinBungee : Plugin(), Listener {
    private val message = mutableListOf<TextComponent>()
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        logger.log(Level.WARNING, "Error in coroutine: ${exception.message}", exception)
    }
    private val pluginScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + exceptionHandler)
    private val allowedPlayers = mutableSetOf<String>()
    private val pluginInfoCache = ConcurrentHashMap<String, List<TextComponent>>()
    private var cacheUpdateInterval = 5L

    override fun onEnable() {
        setupConfig()
        loadConfig()

        message.add(TextComponent("This server is running Kotlin! ${description.version} by ${description.author}"))
        message.add(TextComponent("Server Version: ${ProxyServer.getInstance().version}  - Kotlin Version: ${KotlinVersion.CURRENT}"))

        refreshPluginInfo()

        val pluginManager = proxy.pluginManager
        pluginManager.registerListener(this, this)

        proxy.scheduler.schedule(this, {
            refreshPluginInfo()
        }, cacheUpdateInterval, cacheUpdateInterval, TimeUnit.MINUTES)

        logger.info("KKotlinBungee ${description.version} enabled successfully!")
    }

    private fun setupConfig() {
        if (!dataFolder.exists()) {
            dataFolder.mkdir()
        }

        val configFile = File(dataFolder, "config.yml")

        if (!configFile.exists()) {
            try {
                configFile.createNewFile()
                val defaultConfig = javaClass.getResourceAsStream("/config.yml")
                if (defaultConfig != null) {
                    Files.copy(defaultConfig, configFile.toPath())
                } else {
                    val config = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(configFile)
                    config.set("allowed-players", listOf("KingOf0", "SimitSu"))
                    config.set("cache-update-interval", 5)
                    ConfigurationProvider.getProvider(YamlConfiguration::class.java).save(config, configFile)
                }
            } catch (e: IOException) {
                logger.log(Level.SEVERE, "Could not create config file", e)
            }
        }
    }

    private fun loadConfig() {
        try {
            val configFile = File(dataFolder, "config.yml")
            val config: Configuration = ConfigurationProvider.getProvider(YamlConfiguration::class.java).load(configFile)

            allowedPlayers.clear()
            allowedPlayers.addAll(config.getStringList("allowed-players"))
            if (allowedPlayers.isEmpty()) {
                allowedPlayers.add("KingOf0")
                allowedPlayers.add("SimitSu")
            }

            cacheUpdateInterval = config.getLong("cache-update-interval", 5L)
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Could not load config", e)
            allowedPlayers.add("KingOf0")
            allowedPlayers.add("SimitSu")
        }
    }

    private fun refreshPluginInfo() {
        val pluginManager = proxy.pluginManager
        pluginScope.launch {
            try {
                val relevantPlugins = pluginManager.plugins.filter { plugins.contains(it.description.name) }

                val newMessages = mutableListOf<TextComponent>()
                newMessages.add(TextComponent("This server is running Kotlin! ${description.version} by ${description.author}"))
                newMessages.add(TextComponent("Server Version: ${ProxyServer.getInstance().version}  - Kotlin Version: ${KotlinVersion.CURRENT}"))

                for (plugin in relevantPlugins) {
                    val pluginInfo = mutableListOf<TextComponent>()
                    pluginInfo.add(TextComponent(" > §a${plugin.description.name} §7- §a${plugin.description.version}"))

                    for (dependName in plugin.description.depends) {
                        val depend: Plugin? = pluginManager.getPlugin(dependName)
                        val version = depend?.description?.version ?: ""
                        val enabled = depend != null
                        pluginInfo.add(TextComponent("   §7> §a$dependName §7- Version: §a$version §7- Enabled: §a$enabled"))
                    }

                    pluginInfo.add(TextComponent(""))
                    pluginInfoCache[plugin.description.name] = pluginInfo
                    newMessages.addAll(pluginInfo)
                }

                proxy.scheduler.schedule(this@KKotlinBungee, {
                    message.clear()
                    message.addAll(newMessages)
                }, 0, TimeUnit.MILLISECONDS)
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Error refreshing plugin info", e)
            }
        }
    }

    override fun onDisable() {
        pluginScope.cancel()
        pluginInfoCache.clear()
        logger.info("KKotlinBungee disabled")
    }

    @EventHandler
    fun onJoin(event: ServerConnectEvent) {
        val playerName = event.player.name
        if (!allowedPlayers.contains(playerName)) return

        if (message.isEmpty()) {
            proxy.scheduler.schedule(this, {
                event.player.sendMessage(*message.toTypedArray())
            }, 500, TimeUnit.MILLISECONDS)
        } else {
            event.player.sendMessage(*message.toTypedArray())
        }
    }

    @EventHandler
    fun onProxyReload(event: ProxyReloadEvent) {
        loadConfig()
        refreshPluginInfo()
    }
}