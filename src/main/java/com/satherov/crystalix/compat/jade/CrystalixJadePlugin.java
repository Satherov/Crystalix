package com.satherov.crystalix.compat.jade;

import java.util.Locale;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.block.CrystalixGlass;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class CrystalixJadePlugin implements IWailaPlugin {

    private static final ResourceLocation CRYSTALIX_BLOCK = ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "crystalix_block");

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CrystalixComponentProvider.INSTANCE, CrystalixGlass.class);
    }

    enum CrystalixComponentProvider implements IBlockComponentProvider  {
        INSTANCE;

        @Override
        public void appendTooltip(
                ITooltip tooltip,
                BlockAccessor accessor,
                IPluginConfig config
        ) {
            tooltip.add(getTranslation("shadeless", accessor.getBlockState().getValue(CrystalixGlass.SHADELESS)));
            tooltip.add(getTranslation("reinforced", accessor.getBlockState().getValue(CrystalixGlass.REINFORCED)));
            tooltip.add(getTranslation("light", accessor.getBlockState().getValue(CrystalixGlass.LIGHT).name().toLowerCase(Locale.ROOT)));
            tooltip.add(getTranslation("ghost", accessor.getBlockState().getValue(CrystalixGlass.GHOST).name().toLowerCase(Locale.ROOT)));
        }

        @Override
        public ResourceLocation getUid() {
            return CrystalixJadePlugin.CRYSTALIX_BLOCK;
        }
    }

    private static Component getTranslation(String key, boolean state) {
        return Component.translatable(String.format("%s.property.%s", Crystalix.MOD_ID, key)).withStyle(ChatFormatting.GRAY)
                .append(Component.literal(": ")).withStyle(ChatFormatting.GRAY)
                .append(Component.translatable(String.format("%s.property.%s.%s", Crystalix.MOD_ID, key, state ? "enabled" : "disabled")).withStyle(state ? ChatFormatting.DARK_GREEN : ChatFormatting.DARK_RED));
    }

    private static Component getTranslation(String key, String state) {
        return Component.translatable(String.format("%s.property.%s", Crystalix.MOD_ID, key))
                .append(Component.literal(": "))
                .append(Component.translatable(String.format("%s.property.%s.%s", Crystalix.MOD_ID, key, state)));
    }
}
