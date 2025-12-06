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
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

import java.util.List;

@NothingNull
public class CrystalixWand extends Item {
    
    public CrystalixWand(Properties properties) {
        super(properties
                .stacksTo(1)
                .component(CSRegistry.INVISIBLE, false)
                .component(CSRegistry.SHADELESS, false)
                .component(CSRegistry.REINFORCED, false)
                .component(CSRegistry.FLUIDLOGGABLE, false)
                .component(CSRegistry.TRANSPARENT, false)
                .component(CSRegistry.REDSTONE, false)
                .component(CSRegistry.CONDUCTOR, false)
                .component(CSRegistry.LIGHT, CSProperties.Light.NONE)
                .component(CSRegistry.GHOST, CSProperties.Ghost.BLOCK_ALL)
                .component(CSRegistry.COLOR, 0xFFFFFF)
                .component(CSRegistry.APPLY_COLORLESS, false)
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
        components.add(CSLanguage.TOOLTIP_COLORLESS.text(
                ChatFormatting.DARK_GRAY,
                ComponentUtils.wrapInSquareBrackets(InputConstants.getKey(GLFW.GLFW_KEY_LEFT_CONTROL, -1).getDisplayName().copy().withStyle(ChatFormatting.GOLD))
        ));
        components.add(CSLanguage.PROPERTY_APPLY_COLORLESS.text(
                ChatFormatting.GRAY,
                stack.getOrDefault(CSRegistry.APPLY_COLORLESS, false) ? CSLanguage.PROPERTY_ENABLED.text(ChatFormatting.DARK_GREEN) : CSLanguage.PROPERTY_DISABLED.text(ChatFormatting.DARK_RED)
        ));
        for (IProperty<?> property : CSProperties.of(stack).properties().values()) {
            components.add(property.text());
        }
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.FAIL;
        if (!(context.getPlayer() instanceof ServerPlayer player)) return InteractionResult.FAIL;
        
        BlockPos blockpos = context.getClickedPos();
        if (!this.applyToBlock(level, blockpos, player)) {
            return InteractionResult.FAIL;
        }
        
        return InteractionResult.SUCCESS;
    }
    
    public boolean applyToBlock(ServerLevel level, BlockPos pos, ServerPlayer player) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CrystalixGlass glass)) return false;
        ItemStack wand = CrystalixWand.find(player);
        state = glass.fromStack(state, wand);
        
        if (player.isShiftKeyDown()) {
            BatchProcessor.schedule(BatchProcessor.Batch.of(player, pos, state, wand));
        } else if (!glass.setEntityProperties(level, pos, state, wand)) {
            level.setBlockAndUpdate(pos, state);
        }
        
        return true;
    }
}
