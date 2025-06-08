package com.myogoo.extendedterminal.menu;

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
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.crafting.CraftConfirmMenu;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.PlayerInternalInventory;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.google.common.base.Preconditions;
import com.myogoo.extendedterminal.menu.slot.ETArmorSlot;
import com.myogoo.extendedterminal.menu.slot.ETBaseCraftingSlot;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public abstract class ETBaseTerminalMenu<R extends Recipe<?>> extends MEStorageMenu implements ICraftingGridMenu {
    protected RecipeHolder<R> currentRecipe;
    protected final ETArmorSlot[] armorSlots = new ETArmorSlot[4];
    private static final String ACTION_CLEAR_TO_PLAYER = "clearToPlayer";

    public ETBaseTerminalMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
        super(menuType, id, ip, host);

        registerClientAction(ACTION_CLEAR_TO_PLAYER, this::clearToPlayerInventory);
        var armorInventory = new AppEngInternalInventory(4);

        for(int i = 0; i < Inventory.ALL_ARMOR_SLOTS.length; i++) {
            this.addSlot(this.armorSlots[i] = new ETArmorSlot(armorInventory,Inventory.ALL_ARMOR_SLOTS[i]), ETSlotSemantics.PLAYER_ARMOR);
            this.armorSlots[i].set(this.getPlayerInventory().getArmor(i));
        }
    }

    @Override
    public void onSlotChange(Slot slot) {
        if(slot instanceof ETArmorSlot armorSlot) {
            this.getPlayerInventory().armor.set(armorSlot.getSlotIndex(), armorSlot.getItem().copy());
        }
        super.onSlotChange(slot);
    }

    public RecipeHolder<R> getCurrentRecipe() {
        return this.currentRecipe;
    }

    //Abstract Methods
    public abstract void clearCraftingGrid();

    public abstract SlotSemantic getCraftingGridSlotSemantic();

    public abstract SlotSemantic getOutputSlotSemantic();

    public abstract int getCraftingMatrixSize();

    public abstract int getCraftingMatrixWidth();

    protected abstract void updateCurrentRecipeAndOutput(boolean forceUpdate);

    public int getCraftingMatrixHeight() {
        return this.getCraftingMatrixWidth();
    }

    //Override Methods

    @Override
    public boolean hasIngredient(Ingredient ingredient, Object2IntOpenHashMap<Object> reservedAmounts) {
        for (var slot : getSlots(this.getCraftingGridSlotSemantic())) {
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
    public void startAutoCrafting(List<AutoCraftEntry> toCraft) {
        CraftConfirmMenu.openWithCraftingList(getActionHost(), (ServerPlayer) getPlayer(), getLocator(), toCraft);
    }

    @Override
    public IEnergySource getEnergySource() {
        return this.energySource;
    }

    @Override
    public void slotsChanged(@NotNull Container inventory) {
        updateCurrentRecipeAndOutput(false);
    }

    protected boolean isCraftable(ItemStack itemStack) {
        var clientRepo = getClientRepo();

        if (clientRepo != null) {
            for (var stack : clientRepo.getAllEntries()) {
                if (AEItemKey.matches(stack.getWhat(), itemStack) && stack.isCraftable()) {
                    return true;
                }
            }
        }

        return false;
    }

    public CraftingTermMenu.MissingIngredientSlots findMissingIngredients(Map<Integer, Ingredient> ingredients) {

        // Try to figure out if any slots have missing ingredients
        // Find every "slot" (in JEI parlance) that has no equivalent item in the item repo or player inventory
        Set<Integer> missingSlots = new HashSet<>(); // missing but not craftable
        Set<Integer> craftableSlots = new HashSet<>(); // missing but craftable

        // We need to track how many of a given item stack we've already used for other slots in the recipe.
        // Otherwise recipes that need 4x<item> will not correctly show missing items if at least 1 of <item> is in
        // the grid.
        var reservedGridAmounts = new Object2IntOpenHashMap<>();
        var playerItems = getPlayerInventory().items;
        var reservedPlayerItems = new int[playerItems.size()];

        for (var entry : ingredients.entrySet()) {
            var ingredient = entry.getValue();

            boolean found = false;
            // Player inventory is cheaper to check
            for (int i = 0; i < playerItems.size(); i++) {
                // Do not consider locked slots
                if (isPlayerInventorySlotLocked(i)) {
                    continue;
                }

                var stack = playerItems.get(i);
                if (stack.getCount() - reservedPlayerItems[i] > 0 && ingredient.test(stack)) {
                    reservedPlayerItems[i]++;
                    found = true;
                    break;
                }
            }

            // Then check the terminal screen's repository of network items
            if (!found) {
                // We use AE stacks to get an easily comparable item type key that ignores stack size
                if (hasIngredient(ingredient, reservedGridAmounts)) {
                    reservedGridAmounts.merge(ingredient, 1, Integer::sum);
                    found = true;
                }
            }

            // Check the terminal once again, but this time for craftable items
            if (!found) {
                for (var stack : ingredient.getItems()) {
                    if (isCraftable(stack)) {
                        craftableSlots.add(entry.getKey());
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                missingSlots.add(entry.getKey());
            }
        }

        return new CraftingTermMenu.MissingIngredientSlots(missingSlots, craftableSlots);
    }


    public void clearToPlayerInventory() {
        if (isClientSide()) {
            sendClientAction(ACTION_CLEAR_TO_PLAYER);
            return;
        }

        var craftingGridInv = this.getCraftingMatrix();
        var playerInv = new PlayerInternalInventory(getPlayerInventory());

        for (int i = 0; i < craftingGridInv.size(); ++i) {
            for (int emptyLoop = 0; emptyLoop < 2; ++emptyLoop) {
                boolean allowEmpty = emptyLoop == 1;

                // Hotbar first
                final int HOTBAR_SIZE = 9;
                for (int j = HOTBAR_SIZE; j-- > 0; ) {
                    if (playerInv.getStackInSlot(j).isEmpty() == allowEmpty) {
                        craftingGridInv.setItemDirect(i,
                                playerInv.getSlotInv(j).addItems(craftingGridInv.getStackInSlot(i)));
                    }
                }
                // Rest of inventory
                for (int j = HOTBAR_SIZE; j < Inventory.INVENTORY_SIZE; ++j) {
                    if (playerInv.getStackInSlot(j).isEmpty() == allowEmpty) {
                        craftingGridInv.setItemDirect(i,
                                playerInv.getSlotInv(j).addItems(craftingGridInv.getStackInSlot(i)));
                    }
                }
            }
        }
    }
}
