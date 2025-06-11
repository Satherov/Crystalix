package com.satherov.crystalix.compat.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.satherov.crystalix.core.annotations.NothingNull;
import com.satherov.crystalix.core.lang.CrystalixLanguage;
import com.satherov.crystalix.core.lang.ILangEntry;
import com.satherov.crystalix.core.lang.ITranslatable;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@NothingNull
@WailaPlugin
public class CrystalixJadePlugin implements IWailaPlugin {

    private static final ResourceLocation CRYSTALIX_BLOCK = ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "crystalix_block");

    private static Component getTranslation(ILangEntry langEntry, boolean state) {
        return langEntry.translateFormatted(ChatFormatting.GRAY)
                .append(Component.literal(" "))
                .append(state
                        ? CrystalixLanguage.PROPERTY_ENABLED.translateFormatted(ChatFormatting.DARK_GREEN)
                        : CrystalixLanguage.PROPERTY_DISABLED.translateFormatted(ChatFormatting.DARK_RED));
    }

    private static <T extends ITranslatable> Component getTranslation(T state) {
        return state.getTranslation();
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CrystalixComponentProvider.INSTANCE, CrystalixGlass.class);
    }

    enum CrystalixComponentProvider implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(
                ITooltip tooltip,
                BlockAccessor accessor,
                IPluginConfig config
        ) {
            tooltip.add(getTranslation(CrystalixLanguage.PROPERTY_INVISIBLE, accessor.getBlockState().getValue(CrystalixGlass.INVISIBLE)));
            tooltip.add(getTranslation(CrystalixLanguage.PROPERTY_SHADELESS, accessor.getBlockState().getValue(CrystalixGlass.SHADELESS)));
            tooltip.add(getTranslation(CrystalixLanguage.PROPERTY_REINFORCED, accessor.getBlockState().getValue(CrystalixGlass.REINFORCED)));
            tooltip.add(getTranslation(accessor.getBlockState().getValue(CrystalixGlass.LIGHT)));
            tooltip.add(getTranslation(accessor.getBlockState().getValue(CrystalixGlass.GHOST)));
        }

        @Override
        public ResourceLocation getUid() {
            return CrystalixJadePlugin.CRYSTALIX_BLOCK;
        }
    }
}
