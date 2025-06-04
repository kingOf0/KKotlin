package com.kingOf0.kotlin

import com.kingOf0.kotlin.KKotlinManager.plugins
import de.tr7zw.changeme.nbtapi.NBT
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.event.ServerConnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.util.concurrent.TimeUnit


class KKotlinBungee : Plugin(), Listener {

    private val message = mutableListOf(
        TextComponent("This server is running Kotlin! ${description.version} by ${description.author}"),
        TextComponent("Server Version: ${ProxyServer.getInstance().version}  - Kotlin Version: ${KotlinVersion.CURRENT}")
    )

    override fun onEnable() {

        NBT.preloadApi()

        val pluginManager = proxy.pluginManager
        proxy.scheduler.schedule(this, {
            pluginManager.plugins.filter { plugins.contains(it.description.name) }.forEach {
                message.add(TextComponent(" > §a${it.description.name} §7- §a${it.description.version}"))

                it.description.depends.forEach { dependName ->
                    val depend: Plugin? = pluginManager.getPlugin(dependName)
                    val version = depend?.description?.version ?: ""
                    val enabled = depend != null
                    message.add(TextComponent("   §7> §a$dependName §7- Version: §a$version §7- Enabled: §a$enabled"))
                }
                message.add(TextComponent(""))
            }
        }, 1, TimeUnit.SECONDS)

        pluginManager.registerListener(this, this)
    }

    override fun onDisable() {

    }

    @EventHandler
    fun onJoin(event: ServerConnectEvent) {
        if (event.player.name != "KingOf0" && event.player.name != "SimitSu") return
        event.player.sendMessage(*message.toTypedArray())
    }

}