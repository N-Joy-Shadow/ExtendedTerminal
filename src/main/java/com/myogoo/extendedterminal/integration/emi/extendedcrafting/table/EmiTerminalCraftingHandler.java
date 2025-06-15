package com.myogoo.extendedterminal.integration.emi.extendedcrafting.table;

import appeng.core.localization.ItemModText;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapedTableRecipe;
import com.myogoo.extendedterminal.integration.ItemListTermCraftingHelper;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.*;
import com.myogoo.extendedterminal.util.ETCraftingRecipeHelper;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.myogoo.extendedterminal.integration.ItemListTermCraftingHelper.getGuiSlotToIngredientMap;

public class EmiTerminalCraftingHandler<T extends ExtendedTerminalBaseMenu> extends AbstractTableRecipeHandler<T> {
    public final static EmiTerminalCraftingHandler<BasicTerminalMenu> EmiBasicTerminalCraftingHandler = new EmiTerminalCraftingHandler<>(BasicTerminalMenu.class,ETMenuType.BASIC_TERMINAL);
    public final static EmiTerminalCraftingHandler<AdvancedTerminalMenu> EmiAdvancedTerminalCraftingHandler = new EmiTerminalCraftingHandler<>(AdvancedTerminalMenu.class,ETMenuType.ADVANCED_TERMINAL);
    public final static EmiTerminalCraftingHandler<EliteTerminalMenu> EmiEliteTerminalCraftingHandler = new EmiTerminalCraftingHandler<>(EliteTerminalMenu.class,ETMenuType.ELITE_TERMINAL);
    public final static EmiTerminalCraftingHandler<UltimateTerminalMenu> EmiUltimateTerminalCraftingHandler = new EmiTerminalCraftingHandler<>(UltimateTerminalMenu.class, ETMenuType.ULTIMATE_TERMINAL);

    private final ETMenuType menuType;
    public EmiTerminalCraftingHandler(Class<T> containerClass,ETMenuType menuType) {
        super(containerClass);
        this.menuType = menuType;
    }
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(ExtendedCraftingTableRecipe.getCategoryFromMenuType(this.menuType));
    }


    @Override
    protected Result transferRecipe(T menu, @Nullable RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {
        var recipeId = holder != null ? holder.id() : null;
        var recipe = holder != null ? holder.value() : null;

        boolean craftingRecipe = isCraftingRecipe(recipe, emiRecipe);
        if (!craftingRecipe) {
            return Result.createNotApplicable();
        }

        if (!fitsInNxNGrid(recipe, emiRecipe, menuType.getGridSize())) {
            return Result.createFailed(ItemModText.RECIPE_TOO_LARGE.text());
        }

        if (recipe == null) {
            recipe = createFakeRecipe(emiRecipe);
        }

        // Find missing ingredient
        var slotToIngredientMap = getGuiSlotToIngredientMap(recipe, menuType.getSize());
        var missingSlots = menu.findMissingIngredients(slotToIngredientMap);

        if (missingSlots.missingSlots().size() == slotToIngredientMap.size()) {
            // All missing, can't do much...
            return Result.createFailed(ItemModText.NO_ITEMS.text(), missingSlots.missingSlots());
        }

        if (!doTransfer) {
            if (missingSlots.anyMissingOrCraftable()) {
                // Highlight the slots with missing ingredients
                return new Result.PartiallyCraftable(missingSlots);
            }
        } else {
            // Thank you RS for pioneering this amazing feature! :)
            boolean craftMissing = AbstractContainerScreen.hasControlDown();
            ItemListTermCraftingHelper.performTransfer(menu, recipeId, recipe, craftMissing);
        }

        // No error
        return Result.createSuccessful();
    }

    private Recipe<?> createFakeRecipe(EmiRecipe display) {
        var ingredients = NonNullList.withSize(menuType.getGridSize(), Ingredient.EMPTY);

        for (int i = 0; i < Math.min(display.getInputs().size(), ingredients.size()); i++) {
            var ingredient = Ingredient.of(display.getInputs().get(i).getEmiStacks().stream()
                    .map(EmiStack::getItemStack)
                    .filter(is -> !is.isEmpty()));
            ingredients.set(i, ingredient);
        }

        var pattern = new ShapedRecipePattern(menuType.getGridSize(), menuType.getGridSize(), ingredients, Optional.empty());
        return new ShapedTableRecipe(pattern,ItemStack.EMPTY, menuType.getTier());
    }



}
