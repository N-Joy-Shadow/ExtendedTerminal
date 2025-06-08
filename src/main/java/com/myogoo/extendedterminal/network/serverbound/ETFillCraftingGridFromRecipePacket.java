package com.myogoo.extendedterminal.network.serverbound;

import appeng.api.config.FuzzyMode;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.core.network.ServerboundPacket;
import appeng.core.network.serverbound.FillCraftingGridFromRecipePacket;
import appeng.helpers.ICraftingGridMenu;
import appeng.items.storage.ViewCellItem;
import appeng.me.storage.NullInventory;
import appeng.util.prioritylist.IPartitionList;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.myogoo.extendedterminal.ExtendedTerminal;
import com.myogoo.extendedterminal.util.ETCraftingRecipeHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;


import java.util.*;

public record ETFillCraftingGridFromRecipePacket(
        @Nullable ResourceLocation recipeId,
        List<ItemStack> ingredientTemplates,
        boolean craftMissing
) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, ETFillCraftingGridFromRecipePacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    ETFillCraftingGridFromRecipePacket::write,
                    ETFillCraftingGridFromRecipePacket::decode);

    public static final Type<ETFillCraftingGridFromRecipePacket> TYPE = new CustomPacketPayload
            .Type<>(ExtendedTerminal.makeId("fill_crafting_grid_from_recipe"));

    @Override
    public CustomPacketPayload.Type<ETFillCraftingGridFromRecipePacket> type() {
        return TYPE;
    }

    public ETFillCraftingGridFromRecipePacket(
            @Nullable ResourceLocation recipeId,
            List<ItemStack> ingredientTemplates,
            boolean craftMissing
    ) {
        this.recipeId = recipeId;
        this.ingredientTemplates = NonNullList.copyOf(ingredientTemplates.stream().map(ItemStack::copy).toList());
        this.craftMissing = craftMissing;

    }

    public static ETFillCraftingGridFromRecipePacket decode(RegistryFriendlyByteBuf stream) {
        ResourceLocation recipeId = null;
        if (stream.readBoolean()) {
            recipeId = stream.readResourceLocation();
        }

        var ingredientTemplates = NonNullList.withSize(stream.readInt(), ItemStack.EMPTY);
        for (int i = 0; i < ingredientTemplates.size(); i++) {
            ingredientTemplates.set(i, ItemStack.OPTIONAL_STREAM_CODEC.decode(stream));
        }
        var craftMissing = stream.readBoolean();

        return new ETFillCraftingGridFromRecipePacket(recipeId, ingredientTemplates, craftMissing);
    }

    public void write(RegistryFriendlyByteBuf data) {
        if (recipeId != null) {
            data.writeBoolean(true);
            data.writeResourceLocation(recipeId);
        } else {
            data.writeBoolean(false);
        }
        data.writeInt(ingredientTemplates.size());
        for (var stack : ingredientTemplates) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(data, stack);
        }
        data.writeBoolean(craftMissing);
    }


    @Override
    public void handleOnServer(ServerPlayer player) {
        // Setup and verification
        var menu = player.containerMenu;
        if (!(menu instanceof ICraftingGridMenu cct)) {
            // Server might have closed the menu before the client-packet is processed. This is not an error.
            return;
        }

        var energy = cct.getEnergySource();
        @Nullable
        ICraftingService craftingService;
        @Nullable
        IStorageService storageService;
        MEStorage networkStorage;
        KeyCounter cachedStorage;

        @Nullable
        var node = cct.getGridNode();
        if (node != null && cct.getLinkStatus().connected()) {
            craftingService = node.getGrid().getCraftingService();
            storageService = node.getGrid().getStorageService();
            networkStorage = storageService.getInventory();
            cachedStorage = storageService.getCachedInventory();
        } else {
            craftingService = null;
            storageService = null;
            networkStorage = NullInventory.of();
            cachedStorage = new KeyCounter();
        }

        var craftMatrix = cct.getCraftingMatrix();

        // We'll try to use the best possible ingredients based on what's available in the network

        var filter = ViewCellItem.createItemFilter(cct.getViewCells());
        var ingredients = getDesiredIngredients(player);

        // Prepare to autocraft some stuff
        var toAutoCraft = new LinkedHashMap<AEItemKey, IntList>();
        boolean touchedGridStorage = false;

        // Handle each slot
        for (var x = 0; x < craftMatrix.size(); x++) {
            var currentItem = craftMatrix.getStackInSlot(x);
            var ingredient = ingredients.get(x);

            // Move out items blocking the grid
            if (!currentItem.isEmpty()) {
                // Put away old item, if not correct
                if (ingredient.test(currentItem)) {
                    // Grid already has an item that matches the ingredient
                    continue;
                } else {
                    var in = AEItemKey.of(currentItem);
                    var inserted = StorageHelper.poweredInsert(energy, networkStorage, in, currentItem.getCount(),
                            cct.getActionSource());
                    if (inserted > 0) {
                        touchedGridStorage = true;
                    }
                    if (inserted < currentItem.getCount()) {
                        currentItem = currentItem.copy();
                        currentItem.shrink((int) inserted);
                    } else {
                        currentItem = ItemStack.EMPTY;
                    }

                    // If more is remaining, try moving it to the player inventory
                    player.getInventory().add(currentItem);

                    craftMatrix.setItemDirect(x, currentItem.isEmpty() ? ItemStack.EMPTY : currentItem);
                }
            }

            if (ingredient.isEmpty()) {
                continue;
            }

            // Try to find the best item for this slot. Sort by the amount available in the last tick,
            // then try to extract from most to least available item until 1 can be extracted.
            if (currentItem.isEmpty()) {
                var request = findBestMatchingItemStack(ingredient, filter, cachedStorage);
                for (var what : request) {
                    var extracted = StorageHelper.poweredExtraction(energy, networkStorage, what, 1,
                            cct.getActionSource());
                    if (extracted > 0) {
                        touchedGridStorage = true;
                        currentItem = what.toStack(Ints.saturatedCast(extracted));
                        break;
                    }
                }
            }

            // If still nothing, try taking it from the player inventory
            if (currentItem.isEmpty()) {
                currentItem = takeIngredientFromPlayer(cct, player, ingredient);
            }
            craftMatrix.setItemDirect(x, currentItem);

            // If we couldn't find the item, schedule its autocrafting
            if (currentItem.isEmpty() && craftMissing && craftingService != null) {
                int slot = x;
                findCraftableKey(ingredient, craftingService).ifPresent(key -> {
                    toAutoCraft.computeIfAbsent(key, k -> new IntArrayList()).add(slot);
                });
            }
        }

        menu.slotsChanged(craftMatrix.toContainer());

        if (!toAutoCraft.isEmpty()) {
            // Invalidate the grid storage cache if we modified it. The crafting plan will use
            // the outdated cached inventory otherwise.
            if (touchedGridStorage) {
                storageService.invalidateCache();
            }

            // This must be the last call since it changes the menu!
            var stacks = toAutoCraft.entrySet().stream()
                    .map(e -> new ICraftingGridMenu.AutoCraftEntry(e.getKey(), e.getValue())).toList();
            cct.startAutoCrafting(stacks);
        }
    }

    private ItemStack takeIngredientFromPlayer(ICraftingGridMenu cct, ServerPlayer player, Ingredient ingredient) {
        var playerInv = player.getInventory();
        for (int i = 0; i < playerInv.items.size(); i++) {
            // Do not take ingredients out of locked slots
            if (cct.isPlayerInventorySlotLocked(i)) {
                continue;
            }

            var item = playerInv.getItem(i);
            if (ingredient.test(item)) {
                var result = item.split(1);
                if (!result.isEmpty()) {
                    return result;
                }
            }
        }
        return ItemStack.EMPTY;
    }
    private NonNullList<Ingredient> getDesiredIngredients(Player player) {
        // Try to retrieve the real recipe on the server-side
        if (this.recipeId != null) {
            var recipe = player.level().getRecipeManager().byKey(this.recipeId).orElse(null);
            if (recipe != null) {
                return ETCraftingRecipeHelper.ensureNxNCraftingMatrix(recipe.value());
            }
        }

        // If the recipe is unavailable for any reason, use the templates provided by the client
        var ingredients = NonNullList.withSize(this.ingredientTemplates.size(), Ingredient.EMPTY);
        Preconditions.checkArgument(ingredients.size() == this.ingredientTemplates.size(),
                "Got %d ingredient templates from client, expected %d",
                ingredientTemplates.size(), ingredients.size());
        for (int i = 0; i < ingredients.size(); i++) {
            var template = ingredientTemplates.get(i);
            if (!template.isEmpty()) {
                ingredients.set(i, Ingredient.of(template));
            }
        }

        return ingredients;
    }

    /**
     * //@see FillCraftingGridFromRecipePacket#findBestMatchingItemStack(Ingredient, IPartitionList, KeyCounter)
     */
    private List<AEItemKey> findBestMatchingItemStack(Ingredient ingredient, IPartitionList filter,
                                                      KeyCounter storage) {
        return Arrays.stream(ingredient.getItems())//
                .map(AEItemKey::of) //
                .filter(r -> r != null && (filter == null || filter.isListed(r)))
                .flatMap(s -> storage.findFuzzy(s, FuzzyMode.IGNORE_ALL).stream())
                // While FuzzyMode.IGNORE_ALL will retrieve all stacks of the same Item which matches
                // standard Vanilla Ingredient matching, there are NBT-matching Ingredient subclasses on Forge,
                // and Mods might actually have mixed into Ingredient
                .filter(e -> ((AEItemKey) e.getKey()).matches(ingredient))
                // Sort in descending order of availability
                .sorted((a, b) -> Long.compare(b.getLongValue(), a.getLongValue()))
                .map(e -> (AEItemKey) e.getKey())
                .toList();
    }

    private Optional<AEItemKey> findCraftableKey(Ingredient ingredient, ICraftingService craftingService) {
        return Arrays.stream(ingredient.getItems())
                .map(AEItemKey::of)
                .map(s -> (AEItemKey) craftingService.getFuzzyCraftable(s,
                        key -> ((AEItemKey) key).matches(ingredient)))
                .filter(Objects::nonNull)
                .findAny();
    }

}
