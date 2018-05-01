package com.osh.patterns.lib.handlers.actions;

import com.osh.patterns.lib.handlers.Callable;
import com.osh.patterns.lib.handlers.data.ErrorHandler;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public interface Command<Result, Data> {
    void execute(Data data, Callable<Result> callable, ErrorHandler errorHandler);
}
