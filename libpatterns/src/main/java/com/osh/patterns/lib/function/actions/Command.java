package com.osh.patterns.lib.function.actions;

import com.osh.patterns.lib.function.Consumer;
import com.osh.patterns.lib.function.data.ErrorConsumer;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public interface Command<Result, Data> {
    void execute(Data data, Consumer<Result> resultConsumer, ErrorConsumer errorConsumer);
}
