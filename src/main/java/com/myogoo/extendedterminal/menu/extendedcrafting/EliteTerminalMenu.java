package com.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.storage.ITerminalHost;
import appeng.menu.implementations.MenuTypeBuilder;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import com.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class EliteTerminalMenu extends ETBaseTerminalMenu {
    public static final MenuType<EliteTerminalMenu> TYPE = MenuTypeBuilder
            .create(EliteTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ETMenuType.ELITE_TERMINAL.getId());

    public EliteTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host, ETMenuType.ELITE_TERMINAL);
    }
}
