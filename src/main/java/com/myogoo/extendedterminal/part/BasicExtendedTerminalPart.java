package com.myogoo.extendedterminal.part;

import appeng.api.inventories.InternalInventory;
import appeng.api.networking.GridFlags;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractTerminalPart;
import appeng.util.inv.AppEngInternalInventory;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class BasicExtendedTerminalPart extends AbstractTerminalPart {

    public static final ResourceLocation BASIC_CRAFTING_INV = ExtendedTerminal.makeId("basic_crafting_inventory");

    @PartModels
    private static final ResourceLocation MODEL_ON = AppEng.makeId("part/crafting_terminal_off");
    @PartModels
    protected static final ResourceLocation MODEL_BASE = AppEng.makeId("part/display_base");
    @PartModels
    protected static final ResourceLocation MODEL_STATUS_ON = AppEng.makeId("part/display_status_on");

    public static final IPartModel MODEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);

    private final AppEngInternalInventory craftingGrid = new AppEngInternalInventory(this, 3*3);

    public BasicExtendedTerminalPart(IPartItem<?> partItem) {
        super(partItem);
        getMainNode()
                .setIdlePowerUsage(1)
                .setFlags(GridFlags.REQUIRE_CHANNEL);
    }

    @Override
    public IPartModel getStaticModels() {
        return MODEL;
    }

    @Override
    public MenuType<?> getMenuType(Player p) {
        return BasicTerminalMenu.TYPE;
    }

    @Override
    public InternalInventory getSubInventory(ResourceLocation id) {
        if (id.equals(BASIC_CRAFTING_INV)) {
            return craftingGrid;
        } else {
            return super.getSubInventory(id);
        }
    }

}
