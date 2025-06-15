package com.myogoo.extendedterminal.integration;

import appeng.api.stacks.AEItemKey;
import appeng.core.AELog;
import appeng.core.network.ServerboundPacket;
import appeng.integration.modules.itemlists.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.util.CraftingRecipeUtil;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import com.myogoo.extendedterminal.network.serverbound.ETFillCraftingGridFromRecipePacket;
import com.myogoo.extendedterminal.util.ETCraftingRecipeHelper;
import com.myogoo.extendedterminal.util.extendedcrafting.ExtendedCraftingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ItemListTermCraftingHelper {
    private static final Comparator<GridInventoryEntry> ENTRY_COMPARATOR = Comparator
            .comparing(GridInventoryEntry::getStoredAmount);

    private ItemListTermCraftingHelper() {
    }

    public static void performTransfer(ExtendedTerminalBaseMenu menu, @Nullable ResourceLocation recipeId, Recipe<?> recipe,
                                       boolean craftMissing) {

        // We send the items in the recipe in any case to serve as a fallback in case the recipe is transient
        var templateItems = findGoodTemplateItems(recipe, menu);

        // Don't transmit a recipe id to the server in case the recipe is not actually resolvable
        // this is the case for recipes synthetically generated for JEI
        if (recipeId != null && menu.getPlayer().level().getRecipeManager().byKey(recipeId).isEmpty()) {
            AELog.debug("Cannot send recipe id %s to server because it's transient", recipeId);
            recipeId = null;
        }

        ServerboundPacket message = new ETFillCraftingGridFromRecipePacket(recipeId, templateItems, craftMissing);
        PacketDistributor.sendToServer(message);
    }

    private static NonNullList<ItemStack> findGoodTemplateItems(Recipe<?> recipe, ExtendedTerminalBaseMenu menu) {
        var ingredientPriorities = EncodingHelper.getIngredientPriorities(menu, ENTRY_COMPARATOR);

        var templateItems = NonNullList.withSize(menu.getCraftingMatrixSize(), ItemStack.EMPTY);
        var ingredients = ensureNxNCraftingMatrix(recipe);
        for (int i = 0; i < ingredients.size(); i++) {
            var ingredient = ingredients.get(i);
            if (!ingredient.isEmpty()) {
                // Try to find the best item. In case the ingredient is a tag, it might contain versions the
                // player doesn't actually have
                var stack = ingredientPriorities.entrySet()
                        .stream()
                        .filter(e -> e.getKey() instanceof AEItemKey itemKey && itemKey.matches(ingredient))
                        .max(Comparator.comparingInt(Map.Entry::getValue))
                        .map(e -> ((AEItemKey) e.getKey()).toStack())
                        .orElse(ingredient.getItems()[0]);
                templateItems.set(i, stack);
            }
        }
        return templateItems;
    }

    public static Map<Integer, Ingredient> getGuiSlotToIngredientMap(Recipe<?> recipe, int gridWidth) {
        // Ensure ingredients fit in NxN grid
        var raw = recipe.getIngredients();
        java.util.List<Ingredient> ingredients;
        if (recipe instanceof ITableRecipe tableRecipe) {
            ingredients = ensureNxNCraftingMatrix(tableRecipe);
        } else {
            ingredients = raw;
        }
        // Clip to grid capacity
        int max = gridWidth * gridWidth;
        int count = Math.min(ingredients.size(), max);
        var result = new HashMap<Integer, Ingredient>(count);
        for (int i = 0; i < count; i++) {
            var guiSlot = (i / gridWidth) * gridWidth + (i % gridWidth);
            var ing = ingredients.get(i);
            if (!ing.isEmpty()) {
                result.put(guiSlot, ing);
            }
        }
        return result;
    }

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

