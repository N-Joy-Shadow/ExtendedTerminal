package com.myogoo.extendedterminal.integration.emi.extendedcrafting.table;

import appeng.api.stacks.GenericStack;
import appeng.core.AEConfig;
import appeng.integration.modules.emi.EmiStackHelper;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.MEStorageMenu;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import com.myogoo.extendedterminal.menu.ETMenuType;
import com.myogoo.extendedterminal.menu.extendedcrafting.AdvancedTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.EliteTerminalMenu;
import com.myogoo.extendedterminal.menu.extendedcrafting.UltimateTerminalMenu;
import dev.emi.emi.api.recipe.EmiPlayerInventory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmiTerminalCraftingHandler<T extends ETBaseTerminalMenu> implements StandardRecipeHandler<T> {
    public final static EmiTerminalCraftingHandler<BasicTerminalMenu> EmiBasicTerminalCraftingHandler = new EmiTerminalCraftingHandler<>(ETMenuType.BASIC_TERMINAL);
    public final static EmiTerminalCraftingHandler<AdvancedTerminalMenu> EmiAdvancedTerminalCraftingHandler = new EmiTerminalCraftingHandler<>(ETMenuType.ADVANCED_TERMINAL);
    public final static EmiTerminalCraftingHandler<EliteTerminalMenu> EmiEliteTerminalCraftingHandler = new EmiTerminalCraftingHandler<>(ETMenuType.ELITE_TERMINAL);
    public final static EmiTerminalCraftingHandler<UltimateTerminalMenu> EmiUltimateTerminalCraftingHandler = new EmiTerminalCraftingHandler<>(ETMenuType.ULTIMATE_TERMINAL);

    private final ETMenuType menuType;
    public EmiTerminalCraftingHandler(ETMenuType menuType) {
        this.menuType = menuType;
    }
    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().equals(ExtendedCraftingTableRecipe.getCategoryFromMenuType(this.menuType));
    }

    @Override
    public boolean alwaysDisplaySupport(EmiRecipe recipe) {
        return StandardRecipeHandler.super.alwaysDisplaySupport(recipe);
    }

    @Override
    public List<ClientTooltipComponent> getTooltip(EmiRecipe recipe, EmiCraftContext<T> context) {

        return StandardRecipeHandler.super.getTooltip(recipe, context);
    }

    @Override
    public List<Slot> getInputSources(T menu) {
        var slots = new ArrayList<Slot>();
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_HOTBAR));
        slots.addAll(menu.getSlots(SlotSemantics.PLAYER_INVENTORY));
        slots.addAll(menu.getSlots(menu.getEtMenuType().getSlotSemanticGrid()));
        return slots;
    }

    @Override
    public List<Slot> getCraftingSlots(T menu) {
        return menu.getSlots(menu.getEtMenuType().getSlotSemanticGrid());
    }

    @Override
    public @Nullable Slot getOutputSlot(T menu) {
        for(var slot : menu.getSlots(menu.getEtMenuType().getSlotSemanticResult())) {
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
        return true;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<T> context) {
        return StandardRecipeHandler.super.craft(recipe, context);
    }

    @Override
    public void render(EmiRecipe recipe, EmiCraftContext<T> context, List<Widget> widgets, GuiGraphics draw) {
        StandardRecipeHandler.super.render(recipe, context, widgets, draw);
    }

    private void transferRecipe(T menu, RecipeHolder<?> holder, EmiRecipe emiRecipe, boolean doTransfer) {

    }
}
