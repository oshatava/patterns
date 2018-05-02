package com.osh.patterns.lib.handlers;

/**
 * Created by Oleg Shatava call 30.04.18.
 */

public interface Action<T> {
    void call(T data);
}
