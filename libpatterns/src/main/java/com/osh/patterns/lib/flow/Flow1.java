package com.osh.patterns.lib.flow;

import com.osh.patterns.lib.handlers.Consumer;
import com.osh.patterns.lib.handlers.Executor;
import com.osh.patterns.lib.handlers.Job;
import com.osh.patterns.lib.handlers.Mapper;
import com.osh.patterns.lib.handlers.Provider;
import com.osh.patterns.lib.handlers.data.ErrorConsumer;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public class Flow1<T> {
    private Provider<Executor> jobExecutorProvider;
    private Provider<Executor> resultExecutorProvider;
    private Runnable next;
    private T value;
    private ErrorConsumer errorConsumer;
    private Consumer<T> resultHandler;
    private Runnable onCompleted;
    private Flow1 root;

    private Flow1(T value) {
        this.value = value;
        this.root = this;
    }

    private Flow1(Flow1 root,
                  Provider<Executor> jobExecutorProvider,
                  Provider<Executor> resultExecutorProvider,
                  ErrorConsumer errorConsumer) {
        this.root = root;
        this.jobExecutorProvider = jobExecutorProvider;
        this.resultExecutorProvider = resultExecutorProvider;
        this.errorConsumer = errorConsumer;
    }

    public static <T> Flow1<T> given(T value) {
        return new Flow1<>(value);
    }

    public <R> Flow1<R> next(Job<R, T> job) {
        Flow1<R> item = new Flow1<>(root, jobExecutorProvider, resultExecutorProvider, errorConsumer);
        this.next = makeRun(() -> value, job, item);
        return item;
    }

    public <R> Flow1<R> map(Mapper<R, T> mapper) {
        return next(mapper::map);
    }

    public Flow1<T> executeOn(Provider<Executor> jobExecutorProvider) {
        this.jobExecutorProvider = jobExecutorProvider;
        return this;
    }

    public Flow1<T> resultOn(Provider<Executor> resultExecutorProvider) {
        this.resultExecutorProvider = resultExecutorProvider;
        return this;
    }

    public Flow1<T> onError(ErrorConsumer errorConsumer) {
        this.errorConsumer = errorConsumer;
        return this;
    }

    public Flow1<T> consumer(Consumer<T> resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

    public Flow1<T> onCompleted(Runnable onCompleted) {
        this.onCompleted = onCompleted;
        return this;
    }

    private void notifyOnCompleted() {
        final Executor resultExecutor = resultExecutorProvider.get();
        if (onCompleted != null) {
            resultExecutor.execute(() -> onCompleted.run());
        }
    }

    private void notifyOnResult() {
        final Executor resultExecutor = resultExecutorProvider.get();
        if (resultHandler != null) {
            resultExecutor.execute(() -> resultHandler.accept(value));
        }
    }

    private void notifyOnError(Exception e) {
        final Executor resultExecutor = resultExecutorProvider.get();
        if (errorConsumer != null) {
            resultExecutor.execute(() -> errorConsumer.accept(e));
        }
    }

    private void runNext() {
        if (next != null) {
            next.run();
        }
    }

    private <R> Runnable makeRun(final Provider<T> data, Job<R, T> job, Flow1<R> next) {
        return () -> next.jobExecutorProvider.get().execute(() -> {
            try {
                final R result = job.make(data.get());
                next.value = result;
                next.notifyOnResult();
                next.notifyOnCompleted();
                next.runNext();
            } catch (Exception e) {
                next.notifyOnError(e);
            }
        });
    }

    public void run() {
        root.runNext();
    }
}
