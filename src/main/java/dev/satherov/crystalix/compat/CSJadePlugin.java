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
import net.minecraft.world.item.ItemStack;

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
                .append(Component.literal(String.format("#%06X", (0xFFFFFF & color)).toUpperCase()).withColor(color));
    }
    
    private static void showTooltips(ITooltip tooltip, BlockAccessor accessor) {
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_INVISIBLE, accessor.getBlockState().getValue(CrystalixGlass.INVISIBLE)));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_SHADELESS, accessor.getBlockState().getValue(CrystalixGlass.SHADELESS)));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_REINFORCED, accessor.getBlockEntity() instanceof CrystalixGlassTile tile && tile.isReinforced()));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_FLUIDLOGGABLE, accessor.getBlockState().getValue(CrystalixGlass.FLUIDLOGGABLE)));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_TRANSPARENT, accessor.getBlockState().getValue(CrystalixGlass.TRANSPARENT)));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_REDSTONE, accessor.getBlockState().getValue(CrystalixGlass.REDSTONE)));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_CONDUCTOR, accessor.getBlockEntity() instanceof CrystalixGlassTile tile && tile.isConductor()));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_LIGHT, accessor.getBlockState().getValue(CrystalixGlass.LIGHT)));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_GHOST, accessor.getBlockState().getValue(CrystalixGlass.GHOST)));
        tooltip.add(CSJadePlugin.getTranslation(CSLanguage.PROPERTY_COLOR, accessor.getBlockEntity() instanceof CrystalixGlassTile tile ? tile.getColor() : 0xFFFFFF));
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
                case ALWAYS -> CSJadePlugin.showTooltips(tooltip, accessor);
                case WAND -> {
                    if (!CrystalixWand.find(accessor.getPlayer()).equals(ItemStack.EMPTY)) CSJadePlugin.showTooltips(tooltip, accessor);
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
