package com.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import com.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class BasicTerminalMenu extends ETBaseTerminalMenu  {
    public static final MenuType<BasicTerminalMenu> TYPE = MenuTypeBuilder
            .create(BasicTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.BASIC_TERMINAL.getId());

    public BasicTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.BASIC_TERMINAL);
    }
}
