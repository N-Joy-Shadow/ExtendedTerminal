package com.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import com.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class UltimateTerminalMenu extends ExtendedTerminalBaseMenu {
    public static final MenuType<UltimateTerminalMenu> TYPE = MenuTypeBuilder
            .create(UltimateTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.ULTIMATE_TERMINAL.getId());

    public UltimateTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.ULTIMATE_TERMINAL);
    }
}
