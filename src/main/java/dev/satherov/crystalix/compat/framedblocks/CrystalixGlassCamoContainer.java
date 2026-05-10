package dev.satherov.crystalix.compat.framedblocks;

import lombok.Getter;

import dev.satherov.crystalix.CXConfig;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.common.item.CrystalixWandItem;
import dev.satherov.crystalix.common.properties.CrystalixModelState;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.client.lang.GenericLang;
import dev.satherov.sathlib.common.properties.BlockItemProperty;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jspecify.annotations.Nullable;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerClientHandler;
import io.github.xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import io.github.xfacthd.framedblocks.api.model.data.ModelDataEntry;

import java.util.function.Consumer;

@NothingNull
public class CrystalixGlassCamoContainer extends AbstractBlockCamoContainer<CrystalixGlassCamoContainer> {
    
    private final @Getter int tintColor;
    private final @Getter boolean light;
    private final @Getter CrystalixModelState modelState;
    
    protected CrystalixGlassCamoContainer(BlockState state, int tintColor, boolean light, CrystalixModelState modelState) {
        super(state);
        this.tintColor = tintColor;
        this.light = light;
        this.modelState = modelState;
    }
    
    @Override
    public Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return this.tintColor;
    }
    
    @Override
    public void appendJadeTooltip(Level level, BlockPos pos, Player player, Consumer<Component> appender) {
        final BlockState state = level.getBlockState(pos);
        final BlockEntity entity = level.getBlockEntity(pos);
        final ItemStack stack = CXRegistry.CRYSTALIX_WAND.get().getDefaultInstance();
        switch (CXConfig.Client.getJadeMode()) {
            case ALWAYS -> this.appendTooltip(appender, stack, state, entity);
            case WAND -> {
                if (!CrystalixWandItem.find(player).isEmpty()) this.appendTooltip(appender, stack, state, entity);
            }
            case NEVER -> { }
        }
    }
    
    private void appendTooltip(Consumer<Component> appender, ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
        CXProperties.CONTAINER.forEach(property -> {
            final Component value = this.getValueComponent(property, stack, state, entity);
            appender.accept(SLComponent.empty()
                    .append(property.getName().translate(ChatFormatting.GRAY))
                    .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                    .append(value));
        });
    }
    
    private Component getValueComponent(BlockItemProperty<?> property, ItemStack stack, BlockState state, @Nullable BlockEntity entity) {
        if (property == CXProperties.COLOR || property == CXProperties.MATERIAL || property == CXProperties.SHADELESS || property == CXProperties.TINTED) {
            return property.displayBlockValue(stack, state, entity);
        }
        
        if (property == CXProperties.LIGHT) { ;
            return this.light ? CXLanguage.PROPERTY_LIGHT_LIGHT.translate(ChatFormatting.GOLD) : GenericLang.NONE.translate(ChatFormatting.GRAY);
        }
        
        return GenericLang.UNSUPPORTED.translate(ChatFormatting.RED);
    }
    
    @Override
    public @Nullable ModelDataEntry<CrystalixModelState> computeQueryData(Level level, BlockPos pos) {
        return new ModelDataEntry<>(CrystalixGlassBlockEntity.MODEL_STATE.property(), this.modelState);
    }
    
    @Override
    public int hashCode() {
        int result = this.content.hashCode();
        result = 31 * result + Integer.hashCode(this.tintColor);
        result = 31 * result + this.modelState.hashCode();
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof CrystalixGlassCamoContainer other)) return false;
        return this.content.equals(other.content) && this.tintColor == other.tintColor && this.modelState.equals(other.modelState);
    }
    
    @Override
    public String toString() {
        return "CrystalixGlassCamoContainer{content=" + this.content + ", tintColor=" + Integer.toHexString(0xFF000000 | this.tintColor) + ", modelState=" + this.modelState + "}";
    }
    
    @Override
    public AbstractBlockCamoContainerFactory<CrystalixGlassCamoContainer> getFactory() {
        return CXFramedBlocksCompat.CRYSTALIX_GLASS_CAMO_FACTORY.get();
    }
    
    @Override
    public CamoContainerClientHandler<BlockCamoContent, CrystalixGlassCamoContainer> getClientHandler() {
        return CrystalixGlassCamoContainerClientHandler.INSTANCE;
    }
}
