package com.myogoo.extendedterminal.client.screen;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.core.AEConfig;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import appeng.menu.SlotSemantic;
import com.mojang.blaze3d.platform.InputConstants;
import com.myogoo.extendedterminal.menu.ETBaseTerminalMenu;
import com.myogoo.extendedterminal.menu.slot.ETArmorSlot;
import com.myogoo.extendedterminal.menu.slot.ETBaseCraftingSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ETBaseTerminalScreen<T extends ETBaseTerminalMenu> extends MEStorageScreen<T> {
    public ETBaseTerminalScreen(T menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        ActionButton clearBtn = new ActionButton(ActionItems.S_STASH, btn -> menu.clearCraftingGrid());
        clearBtn.setHalfSize(true);
        clearBtn.setDisableBackground(true);
        widgets.add("clearCraftingGrid", clearBtn);

        ActionButton clearToPlayerInvBtn = new ActionButton(ActionItems.S_STASH_TO_PLAYER_INV,
                btn -> menu.clearToPlayerInventory());
        clearToPlayerInvBtn.setHalfSize(true);
        clearToPlayerInvBtn.setDisableBackground(true);
        widgets.add("clearToPlayerInv",clearToPlayerInvBtn);

    }

    @Override
    public void onClose() {
        if(AEConfig.instance().isClearGridOnClose()) {
            this.getMenu().clearCraftingGrid();
        }
        super.onClose();
    }

    @Override
    protected void slotClicked(Slot slot, int slotIdx, int mouseButton, ClickType clickType) {
        if (slot instanceof ETBaseCraftingSlot) {
            InventoryAction action;
            if (hasShiftDown()) {
                action = InventoryAction.CRAFT_SHIFT;
            } else if (InputConstants.isKeyDown(getMinecraft().getWindow().getWindow(), GLFW.GLFW_KEY_SPACE)) {
                action = InventoryAction.CRAFT_ALL;
            } else {
                action = mouseButton == 1 ? InventoryAction.CRAFT_STACK : InventoryAction.CRAFT_ITEM;
            }

            final InventoryActionPacket p = new InventoryActionPacket(action, slotIdx, 0);
            PacketDistributor.sendToServer(p);

            return;
        }
        if(slot instanceof ETArmorSlot armorSlot) {
            var selectedArmor = armorSlot.getItem().copy();
            armorSlot.clearStack();
        }


        super.slotClicked(slot, slotIdx, mouseButton, clickType);
    }

    @Override
    public void init() {
        super.init();
        var etMenuType = this.getMenu().getEtMenuType();
        drawCraftingSlot(etMenuType.getSlotSemanticGrid(),etMenuType.getSize());
    }

    protected void drawCraftingSlot(SlotSemantic slotSemantics, int line) {
        List<Slot> craftingSlots = this.getMenu().getSlots(slotSemantics);
        Slot firstSlot = craftingSlots.getFirst();

        int craftGridStartX = firstSlot.x;
        int craftGridStartY = firstSlot.y;

        for(int row = 0; row < line; row++) {
            for(int col = 0; col < line; col++) {
                int index = row * line + col;
                Slot slot = craftingSlots.get(index);
                int x = craftGridStartX + col * 18;
                int y = craftGridStartY + row * 18;
                slot.x = x;
                slot.y = y;
            }
        }
    }
}
