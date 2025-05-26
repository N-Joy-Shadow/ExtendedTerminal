package com.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.client.gui.style.ScreenStyle;
import com.myogoo.extendedterminal.client.screen.ETBaseTerminalScreen;
import com.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class UltimateTerminalScreen extends ETBaseTerminalScreen<UltimateTerminalMenu> {
    public UltimateTerminalScreen(UltimateTerminalMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
