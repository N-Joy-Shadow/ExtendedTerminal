package com.myogoo.extendedterminal.init;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import appeng.api.parts.PartModels;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.ItemDefinition;
import appeng.items.parts.PartItem;
import appeng.items.parts.PartModelsHelper;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.part.BasicExtendedTerminalPart;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ETItems {
    public static final DeferredRegister.Items REGISTER = DeferredRegister.createItems(ExtendedTerminal.MODID);

    public static final List<ItemDefinition<?>> ITEMS = new ArrayList<>();

    public static final ItemDefinition<?> EXAMPLE_ITEM = createItem("example item", "example_item", Item::new);

    public static final ItemDefinition<?> BASIC_TERMINAL_PART_ITEM = createPart("basic crafting terminal", "basic_terminal", BasicExtendedTerminalPart.class, BasicExtendedTerminalPart::new);

    private static <T extends Item> ItemDefinition<T> createItem(String name, String id, Function<Item.Properties, T> itemFactory) {
        var item = new ItemDefinition<>(name, REGISTER.registerItem(id, itemFactory));
        ITEMS.add(item);
        return item;
    }

    private static <T extends IPart>ItemDefinition<PartItem<T>> createPart(String name, String id, Class<T> partClass, Function<IPartItem<T>, T> partFactory) {
        PartModels.registerModels(PartModelsHelper.createModels(AEParts.CRAFTING_TERMINAL.getClass()));
        return createItem(name, id, p -> new PartItem<>(p, partClass,partFactory));
    }

}
