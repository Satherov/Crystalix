package dev.satherov.crystalix.client;

import lombok.experimental.UtilityClass;

import dev.satherov.crystalix.CXConfig;
import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.registry.CXRegistry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ExtractLevelRenderStateEvent;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.joml.Vector3f;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@UtilityClass
@SuppressWarnings("deprecation")
@EventBusSubscriber(modid = Crystalix.MOD_ID, value = Dist.CLIENT)
public final class CXBlockHighlighter {
    
    private static final ContextKey<OutlineState> OUTLINE_STATE = new ContextKey<>(Crystalix.id("highlighted_blocks"));
    
    private static final long HIGHLIGHT_DURATION_MS = 10_000L;
    private static final long SCAN_INTERVAL_TICKS = 10L;
    private static final int OUTLINE_COLOR = 0xFFFFFFFF;
    
    private static long highlightUntilMs = 0L;
    private static int cachedRadius = -1;
    private static ResourceKey<Level> cachedDimension = null;
    private static ChunkPos cachedCenter = ChunkPos.ZERO;
    private static long cachedScanGameTime = Long.MIN_VALUE;
    private static OutlineState cachedOutline = OutlineState.EMPTY;
    
    public static void highlight() {
        CXBlockHighlighter.highlightUntilMs = Util.getMillis() + CXBlockHighlighter.HIGHLIGHT_DURATION_MS;
        CXBlockHighlighter.invalidateCache();
    }
    
    @SubscribeEvent
    private static void onExtractLevelRenderState(final ExtractLevelRenderStateEvent event) {
        final LevelRenderState state = event.getRenderState();
        if (!CXBlockHighlighter.isHighlighting()) {
            state.setRenderData(CXBlockHighlighter.OUTLINE_STATE, null);
            if (!CXBlockHighlighter.cachedOutline.isEmpty()) CXBlockHighlighter.invalidateCache();
            return;
        }
        
        final Minecraft minecraft = Minecraft.getInstance();
        final LocalPlayer player = minecraft.player;
        
        if (player == null) {
            state.setRenderData(CXBlockHighlighter.OUTLINE_STATE, null);
            return;
        }
        
        final ClientLevel level = event.getLevel();
        final OutlineState outline = CXBlockHighlighter.getHighlightedOutline(minecraft, level, player, CXConfig.Client.getHighlightChunkRadius());
        
        if (outline.isEmpty()) {
            state.setRenderData(CXBlockHighlighter.OUTLINE_STATE, null);
            return;
        }
        
        state.setRenderData(CXBlockHighlighter.OUTLINE_STATE, outline);
        state.haveGlowingEntities = true;
    }
    
    @SubscribeEvent
    public static void onSubmitCustomGeometry(final SubmitCustomGeometryEvent event) {
        final OutlineState outline = event.getLevelRenderState().getRenderData(CXBlockHighlighter.OUTLINE_STATE);
        if (outline == null || outline.isEmpty()) return;
        
        final PoseStack stack = event.getPoseStack();
        final Vec3 cam = event.getLevelRenderState().cameraRenderState.pos;
        
        stack.pushPose();
        stack.translate(-cam.x, -cam.y, -cam.z);
        
        event.getSubmitNodeCollector().submitCustomGeometry(
                stack, RenderTypes.outline(TextureAtlas.LOCATION_BLOCKS),
                (pose, buffer) -> CXBlockHighlighter.renderOutline(outline, pose, buffer)
        );
        
        stack.popPose();
    }
    
    private static boolean isHighlighting() {
        return Util.getMillis() < CXBlockHighlighter.highlightUntilMs;
    }
    
    private static OutlineState getHighlightedOutline(final Minecraft minecraft, final ClientLevel level, final LocalPlayer player, final int radius) {
        final ChunkPos center = player.chunkPosition();
        final long time = level.getGameTime();
        
        if (CXBlockHighlighter.canUseCachedOutline(level, center, time, radius)) {
            return CXBlockHighlighter.cachedOutline;
        }
        
        final LongSet blocks = CXBlockHighlighter.findHighlightableBlocks(level, center, radius);
        final TextureAtlasSprite sprite = minecraft.getModelManager()
                .getBlockStateModelSet()
                .missingModel()
                .particleMaterial()
                .sprite();
        
        final OutlineState outline = new OutlineState(
                List.copyOf(CXBlockHighlighter.mergeFaces(blocks)),
                sprite.getU(0.5F),
                sprite.getV(0.5F)
        );
        
        CXBlockHighlighter.cachedDimension = level.dimension();
        CXBlockHighlighter.cachedCenter = center;
        CXBlockHighlighter.cachedRadius = radius;
        CXBlockHighlighter.cachedScanGameTime = time;
        CXBlockHighlighter.cachedOutline = outline;
        
        return outline;
    }
    
