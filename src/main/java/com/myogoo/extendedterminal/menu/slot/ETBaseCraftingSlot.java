package com.myogoo.extendedterminal.menu.slot;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.helpers.ICraftingGridMenu;
import appeng.helpers.InventoryAction;
import appeng.items.storage.ViewCellItem;
import appeng.menu.me.items.CraftingTermMenu;
import appeng.menu.slot.AppEngCraftingSlot;
import appeng.util.Platform;
import appeng.util.inv.CarriedItemInventory;
import appeng.util.inv.PlayerInternalInventory;
import appeng.util.prioritylist.IPartitionList;
import com.blakebr0.extendedcrafting.api.TableCraftingInput;
import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ETBaseCraftingSlot extends AppEngCraftingSlot {

    private final InternalInventory craftInv;
    private final InternalInventory pattern;

    private final IActionSource mySrc;
    private final IEnergySource energySrc;
    private final MEStorage storage;
    private final ICraftingGridMenu menu;

    private final ETMenuType menuType;

    public ETBaseCraftingSlot(Player player, IActionSource mySrc, IEnergySource energySrc,
                              MEStorage storage, InternalInventory cMatrix, InternalInventory secondMatrix,
                              ICraftingGridMenu ccp, ETMenuType menuType) {
        super(player, cMatrix);
        this.energySrc = energySrc;
        this.storage = storage;
        this.mySrc = mySrc;
        this.pattern = cMatrix;
        this.craftInv = secondMatrix;
        this.menu = ccp;
        this.menuType = menuType;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack is) { }

    public void doClick(InventoryAction action, Player who) {
        if (this.getItem().isEmpty()) {
            return;
        }
        if (isRemote()) {
            return;
        }

        final var howManyPerCraft = this.getItem().getCount();

        int maxTimesToCraft;
        InternalInventory target;
        if (action == InventoryAction.CRAFT_SHIFT || action == InventoryAction.CRAFT_ALL) // craft into player
        // inventory...
        {
            target = new PlayerInternalInventory(who.getInventory());
            if (action == InventoryAction.CRAFT_SHIFT) {
                maxTimesToCraft = (int) Math
                        .floor((double) this.getItem().getMaxStackSize() / (double) howManyPerCraft);
            } else {
                maxTimesToCraft = (int) Math.floor((double) this.getItem().getMaxStackSize() / (double) howManyPerCraft
                        * Inventory.INVENTORY_SIZE);
            }
        } else if (action == InventoryAction.CRAFT_STACK) // craft into hand, full stack
        {
            target = new CarriedItemInventory(getMenu());
            maxTimesToCraft = (int) Math.floor((double) this.getItem().getMaxStackSize() / (double) howManyPerCraft);
        } else
        // pick up what was crafted...
        {
            // This is a shortcut to ensure that for mods that create recipes with result counts larger than
            // the max stack size, it remains possible to pick up those items at least _once_.
            if (getMenu().getCarried().isEmpty()) {
                getMenu().setCarried(craftItem(who, storage, storage.getAvailableStacks()));
                return;
            }

            target = new CarriedItemInventory(getMenu());
            maxTimesToCraft = 1;
        }

        // Since we may be crafting multiple times, we have to ensure that we keep crafting the same item.
        // This may not be the case if not all crafting grid slots have the same number of items in them,
        // and some ingredients run-out after a few crafts.
        var itemAtStart = this.getItem().copy();
        if (itemAtStart.isEmpty()) {
            return;
        }

        for (var x = 0; x < maxTimesToCraft; x++) {
            // Stop if the recipe output has changed (i.e. due to fully consumed input slots)
            if (!ItemStack.isSameItemSameComponents(itemAtStart, getItem())) {
                return;
            }

            // Stop if the target inventory is full
            if (!target.simulateAdd(itemAtStart).isEmpty()) {
                return;
            }

            var all = storage.getAvailableStacks();
            var extra = target.addItems(craftItem(who, storage, all));

            // If we couldn't actually add what we crafted, we drop it and stop
            if (!extra.isEmpty()) {
                Platform.spawnDrops(who.level(), who.blockPosition(), List.of(extra));
                return;
            }
        }
    }

    protected RecipeHolder<ITableRecipe> findRecipe(TableCraftingInput ic, Level level) {
        if (this.menu instanceof ExtendedTerminalBaseMenu terminalMenu) {
            var recipe = terminalMenu.getCurrentRecipe();

            if (recipe != null && recipe.value().matches(ic, level)) {
                return terminalMenu.getCurrentRecipe();
            }
        }

        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), ic, level).orElse(null);
    }

        protected NonNullList<ItemStack> getRemainingItems(TableCraftingInput ic, Level level) {
            if (this.menu instanceof ExtendedTerminalBaseMenu terminalMenu) {
                var recipe = terminalMenu.getCurrentRecipe();

                if (recipe != null && recipe.value().matches(ic, level)) {
                    return terminalMenu.getCurrentRecipe().value().getRemainingItems(ic);
                }
            }

            return super.getRemainingItems(ic, level);
        }

    private ItemStack craftItem(Player p, MEStorage inv, KeyCounter all) {
        // update crafting matrix...
        var is = this.getItem().copy();
        if (is.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Make sure the item in the slot is still the same item as before
        final var set = new ItemStack[this.getPattern().size()];
        // Safeguard for empty slots in the inventory for now
        Arrays.fill(set, ItemStack.EMPTY);

        // add one of each item to the items on the board...
        var level = p.level();
        if (!level.isClientSide()) {
            final var ic = NonNullList.withSize(menuType.getGridSize(), ItemStack.EMPTY);
            for (var x = 0; x < menuType.getGridSize(); x++) {
                ic.set(x, this.getPattern().getStackInSlot(x));
            }
            var recipeInput = TableCraftingInput.of(menuType.getSize()
                    , menuType.getSize(), ic, menuType.getTier());

            final var r = this.findRecipe(recipeInput, level);
            setRecipeUsed(r);

            if (r == null) {
                return ItemStack.EMPTY;
            }

            is = r.value().assemble(recipeInput, level.registryAccess());

            if (inv != null) {
                var filter = ViewCellItem.createItemFilter(this.menu.getViewCells());
                for (var x = 0; x < this.getPattern().size(); x++) {
                    if (!this.getPattern().getStackInSlot(x).isEmpty()) {
                        set[x] = extractItemsByRecipe(this.energySrc, this.mySrc, inv, level, r.value(), is,
                                recipeInput.width(), recipeInput.height(),
                                ic,
                                this.getPattern().getStackInSlot(x), x, all,
                                filter,
                                menuType);
                        ic.set(x, set[x]);
                    }
                }
            }
        }

        if (this.preCraft(p, inv, set, is)) {
            this.makeItem(p, is);
            this.postCraft(p, inv, set, is);
        }

        p.containerMenu.slotsChanged(this.craftInv.toContainer());

        return is;
    }

    private static ItemStack extractItemsByRecipe(IEnergySource energySrc,
                                                  IActionSource mySrc,
                                                  MEStorage src,
                                                  Level level,
                                                  ITableRecipe r,
                                                  ItemStack output,
                                                  int gridWidth, int gridHeight,
                                                  List<ItemStack> craftingItems,
                                                  ItemStack providedTemplate,
                                                  int slot,
                                                  KeyCounter items,
                                                  IPartitionList filter,
                                                  ETMenuType menuType) {
        if (energySrc.extractAEPower(1, Actionable.SIMULATE, PowerMultiplier.CONFIG) > 0.9) {
            if (providedTemplate == null) {
                return ItemStack.EMPTY;
            }

            var ae_req = AEItemKey.of(providedTemplate);

            if (filter == null || filter.isListed(ae_req)) {
                var extracted = src.extract(ae_req, 1, Actionable.MODULATE, mySrc);
                if (extracted > 0) {
                    energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    return ae_req.toStack();
                }
            }

            var checkFuzzy = !providedTemplate.getComponents().isEmpty() || providedTemplate.isDamageableItem();

            if (items != null && checkFuzzy) {
                var craftingInputItems = new ArrayList<>(craftingItems);

                for (var x : items) {
                    if (x.getKey() instanceof AEItemKey itemKey) {
                        if (providedTemplate.getItem() == itemKey.getItem() && !itemKey.matches(output)) {

                            craftingInputItems.set(slot, itemKey.toStack());
                            var adjustedCraftingInput = TableCraftingInput.of(gridWidth, gridHeight, craftingInputItems,menuType.getTier());
                            if (r.matches(adjustedCraftingInput, level)
                                    && ItemStack.matches(r.assemble(adjustedCraftingInput, level.registryAccess()),
                                    output)) {
                                if (filter == null || filter.isListed(itemKey)) {
                                    var ex = src.extract(itemKey, 1, Actionable.MODULATE, mySrc);
                                    if (ex > 0) {
                                        energySrc.extractAEPower(1, Actionable.MODULATE, PowerMultiplier.CONFIG);
                                        return itemKey.toStack();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }


    //?
    private boolean preCraft(Player p, MEStorage inv, ItemStack[] set,
                             ItemStack result) {
        return true;
    }

    private void makeItem(Player player, ItemStack stack) {
        //this.amountCrafted += stack.getCount();
        this.checkTakeAchievements(stack);

        var items = NonNullList.withSize(this.craftInv.size(), ItemStack.EMPTY);
        for(int i = 0; i < this.craftInv.size(); i++) {
            items.set(i, this.craftInv.getStackInSlot(i));
        }
        TableCraftingInput positioned = TableCraftingInput.of(
                this.menuType.getSize(), this.menuType.getSize(), items, this.menuType.getTier());

        CommonHooks.setCraftingPlayer(player);
        var remainingItems = this.getRemainingItems(positioned,player.level());
        CommonHooks.setCraftingPlayer(null);

        for(int y = 0; y < menuType.getSize(); y++) {
            for(int x = 0; x < menuType.getSize(); x++) {
                var slotIdx = y * menuType.getSize() + x;
                var remainderIdx = (y - positioned.top()) * 3 + (x - positioned.left());

                // Consumes the item from the grid
                this.craftInv.extractItem(slotIdx, 1, false);

                if (remainderIdx >= 0 && remainderIdx < remainingItems.size()) {
                    var remainingInSlot = remainingItems.get(remainderIdx);
                    if (!remainingInSlot.isEmpty()) {
                        if (this.craftInv.getStackInSlot(slotIdx).isEmpty()) {
                            this.craftInv.setItemDirect(slotIdx, remainingInSlot);
                        } else if (!player.getInventory().add(remainingInSlot)) {
                            player.drop(remainingInSlot, false);
                        }
                    }
                }
            }
        }
    }

    private void postCraft(Player p, MEStorage inv, ItemStack[] set,
                           ItemStack result) {
        final List<ItemStack> drops = new ArrayList<>();

        // add one of each item to the items on the board...
        if (!p.getCommandSenderWorld().isClientSide()) {
            // set new items onto the crafting table...
            for (var x = 0; x < this.craftInv.size(); x++) {
                if (this.craftInv.getStackInSlot(x).isEmpty()) {
                    this.craftInv.setItemDirect(x, set[x]);
                } else if (!set[x].isEmpty()) {
                    var what = AEItemKey.of(set[x]);
                    var amount = set[x].getCount();
                    var inserted = inv.insert(what, amount, Actionable.MODULATE,
                            this.mySrc);
                    // eek! put it back!
                    if (inserted < amount) {
                        drops.add(what.toStack((int) (amount - inserted)));
                    }
                }
            }
        }

        if (!drops.isEmpty()) {
            Platform.spawnDrops(p.level(), new BlockPos((int) p.getX(), (int) p.getY(), (int) p.getZ()), drops);
        }
    }

    private InternalInventory getPattern() {
        return this.pattern;
    }


}

