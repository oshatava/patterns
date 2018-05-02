package com.osh.patterns.lib.handlers.actions;

import com.osh.patterns.lib.handlers.Consumer;
import com.osh.patterns.lib.handlers.data.ErrorConsumer;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public interface Command<Result, Data> {
    void execute(Data data, Consumer<Result> resultConsumer, ErrorConsumer errorConsumer);
}
