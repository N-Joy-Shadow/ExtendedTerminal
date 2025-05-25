package com.myogoo.extendedterminal.integration.emi.extendedcrafting.table;

import appeng.core.definitions.AEParts;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.myogoo.extendedterminal.ExtendedTerminal;
import dev.emi.emi.api.recipe.BasicEmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ExtendedCraftingTableRecipe extends BasicEmiRecipe {
    public static final EmiRecipeCategory CATEGORY = new EmiRecipeCategory(
            ExtendedTerminal.makeId("basic_crafting_table"),
            EmiStack.of(AEParts.CRAFTING_TERMINAL));
    private final static int baseInX = 62;
    private final static int baseInY = 0;
    private final ITableRecipe recipe;
    private final ClientLevel level;

    public ExtendedCraftingTableRecipe(RecipeHolder<ITableRecipe> recipeHolder) {
        super(CATEGORY, recipeHolder.id(), calcInvX(recipeHolder.value().getTier()), calcInvY(recipeHolder.value().getTier()));
        this.recipe = recipeHolder.value();
        this.level = Minecraft.getInstance().level;
        for (var ingredient : this.recipe.getIngredients()) {
            this.inputs.add(EmiIngredient.of(ingredient));
        }

        this.outputs.add(EmiStack.of(this.recipe.getResultItem(level.registryAccess())));
    }


    @Override
    public void addWidgets(WidgetHolder widgets) {
        for (int i = 0; i < this.inputs.size(); i++) {
            var ingredient = this.inputs.get(i);
            widgets.addSlot(ingredient, 8 + (i % this.getTierColNRow()) * 18, 18 + (i / this.getTierColNRow()) * 18)
                    .drawBack(false);
        }
        widgets.addSlot(EmiStack.of(recipe.getResultItem(level.registryAccess())), 100, 36)
                .drawBack(false);

        widgets.addText(Component.literal(String.format("Require Tier %d Crafting Table", this.recipe.getTier())), 80, 24, 0x7E7E7E, false);
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

    private static int calcInvX(int tier) {
        return baseInX + 18 * (2 * tier + 1);
    }

    private static int calcInvY(int tier) {
        return baseInY + 18 * (tier + 1);
    }
}
