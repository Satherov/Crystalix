package com.satherov.crystalix.content.item;

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

import com.satherov.crystalix.content.BatchProcessor;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.content.properties.ITranslatableProperty;
import com.satherov.crystalix.core.annotations.NothingNull;
import com.satherov.crystalix.core.lang.CrystalixLanguage;

import java.util.Arrays;
import java.util.List;

@NothingNull
public class CrystalixWand extends Item {
    
    public CrystalixWand(Properties properties) {
        super(properties
                      .stacksTo(1)
                      .component(CrystalixRegistry.INVISIBLE, false)
                      .component(CrystalixRegistry.SHADELESS, false)
                      .component(CrystalixRegistry.REINFORCED, false)
                      .component(CrystalixRegistry.WATERLOGGABLE, false)
                      .component(CrystalixRegistry.LIGHT, BlockProperties.Light.NONE)
                      .component(CrystalixRegistry.GHOST, BlockProperties.Ghost.BLOCK_ALL));
    }
    
    public static void sendMessage(Player player, ITranslatableProperty<?> property) {
        player.displayClientMessage(property.getTranslation(), true);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(CrystalixLanguage.TOOLTIP_BULK.translateFormatted(ChatFormatting.GRAY));
        Arrays.stream(new BlockProperties(stack).properties).toList().forEach(property -> tooltipComponents.add(property.getTranslation()));
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
        ItemStack wand = player.getMainHandItem();
        
        if (player.isShiftKeyDown()) {
            BatchProcessor.schedule(BatchProcessor.Batch.of(player, pos, wand));
        } else {
            level.setBlock(pos, glass.modifyFromWand(level.getBlockState(pos), wand), 3);
        }
        
        return true;
    }
}
