package dev.satherov.crystalix;

import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.client.screen.CXRadialScreen;
import dev.satherov.crystalix.common.block.CrystalixGlassBlock;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.crystalix.network.SwapPropertiesPayload;
import dev.satherov.crystalix.network.ToggleColorless;
import dev.satherov.sathlib.client.input.SLKeybindManager;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.lwjgl.glfw.GLFW;

import java.util.List;

@Mod(value = Crystalix.MOD_ID, dist = Dist.CLIENT)
public class CrystalixClient {
    
    public static final SLKeybindManager KEYBINDS = SLKeybindManager.create(Crystalix.MOD_ID, Crystalix.id("default"));
    
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
    
    public static final KeyMapping OPEN_WAND_EDITOR = CrystalixClient.KEYBINDS.add(CXLanguage.KEY_OPEN_WAND_EDITOR, GLFW.GLFW_KEY_V, event -> {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        if (mc.level == null || player == null || mc.screen != null) return;
        
        final ItemStack stack = CrystalixWandItem.find(player);
        if (stack.isEmpty()) return;
        
        if (event.getAction() == GLFW.GLFW_PRESS) {
            if (mc.screen != null) return;
            mc.setScreen(new CXRadialScreen(stack));
        } else if (event.getAction() == GLFW.GLFW_RELEASE) {
            if (mc.screen instanceof CXRadialScreen) mc.setScreen(null);
        }
    });
    
    public static final KeyMapping TOGGLE_COLORLESS = CrystalixClient.KEYBINDS.add(CXLanguage.KEY_TOGGLE_COLORLESS, GLFW.GLFW_KEY_LEFT_CONTROL, event -> {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        if (mc.level == null || player == null || mc.screen != null) return;
        
        final ItemStack stack = CrystalixWandItem.find(player);
        if (stack.isEmpty()) return;
        
        if (event.getAction() == GLFW.GLFW_PRESS) {
            boolean enabled = !stack.getOrDefault(CXRegistry.APPLY_COLORLESS, false);
            stack.set(CXRegistry.APPLY_COLORLESS, enabled);
            ClientPacketDistributor.sendToServer(new ToggleColorless(enabled));
        }
    });
    
    public static final KeyMapping PICK_BLOCK = CrystalixClient.KEYBINDS.add(CXLanguage.KEY_PICK_BLOCK, GLFW.GLFW_KEY_X, event -> {
        final Minecraft mc = Minecraft.getInstance();
        final Player player = mc.player;
        final Level level = mc.level;
        if (level == null || player == null || mc.screen != null) return;
        
        final ItemStack stack = CrystalixWandItem.find(player);
        if (stack.isEmpty()) return;
        
        if (event.getAction() == GLFW.GLFW_PRESS) {
            final BlockPos pos = player.pick(player.blockInteractionRange(), 1.0F, false) instanceof BlockHitResult hit ? hit.getBlockPos() : null;
            if (pos == null) return;
            
            final BlockState state = level.getBlockState(pos);
            final CrystalixGlassBlockEntity entity = CXRegistry.GLASS_BLOCK_ENTITY.get().getBlockEntity(level, pos);
            if (!(state.getBlock() instanceof CrystalixGlassBlock) || entity == null) return;
            
            if (CXProperties.CONTAINER.matches(stack, state, entity)) {
                player.sendOverlayMessage(CXLanguage.MESSAGE_PROPERTY_MATCH.translate(ChatFormatting.GRAY));
                return;
            }
            
            CXProperties.CONTAINER.applyToItem(stack, state, entity);
            ClientPacketDistributor.sendToServer(new SwapPropertiesPayload(pos));
            player.sendOverlayMessage(CXLanguage.MESSAGE_PROPERTY_PICK.translate(ChatFormatting.GRAY));
        }
    });
    
    public CrystalixClient(final IEventBus bus, final ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        bus.addListener(RegisterColorHandlersEvent.BlockTintSources.class, event ->
                event.register(CrystalixClient.GLASS_BLOCK_TINT, CXRegistry.CRYSTALIX_BLOCK.get())
        );
        CrystalixClient.KEYBINDS.register(bus);
    }
}
