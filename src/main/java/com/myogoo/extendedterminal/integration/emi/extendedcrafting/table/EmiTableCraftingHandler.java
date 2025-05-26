package com.myogoo.extendedterminal.integration.emi.extendedcrafting.table;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class EmiTableCraftingHandler<T extends BaseContainerMenu> implements StandardRecipeHandler<T> {
    @Override
    public List<Slot> getInputSources(T handler) {
        return List.of();
    }

    @Override
    public List<Slot> getCraftingSlots(T handler) {
        return List.of();
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return false;
    }
}
