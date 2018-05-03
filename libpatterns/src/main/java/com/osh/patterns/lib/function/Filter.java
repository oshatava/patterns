package com.osh.patterns.lib.function;

/**
 * Created by Oleg Shatava on 03.05.18.
 */

public interface Filter<T> {
    boolean match(T value);
}
