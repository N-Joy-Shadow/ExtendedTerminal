package com.myogoo.extendedterminal.client;

import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.init.client.InitScreens;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.client.screen.extendedcrafting.BasicTerminalScreen;
import com.myogoo.extendedterminal.init.ETItems;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = ExtendedTerminal.MODID, dist = Dist.CLIENT)
public class ETClient {
    public ETClient(IEventBus eventBus) {
        eventBus.addListener(ETClient::initScreens);
        eventBus.addListener(RegisterColorHandlersEvent.Item.class,ETClient::initColor);
    }

    public static void initScreens(RegisterMenuScreensEvent event) {
        InitScreens.register(event, BasicTerminalMenu.TYPE, BasicTerminalScreen::new ,"/screens/extended_terminal/basic_terminal.json");
    }

    private static void initColor(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> new StaticItemColor(AEColor.TRANSPARENT).getColor(stack, tintIndex) | 0xFF000000, ETItems.BASIC_TERMINAL_PART_ITEM.get());
    }


}