    private static boolean canUseCachedOutline(final ClientLevel level, final ChunkPos center, final long gameTime, final int radius) {
        return level.dimension().equals(CXBlockHighlighter.cachedDimension)
                && center.equals(CXBlockHighlighter.cachedCenter)
                && CXBlockHighlighter.cachedRadius == radius
                && gameTime - CXBlockHighlighter.cachedScanGameTime < CXBlockHighlighter.SCAN_INTERVAL_TICKS;
    }
    
    private static LongSet findHighlightableBlocks(final ClientLevel level, final ChunkPos center, int radius) {
        final LongSet blocks = new LongOpenHashSet();
        
        for (int chunkX = center.x() - radius; chunkX <= center.x() + radius; chunkX++) {
            for (int chunkZ = center.z() - radius; chunkZ <= center.z() + radius; chunkZ++) {
                final LevelChunk chunk = level.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
                
                if (chunk == null) {
                    continue;
                }
                
                chunk.findBlocks(
                        state -> state.is(CXRegistry.CRYSTALIX_BLOCK_TAG),
                        (pos, ignored) -> blocks.add(pos.asLong())
                );
            }
        }
        
        return blocks;
    }
    
    private static List<FaceQuad> mergeFaces(final LongSet blocks) {
        final List<FaceQuad> quads = new ArrayList<>();
        if (blocks.isEmpty()) return quads;
        
        for (final Direction direction : Direction.values()) {
            final Int2ObjectOpenHashMap<LongOpenHashSet> facesByPlane = new Int2ObjectOpenHashMap<>();
            
            for (final long block : blocks) {
                if (blocks.contains(BlockPos.offset(block, direction))) continue;
                
                final int x = BlockPos.getX(block);
                final int y = BlockPos.getY(block);
                final int z = BlockPos.getZ(block);
                
                final int plane = CXBlockHighlighter.plane(direction, x, y, z);
                final long cell = CXBlockHighlighter.pack(
                        CXBlockHighlighter.faceU(direction, x, y, z),
                        CXBlockHighlighter.faceV(direction, x, y, z)
                );
                
                facesByPlane.computeIfAbsent(plane, ignored -> new LongOpenHashSet()).add(cell);
            }
            
            for (final var entry : facesByPlane.int2ObjectEntrySet()) {
                CXBlockHighlighter.mergePlaneFaces(quads, direction, entry.getIntKey(), entry.getValue());
            }
        }
        
        return quads;
    }
    
    private static void mergePlaneFaces(final List<FaceQuad> quads, final Direction direction, final int plane, final LongSet cells) {
        final LongOpenHashSet remaining = new LongOpenHashSet(cells);
        final List<Long> sorted = new ArrayList<>(cells.size());
        sorted.addAll(cells);
        sorted.sort(Comparator.comparingInt(CXBlockHighlighter::cellV).thenComparingInt(CXBlockHighlighter::cellU));
        
        for (final long cell : sorted) {
            if (!remaining.contains(cell)) continue;
            final int startU = CXBlockHighlighter.cellU(cell);
            final int startV = CXBlockHighlighter.cellV(cell);
            
            int width = 1;
            while (remaining.contains(CXBlockHighlighter.pack(startU + width, startV))) {
                width++;
            }
            
            int height = 1;
            while (CXBlockHighlighter.hasRow(remaining, startU, startV + height, width)) {
                height++;
            }
            
            CXBlockHighlighter.removeMergedCells(remaining, startU, startV, width, height);
            
            quads.add(CXBlockHighlighter.createFaceQuad(
                    direction,
                    plane,
                    startU,
                    startV,
                    startU + width,
                    startV + height
            ));
        }
    }
    
