package com.myogoo.extendedterminal.init;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.BasicTerminalMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.client.gui.screens.MenuScreens;

import java.util.function.Supplier;

public final class ETMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, ExtendedTerminal.MODID);

    public static final Supplier<MenuType<BasicTerminalMenu>> BASIC_TERMINAL = REGISTER.register("basic_terminal", () -> BasicTerminalMenu.TYPE);

}
