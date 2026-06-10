package dev.satherov.crystalix.common.item;

import dev.satherov.crystalix.CXConfig;
import dev.satherov.crystalix.CrystalixClient;
import dev.satherov.crystalix.client.lang.CXLanguage;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.common.properties.GhostState;
import dev.satherov.crystalix.common.properties.GlassMaterial;
import dev.satherov.crystalix.common.properties.LightState;
import dev.satherov.crystalix.core.registry.CXProperties;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.common.item.SLItem;
import dev.satherov.sathlib.common.item.SLItemProperties;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.network.chat.SLComponent;
import dev.satherov.sathlib.util.deferred.SLBlockCrawler;
import dev.satherov.sathlib.util.deferred.SLDeferredTasks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

@NothingNull
public class CrystalixWandItem extends SLItem {
    
    public CrystalixWandItem(SLItemProperties properties) {
        super(properties
                .stacksTo(1)
                // Block State
                .component(CXRegistry.INVISIBLE, false)
                .component(CXRegistry.LIGHT, LightState.NONE)
                .component(CXRegistry.GHOST, GhostState.BLOCK_ALL)
                // Block Entity - Model
                .component(CXRegistry.COLOR, 0xFFFFFF)
                .component(CXRegistry.MATERIAL, GlassMaterial.defaultMaterial())
                .component(CXRegistry.SHADELESS, false)
                .component(CXRegistry.TINTED, false)
                // Block Entity
                .component(CXRegistry.REINFORCED, false)
                .component(CXRegistry.WATERLOGGABLE, false)
                .component(CXRegistry.CONDUCTOR, false)
                .component(CXRegistry.REDSTONE, 0)
                // Other
                .component(CXRegistry.APPLY_COLORLESS, false)
        );
    }
    
    public static ItemStack find(Player player) {
        final ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof CrystalixWandItem) return main;
        final ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof CrystalixWandItem) return off;
        return ItemStack.EMPTY;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(CXLanguage.TOOLTIP_BULK.translate(ChatFormatting.DARK_GRAY));
        builder.accept(CXLanguage.TOOLTIP_PICK_PROPERTY.translate(
                ChatFormatting.DARK_GRAY,
                SLComponent.squareBrackets(SLComponent.key(CrystalixClient.PICK_BLOCK.getKey()).style(ChatFormatting.GOLD)))
        );
        builder.accept(CXLanguage.TOOLTIP_COLORLESS.translate(
                ChatFormatting.DARK_GRAY,
                SLComponent.squareBrackets(SLComponent.key(CrystalixClient.TOGGLE_COLORLESS.getKey()).style(ChatFormatting.GOLD)))
        );
        CXProperties.CONTAINER.forEach(property -> builder.accept(SLComponent.empty()
                .append(property.getName().translate(ChatFormatting.GRAY))
                .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
                .append(property.displayItemValue(stack, CXRegistry.CRYSTALIX_BLOCK.get().defaultBlockState())))
        );
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.CONSUME;
        if (!(context.getPlayer() instanceof ServerPlayer player)) return InteractionResult.CONSUME;
        
        final ItemStack stack = context.getItemInHand();
        final BlockPos blockPos = context.getClickedPos();
        final BlockState blockState = level.getBlockState(blockPos);
        
        if (player.isShiftKeyDown()) {
            // Make sure we use the stack at the time of the click and 
            // don't reference the mutable object in the players inventory
            final ItemStack copy = stack.copy();
            SLDeferredTasks.register(
                    SLBlockCrawler.builder(level, blockPos)
                            .predicate((pos, state) -> {
                                if (!state.is(CXRegistry.CRYSTALIX_BLOCK_TAG)) return false;
                                final CrystalixGlassBlockEntity entity = CXRegistry.GLASS_BLOCK_ENTITY.get().getBlockEntity(level, pos);
                                if (entity == null) return false;
                                return !CXProperties.CONTAINER.matches(copy, state, entity);
                            })
                            .consumer((pos, state) -> CrystalixWandItem.updateBlock(level, copy, pos, state))
                            .iterations(CXConfig.Common.getMaxBlockEditsPerTick())
                            .stopCondition(c -> c.visitedCount() > CXConfig.Common.getMaxBlockEdits())
                            .build()
            );
        }
        
        return CrystalixWandItem.updateBlock(level, stack, blockPos, blockState) ? InteractionResult.SUCCESS_SERVER : InteractionResult.FAIL;
    }
    
    private static boolean updateBlock(ServerLevel level, ItemStack stack, BlockPos pos, BlockState state) {
        final CrystalixGlassBlockEntity entity = CXRegistry.GLASS_BLOCK_ENTITY.get().getBlockEntity(level, pos);
        if (!state.is(CXRegistry.CRYSTALIX_BLOCK_TAG) || entity == null) return false;
        
        BlockState updated = CXProperties.CONTAINER.applyToBlock(stack, state, entity).state();
        level.setBlockAndUpdate(pos, updated);
        return true;
    }
}
