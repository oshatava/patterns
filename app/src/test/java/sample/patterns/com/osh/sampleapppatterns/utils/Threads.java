package sample.patterns.com.osh.sampleapppatterns.utils;

import com.osh.patterns.lib.handlers.Executor;
import com.osh.patterns.lib.handlers.Provider;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Oleg Shatava on 01.05.18.
 */

public class Threads {
    private static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
    private static final ExecutorService workerThread = Executors.newFixedThreadPool(8);
    private static final Provider<Executor> jobExecutorProviderS = () -> (Executor) workerThread::execute;
    private static final Provider<Executor> resultExecutorProvider = () -> (Executor) queue::add;

    private Threads() {
        throw new IllegalStateException("Not intended for create new instance");
    }

    public static Provider<Executor> singleThread() {
        return jobExecutorProviderS;
    }

    public static Provider<Executor> mainThread() {
        return resultExecutorProvider;
    }

    public static void sleep(long msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void loop(long workTime) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < workTime) {
            sleep(0);
            if (queue.isEmpty())
                continue;
            try {
                Runnable task = queue.take();
                task.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
