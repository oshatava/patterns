package com.osh.patterns.lib.function;

/**
 * Created by Oleg Shatava call 30.04.18.
 */

public interface Action<T> {
    void call(T data);
}