    private static void removeMergedCells(
            final LongOpenHashSet remaining,
            final int startU,
            final int startV,
            final int width,
            final int height
    ) {
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                remaining.remove(CXBlockHighlighter.pack(startU + u, startV + v));
            }
        }
    }
    
    private static boolean hasRow(final LongSet remaining, final int startU, final int v, final int width) {
        for (int u = 0; u < width; u++) {
            if (!remaining.contains(CXBlockHighlighter.pack(startU + u, v))) {
                return false;
            }
        }
        
        return true;
    }
    
    @SuppressWarnings("DuplicatedCode")
    private static FaceQuad createFaceQuad(
            final Direction direction,
            final int plane,
            final int minU,
            final int minV,
            final int maxU,
            final int maxV
    ) {
        return switch (direction) {
            case DOWN -> new FaceQuad(
                    new Vector3f(minU, plane, minV),
                    new Vector3f(maxU, plane, minV),
                    new Vector3f(maxU, plane, maxV),
                    new Vector3f(minU, plane, maxV)
            );
            case UP -> new FaceQuad(
                    new Vector3f(minU, plane, minV),
                    new Vector3f(minU, plane, maxV),
                    new Vector3f(maxU, plane, maxV),
                    new Vector3f(maxU, plane, minV)
            );
            case NORTH -> new FaceQuad(
                    new Vector3f(minU, minV, plane),
                    new Vector3f(maxU, minV, plane),
                    new Vector3f(maxU, maxV, plane),
                    new Vector3f(minU, maxV, plane)
            );
            case SOUTH -> new FaceQuad(
                    new Vector3f(minU, minV, plane),
                    new Vector3f(minU, maxV, plane),
                    new Vector3f(maxU, maxV, plane),
                    new Vector3f(maxU, minV, plane)
            );
            case WEST -> new FaceQuad(
                    new Vector3f(plane, minV, minU),
                    new Vector3f(plane, maxV, minU),
                    new Vector3f(plane, maxV, maxU),
                    new Vector3f(plane, minV, maxU)
            );
            case EAST -> new FaceQuad(
                    new Vector3f(plane, minV, minU),
                    new Vector3f(plane, minV, maxU),
                    new Vector3f(plane, maxV, maxU),
                    new Vector3f(plane, maxV, minU)
            );
        };
    }
    
    private static void renderOutline(final OutlineState outline, final PoseStack.Pose pose, final VertexConsumer buffer) {
        for (final FaceQuad quad : outline.quads()) {
            CXBlockHighlighter.vertex(buffer, pose, quad.v0(), outline.u(), outline.v());
            CXBlockHighlighter.vertex(buffer, pose, quad.v1(), outline.u(), outline.v());
            CXBlockHighlighter.vertex(buffer, pose, quad.v2(), outline.u(), outline.v());
            CXBlockHighlighter.vertex(buffer, pose, quad.v3(), outline.u(), outline.v());
        }
    }
    
    private static void vertex(final VertexConsumer buffer, final PoseStack.Pose pose, final Vector3f pos, final float u, final float v) {
        buffer.addVertex(pose, pos.x, pos.y, pos.z).setUv(u, v).setColor(CXBlockHighlighter.OUTLINE_COLOR);
    }
    
    private static int plane(final Direction direction, final int x, final int y, final int z) {
        return switch (direction) {
            case DOWN -> y;
            case UP -> y + 1;
            case NORTH -> z;
            case SOUTH -> z + 1;
            case WEST -> x;
            case EAST -> x + 1;
        };
    }
    
    private static int faceU(final Direction direction, final int x, final int y, final int z) {
        return switch (direction) {
            case DOWN, UP, NORTH, SOUTH -> x;
            case WEST, EAST -> z;
        };
    }
    
    private static int faceV(final Direction direction, final int x, final int y, final int z) {
        return switch (direction) {
            case DOWN, UP -> z;
            case NORTH, SOUTH, WEST, EAST -> y;
        };
    }
    
    private static long pack(final int u, final int v) {
        return (u & 0xFFFFFFFFL) | ((long) v << 32);
    }
    
    private static int cellU(final long cell) {
        return (int) cell;
    }
    
    private static int cellV(final long cell) {
        return (int) (cell >> 32);
    }
    
    private static void invalidateCache() {
        CXBlockHighlighter.cachedDimension = null;
        CXBlockHighlighter.cachedCenter = ChunkPos.ZERO;
        CXBlockHighlighter.cachedRadius = -1;
        CXBlockHighlighter.cachedScanGameTime = Long.MIN_VALUE;
        CXBlockHighlighter.cachedOutline = OutlineState.EMPTY;
    }
    
    private record OutlineState(List<FaceQuad> quads, float u, float v) {
        
        private static final OutlineState EMPTY = new OutlineState(List.of(), 0.0F, 0.0F);
        
        private boolean isEmpty() {
            return this.quads.isEmpty();
        }
    }
    
    private record FaceQuad(Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3) { }
}