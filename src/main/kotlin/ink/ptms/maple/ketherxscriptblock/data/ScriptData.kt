package ink.ptms.maple.ketherxscriptblock.data

import ink.ptms.maple.ketherxscriptblock.Helper
import ink.ptms.maple.ketherxscriptblock.KetherxScriptBlock
import ink.ptms.maple.ketherxscriptblock.Utils
import io.izzel.taboolib.util.Features
import io.izzel.taboolib.util.item.ItemBuilder
import io.izzel.taboolib.util.item.Items
import io.izzel.taboolib.util.item.inventory.MenuBuilder
import io.izzel.taboolib.util.lite.Materials
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player

class ScriptData(
    val location: Location,
    var type: ScriptType = ScriptType.NULL,
    var action: MutableList<String> = ArrayList(),
    var cooldown: Long = 100
) : Helper {

    fun isBlock(block: Block): Boolean {
        return this.location == block.location
    }

    fun openEdit(player: Player) {
        MenuBuilder.builder()
            .title("编辑脚本 ${Utils.fromLocation(location)}")
            .rows(3)
            .build { inv ->
                //9 10 11 12 13 14 15 16 17
                inv.setItem(
                    11,
                    ItemBuilder(Materials.DAYLIGHT_DETECTOR.parseMaterial()).name("§f触发方式")
                        .lore("§7${type.type}").build()
                )
                inv.setItem(
                    13,
                    ItemBuilder(Materials.PISTON.parseMaterial()).name("§f动作").lore(action.map { "§7$it" }).build()
                )
                inv.setItem(
                    15,
                    ItemBuilder(Materials.CLOCK.parseMaterial()).name("§f冷却").lore(listOf("§7${cooldown}tick")).build()
                )
            }.event {
                it.isCancelled = true
                when (it.rawSlot) {
                    11 -> {
                        type = when (type) {
                            ScriptType.NULL -> ScriptType.INTERACT
                            ScriptType.INTERACT -> ScriptType.WALK
                            ScriptType.WALK -> ScriptType.NULL
                        }
                        openEdit(player)
                    }
                    13 -> {
                        player.closeInventory()
                        player.info("请编辑书籍,编辑后点击&f完成&7按钮即可&f保存&7! 保存后书本可丢弃")
                        Features.inputBook(player, "编辑动作", false, action) { book ->
                            player.info("编辑完成!")
                            Items.replaceLore(player.inventory.itemInMainHand, "Input", "Input!!")
                            if (book[0] == "clear") {
                                action.clear()
                                openEdit(player)
                                return@inputBook
                            }
                            action = book
                            openEdit(player)
                        }
                    }
                    15 -> {
                        Features.inputSign(player, arrayOf("${cooldown}tick", "请在第一行输入冷却时间", "单位Tick")) {
                            cooldown = it[0].replace("[^0-9]".toRegex(), "").toLong()
                            openEdit(player)
                        }
                    }
                }
            }.close {
                KetherxScriptBlock.save()
            }.open(player)
    }

}