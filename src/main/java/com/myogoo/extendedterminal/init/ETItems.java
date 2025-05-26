package com.myogoo.extendedterminal.init;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.part.extendedcrafting.AdvancedTerminalPart;
import com.myogoo.extendedterminal.part.extendedcrafting.BasicExtendedTerminalPart;
import com.myogoo.extendedterminal.part.extendedcrafting.EliteTerminalPart;
import com.myogoo.extendedterminal.part.extendedcrafting.UltimateTerminalPart;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ETItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtendedTerminal.MODID);

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static final ItemDefinition<?> BASIC_TERMINAL_PART = createETPart(ETMenuType.BASIC_TERMINAL, BasicExtendedTerminalPart.class, BasicExtendedTerminalPart::new);
    public static final ItemDefinition<?> ADVANCED_TERMINAL_PART = createETPart(ETMenuType.ADVANCED_TERMINAL, AdvancedTerminalPart.class, AdvancedTerminalPart::new);
    public static final ItemDefinition<?> ELITE_TERMINAL_PART = createETPart(ETMenuType.ELITE_TERMINAL, EliteTerminalPart.class, EliteTerminalPart::new);
    public static final ItemDefinition<?> ULTIMATE_TERMINAL_PART = createETPart(ETMenuType.ULTIMATE_TERMINAL, UltimateTerminalPart.class, UltimateTerminalPart::new);

    private static <T extends Item> ItemDefinition<T> createItem(String name, String id, Function<Item.Properties, T> itemFactory) {
        var item = new ItemDefinition<>(name, REGISTER.registerItem(id, itemFactory));
        ITEMS.add(item);
        return item;
    }

    private static <T extends IPart>ItemDefinition<PartItem<T>> createPart(String name, String id, Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        PartModels.registerModels(PartModelsHelper.createModels(partClass));
        return createItem(name, id, p -> new PartItem<>(p, partClass,partFactory));
    }

    private static <T extends IPart>ItemDefinition<PartItem<T>> createETPart(ETMenuType etMenuType, Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        String id = etMenuType.getIdAsString();
        String name = etMenuType.getEnglishName();
        return createPart(name, id, partClass, partFactory);
    }

}
