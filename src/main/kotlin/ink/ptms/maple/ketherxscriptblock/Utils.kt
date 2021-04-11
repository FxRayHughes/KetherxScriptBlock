package ink.ptms.maple.ketherxscriptblock

import io.izzel.taboolib.kotlin.kether.KetherShell
import io.izzel.taboolib.kotlin.kether.common.util.LocalizedException
import io.izzel.taboolib.util.Coerce
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.NumberConversions
import java.util.concurrent.CompletableFuture

object Utils {
    fun eval(player: Player, action: List<String>) {
        try {
            KetherShell.eval(action) {
                sender = player
            }
        } catch (e: LocalizedException) {
            e.print()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun LocalizedException.print() {
        println("[KetherxScriptBlock] Unexpected exception while parsing kether shell:")
        localizedMessage.split("\n").forEach {
            println("[KetherxScriptBlock] $it")
        }
    }

    fun fromLocation(location: Location): String {
        return "${location.world?.name},${location.x},${location.y},${location.z}".replace(".","__")
    }

    fun toLocation(source: String): Location {
        return source.replace("__",".").split(",").run {
            Location(
                Bukkit.getWorld(get(0)),
                getOrElse(1) { "0" }.asDouble(),
                getOrElse(2) { "0" }.asDouble(),
                getOrElse(3) { "0" }.asDouble()
            )
        }
    }

    private fun String.asDouble(): Double {
        return NumberConversions.toDouble(this)
    }
}