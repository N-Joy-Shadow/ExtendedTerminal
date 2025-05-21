package com.myogoo.extendedterminal.menu;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nullable;
import java.util.Objects;

public enum ETMenuType {
    BASIC_TERMINAL(3, ModRecipeTypes.TABLE.get(),1),

    ADVANCED_TERMINAL(5, ModRecipeTypes.TABLE.get(),2),

    ELITE_TERMINAL(7, ModRecipeTypes.TABLE.get(),3),

    ULTIMATE_TERMINAL(9, ModRecipeTypes.TABLE.get(),4),

    AVARITIA_TERMINAL(9, ModRecipeTypes.TABLE.get(), -1),;


    private final String slotSemanticId_GRID;
    private final String slotSemanticId_RESULT;
    private final int size;
    private final RecipeType<? extends CraftingInput> recipeType;
    private final int tier;
    ETMenuType(int size, RecipeType<? extends CraftingInput> recipeType, int tier) {
        this.slotSemanticId_GRID = this.name() + " _GRID";
        this.slotSemanticId_RESULT = this.name() + " _RESULT";
        this.size = size;
        this.recipeType = recipeType;
        this.tier = tier;

        SlotSemantics.register(this.slotSemanticId_GRID,false);
        SlotSemantics.register(this.slotSemanticId_RESULT,false);
    }

    public int getGridSize() {
        return size * size;
    }

    public int getSize() {
        return size;
    }

    public ResourceLocation getCraftingInventory() {
        return ExtendedTerminal.makeId(this.name().toLowerCase() + "_inventory");
    }

    public Tuple<SlotSemantic, SlotSemantic> getSlotSemantic() {
        if (SlotSemantics.get(this.slotSemanticId_GRID) == null ||
                SlotSemantics.get(this.slotSemanticId_RESULT) == null) {
            throw new IllegalStateException("Slot semantics not initialized for " + this.name());
        } else {
            return new Tuple<>(
                Objects.requireNonNull(SlotSemantics.get(this.slotSemanticId_GRID)),
                Objects.requireNonNull(SlotSemantics.get(this.slotSemanticId_RESULT)));
        }
    }

    public RecipeType<? extends CraftingInput> getRecipeType() {
        return recipeType;
    }

    public int getTier() {
        return tier;
    }

}
