package com.myogoo.extendedterminal.init;

import appeng.core.definitions.BlockDefinition;
import com.myogoo.extendedterminal.ExtendedTerminal;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public class ETBlocks {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(Registries.BLOCK, ExtendedTerminal.MODID);

    public static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();


}
