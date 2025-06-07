package com.myogoo.extendedterminal.menu.extendedcrafting;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.ITerminalHost;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.ICraftingGridMenu;
import appeng.helpers.InventoryAction;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.SlotSemantic;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.PlayerInternalInventory;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.google.common.base.Preconditions;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.ETSlotSemantics;
import com.myogoo.extendedterminal.menu.slot.ETArmorSlot;
import com.myogoo.extendedterminal.menu.slot.ETBaseCraftingSlot;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;


public class ExtendedTerminalBaseMenu extends ETBaseTerminalMenu<ITableRecipe> {
    private final ETBaseCraftingSlot outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    private final CraftingMatrixSlot[] craftingSlots;
    private final ETArmorSlot[] armorSlots = new ETArmorSlot[4];
    private final ETMenuType etMenuType;
    @Nullable
    private TableCraftingInput lastTestedInput;


    public ExtendedTerminalBaseMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType) {
        super(menuType, id, ip, host);
        this.etMenuType = etMenuType;

        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[etMenuType.getGridSize()];
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(etMenuType.getCraftingInventory());
        for(int i = 0; i < etMenuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this,craftingGridInv,i), etMenuType.getSlotSemanticGrid());
        }

        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new ETBaseCraftingSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.energySource, linkStatusInventory, craftingGridInv, craftingGridInv, this,etMenuType),
                etMenuType.getSlotSemanticResult());

        updateCurrentRecipeAndOutput(true);
    }

    @Override
    public void onSlotChange(Slot slot) {
        if(slot instanceof ETArmorSlot armorSlot) {
            this.getPlayerInventory().armor.set(armorSlot.getSlotIndex(), armorSlot.getItem().copy());
        }
        super.onSlotChange(slot);
    }

    @Override
    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        PacketDistributor.sendToServer(p);
    }

    @Override
    protected void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for(var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }
        var testInput = TableCraftingInput.of(etMenuType.getSize(),etMenuType.getSize(),testItems,etMenuType.getTier());

        if (testInput.equals(this.lastTestedInput) && !forceUpdate) {
            return;
        }

        var level = getPlayer().level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), testInput, level)
                .orElse(null);
        if(this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.value().assemble(testInput,level.registryAccess()));
        }
    }
    public RecipeHolder<ITableRecipe> getCurrentRecipe() {
        return this.currentRecipe;
    }

    public ETMenuType getETMenuType() {
        return etMenuType;
    }

    @Override
    public SlotSemantic getCraftingGridSlotSemantic() {
        return this.etMenuType.getSlotSemanticGrid();
    }

    @Override
    public SlotSemantic getOutputSlotSemantic() {
        return this.etMenuType.getSlotSemanticResult();
    }

    @Override
    public int getCraftingMatrixSize() {
        return this.etMenuType.getGridSize();
    }

    @Override
    public int getCraftingMatrixWidth() {
        return this.etMenuType.getSize();
    }

    @Override
    public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(etMenuType.getCraftingInventory());
    }

    @Override
    public void doAction(ServerPlayer player, InventoryAction action, int slot, long id) {
        var s = this.getSlot(slot);
        if(s instanceof ETBaseCraftingSlot craftingSlot) {
            switch (action) {
                case CRAFT_SHIFT:
                case CRAFT_ALL:
                case CRAFT_ITEM:
                case CRAFT_STACK:
                    craftingSlot.doClick(action, player);
            }
            return;
        }
        super.doAction(player, action, slot, id);
    }
}
