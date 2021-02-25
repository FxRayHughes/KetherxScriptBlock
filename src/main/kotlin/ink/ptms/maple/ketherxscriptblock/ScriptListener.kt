package ink.ptms.maple.ketherxscriptblock

import ink.ptms.maple.ketherxscriptblock.data.ScriptType
import io.izzel.taboolib.module.inject.TListener
import io.izzel.taboolib.util.item.Items
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.EquipmentSlot

@TListener
class ScriptListener : Listener {

    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if(event.hand != EquipmentSlot.HAND){
            return
        }
        if (event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.LEFT_CLICK_BLOCK) {
            val block = event.clickedBlock ?: return
            KetherxScriptBlock.getScript(block.location).run {
                if (this == null) {
                    return@run
                }
                if (this.type != ScriptType.INTERACT || Cooldown.isCooldown(
                        event.player.name,
                        Utils.fromLocation(this.location)
                    )
                ) {
                    return@run
                }
                Utils.check(event.player, this.condition).thenAccept {
                    if (it) {
                        Utils.eval(event.player, this.action)
                        Cooldown.toCooldown(event.player.name, Utils.fromLocation(this.location), this.cooldown.toInt())
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.to == null || event.from.block == event.to!!.block) {
            return
        }
        KetherxScriptBlock.getScript(event.to!!.block.getRelative(BlockFace.DOWN).location).run {
            if (this == null) {
                return@run
            }
            if (this.type != ScriptType.WALK || Cooldown.isCooldown(
                    event.player.name,
                    Utils.fromLocation(this.location)
                )
            ) {
                return@run
            }
            Utils.check(event.player, this.condition).thenAccept {
                if (it) {
                    Utils.eval(event.player, this.action)
                    Cooldown.toCooldown(event.player.name, Utils.fromLocation(this.location), this.cooldown.toInt())
                }
            }
        }
    }


    @EventHandler
    fun onPlayerInv(event: InventoryClickEvent) {
        val item = event.currentItem
        if (Items.hasLore(item, "Input!!")) {
            item?.amount = 0
        }
    }

    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack
        if (Items.hasLore(item, "Input!!")) {
            item.amount = 0
        }
    }

    @EventHandler
    fun onPlayerItemHeldEvent(event: PlayerItemHeldEvent) {
        val item = event.player.inventory.getItem(event.previousSlot)
        if (Items.hasLore(item, "Input!!")) {
            item?.amount = 0
        }
        if (Items.hasLore(event.player.inventory.getItem(event.newSlot), "Input!!")) {
            event.player.inventory.getItem(event.newSlot)?.amount = 0
        }
    }

}