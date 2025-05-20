package com.myogoo.extendedterminal.client.screen;

import appeng.api.config.ActionItems;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.core.AEConfig;
import com.myogoo.extendedterminal.menu.BasicTerminalMenu;
import com.myogoo.extendedterminal.menu.ETSlotSemantics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.logging.Logger;

public class BasicTerminalScreen extends MEStorageScreen<BasicTerminalMenu> {
    private static final Logger LOGGER = Logger.getLogger(BasicTerminalScreen.class.getName());

    public BasicTerminalScreen(BasicTerminalMenu menu, Inventory inventory, Component title, ScreenStyle style) {
        super(menu, inventory, title, style);
        ActionButton clearBtn = new ActionButton(ActionItems.S_STASH, btn -> menu.clearCraftingGrid());
    }

    @Override
    public void onClose() {
        if(AEConfig.instance().isClearGridOnClose()) {
            this.getMenu().clearCraftingGrid();
        }
        super.onClose();
    }

    @Override
    public void init() {
        super.init();
        LOGGER.info(String.format("%d %d\n",this.leftPos, this.topPos));
        List<Slot> craftingSlots = this.getMenu().getSlots(ETSlotSemantics.BASIC_CRAFTING_GRID);
        int craftGridStartX = this.leftPos + 10;
        int craftGridStartY = this.topPos + 10;
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                Slot slot = craftingSlots.get(index);
                int x = craftGridStartX + col * 18;
                int y = craftGridStartY + row * 18;
                slot.x = x;
                slot.y = y;
            }
        }
    }


}
