package com.osh.patterns.lib.chain;

import com.osh.patterns.lib.annotations.NonNull;
import com.osh.patterns.lib.handlers.Mapper;
import com.osh.patterns.lib.handlers.actions.RequestHandler;
import com.osh.patterns.lib.handlers.data.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg Shatava call 30.04.18.
 */

public class ChainOfResponsibility<A, B> {

    private final List<ChainItem<A, B>> chainItems = new ArrayList<>();
    private final ErrorHandler onError;

    private ChainOfResponsibility(@NonNull List<ChainItem<A, B>> chainItems, ErrorHandler onError) {
        this.onError = onError;
        this.chainItems.addAll(chainItems);
    }

    @NonNull
    public static <A, B> Builder<A, B> builder() {
        return new Builder<>();
    }

    public A handle(B request) {
        for (ChainItem<A, B> chainItem : chainItems) {
            try {
                if (chainItem.handler.map(request)) {
                    return chainItem.processor.map(request);
                }
            } catch (Exception e) {
                if (onError != null) {
                    onError.call(e);
                    break;
                } else {
                    throw e;
                }
            }
        }
        return null;
    }

    public static class Builder<A, B> {

        private final List<ChainItem<A, B>> chainItems = new ArrayList<>();
        private Mapper<A, B> defaultItemProcessor;
        private ErrorHandler onError;

        public Builder<A, B> first(@NonNull final RequestHandler<B> handler,
                                   @NonNull final Mapper<A, B> processor) {
            chainItems.clear();
            return next(handler, processor);
        }

        public Builder<A, B> next(@NonNull final RequestHandler<B> handler,
                                  @NonNull final Mapper<A, B> processor) {
            chainItems.add(new ChainItem<>(handler, processor));
            return this;
        }

        public Builder<A, B> defaultProcessor(Mapper<A, B> defaultItemProcessor) {
            this.defaultItemProcessor = defaultItemProcessor;
            return this;
        }

        public Builder<A, B> onError(ErrorHandler onError) {
            this.onError = onError;
            return this;
        }

        public ChainOfResponsibility<A, B> build() {
            if (defaultItemProcessor != null) {
                next(request -> true,
                        defaultItemProcessor
                );
            }
            return new ChainOfResponsibility<>(chainItems, onError);
        }
    }

    private static class ChainItem<A, B> {
        @NonNull
        private final RequestHandler<B> handler;
        @NonNull
        private final Mapper<A, B> processor;

        public ChainItem(@NonNull RequestHandler<B> handler,
                         @NonNull Mapper<A, B> processor) {
            this.handler = handler;
            this.processor = processor;
        }
    }
}
