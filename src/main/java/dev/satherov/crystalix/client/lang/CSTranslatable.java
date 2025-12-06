package dev.satherov.crystalix.client.lang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public interface CSTranslatable {
    
    /**
     * Returns the translation key of the given entry;
     *
     * @return translation key
     */
    String key();
    
    /**
     * Returns the English translation of the given entry;
     *
     * @return english translation
     */
    String translation();
    
    /**
     * Returns a {@link MutableComponent} holding the translation key of the given entry;
     *
     * @return Translatable component
     */
    default MutableComponent text() {
        return Component.translatable(this.key());
    }
    
    /**
     * Returns a {@link MutableComponent} holding the translation key of the given entry taking in additional arguments
     * Useful for formatting strings as such {@code text("%s is %s", "a", "b")} would return {@code "a is b"}
     * <p>
     * Will also be able to
     *
     * @param args Additional Arguments. All entries must either be a {@link String}, {@link Number}, {@link Boolean}, {@link Component} or {@link ChatFormatting}
     *
     * @return Translatable component
     */
    default MutableComponent text(Object... args) {
        List<ChatFormatting> formatting = new ArrayList<>();
        List<Object> filtered = new ArrayList<>();
        
        for (Object arg : args) {
            if (arg == null) continue;
            if (arg instanceof ChatFormatting format) {
                formatting.add(format);
            } else {
                filtered.add(arg);
            }
        }
        
        MutableComponent component = Component.translatable(this.key());
        
        if (!filtered.isEmpty()) {
            component = Component.translatable(this.key(), filtered.toArray());
        }
        
        if (!formatting.isEmpty()) {
            component = component.withStyle(formatting.toArray(new ChatFormatting[0]));
        }
        
        return component;
    }
}
