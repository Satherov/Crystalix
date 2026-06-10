package dev.satherov.crystalix;

import dev.satherov.crystalix.client.CXKeybinds;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.core.registry.CXRegistry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@Mod(value = Crystalix.MOD_ID, dist = Dist.CLIENT)
public class CrystalixClient {
    
    private static final List<BlockTintSource> GLASS_BLOCK_TINT = List.of(new BlockTintSource() {
        
        @Override
        public int color(BlockState state) {
            return ARGB.opaque(0xFFFFFF);
        }
        
        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            CrystalixGlassBlockEntity entity = CXRegistry.GLASS_BLOCK_ENTITY.get().getBlockEntity(level, pos);
            return ARGB.opaque(entity != null ? entity.getColor() : 0xFFFFFF);
        }
    });
    
    public CrystalixClient(final IEventBus bus, final ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        bus.addListener(RegisterColorHandlersEvent.BlockTintSources.class, event ->
                event.register(CrystalixClient.GLASS_BLOCK_TINT, CXRegistry.CRYSTALIX_BLOCK.get())
        );
        CXKeybinds.MANAGER.register(bus);
    }
}
