package com.satherov.crystalix.content.properties;

import net.minecraft.network.chat.MutableComponent;

public interface ITranslatableProperty<T> extends IProperty<T> {

    MutableComponent getTranslation();
}


