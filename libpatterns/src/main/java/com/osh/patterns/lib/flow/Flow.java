package com.osh.patterns.lib.flow;

import com.osh.patterns.lib.function.Action;
import com.osh.patterns.lib.function.Consumer;
import com.osh.patterns.lib.function.Executor;
import com.osh.patterns.lib.function.Job;
import com.osh.patterns.lib.function.Provider;
import com.osh.patterns.lib.function.data.ErrorConsumer;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public class Flow<Result, Data> implements Action<Data> {
    private final Job<Result, Data> job;
    private Provider<Executor> jobExecutorProvider;
    private Provider<Executor> resultExecutorProvider;
    private ErrorConsumer errorConsumer;
    private Consumer<Result> resultHandler;
    private Runnable onCompleted;
    private Flow root;
    private Action<Result> next;

    private Flow(Job<Result, Data> job, Flow root,
                 Provider<Executor> jobExecutorProvider,
                 Provider<Executor> resultExecutorProvider,
                 ErrorConsumer errorConsumer) {
        this.root = root;
        this.job = job;
        this.jobExecutorProvider = jobExecutorProvider;
        this.resultExecutorProvider = resultExecutorProvider;
        this.errorConsumer = errorConsumer;
    }

    public Flow(Job<Result, Data> job) {
        this.job = job;
        this.root = this;
    }

    public static <T, Data> Flow<T, Data> first(Job<T, Data> job) {
        Flow<T, Data> item = new Flow<>(job);
        return item;
    }

    public Flow<Result, Data> onError(ErrorConsumer errorConsumer) {
        this.errorConsumer = errorConsumer;
        return this;
    }

    public Flow<Result, Data> executeOn(Provider<Executor> jobExecutorProvider) {
        this.jobExecutorProvider = jobExecutorProvider;
        return this;
    }

    public Flow<Result, Data> resultOn(Provider<Executor> resultExecutorProvider) {
        this.resultExecutorProvider = resultExecutorProvider;
        return this;
    }

    public Flow<Result, Data> onData(Consumer<Result> resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

    public Flow<Result, Data> onCompleted(Runnable onCompleted) {
        this.onCompleted = onCompleted;
        return this;
    }

    public <T> Flow<T, Result> next(Job<T, Result> job) {
        Flow<T, Result> item = new Flow<>(job, root, jobExecutorProvider,
                resultExecutorProvider, errorConsumer);
        this.next = item;
        return item;
    }

    @Override
    public void call(Data data) {
        run(data);
    }

    @SuppressWarnings("unchecked")
    public <T> Action<T> getCall() {
        return (Action<T>) root;
    }

    private void run(final Data data) {
        final Executor jobExecutor = jobExecutorProvider.get();
        final Executor resultExecutor = resultExecutorProvider.get();
        jobExecutor.execute(() -> {
            try {
                Result result = job.make(data);
                if (resultHandler != null) {
                    resultExecutor.execute(() -> resultHandler.accept(result));
                }
                if (next != null) {
                    next.call(result);
                }
            } catch (Exception e) {
                if (errorConsumer != null)
                    resultExecutor.execute(() -> errorConsumer.accept(e));
                else
                    throw e;
            }
            if (onCompleted != null) {
                resultExecutor.execute(() -> onCompleted.run());
            }
        });
    }

}
