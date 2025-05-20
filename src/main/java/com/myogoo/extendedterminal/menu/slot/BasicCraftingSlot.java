package com.myogoo.extendedterminal.menu.slot;

import appeng.api.inventories.InternalInventory;
import appeng.helpers.InventoryAction;
import appeng.menu.slot.AppEngCraftingSlot;
import net.minecraft.world.entity.player.Player;

public class BasicCraftingSlot extends AppEngCraftingSlot {
    public BasicCraftingSlot(Player player, InternalInventory craftingGrid) {
        super(player, craftingGrid);
    }

    public void doClick(InventoryAction action, Player who) {

    }
}
