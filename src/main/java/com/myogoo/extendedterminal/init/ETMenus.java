package com.myogoo.extendedterminal.init;

import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ETMenus {
    public static final DeferredRegister<MenuType<?>> REGISTER = DeferredRegister.create(Registries.MENU, ExtendedTerminal.MODID);

    public static final Supplier<MenuType<BasicTerminalMenu>> BASIC_TERMINAL = REGISTER.register(ETMenuType.BASIC_TERMINAL.getIdAsString(), () -> BasicTerminalMenu.TYPE);
    public static final Supplier<MenuType<AdvancedTerminalMenu>> ADVANCED_TERMINAL = REGISTER.register(ETMenuType.ADVANCED_TERMINAL.getIdAsString(), () -> AdvancedTerminalMenu.TYPE);
    public static final Supplier<MenuType<EliteTerminalMenu>> ELITE_TERMINAL = REGISTER.register(ETMenuType.ELITE_TERMINAL.getIdAsString(), () -> EliteTerminalMenu.TYPE);
    public static final Supplier<MenuType<?>> ULTIMATE_TERMINAL = REGISTER.register(ETMenuType.ULTIMATE_TERMINAL.getIdAsString(), () -> UltimateTerminalMenu.TYPE);
}
