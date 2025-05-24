package com.myogoo.extendedterminal.part.extendedcrafting;

import appeng.api.parts.IPartItem;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import com.myogoo.extendedterminal.part.ETBaseTerminalPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class AdvancedTerminalPart extends ETBaseTerminalPart {
    public AdvancedTerminalPart(IPartItem<?> partItem) {
        super(partItem, ETMenuType.ADVANCED_TERMINAL);
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return AdvancedTerminalMenu.TYPE;
    }
}
