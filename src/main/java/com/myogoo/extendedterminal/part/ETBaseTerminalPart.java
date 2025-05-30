package com.myogoo.extendedterminal.part;

import appeng.api.inventories.InternalInventory;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.ETMenuType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ETBaseTerminalPart extends AbstractTerminalPart {

    @PartModels
    private static final ResourceLocation MODEL_ON = ExtendedTerminal.makeId("part/extended_terminal_on");
    @PartModels
    private static final ResourceLocation MODEL_OFF = ExtendedTerminal.makeId("part/extended_terminal_off");

    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_ON);

    private final AppEngInternalInventory craftingGrid;
    private final ETMenuType etMenuType;

    public ETBaseTerminalPart(IPartItem<?> partItem, ETMenuType etMenuType) {
        super(partItem);
        this.etMenuType = etMenuType;
        this.craftingGrid = new AppEngInternalInventory(this, etMenuType.getGridSize());
    }

    @Override
    public void clearContent() {
        super.clearContent();
        craftingGrid.clear();
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF,MODELS_ON,MODELS_ON);
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

    @Override
    public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
         super.addAdditionalDrops(drops, wrenched);
        for(var is : this.craftingGrid) {
            if(!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

}
