package com.osh.patterns.lib;

import com.osh.patterns.lib.function.Provider;
import com.osh.patterns.lib.function.data.Maybe;

/**
 * Created by Oleg Shatava on 03.05.18.
 */

public class Base<T> implements Provider<Maybe<T>> {
    private T value;

    protected Base(T value) {
        this.value = value;
    }


    @Override
    public Maybe<T> get() {
        return new Maybe<>(value);
    }

    protected T getValue() {
        return value;
    }

    protected void setValue(T value) {
        this.value = value;
    }

}
