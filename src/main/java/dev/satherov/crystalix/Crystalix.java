package dev.satherov.crystalix;

import dev.satherov.crystalix.config.ConfigLoader;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.javafmlmod.FMLModContainer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import lombok.Getter;

@Mod(Crystalix.MOD_ID)
@EventBusSubscriber(modid = Crystalix.MOD_ID)
public class Crystalix {
    
    public static final String MOD_ID = "crystalix";
    private static @Getter Crystalix instance;
    private final @Getter ConfigLoader loader = ConfigLoader.create();
    
    public Crystalix(IEventBus bus, FMLModContainer container) {
        if (instance != null) throw new IllegalStateException("Already initialized");
        instance = this;
        loader.discover(container);
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
        
        CSRegistry.ENTRIES.cellSet().forEach((cell) -> {
            String name = cell.getColumnKey().key();
            if (name.equals("peach")
                    || name.equals("aquamarine")
                    || name.equals("fluorescent")
                    || name.equals("mint")
                    || name.equals("maroon")
                    || name.equals("bubblegum")
                    || name.equals("lavender")
                    || name.equals("persimmon")
                    || name.equals("cherenkov")
                    || name.equals("amber")
                    || name.equals("honey")
                    || name.equals("ultramarine")
                    || name.equals("spring_green")
                    || name.equals("rose")
                    || name.equals("navy")
                    || name.equals("icy_blue")
                    || name.equals("wine")
                    || name.equals("conifer")
            ) {
                if (cell.getRowKey().equals(CSRegistry.Types.GLASS)) {
                    BuiltInRegistries.ITEM.addAlias(dye(String.format("crystalix_%s_crystalix_glass", name)), cell.getValue().getId());
                    BuiltInRegistries.BLOCK.addAlias(dye(String.format("crystalix_%s_crystalix_glass", name)), cell.getValue().getId());
                }
                if (cell.getRowKey().equals(CSRegistry.Types.CLEAR)) {
                    BuiltInRegistries.ITEM.addAlias(dye(String.format("crystalix_%s_clear_crystalix_glass", name)), cell.getValue().getId());
                    BuiltInRegistries.BLOCK.addAlias(dye(String.format("crystalix_%s_clear_crystalix_glass", name)), cell.getValue().getId());
                }
                if (cell.getRowKey().equals(CSRegistry.Types.BORDERED)) {
                    BuiltInRegistries.ITEM.addAlias(dye(String.format("crystalix_%s_bordered_crystalix_glass", name)), cell.getValue().getId());
                    BuiltInRegistries.BLOCK.addAlias(dye(String.format("crystalix_%s_bordered_crystalix_glass", name)), cell.getValue().getId());
                }
            }
        });
    }
    
    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
    
    private static ResourceLocation dye(String path) {
        return ResourceLocation.fromNamespaceAndPath("dyenamicsandfriends", path);
    }
}
