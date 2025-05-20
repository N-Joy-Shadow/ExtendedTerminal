package com.myogoo.extendedterminal.menu;

import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ITerminalHost;
import appeng.client.Point;
import appeng.client.gui.style.SlotPosition;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.ICraftingGridMenu;
import appeng.helpers.InventoryAction;
import appeng.me.storage.LinkStatusRespectingInventory;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.slot.CraftingMatrixSlot;
import appeng.menu.slot.CraftingTermSlot;
import appeng.parts.reporting.CraftingTerminalPart;
import com.blakebr0.extendedcrafting.ExtendedCrafting;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.google.common.base.Preconditions;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.init.ETMenus;
import com.myogoo.extendedterminal.menu.slot.BasicCraftingSlot;
import com.myogoo.extendedterminal.part.BasicExtendedTerminalPart;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class BasicTerminalMenu extends MEStorageMenu implements ICraftingGridMenu {
    public static final MenuType<BasicTerminalMenu> TYPE = MenuTypeBuilder
            .create(BasicTerminalMenu::new, ITerminalHost.class)
            .buildUnregistered(ExtendedTerminal.makeId("basic_terminal"));

    private static final String ACTION_CLEAR_TO_PLAYER = "clearToPlayer";

    private final ISegmentedInventory craftingInventoryHost;
    private final CraftingMatrixSlot[] craftingSlots = new CraftingMatrixSlot[9];
    @Nullable
    private CraftingInput lastTestedInput;
    private final CraftingTermSlot outputSlot;
    private RecipeHolder<ITableRecipe> currentRecipe;

    private final int size;

    public BasicTerminalMenu(int id, Inventory playerInventory, ITerminalHost host) {
        this(TYPE, id, playerInventory, host,true);
    }

    public BasicTerminalMenu(MenuType<?> menuType, int id, Inventory playerInventory, ITerminalHost host, boolean
                             bindInventory) {
        super(menuType, id, playerInventory, host, bindInventory);
        this.size = 3;

        this.craftingInventoryHost = (ISegmentedInventory) host;

        var craftingGridInv = this.craftingInventoryHost
                        .getSubInventory(BasicExtendedTerminalPart.BASIC_CRAFTING_INV);

        for (int i = 0; i < this.size * this.size; i++) {
            this.addSlot(this.craftingSlots[i] = new CraftingMatrixSlot(this, craftingGridInv, i),
                    ETSlotSemantics.BASIC_CRAFTING_GRID);
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

        var testInput = TableCraftingInput.of(this.size,this.size,testItems,1);

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

    @Override
    public InternalInventory getCraftingMatrix() {
        return this.craftingInventoryHost.getSubInventory(CraftingTerminalPart.INV_CRAFTING);
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

}
