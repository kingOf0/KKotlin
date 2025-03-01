package com.kingOf0.kotlin

import com.kingOf0.kotlin.KKotlinManager.plugins
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level

class KKotlin : JavaPlugin(), Listener {
    private val message = mutableListOf<String>()
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        logger.log(Level.WARNING, "Error in coroutine: ${exception.message}", exception)
    }
    private val pluginScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + exceptionHandler)
    private val allowedPlayers = mutableSetOf<String>()
    private val pluginInfoCache = ConcurrentHashMap<String, List<String>>()
    private var cacheUpdateInterval = 300L

    override fun onEnable() {
        saveDefaultConfig()
        loadConfig()

        message.add("This server is running Kotlin! ${description.version} by ${description.authors}")
        message.add("Server Version: ${Bukkit.getVersion()} Bukkit Version: ${Bukkit.getBukkitVersion()} - Kotlin Version: ${KotlinVersion.CURRENT}")

        refreshPluginInfo()

        server.pluginManager.registerEvents(this, this)

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, Runnable {
            refreshPluginInfo()
        }, cacheUpdateInterval * 20L, cacheUpdateInterval * 20L)

        logger.info("KKotlin ${description.version} enabled successfully!")
    }

    private fun loadConfig() {
        val config: FileConfiguration = config
        allowedPlayers.clear()
        allowedPlayers.addAll(config.getStringList("allowed-players"))
        if (allowedPlayers.isEmpty()) {
            allowedPlayers.add("KingOf0")
            allowedPlayers.add("SimitSu")
        }
        cacheUpdateInterval = config.getLong("cache-update-interval", 300L)
    }

    private fun refreshPluginInfo() {
        val pluginManager = server.pluginManager
        pluginScope.launch {
            try {
                val relevantPlugins = pluginManager.plugins.filter { plugins.contains(it.name) }

                for (plugin in relevantPlugins) {
                    val pluginInfo = mutableListOf<String>()
                    pluginInfo.add(" > §a${plugin.name} §7- §a${plugin.description.version}")

                    for (dependName in plugin.description.depend) {
                        val depend: Plugin? = pluginManager.getPlugin(dependName)
                        val version = depend?.description?.version ?: "0"
                        val enabled = depend?.isEnabled ?: pluginManager.isPluginEnabled(dependName)
                        pluginInfo.add("   §7> §a$dependName §7- Version: §a$version §7- Enabled: §a$enabled")
                    }

                    pluginInfo.add("")
                    pluginInfoCache[plugin.name] = pluginInfo
                }

                Bukkit.getScheduler().runTask(this@KKotlin, Runnable {
                    message.clear()
                    message.add("This server is running Kotlin! ${description.version} by ${description.authors}")
                    message.add("Server Version: ${Bukkit.getVersion()} Bukkit Version: ${Bukkit.getBukkitVersion()} - Kotlin Version: ${KotlinVersion.CURRENT}")
                    pluginInfoCache.values.forEach { message.addAll(it) }
                })
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Error refreshing plugin info", e)
            }
        }
    }

    override fun onDisable() {
        pluginScope.cancel()
        pluginInfoCache.clear()
        logger.info("KKotlin disabled")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onJoin(event: PlayerJoinEvent) {
        val playerName = event.player.name
        if (!allowedPlayers.contains(playerName)) return

        if (pluginInfoCache.isEmpty()) {
            Bukkit.getScheduler().runTaskLater(this, Runnable {
                event.player.sendMessage(message.toTypedArray())
            }, 20L)
        } else {
            event.player.sendMessage(message.toTypedArray())
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPluginEnable(event: PluginEnableEvent) {
        if (plugins.contains(event.plugin.name)) {
            refreshPluginInfo()
        }
    }
}