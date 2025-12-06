package dev.satherov.crystalix.common.properties;

import dev.satherov.crystalix.client.lang.CSTranslatable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public interface IProperty<V> extends CSTranslatable {
    
    ResourceLocation location();
    
    DataComponentType<V> type();
    
    V value();
    
    V next(boolean forward);
    
    V set(V value);
    
    @Override
    default MutableComponent text() {
        MutableComponent component = Component.empty();
        component.append(this.name().withStyle(ChatFormatting.GRAY));
        component.append(Component.literal(": ").withStyle(ChatFormatting.GRAY));
        component.append(this.display());
        return component;
    }
    
    MutableComponent name();
    
    MutableComponent display();
    
    MutableComponent tooltip();
}
