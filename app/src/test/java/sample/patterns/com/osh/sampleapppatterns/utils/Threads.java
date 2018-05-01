package sample.patterns.com.osh.sampleapppatterns.utils;

import com.osh.patterns.lib.handlers.Executor;
import com.osh.patterns.lib.handlers.Provider;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Oleg Shatava on 01.05.18.
 */

public class Threads {
    private final static ScheduledExecutorService mainThread = Executors.newSingleThreadScheduledExecutor();
    private final static ScheduledExecutorService workerThread = Executors.newSingleThreadScheduledExecutor();
    private static Provider<Executor> resultExecutorProvider = () -> (Executor) mainThread::execute;
    private static Provider<Executor> jobExecutorProviderS = () -> (Executor) workerThread::execute;

    public static Provider<Executor> singleThread() {
        return jobExecutorProviderS;
    }

    public static Provider<Executor> mainThread() {
        return resultExecutorProvider;
    }

    public static void sleep(long sec) {
        try {
            Thread.sleep(sec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
