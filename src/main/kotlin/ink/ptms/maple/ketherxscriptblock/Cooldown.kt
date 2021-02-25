package ink.ptms.maple.ketherxscriptblock

import org.bukkit.Bukkit

object Cooldown {

    private val cooldown = mutableMapOf<String, MutableMap<String, Boolean>>()

    fun isCooldown(player: String, type: String): Boolean {
        return cooldown[player]?.get(type) ?: false
    }

    fun toCooldown(player: String, type: String, tick: Int) {
        cooldown[player]?.set(type, true)
        Bukkit.getScheduler().runTaskLater(KetherxScriptBlock.plugin, Runnable {
            cooldown[player]?.remove(type)
        }, tick.toLong())
    }

}