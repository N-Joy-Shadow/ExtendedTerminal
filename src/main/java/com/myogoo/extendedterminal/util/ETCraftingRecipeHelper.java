package com.myogoo.extendedterminal.util;

import appeng.util.CraftingRecipeUtil;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.google.common.base.Preconditions;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class ETCraftingRecipeHelper {

    public static NonNullList<Ingredient> ensureNxNCraftingMatrix(Recipe<?> recipe) {
        var ingredients = recipe.getIngredients();
        NonNullList<Ingredient> expandedIngredients;
        if(recipe instanceof ITableRecipe tableRecipe) {
            switch (tableRecipe.getTier()) {
                case 1 -> expandedIngredients = NonNullList.withSize(9, Ingredient.EMPTY);
                case 2 -> expandedIngredients = NonNullList.withSize(25, Ingredient.EMPTY);
                case 3 -> expandedIngredients = NonNullList.withSize(49, Ingredient.EMPTY);
                case 4 -> expandedIngredients = NonNullList.withSize(81, Ingredient.EMPTY);
                default -> expandedIngredients = NonNullList.withSize(9, Ingredient.EMPTY);
            }

            if(tableRecipe instanceof ShapedTableRecipe shapedTableRecipe) {
                var width = shapedTableRecipe.getWidth();
                var height = shapedTableRecipe.getHeight();
                //일단 보류
                for(int h = 0; h < height; h++) {
                    for(int w = 0; w < width; w++) {
                        var source = w + h * width;
                        var target = w + h * 3;
                        var i = ingredients.get(source);
                        expandedIngredients.set(target, i);
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
