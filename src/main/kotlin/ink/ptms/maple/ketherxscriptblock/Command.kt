package ink.ptms.maple.ketherxscriptblock

import ink.ptms.maple.ketherxscriptblock.data.ScriptData
import io.izzel.taboolib.module.command.base.*
import io.izzel.taboolib.module.tellraw.TellrawJson
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.RoundingMode
import java.text.DecimalFormat

@BaseCommand(name = "ketherxscriptblock", aliases = ["ksb"], permission = "ketherxscriptblock.admin")
class Command : BaseMainCommand(), Helper {

    @SubCommand(description = "脚本列表", permission = "*")
    var list: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            sender.info("服务器拥有的脚本:")
            KetherxScriptBlock.scripts.forEach {
                TellrawJson.create().append("§8 - §f${Utils.fromLocation(it.location)}").hoverText("点击编辑脚本方块")
                    .clickCommand("/ketherxscriptblock editThe ${Utils.fromLocation(it.location)}")
                    .send(sender)
            }
        }
    }

    @SubCommand(description = "创建脚本", permission = "*")
    var create: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            val player = sender as? Player ?: return
            val block = player.getTargetBlockExact() ?: return
            if (block.type == Material.AIR) {
                sender.error("无效的方块.")
                return
            }
            val blockData = KetherxScriptBlock.getScript(block.location)
            if (blockData != null) {
                block.display()
                sender.error("该方块已存在脚本.")
                return
            }
            block.display()
            sender.info("脚本方块已创建.")
            KetherxScriptBlock.scripts.add(
                ScriptData(block.location).run {
                    this.openEdit(player)
                    this
                }
            )
            KetherxScriptBlock.save()
        }
    }

    @SubCommand(description = "移除脚本", permission = "*")
    var remove: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            val player = sender as? Player ?: return
            val block = player.getTargetBlockExact() ?: return
            if (block.type == Material.AIR) {
                sender.error("无效的方块.")
                return
            }
            val blockData = KetherxScriptBlock.getScript(block.location)
            if (blockData == null) {
                block.display()
                sender.error("该方块不存在脚本.")
                return
            }
            block.display()
            sender.info("脚本方块已删除.")
            KetherxScriptBlock.scripts.remove(blockData)
            KetherxScriptBlock.remove(block.location)
        }
    }

    @SubCommand(description = "编辑脚本", permission = "*")
    var edit: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            val player = sender as? Player ?: return
            val block = player.getTargetBlockExact() ?: return
            if (block.type == Material.AIR) {
                sender.error("无效的方块.")
                return
            }
            val blockData = KetherxScriptBlock.getScript(block.location)
            if (blockData == null) {
                block.display()
                sender.error("该方块不存在脚本.")
                return
            }
            block.display()
            blockData.openEdit(player)
            sender.info("正在编辑脚本.")
        }
    }

    @SubCommand(description = "编辑指定位置脚本", permission = "*")
    var editThe: BaseSubCommand = object : BaseSubCommand() {
        override fun getArguments(): Array<Argument> {
            return arrayOf(Argument("脚本位置"))
        }

        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            val player = sender as? Player ?: return
            val block = Utils.toLocation(args[0]).block
            if (block.type == Material.AIR) {
                sender.error("无效的方块.")
                return
            }
            val blockData = KetherxScriptBlock.getScript(block.location)
            if (blockData == null) {
                block.display()
                sender.error("该方块不存在脚本.")
                return
            }
            block.display()
            blockData.openEdit(player)
            sender.info("正在编辑脚本.")
        }
    }

    @SubCommand(description = "周围的脚本", permission = "*")
    var near: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            val player = sender as? Player ?: return
            sender.info("附近脚本:")
            KetherxScriptBlock.scripts.forEach {
                if (it.location.world?.name == player.world.name && it.location.distance(sender.location) < 50) {
                    it.location.block.display()
                    sender.info(
                        "§8 - §f${Utils.fromLocation(it.location)} §7(${
                            getNoMoreThanTwoDigits(
                                it.location.distance(
                                    sender.location
                                )
                            )
                        }m)"
                    )
                }
            }
        }
    }

    @SubCommand(description = "加载脚本", permission = "*")
    var load: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            sender.info("操作完成")
            KetherxScriptBlock.load()
        }
    }

    @SubCommand(description = "保存脚本", permission = "*")
    var save: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            sender.info("操作完成")
            KetherxScriptBlock.save()
        }
    }

    @SubCommand(description = "重载插件", permission = "*")
    var reload: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            sender.info("操作完成")
            KetherxScriptBlock.save()
            KetherxScriptBlock.load()
        }
    }


    private fun getNoMoreThanTwoDigits(number: Double): String {
        val format = DecimalFormat("0.##")
        //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }

}