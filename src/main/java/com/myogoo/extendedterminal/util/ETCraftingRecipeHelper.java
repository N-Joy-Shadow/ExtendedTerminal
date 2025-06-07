package com.myogoo.extendedterminal.util;

import appeng.util.CraftingRecipeUtil;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.myogoo.extendedterminal.util.extendedcrafting.ExtendedCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class ETCraftingRecipeHelper {
    public static NonNullList<Ingredient> ensureNxNCraftingMatrix(Recipe<?> recipe) {
        var ingredients = recipe.getIngredients();
        NonNullList<Ingredient> expandedIngredients;
        if(recipe instanceof ITableRecipe tableRecipe) {
            int size = ExtendedCraftingHelper.getCraftingMatrixSize(tableRecipe);
            expandedIngredients = ExtendedCraftingHelper.makeNxNIngredients(tableRecipe);

            if(tableRecipe instanceof ShapedTableRecipe shapedTableRecipe) {
                var width = shapedTableRecipe.getWidth();
                var height = shapedTableRecipe.getHeight();
                int matrixWidth = ExtendedCraftingHelper.getCraftingMatrixWidth(tableRecipe);
                // Map shaped recipe into center of NxN matrix
                for(int h = 0; h < height; h++) {
                    for(int w = 0; w < width; w++) {
                        var source = w + h * width;
                        var target = w + h * matrixWidth;
                        var ing = ingredients.get(source);
                        expandedIngredients.set(target, ing);
                    }
                }
            } else {
                for(int i = 0; i < ingredients.size(); i++) {
                    expandedIngredients.set(i, ingredients.get(i));
                }
            }
        } else {
            return CraftingRecipeUtil.ensure3by3CraftingMatrix(recipe);
        }
        return expandedIngredients;
    }
}
