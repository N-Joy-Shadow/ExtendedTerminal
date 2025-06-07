package com.myogoo.extendedterminal.util.extendedcrafting;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;

public final class ExtendedCraftingHelper {
    public static int getCraftingMatrixWidth(ITableRecipe recipe) {
        return recipe.getTier() * 2 + 1;
    }

    public static int getCraftingMatrixHeight(ITableRecipe recipe) {
        return recipe.getTier() * 2 + 1;
    }

    public static int getCraftingMatrixSize(ITableRecipe recipe) {
        int dim = getCraftingMatrixWidth(recipe);
        return dim * dim;
    }

    public static NonNullList<Ingredient> makeNxNIngredients(ITableRecipe recipe) {
        return NonNullList.withSize(getCraftingMatrixSize(recipe), Ingredient.EMPTY);
    }
}

