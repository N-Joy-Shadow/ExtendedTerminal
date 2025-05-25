package com.myogoo.extendedterminal.integration.emi;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.myogoo.extendedterminal.init.ETItems;
import com.myogoo.extendedterminal.integration.emi.extendedcrafting.table.ExtendedCraftingTableRecipe;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.ItemStack;

@EmiEntrypoint
public class ETEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        ItemStack basicTerminal = ETItems.BASIC_TERMINAL_PART_ITEM.stack();

        //Basic Crafting Terminal //test
        registry.addCategory(ExtendedCraftingTableRecipe.CATEGORY);
        registry.addWorkstation(ExtendedCraftingTableRecipe.CATEGORY, EmiStack.of(basicTerminal));
        registry.getRecipeManager().getAllRecipesFor(ModRecipeTypes.TABLE.get())
                .stream()
                .map(ExtendedCraftingTableRecipe::new)
                .forEach(registry::addRecipe);
    }
}
