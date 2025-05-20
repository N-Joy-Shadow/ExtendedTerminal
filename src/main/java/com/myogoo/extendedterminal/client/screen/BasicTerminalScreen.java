package com.myogoo.extendedterminal.client.screen;

import appeng.api.config.ActionItems;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.core.AEConfig;
import com.myogoo.extendedterminal.menu.BasicTerminalMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class BasicTerminalScreen extends MEStorageScreen<BasicTerminalMenu> {
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

}
