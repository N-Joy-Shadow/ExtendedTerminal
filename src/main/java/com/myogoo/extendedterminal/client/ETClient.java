package com.myogoo.extendedterminal.client;

import appeng.init.client.InitScreens;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.client.screen.extendedcrafting.AdvancedTerminalScreen;
import com.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import com.myogoo.extendedterminal.client.screen.extendedcrafting.EliteTerminalScreen;
import com.myogoo.extendedterminal.client.screen.extendedcrafting.UltimateTerminalScreen;
import com.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = ExtendedTerminal.MODID, dist = Dist.CLIENT)
public class ETClient {
    public ETClient(IEventBus eventBus) {
        eventBus.addListener(ETClient::initScreens);
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event, BasicTerminalMenu.TYPE, BasicTerminalScreen::new, "/screens/extended_terminal/basic_terminal.json");
        InitScreens.register(event, AdvancedTerminalMenu.TYPE, AdvancedTerminalScreen::new, "/screens/extended_terminal/advanced_terminal.json");
        InitScreens.register(event, EliteTerminalMenu.TYPE, EliteTerminalScreen::new, "/screens/extended_terminal/elite_terminal.json");
        InitScreens.register(event, UltimateTerminalMenu.TYPE, UltimateTerminalScreen::new, "/screens/extended_terminal/ultimate_terminal.json");
    }
}

