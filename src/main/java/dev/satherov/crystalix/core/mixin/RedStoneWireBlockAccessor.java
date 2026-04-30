package dev.satherov.crystalix.core.mixin;

import net.minecraft.world.level.block.RedStoneWireBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RedStoneWireBlock.class)
public interface RedStoneWireBlockAccessor {
    
    @Accessor("COLORS")
    static int[] getColors() {
        throw new AssertionError();
    }
}
