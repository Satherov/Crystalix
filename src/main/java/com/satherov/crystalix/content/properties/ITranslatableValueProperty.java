package com.satherov.crystalix.content.properties;

import net.minecraft.network.chat.MutableComponent;

import com.satherov.crystalix.core.lang.ITranslatable;

public interface ITranslatableValueProperty<T extends ITranslatable> extends ITranslatableProperty<T> {
    
    @Override
    default MutableComponent getTranslation() {
        return get().getTranslation();
    }
}



