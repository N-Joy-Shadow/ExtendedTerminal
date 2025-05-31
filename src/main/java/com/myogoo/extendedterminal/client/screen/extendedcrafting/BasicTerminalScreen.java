package com.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.client.gui.style.ScreenStyle;
import com.myogoo.extendedterminal.client.screen.ETBaseTerminalScreen;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BasicTerminalScreen extends ETBaseTerminalScreen<BasicTerminalMenu> {
    public BasicTerminalScreen(BasicTerminalMenu menu, Inventory inventory, Component title, ScreenStyle style) {
        super(menu, inventory, title, style);
    }
}
