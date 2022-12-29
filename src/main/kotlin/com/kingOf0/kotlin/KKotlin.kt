package com.kingOf0.kotlin

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

class KKotlin : JavaPlugin(), Listener {

    private val message = mutableListOf("This server is running Kotlin! ${description.version} by ${description.authors}", "Server Version: ${Bukkit.getVersion()} Bukkit Version: ${Bukkit.getBukkitVersion()} - Kotlin Version: ${KotlinVersion.CURRENT}")

    override fun onEnable() {
        val plugins = listOf(
            "KFarmer",
            "KSpawners",
            "KCombatLog",
            "KFactions",
            "KClaim",
            "KAnnouncer",
            "KDailyRewards",
            "KModerations",
            "KRoulette",
            "KRankUp",
            "KConomy",
            "KLands"
        )
        val pluginManager = server.pluginManager
        Bukkit.getScheduler().runTaskLater(this, {
            pluginManager.plugins.filter { plugins.contains(it.name) }.forEach {
                message.add(" > §a${it.name} §7- §a${it.description.version}")

                it.description.depend.forEach { dependName ->
                    val depend: Plugin? = pluginManager.getPlugin(dependName)
                    val version = depend?.description?.version ?: "0"
                    val enabled = depend?.isEnabled ?: pluginManager.isPluginEnabled(dependName)
                    message.add("   §7> §a$dependName §7- Version: §a$version §7- Enabled: §a$enabled")
                }
                message.add("")
            }
        }, 20L)

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {

    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (event.player.name != "KingOf0" && event.player.name != "SimitSu") return
        event.player.sendMessage(message.toTypedArray())
    }

}