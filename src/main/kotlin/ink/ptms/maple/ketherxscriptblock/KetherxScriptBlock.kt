package ink.ptms.maple.ketherxscriptblock

import ink.ptms.maple.ketherxscriptblock.data.ScriptData
import ink.ptms.maple.ketherxscriptblock.data.ScriptType
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.db.local.LocalFile
import io.izzel.taboolib.module.inject.TFunction
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.util.Files
import io.izzel.taboolib.util.item.Items
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import kotlin.collections.ArrayList

object KetherxScriptBlock : Plugin() {

    @LocalFile("scripts.yml")
    lateinit var data: FileConfiguration
        private set

    val scripts = ArrayList<ScriptData>()

    fun getScript(location: Location): ScriptData? {
        return scripts.firstOrNull { it.location == location }
    }

    override fun onDisable() {
        save()
    }

    @TSchedule
    fun load() {
        scripts.clear()
        data.getKeys(false).forEach { key ->
            val datas = ScriptData(Utils.toLocation(key),
                ScriptType.valueOf(data.getString("${key}.type", "NULL")!!),
                data.getStringList("${key}.action"),
                data.getLong("${key}.cooldown"))
            scripts.add(datas)
            save()
        }
    }

    fun remove(location: Location) {
        data.set(Utils.fromLocation(location), null)
        load()
    }

    @TFunction.Cancel
    fun save() {
        data.getKeys(false).forEach { data.set(it, null) }
        scripts.forEach { block ->
            val location = Utils.fromLocation(block.location)
            data.set("$location.type", block.type.name)
            data.set("$location.action", block.action)
            data.set("$location.cooldown", block.cooldown)
        }
    }
}