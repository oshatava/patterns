package com.osh.patterns.lib.function;

/**
 * Created by Oleg Shatava call 30.04.18.
 */
public interface Job<Result, Data> {
    Result make(Data data);
}
