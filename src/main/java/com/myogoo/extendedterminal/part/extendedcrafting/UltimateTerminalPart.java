package com.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import com.myogoo.extendedterminal.part.ETBaseTerminalPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class UltimateTerminalPart extends ETBaseTerminalPart {
    public UltimateTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ULTIMATE_TERMINAL);
    }

    @Override
    public MenuType<?> getMenuType(Player p) { return UltimateTerminalMenu.TYPE; }
}
