package com.myogoo.extendedterminal.client.screen.extendedcrafting;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ActionButton;
import appeng.core.AEConfig;
import appeng.core.network.serverbound.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import com.mojang.blaze3d.platform.InputConstants;
import com.myogoo.extendedterminal.client.screen.ETBaseTerminalScreen;
import com.myogoo.extendedterminal.menu.extendedcrafting.BasicTerminalMenu;
import com.myogoo.extendedterminal.menu.ETSlotSemantics;
import com.myogoo.extendedterminal.menu.slot.ETBaseCraftingSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.logging.Logger;

public class BasicTerminalScreen extends ETBaseTerminalScreen<BasicTerminalMenu> {
    public BasicTerminalScreen(BasicTerminalMenu menu, Inventory inventory, Component title, ScreenStyle style) {
        super(menu, inventory, title, style);
    }
}
