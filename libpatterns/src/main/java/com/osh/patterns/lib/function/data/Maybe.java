package com.osh.patterns.lib.function.data;


import com.osh.patterns.lib.function.Provider;

/**
 * Created by Oleg Shatava on 03.05.18.
 */

public class Maybe<T> implements Provider<T> {
    private final T value;

    public Maybe(T value) {
        this.value = value;
    }

    public boolean has() {
        return value != null;
    }

    @Override
    public T get() {
        return value;
    }
}
