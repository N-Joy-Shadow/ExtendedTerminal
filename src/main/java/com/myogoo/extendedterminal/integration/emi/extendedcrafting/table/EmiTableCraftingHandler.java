package com.myogoo.extendedterminal.integration.emi.extendedcrafting.table;

import com.blakebr0.cucumber.container.BaseContainerMenu;
import com.blakebr0.extendedcrafting.container.AdvancedTableContainer;
import com.blakebr0.extendedcrafting.container.BasicTableContainer;
import com.blakebr0.extendedcrafting.container.EliteTableContainer;
import com.blakebr0.extendedcrafting.container.UltimateTableContainer;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class EmiTableCraftingHandler<T extends BaseContainerMenu> implements StandardRecipeHandler<T> {

    public final static EmiTableCraftingHandler<BasicTableContainer> EmiBasicTableCraftingHandler = new EmiTableCraftingHandler<>(ExtendedCraftingTableRecipe.BASIC_TABLE_CRAFTING_CATEGORY, 9);
    public final static EmiTableCraftingHandler<AdvancedTableContainer> EmiAdvancedTableCraftingHandler = new EmiTableCraftingHandler<>(ExtendedCraftingTableRecipe.ADVANCED_TABLE_CRAFTING_CATEGORY, 25);
    public final static EmiTableCraftingHandler<EliteTableContainer> EmiEliteTableCraftingHandler = new EmiTableCraftingHandler<>(ExtendedCraftingTableRecipe.ELITE_TABLE_CRAFTING_CATEGORY, 49);
    public final static EmiTableCraftingHandler<UltimateTableContainer> EmiUltimateTableCraftingHandler = new EmiTableCraftingHandler<>(ExtendedCraftingTableRecipe.ULTIMATE_TABLE_CRAFTING_CATEGORY, 81);

    public final EmiRecipeCategory category;
    public final int gridSize;

    public EmiTableCraftingHandler(EmiRecipeCategory category, int gridSize) {
        this.category = category;
        this.gridSize = gridSize;
    }
    @Override
    public List<Slot> getInputSources(T menu) {
        return menu.slots;
    }

    @Override
    public List<Slot> getCraftingSlots(T menu) {
        ArrayList<Slot> slots = new ArrayList<Slot>();
        for(int i = 1; i <= this.gridSize; i ++) {
            slots.add(menu.getSlot(i));
        }
        return slots;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(this.category);
    }

}
