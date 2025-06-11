package com.satherov.crystalix.content.properties;

public interface IProperty<T> {

    String getKey();

    String getValueString();

    void setValueString(String val);

    void set(T val);

    T get();

    T next(boolean dir);

    default T next() {
        return next(true);
    }
}
