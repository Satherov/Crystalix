package dev.satherov.crystalix;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.block.CrystalixGlassTile;
import dev.satherov.crystalix.config.ConfigLoader;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Mod(Crystalix.MOD_ID)
@EventBusSubscriber(modid = Crystalix.MOD_ID)
public class Crystalix {
    
    public static final String MOD_ID = "crystalix";
    private static @Getter Crystalix instance;
    private final @Getter ConfigLoader loader = ConfigLoader.create();
    
    public Crystalix(IEventBus bus, FMLModContainer container) {
        if (Crystalix.instance != null) throw new IllegalStateException("Already initialized");
        Crystalix.instance = this;
        this.loader.discover(container);
        CSRegistry.register(bus);
    }
    
    @SubscribeEvent
    private static void opInDev(final PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer entity)) return;
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            ServerLevel level = entity.serverLevel();
            MinecraftServer server = level.getServer();
            server.getPlayerList().op(entity.getGameProfile());
        }
    }
    
    @SubscribeEvent
    private static void onConfigLoad(final ModConfigEvent.Loading event) {
        Crystalix.getInstance().getLoader().update(event.getConfig().getSpec());
    }
    
    @SubscribeEvent
    private static void onConfigReload(final ModConfigEvent.Reloading event) {
        Crystalix.getInstance().getLoader().update(event.getConfig().getSpec());
    }
    
    @SubscribeEvent
    private static void registerAliases(final FMLLoadCompleteEvent event) {
        CSRegistry.OLD_ENTRIES.cellSet().forEach(cell -> 
            BuiltInRegistries.ITEM.addAlias(cell.getValue().getId(), Crystalix.rl(cell.getRowKey().format()))
        );
    }
    
    @SubscribeEvent
    private static void onChunkLoad(final ChunkEvent.Load event) {
        if (event.isNewChunk()) return;
        
        ChunkAccess chunk = event.getChunk();
        if (chunk.getData(CSRegistry.MIGRATED)) return;
        Level world = event.getChunk().getLevel();
        if (!(world instanceof ServerLevel level)) return;
        
        chunk.setData(CSRegistry.MIGRATED, Boolean.TRUE);
        List<LevelChunkSection> sections = Arrays.stream(chunk.getSections())
                .filter(section -> !section.hasOnlyAir())
                .filter(section -> section.maybeHas(state -> state.getBlock() instanceof CrystalixGlass))
                .toList();
        if (sections.isEmpty()) return;
        
        Map<BlockPos, BlockState> map = new HashMap<>();
        chunk.findBlocks(
                (state) -> state.getBlock() instanceof CrystalixGlass glass && glass.color() != null,
                (pos, state) -> map.put(pos.immutable(), state)
        );
        
        LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).tell(new TickTask(1, () -> {
            map.forEach((pos, state) -> {
                CrystalixGlass glass = (CrystalixGlass) state.getBlock();
                int color = Objects.requireNonNull(glass.color()).color();
                
                BlockState migrated = CSRegistry.ENTRIES.get(glass.type())
                        .get().defaultBlockState()
                        .setValue(CrystalixGlass.INVISIBLE, state.getValue(CrystalixGlass.INVISIBLE))
                        .setValue(CrystalixGlass.SHADELESS, state.getValue(CrystalixGlass.SHADELESS))
                        .setValue(CrystalixGlass.WATERLOGGABLE, state.getValue(CrystalixGlass.WATERLOGGABLE))
                        .setValue(CrystalixGlass.WATERLOGGED, state.getValue(CrystalixGlass.WATERLOGGED))
                        .setValue(CrystalixGlass.TRANSPARENT, color < 0)
                        .setValue(CrystalixGlass.LIGHT, state.getValue(CrystalixGlass.LIGHT))
                        .setValue(CrystalixGlass.GHOST, state.getValue(CrystalixGlass.GHOST));
                
                chunk.setBlockState(pos, migrated, false);
                CrystalixGlassTile tile = new CrystalixGlassTile(pos, Objects.requireNonNull(migrated));
                tile.setColor(state, color);
                tile.setReinforced(state.getValue(CrystalixGlass.REINFORCED));
                
                chunk.setBlockEntity(tile);
                level.updateNeighborsAt(pos, migrated.getBlock());
            });
        }));
    }
    
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, path);
    }
}
