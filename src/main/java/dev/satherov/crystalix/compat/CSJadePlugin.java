package dev.satherov.crystalix.compat;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.client.lang.CSTranslatable;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.block.CrystalixGlassTile;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.config.CSClientConfig;
import dev.satherov.crystalix.core.annotations.NothingNull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@NothingNull
@WailaPlugin
public class CSJadePlugin implements IWailaPlugin {
    
    private static final ResourceLocation CRYSTALIX_BLOCK = ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "crystalix_block");
    
    private static Component getTranslation(CSLanguage language, boolean state) {
        return language.text(ChatFormatting.GRAY)
                .append(Component.literal(": "))
                .append(state ? CSLanguage.PROPERTY_ENABLED.text(ChatFormatting.DARK_GREEN) : CSLanguage.PROPERTY_DISABLED.text(ChatFormatting.DARK_RED));
    }
    
    private static <T extends CSTranslatable> Component getTranslation(CSLanguage language, T state) {
        return language.text(ChatFormatting.GRAY)
                .append(Component.literal(": "))
                .append(state.text());
    }
    
    private static Component getTranslation(CSLanguage language, int color) {
        return language.text(ChatFormatting.GRAY)
                .append(Component.literal(": "))
                .append(Component.literal("#" + Integer.toHexString(color).toUpperCase()).withColor(color));
    }
    
    private static void showTooltips(ITooltip tooltip, BlockAccessor accessor) {
        tooltip.add(getTranslation(CSLanguage.PROPERTY_INVISIBLE, accessor.getBlockState().getValue(CrystalixGlass.INVISIBLE)));
        tooltip.add(getTranslation(CSLanguage.PROPERTY_SHADELESS, accessor.getBlockState().getValue(CrystalixGlass.SHADELESS)));
        tooltip.add(getTranslation(CSLanguage.PROPERTY_REINFORCED, accessor.getBlockState().getValue(CrystalixGlass.REINFORCED)));
        tooltip.add(getTranslation(CSLanguage.PROPERTY_WATERLOGGABLE, accessor.getBlockState().getValue(CrystalixGlass.WATERLOGGABLE)));
        tooltip.add(getTranslation(CSLanguage.PROPERTY_LIGHT, accessor.getBlockState().getValue(CrystalixGlass.LIGHT)));
        tooltip.add(getTranslation(CSLanguage.PROPERTY_GHOST, accessor.getBlockState().getValue(CrystalixGlass.GHOST)));
        tooltip.add(getTranslation(CSLanguage.PROPERTY_COLOR, accessor.getBlockEntity() instanceof CrystalixGlassTile tile ? tile.getColor() : 0xFFFFFF));
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
            switch (CSClientConfig.getJadeMode()) {
                case ALWAYS -> showTooltips(tooltip, accessor);
                case WAND -> {
                    if (accessor.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof CrystalixWand ||
                            accessor.getPlayer().getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof CrystalixWand
                    ) {
                        showTooltips(tooltip, accessor);
                    }
                }
                case NEVER -> { /* ignored */ }
            }
        }
        
        @Override
        public ResourceLocation getUid() {
            return CSJadePlugin.CRYSTALIX_BLOCK;
        }
    }
}
