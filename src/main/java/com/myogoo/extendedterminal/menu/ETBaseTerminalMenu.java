package com.myogoo.extendedterminal.menu;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.ICraftingGridMenu;
import appeng.helpers.InventoryAction;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.menu.slot.CraftingTermSlot;
import appeng.parts.reporting.CraftingTerminalPart;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.google.common.base.Preconditions;
import com.myogoo.extendedterminal.ExtendedTerminal;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ETBaseTerminalMenu<R extends Recipe<? extends CraftingInput>> extends MEStorageMenu implements ICraftingGridMenu {
    private RecipeHolder<R> currentRecipe;
    private final CraftingTermSlot outputSlot;
    private final ISegmentedInventory craftingInventoryHost;
    private final CraftingMatrixSlot[] craftingSlots;
    private final ETMenuType etMenuType;
    @Nullable
    private CraftingInput lastTestedInput;
    private static final String ACTION_CLEAR_TO_PLAYER = "clearToPlayer";


    public ETBaseTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, ETMenuType etMenuType) {
        super(menuType, id, ip, host);
        this.etMenuType = etMenuType;

        this.craftingInventoryHost = (ISegmentedInventory) host;
        this.craftingSlots = new CraftingMatrixSlot[etMenuType.getGridSize()];
        var craftingGridInv = this.craftingInventoryHost
                .getSubInventory(etMenuType.getCraftingInventory());
        for(int i = 0; i < etMenuType.getGridSize(); i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this,craftingGridInv,i), etMenuType.getSlotSemantic().getB());
        }


        var linkStatusInventory = new LinkStatusRespectingInventory(host.getInventory(), this::getLinkStatus);
        this.addSlot(this.outputSlot = new CraftingTermSlot(this.getPlayerInventory().player, this.getActionSource(),
                        this.energySource, linkStatusInventory, craftingGridInv, craftingGridInv, this),
                ETSlotSemantics.BASIC_CRAFTING_RESULT);

        updateCurrentRecipeAndOutput(true);

        registerClientAction(ACTION_CLEAR_TO_PLAYER, this::clearCraftingGrid);
    }

    public void clearCraftingGrid() {
        Preconditions.checkState(isClientSide());
        CraftingMatrixSlot slot = craftingSlots[0];
        var p = new InventoryActionPacket(InventoryAction.MOVE_REGION, slot.index, 0);
        PacketDistributor.sendToServer(p);
    }

    private void updateCurrentRecipeAndOutput(boolean forceUpdate) {
        var testItems = new ArrayList<ItemStack>(this.craftingSlots.length);
        for(var craftingSlot : craftingSlots) {
            testItems.add(craftingSlot.getItem().copy());
        }

        var testInput = TableCraftingInput.of(etMenuType.getSize(),etMenuType.getSize(),testItems,etMenuType.getTier());

        if (testInput.equals(this.lastTestedInput) && !forceUpdate) {
            return;
        }

        var level = getPlayer().level();
        this.currentRecipe = level.getRecipeManager().getRecipeFor((RecipeType<ITableRecipe>)etMenuType.getRecipeType(), testInput, level)
                .orElse(null);
        if(this.currentRecipe == null) {
            this.outputSlot.set(ItemStack.EMPTY);
        } else {
            this.outputSlot.set(this.currentRecipe.value().assemble(testInput,level.registryAccess()));
        }
    }
    public RecipeHolder<R> getCurrentRecipe() {
        return this.currentRecipe;
    }


    @Override
    public boolean hasIngredient(Ingredient ingredient, Object2IntOpenHashMap<Object> reservedAmounts) {
        for (var slot : getSlots(ETSlotSemantics.BASIC_CRAFTING_GRID)) {
            var stackInSlot = slot.getItem();
            if (!stackInSlot.isEmpty() && ingredient.test(stackInSlot)) {
                var reservedAmount = reservedAmounts.getOrDefault(slot, 0);
                if (stackInSlot.getCount() > reservedAmount) {
                    reservedAmounts.merge(slot, 1, Integer::sum);
                    return true;
                }
            }

        }

        return super.hasIngredient(ingredient, reservedAmounts);
    }


    @Override
    public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(etMenuType.getCraftingInventory());
    }
}
