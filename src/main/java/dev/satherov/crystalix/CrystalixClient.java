package dev.satherov.crystalix;

import dev.satherov.crystalix.client.KeybindManager;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.common.properties.IProperty;
import dev.satherov.crystalix.config.CSClientConfig;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

@Mod(value = Crystalix.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Crystalix.MOD_ID, value = Dist.CLIENT)
public class CrystalixClient {
    
    public CrystalixClient(FMLModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    
    @SubscribeEvent
    public static void registerKeys(final RegisterKeyMappingsEvent event) {
        event.register(KeybindManager.SCREEN_OPENER);
        event.register(KeybindManager.COPY_PROPERTIES);
    }
    
    @SubscribeEvent
    public static void onRenderGuiOverlay(final RenderGuiEvent.Post event) {
        if (CSClientConfig.getWandInfo() == CSClientConfig.AnchorPosition.NONE) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack wand = CrystalixWand.find(mc.player);
        if (wand.isEmpty()) return;
        
        GuiGraphics guiGraphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        
        CSProperties properties = CSProperties.of(wand);
        
        int maxTextWidth = 0;
        for (IProperty<?> property : properties.properties().values()) {
            Component text = property.text();
            int textWidth = mc.font.width(text);
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth;
            }
        }
        
        int boxWidth = maxTextWidth + 20;
        int boxHeight = properties.properties().size() * (mc.font.lineHeight + 1) + 20;
        
        int boxX;
        if (CSClientConfig.getWandInfo() == CSClientConfig.AnchorPosition.TOP_LEFT) {
            boxX = 10;
        } else {
            boxX = screenWidth - boxWidth - 10;
        }
        int boxY = 10;
        
        guiGraphics.fill(
                boxX,
                boxY,
                boxX + boxWidth,
                boxY + boxHeight,
                0x88111111
        );
        
        guiGraphics.renderOutline(
                boxX,
                boxY,
                boxWidth,
                boxHeight,
                0xBB333333
        );
        
        int textX = boxX + 10;
        int textY = boxY + 10;
        
        for (IProperty<?> property : properties.properties().values()) {
            Component text = property.text();
            guiGraphics.drawString(
                    mc.font,
                    text,
                    textX,
                    textY,
                    0xFF0F0F0F
            );
            textY += mc.font.lineHeight + 1;
        }
    }
    
    @SubscribeEvent
    @SuppressWarnings("deprecation")
    public static void renderTypeSetup(final EntityRenderersEvent.RegisterRenderers event) {
        CSRegistry.ENTRIES.cellSet().forEach((cell) -> {
            ItemBlockRenderTypes.setRenderLayer(cell.getValue().get(), RenderType.translucent());
        });
    }
    
    @SubscribeEvent
    public static void onItemColors(final RegisterColorHandlersEvent.Item event) {
        CSRegistry.ENTRIES.cellSet().forEach((cell) -> {
            if (cell.getColumnKey().color() < 0) return;
            event.register((stack, idx) -> (cell.getColumnKey().color() & 0x00FFFFFF) | 0xFF000000, cell.getValue().get());
        });
    }
    
    @SubscribeEvent
    public static void onBlockColors(final RegisterColorHandlersEvent.Block event) {
        CSRegistry.ENTRIES.cellSet().forEach((cell) -> {
            if (cell.getColumnKey().color() < 0) return;
            event.register((state, getter, pos, idx) -> cell.getColumnKey().color(), cell.getValue().get());
        });
    }
}
