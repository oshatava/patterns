package com.osh.patterns.lib.flow;

import com.osh.patterns.lib.handlers.Callable;
import com.osh.patterns.lib.handlers.Executor;
import com.osh.patterns.lib.handlers.Job;
import com.osh.patterns.lib.handlers.Provider;
import com.osh.patterns.lib.handlers.data.ErrorHandler;

/**
 * Created by Oleg Shatava on 30.04.18.
 */

public class Flow<Result, Data> implements Callable<Data> {
    private final Job<Result, Data> job;
    private Callable<Result> resultHandler;
    private Provider<Executor> jobExecutorProvider;
    private Provider<Executor> resultExecutorProvider;
    private ErrorHandler errorHandler;
    private Runnable onCompleted;
    private Flow root;
    private Callable<Result> next;

    private Flow(Job<Result, Data> job, Flow root,
                 Provider<Executor> jobExecutorProvider,
                 Provider<Executor> resultExecutorProvider,
                 ErrorHandler errorHandler) {
        this.root = root;
        this.job = job;
        this.jobExecutorProvider = jobExecutorProvider;
        this.resultExecutorProvider = resultExecutorProvider;
        this.errorHandler = errorHandler;
    }

    public Flow(Job<Result, Data> job) {
        this.job = job;
        this.root = this;
    }

    public static <T, Data> Flow<T, Data> first(Job<T, Data> job) {
        Flow<T, Data> item = new Flow<>(job);
        return item;
    }

    public Flow<Result, Data> onError(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
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

    public Flow<Result, Data> onData(Callable<Result> resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }

    public Flow<Result, Data> onCompleted(Runnable onCompleted) {
        this.onCompleted = onCompleted;
        return this;
    }

    public <T> Flow<T, Result> next(Job<T, Result> job) {
        Flow<T, Result> item = new Flow<>(job, root, jobExecutorProvider,
                resultExecutorProvider, errorHandler);
        this.next = item;
        return item;
    }

    @Override
    public void call(Data data) {
        run(data);
    }

    @SuppressWarnings("unchecked")
    public <T> Callable<T> getCall() {
        return (Callable<T>) root;
    }

    private void run(final Data data) {
        final Executor jobExecutor = jobExecutorProvider.get();
        final Executor resultExecutor = resultExecutorProvider.get();
        jobExecutor.execute(() -> {
            try {
                Result result = job.make(data);
                if (next != null) {
                    next.call(result);
                }
                if (resultHandler != null) {
                    resultExecutor.execute(() -> resultHandler.call(result));
                }
            } catch (Exception e) {
                if (errorHandler != null)
                    resultExecutor.execute(() -> errorHandler.call(e));
                else
                    throw e;
            }
            if (onCompleted != null) {
                resultExecutor.execute(() -> onCompleted.run());
            }
        });
    }

}
