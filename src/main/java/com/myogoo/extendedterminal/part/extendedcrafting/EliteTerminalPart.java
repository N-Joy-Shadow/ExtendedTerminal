package com.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import com.myogoo.extendedterminal.part.ETBaseTerminalPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class EliteTerminalPart extends ETBaseTerminalPart {
    public EliteTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ELITE_TERMINAL);
    }

    @Override
    public MenuType<?> getMenuType(Player p) { return EliteTerminalMenu.TYPE; }
}
