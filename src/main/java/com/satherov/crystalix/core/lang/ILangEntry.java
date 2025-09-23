package com.satherov.crystalix.core.lang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface ILangEntry {
    
    String getTranslationKey();
    
    default MutableComponent translate() {
        return Component.translatable(getTranslationKey());
    }
    
    default MutableComponent translate(Object... args) {
        return Component.translatable(getTranslationKey(), args);
    }
    
    default MutableComponent translateFormatted(ChatFormatting... formats) {
        return Component.translatable(getTranslationKey()).withStyle(formats);
    }
}
