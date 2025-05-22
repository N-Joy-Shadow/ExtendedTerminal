package com.myogoo.extendedterminal.client.screen;

import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.AEConfig;
import appeng.menu.SlotSemantic;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class ETBaseTerminalScreen<T extends ETBaseTerminalMenu> extends MEStorageScreen<T> {
    public ETBaseTerminalScreen(T menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }

    @Override
    public void onClose() {
        if(AEConfig.instance().isClearGridOnClose()) {
            this.getMenu().clearCraftingGrid();
        }
        super.onClose();
    }

    protected void drawCraftingSlot(SlotSemantic slotSemantics, int line) {
        List<Slot> craftingSlots = this.getMenu().getSlots(slotSemantics);
        Slot firstSlot = craftingSlots.getFirst();

        int craftGridStartX = firstSlot.x;
        int craftGridStartY = firstSlot.y;

        for(int row = 0; row < line; row++) {
            for(int col = 0; col < line; col++) {
                int index = row * line + col;
                Slot slot = craftingSlots.get(index);
                int x = craftGridStartX + col * 18;
                int y = craftGridStartY + row * 18;
                slot.x = x;
                slot.y = y;
            }
        }
    }
}
