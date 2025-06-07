package com.myogoo.extendedterminal.integration.emi.extendedcrafting.table;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import com.blakebr0.extendedcrafting.init.ModBlocks;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.*;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class ExtendedCraftingTableRecipe extends BasicEmiRecipe {
    public static final EmiRecipeCategory BASIC_TABLE_CRAFTING_CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("basic_crafting_table"),
            EmiStack.of(ModBlocks.BASIC_TABLE.get().asItem().getDefaultInstance()));

    public static final EmiRecipeCategory ADVANCED_TABLE_CRAFTING_CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("advanced_crafting_table"),
            EmiStack.of(ModBlocks.ADVANCED_TABLE.get().asItem().getDefaultInstance()));

    public static final EmiRecipeCategory ELITE_TABLE_CRAFTING_CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("elite_crafting_table"),
            EmiStack.of(ModBlocks.ELITE_TABLE.get().asItem().getDefaultInstance()));

    public static final EmiRecipeCategory ULTIMATE_TABLE_CRAFTING_CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("ultimate_crafting_table"),
            EmiStack.of(ModBlocks.ULTIMATE_TABLE.get().asItem().getDefaultInstance()));

    private final static int baseInX = 62;
    private final ITableRecipe recipe;
    private final ClientLevel level;

    public ExtendedCraftingTableRecipe(EmiRecipeCategory category,RecipeHolder<ITableRecipe> recipeHolder) {
        super(category, recipeHolder.id(), calcInvX(recipeHolder.value().getTier()), calcInvY(recipeHolder.value().getTier()));
        this.recipe = recipeHolder.value();
        this.level = Minecraft.getInstance().level;
        for (var ingredient : this.recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }

        this.outputs.add(EmiStack.of(this.recipe.getResultItem(level.registryAccess())));
    }


    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < this.getGridSize(); i++) {
            EmiIngredient ingredient;
            if(i < this.inputs.size()) {
                ingredient = this.inputs.get(i);
            } else {
                ingredient = EmiIngredient.of(Ingredient.EMPTY);
            }

            int nx = (i % this.getTierColNRow()) * 18;
            int ny = (i / this.getTierColNRow()) * 18;
            widgets.addTexture(EmiTexture.SLOT,nx, ny);
            widgets.addSlot(ingredient, nx, ny)
                .drawBack(false);
        }



        int outputX = calcInvX(this.recipe.getTier());
        int outputY = ((int) (double) (this.getTierColNRow() / 2)) * 18;

        if(this.recipe instanceof ShapelessTableRecipe) {
            widgets.addTexture(EmiTexture.SHAPELESS, outputX - 25,outputY - 4 - 14);
        }

        widgets.addTexture(EmiTexture.EMPTY_ARROW, outputX - 58, outputY - 4);

        String tooltipText = String.format("Require Tier %d Crafting Table", this.recipe.getTier());
        var tooltip = ClientTooltipComponent.create(FormattedCharSequence.forward(tooltipText, Style.EMPTY));

        widgets.addTexture(EmiTexture.LARGE_SLOT, outputX - 30, outputY - 4)
                .tooltip(List.of(tooltip));
        widgets.addSlot(EmiStack.of(recipe.getResultItem(level.registryAccess())),outputX - 30, outputY - 4).large(true)
                .recipeContext(this)
                .drawBack(false);
    }

    private int getTierColNRow() {
        int tier = this.recipe.getTier();
        switch (tier) {
            case 1 -> {
                return 3;
            }
            case 2 -> {
                return 5;
            }
            case 3 -> {
                return 7;
            }
            case 4 -> {
                return 9;
            }
        }
        return -1;
    }

    private int getGridSize() {
        int tier = this.recipe.getTier();
        switch (tier) {
            case 1 -> {
                return 3 * 3;
            }
            case 2 -> {
                return 5 * 5;
            }
            case 3 -> {
                return 7 * 7;
            }
            case 4 -> {
                return 9 * 9;
            }
        }
        return -1;
    }

    public static EmiRecipeCategory getCategoryFromMenuType(ETMenuType menuType) {
        return switch (menuType) {
            case BASIC_TERMINAL -> BASIC_TABLE_CRAFTING_CATEGORY;
            case ADVANCED_TERMINAL -> ADVANCED_TABLE_CRAFTING_CATEGORY;
            case ELITE_TERMINAL -> ELITE_TABLE_CRAFTING_CATEGORY;
            case ULTIMATE_TERMINAL -> ULTIMATE_TABLE_CRAFTING_CATEGORY;
            default -> throw new IllegalArgumentException("Invalid tier: " + menuType.getEnglishName());
        };
    }

    public static EmiRecipeCategory getCategoryFromClass(Class<?> clazz) {
        if (BasicTerminalMenu.class.equals(clazz)) {
            return BASIC_TABLE_CRAFTING_CATEGORY;
        } else if (AdvancedTerminalMenu.class.equals(clazz)) {
            return ADVANCED_TABLE_CRAFTING_CATEGORY;
        } else if (EliteTerminalMenu.class.equals(clazz)) {
            return ELITE_TABLE_CRAFTING_CATEGORY;
        } else if (UltimateTerminalMenu.class.equals(clazz)) {
            return ULTIMATE_TABLE_CRAFTING_CATEGORY;
        } else {
            throw new IllegalArgumentException("Invalid class: " + clazz.getSimpleName());
        }

    }

    private static int calcInvX(int tier) {
        return baseInX + 18 * (2 * tier + 1);
    }

    private static int calcInvY(int tier) {
        return 18 * (2 * tier + 1);
    }

}
