package com.myogoo.extendedterminal.init;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.ServerboundPacket;
import com.blakebr0.extendedcrafting.ExtendedCrafting;
import com.myogoo.extendedterminal.network.serverbound.ETFillCraftingGridFromRecipePacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ETNetwork {
    public static void init(RegisterPayloadHandlersEvent event) {
        var register = event.registrar(ExtendedCrafting.MOD_ID);

        register.playToServer(ETFillCraftingGridFromRecipePacket.TYPE, ETFillCraftingGridFromRecipePacket.STREAM_CODEC, ServerboundPacket::handleOnServer);
    }
}
