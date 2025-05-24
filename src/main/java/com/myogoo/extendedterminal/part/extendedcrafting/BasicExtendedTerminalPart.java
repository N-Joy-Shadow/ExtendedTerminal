package com.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import com.myogoo.extendedterminal.part.ETBaseTerminalPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class BasicExtendedTerminalPart extends ETBaseTerminalPart {
    public BasicExtendedTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.BASIC_TERMINAL);
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return BasicTerminalMenu.TYPE;
    }

}
