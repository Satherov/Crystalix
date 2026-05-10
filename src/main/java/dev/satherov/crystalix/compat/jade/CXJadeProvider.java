package dev.satherov.crystalix.compat.jade;

import dev.satherov.crystalix.CXConfig;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@NothingNull
public class CXJadeProvider implements IBlockComponentProvider {
    
    public static final CXJadeProvider INSTANCE = new CXJadeProvider();
    
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        switch (CXConfig.Client.getJadeMode()) {
            case ALWAYS -> CXJadeProvider.showTooltips(tooltip, accessor);
            case WAND -> {
                if (!CrystalixWandItem.find(accessor.getPlayer()).isEmpty())
                    CXJadeProvider.showTooltips(tooltip, accessor);
            }
            case NEVER -> { }
        }
    }
    
    private static void showTooltips(ITooltip tooltip, BlockAccessor accessor) {
        final BlockState state = accessor.getBlockState();
        final BlockEntity entity = accessor.getBlockEntity();
        CXProperties.CONTAINER.forEach(property -> tooltip.add(SLComponent.empty()
                .append(property.getName().translate(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(property.displayBlockValue(CXRegistry.CRYSTALIX_WAND.get().getDefaultInstance(), state, entity)))
        );
    }
    
    @Override
    public Identifier getUid() {
        return CXJadePlugin.ID;
    }
}
