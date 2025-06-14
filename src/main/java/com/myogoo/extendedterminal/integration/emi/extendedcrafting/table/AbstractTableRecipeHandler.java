package com.myogoo.extendedterminal.integration.emi.extendedcrafting.table;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.core.AEConfig;
import appeng.integration.modules.emi.EmiStackHelper;
import appeng.integration.modules.itemlists.EncodingHelper;
import appeng.integration.modules.itemlists.TransferHelper;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.CraftingTermMenu;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import com.myogoo.extendedterminal.menu.ETSlotSemantics;
import com.myogoo.extendedterminal.menu.extendedcrafting.ExtendedTerminalBaseMenu;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static appeng.integration.modules.itemlists.TransferHelper.BLUE_SLOT_HIGHLIGHT_COLOR;
import static appeng.integration.modules.itemlists.TransferHelper.RED_SLOT_HIGHLIGHT_COLOR;

abstract class AbstractTableRecipeHandler<T extends ExtendedTerminalBaseMenu> implements StandardRecipeHandler<T> {
    private final Class<T> containerClass;

    AbstractTableRecipeHandler(Class<T> containerClass) {
        this.containerClass = containerClass;
    }


    @Override
    public List<Slot> getInputSources(T menu) {
        var slots = new ArrayList<Slot>();
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_HOTBAR));
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_INVENTORY));
        slots.addAll(menu.getSlots(ETSlotSemantics.PLAYER_ARMOR));
        slots.addAll(menu.getSlots(menu.getCraftingGridSlotSemantic()));
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(T menu) {
        return menu.getSlots(menu.getCraftingGridSlotSemantic());
    }

    @Override
    public @Nullable Slot getOutputSlot(T menu) {
        for(var slot : menu.getSlots(menu.getOutputSlotSemantic())) {
            return slot;
        }
        return null;
    }

    @Override
    public EmiPlayerInventory getInventory(AbstractContainerScreen<T> screen) {
        if (!AEConfig.instance().isExposeNetworkInventoryToEmi()) {
            return StandardRecipeHandler.super.getInventory(screen);
        }

        var list = new ArrayList<EmiStack>();

        for (Slot slot : getInputSources(screen.getMenu())) {
            list.add(EmiStack.of(slot.getItem()));
        }

        if (screen.getMenu() instanceof MEStorageMenu menu) {
            var repo = menu.getClientRepo();

            if (repo != null) {
                for (var entry : repo.getAllEntries()) {
                    if (entry.getStoredAmount() <= 0) {
                        continue; // Skip items that are only craftable
                    }
                    var emiStack = EmiStackHelper
                            .toEmiStack(new GenericStack(entry.getWhat(), entry.getStoredAmount()));
                    if (emiStack != null) {
                        list.add(emiStack);
                    }
                }
            }
        }

        return new EmiPlayerInventory(list);
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<T> context) {
        if (context.getType() == EmiCraftContext.Type.FILL_BUTTON) {
            return transferRecipe(recipe, context, false).canCraft();
        }
        return StandardRecipeHandler.super.canCraft(recipe, context);
    }

    protected abstract Result transferRecipe(T menu,
                                                                                                  @Nullable RecipeHolder<?> holder,
                                                                                                  EmiRecipe emiRecipe,
                                                                                                  boolean doTransfer);

    protected final Result transferRecipe(EmiRecipe emiRecipe, EmiCraftContext<T> context, boolean doTransfer) {
        if (!containerClass.isInstance(context.getScreenHandler())) {
            return Result.createNotApplicable();
        }

        T menu = containerClass.cast(context.getScreenHandler());
        var holder = getRecipeHolder(context.getScreenHandler().getPlayer().level(), emiRecipe);

        var result = transferRecipe(menu, holder, emiRecipe, doTransfer);
        if (result instanceof Result.Success && doTransfer) {
            Minecraft.getInstance().setScreen(context.getScreen());
        }
        return result;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return true;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
        return transferRecipe(recipe, context, true).canCraft();
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<T> context) {
        var tooltip = transferRecipe(recipe, context, false).getTooltip(recipe, context);
        if (tooltip != null) {
            return tooltip.stream()
                    .map(Component::getVisualOrderText)
                    .map(ClientTooltipComponent::create)
                    .toList();
        } else {
            return StandardRecipeHandler.super.getTooltip(recipe, context);
        }
    }

    @Override
    public void render(EmiRecipe recipe, EmiCraftContext<T> context, List<Widget> widgets, GuiGraphics draw) {
        transferRecipe(recipe, context, false).render(recipe, context, widgets, draw);
    }

    @Nullable
    private RecipeHolder<?> getRecipeHolder(Level level, EmiRecipe recipe) {
        if (recipe.getBackingRecipe() != null) {
            return recipe.getBackingRecipe();
        }
        if (recipe.getId() != null) {
            // TODO: This can produce false positives...
            return level.getRecipeManager().byKey(recipe.getId()).orElse(null);
        }
        return null;
    }

    protected final boolean isCraftingRecipe(Recipe<?> recipe, EmiRecipe emiRecipe) {
        return emiRecipe.getCategory().equals(ExtendedCraftingTableRecipe.getCategoryFromClass(containerClass));
    }

    protected final boolean fitsInNxNGrid(Recipe<?> recipe, EmiRecipe emiRecipe,int gridSize) {
        if (recipe != null) {
            return recipe.canCraftInDimensions(gridSize, gridSize);
        } else {
            return true;
        }
    }

    protected static sealed abstract class Result {
        /**
         * @return null doesn't override the default tooltip.
         */
        @Nullable
        List<Component> getTooltip(EmiRecipe recipe, EmiCraftContext<?> context) {
            return null;
        }

        abstract boolean canCraft();

        void render(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                    GuiGraphics draw) {
        }

        static final class Success extends Result {
            @Override
            boolean canCraft() {
                return true;
            }
        }

        /**
         * There are missing ingredients, but at least one is present.
         */
        static final class PartiallyCraftable extends Result {
            private final CraftingTermMenu.MissingIngredientSlots missingSlots;

            public PartiallyCraftable(CraftingTermMenu.MissingIngredientSlots missingSlots) {
                this.missingSlots = missingSlots;
            }

            @Override
            boolean canCraft() {
                return true;
            }

            @Override
            List<Component> getTooltip(EmiRecipe recipe, EmiCraftContext<?> context) {
                // EMI caches this tooltip, we cannot dynamically react to control being held here
                return TransferHelper.createCraftingTooltip(missingSlots, false, false);
            }

            @Override
            void render(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                        GuiGraphics guiGraphics) {
                renderMissingAndCraftableSlotOverlays(getRecipeInputSlots(recipe, widgets), guiGraphics,
                        missingSlots.missingSlots(),
                        missingSlots.craftableSlots());
            }
        }

        /**
         * Indicates that some of the slots can already be crafted by the auto-crafting system.
         */
        static final class EncodeWithCraftables extends Result {
            private final Set<AEKey> craftableKeys;

            /**
             * @param craftableKeys All keys that the current system can auto-craft.
             */
            public EncodeWithCraftables(Set<AEKey> craftableKeys) {
                this.craftableKeys = craftableKeys;
            }

            @Override
            boolean canCraft() {
                return true;
            }

            @Override
            List<Component> getTooltip(EmiRecipe emiRecipe, EmiCraftContext<?> context) {
                var anyCraftable = emiRecipe.getInputs().stream()
                        .anyMatch(ing -> isCraftable(craftableKeys, ing));
                if (anyCraftable) {
                    return TransferHelper.createEncodingTooltip(true, false);
                }
                return null;
            }

            @Override
            void render(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                        GuiGraphics guiGraphics) {
                for (var widget : widgets) {
                    if (widget instanceof SlotWidget slot && isInputSlot(slot)) {
                        if (isCraftable(craftableKeys, slot.getStack())) {
                            var poseStack = guiGraphics.pose();
                            poseStack.pushPose();
                            poseStack.translate(0, 0, 400);
                            var bounds = getInnerBounds(slot);
                            guiGraphics.fill(bounds.x(), bounds.y(), bounds.right(), bounds.bottom(),
                                    BLUE_SLOT_HIGHLIGHT_COLOR);
                            poseStack.popPose();
                        }
                    }
                }
            }

            private static boolean isCraftable(Set<AEKey> craftableKeys, EmiIngredient ingredient) {
                return ingredient.getEmiStacks().stream().anyMatch(emiIngredient -> {
                    var stack = EmiStackHelper.toGenericStack(emiIngredient);
                    return stack != null && craftableKeys.contains(stack.what());
                });
            }
        }

        static final class NotApplicable extends Result {
            @Override
            boolean canCraft() {
                return false;
            }
        }

        static final class Error extends Result {
            private final Component message;
            private final Set<Integer> missingSlots;

            public Error(Component message, Set<Integer> missingSlots) {
                this.message = message;
                this.missingSlots = missingSlots;
            }

            public Component getMessage() {
                return message;
            }

            @Override
            boolean canCraft() {
                return false;
            }

            @Override
            void render(EmiRecipe recipe, EmiCraftContext<? extends AEBaseMenu> context, List<Widget> widgets,
                        GuiGraphics guiGraphics) {

                renderMissingAndCraftableSlotOverlays(getRecipeInputSlots(recipe, widgets), guiGraphics, missingSlots,
                        Set.of());
            }
        }

        static Result.NotApplicable createNotApplicable() {
            return new Result.NotApplicable();
        }

        static Result.Success createSuccessful() {
            return new Result.Success();
        }

        static Result.Error createFailed(Component text) {
            return new Result.Error(text, Set.of());
        }

        static Result.Error createFailed(Component text, Set<Integer> missingSlots) {
            return new Result.Error(text, missingSlots);
        }
    }

    private static void renderMissingAndCraftableSlotOverlays(Map<Integer, SlotWidget> inputSlots,
                                                              GuiGraphics guiGraphics,
                                                              Set<Integer> missingSlots, Set<Integer> craftableSlots) {
        for (var entry : inputSlots.entrySet()) {
            boolean missing = missingSlots.contains(entry.getKey());
            boolean craftable = craftableSlots.contains(entry.getKey());
            if (missing || craftable) {
                var poseStack = guiGraphics.pose();
                poseStack.pushPose();
                poseStack.translate(0, 0, 400);
                var innerBounds = getInnerBounds(entry.getValue());
                guiGraphics.fill(innerBounds.x(), innerBounds.y(), innerBounds.right(),
                        innerBounds.bottom(), missing ? RED_SLOT_HIGHLIGHT_COLOR : BLUE_SLOT_HIGHLIGHT_COLOR);
                poseStack.popPose();
            }
        }
    }

    private static boolean isInputSlot(SlotWidget slot) {
        return slot.getRecipe() == null;
    }

    private static Bounds getInnerBounds(SlotWidget slot) {
        var bounds = slot.getBounds();
        return new Bounds(
                bounds.x() + 1,
                bounds.y() + 1,
                bounds.width() - 2,
                bounds.height() - 2);
    }

    private static Map<Integer, SlotWidget> getRecipeInputSlots(EmiRecipe recipe, List<Widget> widgets) {
        // Map ingredient indices to their respective slots
        var inputSlots = new HashMap<Integer, SlotWidget>(recipe.getInputs().size());
        for (int i = 0; i < recipe.getInputs().size(); i++) {
            for (var widget : widgets) {
                if (widget instanceof SlotWidget slot && isInputSlot(slot)) {
                    if (slot.getStack() == recipe.getInputs().get(i)) {
                        inputSlots.put(i, slot);
                    }
                }
            }
        }
        return inputSlots;
    }
}
