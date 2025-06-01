package com.myogoo.extendedterminal.menu.slot;

import appeng.api.inventories.InternalInventory;
import appeng.menu.slot.AppEngSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

public class ETArmorSlot extends AppEngSlot {
    public ETArmorSlot(InternalInventory inv, int invSlot) {
        super(inv, invSlot);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        if(stack.getItem() instanceof ArmorItem armorItem) {
            return this.getSlotIndex() == armorItem.getEquipmentSlot().getIndex();
        }

        return super.mayPlace(stack);
    }
}
