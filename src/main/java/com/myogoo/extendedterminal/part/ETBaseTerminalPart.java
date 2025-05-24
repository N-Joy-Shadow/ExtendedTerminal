package com.myogoo.extendedterminal.part;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import com.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ETBaseTerminalPart extends AbstractTerminalPart {

    @PartModels
    private static final ResourceLocation MODEL_ON = AppEng.makeId("part/crafting_terminal_off");
    @PartModels
    protected static final ResourceLocation MODEL_BASE = AppEng.makeId("part/display_base");
    @PartModels
    protected static final ResourceLocation MODEL_STATUS_ON = AppEng.makeId("part/display_status_on");

    public static final IPartModel MODEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);

    private AppEngInternalInventory craftingGrid;
    private final ETMenuType etMenuType;

    public ETBaseTerminalPart(IPartItem<?> partItem, ETMenuType etMenuType) {
        super(partItem);
        this.etMenuType = etMenuType;
        this.craftingGrid = new AppEngInternalInventory(this, etMenuType.getGridSize());
    }

    @Override
    public IPartModel getStaticModels() {
        return MODEL;
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if(id.equals(etMenuType.getCraftingInventory())){
            return craftingGrid;
        }
        return super.getSubInventory(id);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        this.craftingGrid.readFromNBT(data, "craftingGrid", registries);
    }

    @Override
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        this.craftingGrid.writeToNBT(data, "craftingGrid", registries);
    }

}
