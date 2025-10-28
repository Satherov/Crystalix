package dev.satherov.crystalix.common.item;

import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.common.properties.IProperty;
import dev.satherov.crystalix.core.CSRegistry;
import dev.satherov.crystalix.core.annotations.NothingNull;
import dev.satherov.crystalix.util.BatchProcessor;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

@NothingNull
public class CrystalixWand extends Item {
    
    public CrystalixWand(Properties properties) {
        super(properties
                      .stacksTo(1)
                      .component(CSRegistry.INVISIBLE, false)
                      .component(CSRegistry.SHADELESS, false)
                      .component(CSRegistry.REINFORCED, false)
                      .component(CSRegistry.WATERLOGGABLE, false)
                      .component(CSRegistry.LIGHT, CSProperties.Light.NONE)
                      .component(CSRegistry.GHOST, CSProperties.Ghost.BLOCK_ALL)
                      .component(CSRegistry.COLOR, CSRegistry.Colors.CLEAR)
        );
    }
    
    public static ItemStack find(Player player) {
        if (player.getMainHandItem().getItem() instanceof CrystalixWand) return player.getMainHandItem();
        if (player.getOffhandItem().getItem() instanceof CrystalixWand) return player.getOffhandItem();
        return ItemStack.EMPTY;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        components.add(CSLanguage.TOOLTIP_BULK.text(ChatFormatting.DARK_GRAY));
        for (IProperty<?> property : CSProperties.of(stack).properties().values()) {
            components.add(property.text());
        }
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.FAIL;
        if (!(context.getPlayer() instanceof ServerPlayer player)) return InteractionResult.FAIL;
        
        BlockPos blockpos = context.getClickedPos();
        if (!applyToBlock(level, blockpos, player)) {
            return InteractionResult.FAIL;
        }
        
        return InteractionResult.SUCCESS;
    }
    
    public boolean applyToBlock(ServerLevel level, BlockPos pos, ServerPlayer player) {
        if (!(level.getBlockState(pos).getBlock() instanceof CrystalixGlass glass)) return false;
        ItemStack wand = CrystalixWand.find(player);
        
        if (player.isShiftKeyDown()) {
            BatchProcessor.schedule(BatchProcessor.Batch.of(player, pos, wand));
        } else {
            level.setBlock(pos, glass.fromStack(level, pos, wand), 3);
        }
        
        return true;
    }
}
