package com.satherov.crystalix;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.satherov.crystalix.client.KeybindManager;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.network.CrystalixNetworking;

@Mod(Crystalix.MOD_ID)
public class Crystalix {
    public static final String MOD_ID = "crystalix";

    public Crystalix() {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        CrystalixRegistry.BLOCKS.register(modEventBus);
        CrystalixRegistry.ITEMS.register(modEventBus);
        CrystalixRegistry.CREATIVE_TABS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CrystalixConfig.SPEC);

        modEventBus.addListener(this::onCommonSetup);

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(this::onClientSetup);
            modEventBus.addListener(this::onRegisterKeyMappings);
            modEventBus.addListener(this::onRegisterRenderers);
        }
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        CrystalixNetworking.register(event);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(new KeybindManager());
    }

    private void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(KeybindManager.CYCLE_SHADELESS);
        event.register(KeybindManager.CYCLE_REINFORCED);
        event.register(KeybindManager.CYCLE_LIGHT);
        event.register(KeybindManager.CYCLE_GHOST);
    }

    private void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        CrystalixRegistry.BLOCKS_MAP.forEach((color, set) ->
                set.forEach((name, block) ->
                        ItemBlockRenderTypes.setRenderLayer(block.get(), RenderType.translucent())
                )
        );
    }
}
