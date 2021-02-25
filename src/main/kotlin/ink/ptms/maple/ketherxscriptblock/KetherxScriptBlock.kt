package ink.ptms.maple.ketherxscriptblock

import ink.ptms.maple.ketherxscriptblock.data.ScriptData
import io.izzel.taboolib.loader.Plugin
import io.izzel.taboolib.module.inject.TSchedule
import io.izzel.taboolib.util.Files
import org.bukkit.Location
import java.io.File

object KetherxScriptBlock : Plugin() {

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
        File(this.plugin.dataFolder, "scripts").listFiles()?.forEach { file ->
            scripts.add(ScriptData.fromJson(Files.readFromFile(file) ?: return@forEach))
        }
    }

    fun remove(location: Location) {
        Files.file(this.plugin.dataFolder, "scripts/${Utils.fromLocation(location)}.json").delete()
        load()
    }

    @TSchedule(period = 20 * 60 * 10, async = true)
    fun save() {
        scripts.forEach {
            Files.toFile(
                it.toJson(),
                Files.file(this.plugin.dataFolder, "scripts/${Utils.fromLocation(it.location)}.json")
            )
        }
    }
}