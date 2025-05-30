package com.myogoo.extendedterminal.init;

import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.ItemDefinition;
import com.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ETCreativeTab {
    public static final DeferredRegister<CreativeModeTab> REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtendedTerminal.MODID);

    static {
        REGISTER.register("itemgroup.extendedcrafting",() -> CreativeModeTab.builder()
                .title(Component.translatable("itemgroup.extendedcrafting.title"))
                .icon(AEParts.CRAFTING_TERMINAL::stack)
                .displayItems((params, output) -> {
                    for (ItemDefinition<?> item : ETItems.ITEMS) {
                        output.accept(item);
                    }
                })
                .build());
    }
}