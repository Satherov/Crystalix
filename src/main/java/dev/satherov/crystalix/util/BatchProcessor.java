package dev.satherov.crystalix.util;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.block.CrystalixGlassTile;
import dev.satherov.crystalix.config.CSCommonConfig;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = Crystalix.MOD_ID)
public class BatchProcessor {
    
    private static final List<Batch> BATCHES = new ArrayList<>();
    
    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        Iterator<Batch> iterator = BATCHES.iterator();
        while (iterator.hasNext()) {
            MinecraftServer server = event.getServer();
            Batch batch = iterator.next();
            ServerLevel level = server.getLevel(batch.dimension);
            if (level == null) continue;
            
            if (batch.tick == Integer.MIN_VALUE) {
                batch.initTick(server.getTickCount());
            } else if (batch.tick + 20 < server.getTickCount() && batch.tick != Integer.MAX_VALUE) {
                ServerPlayer player = server.getPlayerList().getPlayer(batch.owner);
                if (player != null) {
                    player.displayClientMessage(CSLanguage.MESSAGE_SIZE.text(), true);
                    batch.tick = Integer.MAX_VALUE;
                }
            }
            
            if (batch.phase == Batch.Phase.EXPLORING) {
                batch.explore(level);
                if (batch.shouldBeginProcessing()) batch.beginProcessing();
            } else {
                batch.process(level);
            }
            
            if (batch.isDone()) {
                batch.clear();
                iterator.remove();
            }
        }
    }
    
    public static void schedule(Batch batch) {
        BATCHES.add(batch);
    }
    
    public static class Batch {
        private final UUID owner;
        private final ResourceKey<Level> dimension;
        private final BlockState state;
        private final int color;
        
        private final Set<BlockPos> observed = new LinkedHashSet<>();
        private final ArrayDeque<BlockPos> frontier = new ArrayDeque<>();
        
        private final int maxPerTick = CSCommonConfig.getMaxEditOperations();
        private final int maxTotal = CSCommonConfig.getMaxEditForce();
        
        private int tick = Integer.MIN_VALUE;
        private ArrayDeque<BlockPos> queue = null;
        private Phase phase = Phase.EXPLORING;
        
        protected Batch(ServerPlayer player, BlockPos start, BlockState state, int color) {
            this.owner = player.getUUID();
            this.dimension = player.level().dimension();
            this.state = state;
            this.color = color;
            frontier.add(start);
            observed.add(start);
        }
        
        public static Batch of(ServerPlayer player, BlockPos start, BlockState state, int color) {
            return new Batch(player, start, state, color);
        }
        
        protected void explore(LevelAccessor level) {
            if (observed.size() >= maxTotal) return;
            
            int operations = 0;
            while (!frontier.isEmpty() &&
                    operations < maxPerTick &&
                    observed.size() < maxTotal
            ) {
                BlockPos pos = frontier.pollFirst();
                if (!(level.getBlockState(pos).getBlock() instanceof CrystalixGlass)) continue;
                
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) continue;
                            BlockPos n = pos.offset(dx, dy, dz);
                            if (level.isOutsideBuildHeight(n) || !level.isAreaLoaded(n, 0)) continue;
                            if (observed.contains(n)) continue;
                            BlockState visited = level.getBlockState(n);
                            if (visited.getBlock() instanceof CrystalixGlass && visited.getBlock().equals(this.state.getBlock())) {
                                this.observed.add(n);
                                frontier.addLast(n);
                                operations++;
                                if (this.observed.size() >= maxTotal) break;
                            }
                        }
                        if (observed.size() >= maxTotal) break;
                    }
                    if (observed.size() >= maxTotal) break;
                }
            }
        }
        
        protected void initTick(int tick) {
            this.tick = tick;
        }
        
        protected boolean shouldBeginProcessing() {
            return phase == Phase.EXPLORING && (frontier.isEmpty() || observed.size() >= maxTotal);
        }
        
        protected void beginProcessing() {
            phase = Phase.PROCESSING;
            queue = new ArrayDeque<>(observed);
        }
        
        protected void process(ServerLevel level) {
            if (phase != Phase.PROCESSING || queue == null) return;
            
            int operations = 0;
            while (!queue.isEmpty() && operations < maxPerTick) {
                BlockPos pos = queue.pollFirst();
                if (level.isOutsideBuildHeight(pos) || !level.isAreaLoaded(pos, 0)) continue;
                
                BlockState visited = level.getBlockState(pos);
                if (visited.getBlock() instanceof CrystalixGlass && visited.getBlock().equals(this.state.getBlock())) {
                    BlockState apply = this.state.setValue(CrystalixGlass.COLORED, visited.getValue(CrystalixGlass.COLORED)).setValue(CrystalixGlass.WATERLOGGED, visited.getValue(CrystalixGlass.WATERLOGGED));
                    
                    if (color != Integer.MIN_VALUE && level.getBlockEntity(pos) instanceof CrystalixGlassTile tile && tile.getColor() != color) {
                        tile.setColor(apply, color);
                    } else {
                        level.setBlockAndUpdate(pos, apply);
                    }
                }
                operations++;
            }
        }
        
        protected boolean isDone() {
            return phase == Phase.PROCESSING && (queue == null || queue.isEmpty());
        }
        
        protected void clear() {
            frontier.clear();
            observed.clear();
            if (queue != null) queue.clear();
        }
        
        enum Phase {EXPLORING, PROCESSING}
    }
}
