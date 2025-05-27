package com.myogoo.extendedterminal.integration.emi;

import com.blakebr0.extendedcrafting.init.ModBlocks;
import com.blakebr0.extendedcrafting.init.ModMenuTypes;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.myogoo.extendedterminal.init.ETItems;
import com.myogoo.extendedterminal.integration.emi.extendedcrafting.table.EmiTableCraftingHandler;
import com.myogoo.extendedterminal.integration.emi.extendedcrafting.table.EmiTerminalCraftingHandler;
import com.myogoo.extendedterminal.integration.emi.extendedcrafting.table.ExtendedCraftingTableRecipe;
import com.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

@EmiEntrypoint
public class ETEmiPlugin implements EmiPlugin {

    private static final ItemStack[] BasicTables = {
            ETItems.BASIC_TERMINAL_PART.stack(),
            ModBlocks.BASIC_TABLE.get().asItem().getDefaultInstance(),
            ModBlocks.BASIC_AUTO_TABLE.get().asItem().getDefaultInstance()
    };

    private static final ItemStack[] AdvancedTables = {
            ETItems.ADVANCED_TERMINAL_PART.stack(),
            ModBlocks.ADVANCED_TABLE.get().asItem().getDefaultInstance(),
            ModBlocks.ADVANCED_AUTO_TABLE.get().asItem().getDefaultInstance()
    };

    private static final ItemStack[] EliteTables = {
            ETItems.ELITE_TERMINAL_PART.stack(),
            ModBlocks.ELITE_TABLE.get().asItem().getDefaultInstance(),
            ModBlocks.ELITE_AUTO_TABLE.get().asItem().getDefaultInstance()
    };

    private static final ItemStack[] UltimateTables = {
            ETItems.ULTIMATE_TERMINAL_PART.stack(),
            ModBlocks.ULTIMATE_TABLE.get().asItem().getDefaultInstance(),
            ModBlocks.ULTIMATE_AUTO_TABLE.get().asItem().getDefaultInstance()
    };

    @Override
    public void register(EmiRegistry registry) {

        registry.addCategory(ExtendedCraftingTableRecipe.BASIC_TABLE_CRAFTING_CATEGORY);
        registry.addCategory(ExtendedCraftingTableRecipe.ADVANCED_TABLE_CRAFTING_CATEGORY);
        registry.addCategory(ExtendedCraftingTableRecipe.ELITE_TABLE_CRAFTING_CATEGORY);
        registry.addCategory(ExtendedCraftingTableRecipe.ULTIMATE_TABLE_CRAFTING_CATEGORY);

        registry.addRecipeHandler(BasicTerminalMenu.TYPE, EmiTerminalCraftingHandler.EmiBasicTerminalCraftingHandler);
        registry.addRecipeHandler(AdvancedTerminalMenu.TYPE, EmiTerminalCraftingHandler.EmiAdvancedTerminalCraftingHandler);
        registry.addRecipeHandler(EliteTerminalMenu.TYPE, EmiTerminalCraftingHandler.EmiEliteTerminalCraftingHandler);
        registry.addRecipeHandler(UltimateTerminalMenu.TYPE, EmiTerminalCraftingHandler.EmiUltimateTerminalCraftingHandler);

        registry.addRecipeHandler(ModMenuTypes.BASIC_TABLE.get(), EmiTableCraftingHandler.EmiBasicTableCraftingHandler);
        registry.addRecipeHandler(ModMenuTypes.ADVANCED_TABLE.get(), EmiTableCraftingHandler.EmiAdvancedTableCraftingHandler);
        registry.addRecipeHandler(ModMenuTypes.ELITE_TABLE.get(), EmiTableCraftingHandler.EmiEliteTableCraftingHandler);
        registry.addRecipeHandler(ModMenuTypes.ULTIMATE_TABLE.get(), EmiTableCraftingHandler.EmiUltimateTableCraftingHandler);

        Arrays.stream(BasicTables)
                .map(EmiStack::of)
                        .forEach(stack -> registry.addWorkstation(
                                ExtendedCraftingTableRecipe.BASIC_TABLE_CRAFTING_CATEGORY,
                                stack
                        ));

        Arrays.stream(AdvancedTables)
                .map(EmiStack::of)
                .forEach(stack -> registry.addWorkstation(
                        ExtendedCraftingTableRecipe.ADVANCED_TABLE_CRAFTING_CATEGORY,
                        stack
                ));

        Arrays.stream(EliteTables)
                .map(EmiStack::of)
                .forEach(stack -> registry.addWorkstation(
                        ExtendedCraftingTableRecipe.ELITE_TABLE_CRAFTING_CATEGORY,
                        stack
                ));

        Arrays.stream(UltimateTables)
                .map(EmiStack::of)
                .forEach(stack -> registry.addWorkstation(
                        ExtendedCraftingTableRecipe.ULTIMATE_TABLE_CRAFTING_CATEGORY,
                        stack
                ));



        registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.TABLE.get())
                .stream()
                .map(x -> {
                    switch (x.value().getTier()) {
                        case 2 -> {
                            return new ExtendedCraftingTableRecipe(ExtendedCraftingTableRecipe.ADVANCED_TABLE_CRAFTING_CATEGORY,x);
                        }
                        case 3 -> {
                            return new ExtendedCraftingTableRecipe(ExtendedCraftingTableRecipe.ELITE_TABLE_CRAFTING_CATEGORY,x);
                        }
                        case 4 -> {
                            return new ExtendedCraftingTableRecipe(ExtendedCraftingTableRecipe.ULTIMATE_TABLE_CRAFTING_CATEGORY,x);
                        }
                        default -> {
                            return new ExtendedCraftingTableRecipe(ExtendedCraftingTableRecipe.BASIC_TABLE_CRAFTING_CATEGORY,x);                        }
                    }
                })
                .forEach(registry::addRecipe);
    }
}
