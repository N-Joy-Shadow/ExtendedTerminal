package com.myogoo.extendedterminal.menu;

import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import com.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Objects;

public enum ETMenuType {
    BASIC_TERMINAL(3,1, ETSlotSemantics.BASIC_CRAFTING_GRID, ETSlotSemantics.BASIC_CRAFTING_RESULT),

    ADVANCED_TERMINAL(5,2, ETSlotSemantics.ADVANCED_CRAFTING_GRID, ETSlotSemantics.ADVANCED_CRAFTING_RESULT),

    ELITE_TERMINAL(7,3, ETSlotSemantics.ELITE_CRAFTING_GRID, ETSlotSemantics.ELITE_CRAFTING_RESULT),

    ULTIMATE_TERMINAL(9,4, ETSlotSemantics.ULTIMATE_CRAFTING_GRID, ETSlotSemantics.ULTIMATE_CRAFTING_RESULT),

    AVARITIA_TERMINAL(9, -1, ETSlotSemantics.AVARITIA_CRAFTING_GRID, ETSlotSemantics.AVARITIA_CRAFTING_RESULT),;

    private final SlotSemantic slotSemantic_GRID;
    private final SlotSemantic slotSemantic_RESULT;
    private final int size;
    private final int tier;
    ETMenuType(int size, int tier, SlotSemantic slotSemantic_GRID, SlotSemantic slotSemantic_RESULT) {
        this.slotSemantic_GRID = slotSemantic_GRID;
        this.slotSemantic_RESULT =  slotSemantic_RESULT;
        this.size = size;
        this.tier = tier;
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

    public SlotSemantic getSlotSemanticGrid() {
        return slotSemantic_GRID;
    }

    public SlotSemantic getSlotSemanticResult() {
        return slotSemantic_RESULT;
    }

    public int getTier() {
        return tier;
    }

    public ResourceLocation getId() {
        return ExtendedTerminal.makeId(this.getIdAsString());
    }

    public String getIdAsString() {
        return this.name().toLowerCase();
    }

    public String getEnglishName() {
        return this.name().toLowerCase().replace("_", "");
    }
}
