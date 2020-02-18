package ru.artfect.wynnlang;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Multithreading {

    static ExecutorService POOL = Executors.newCachedThreadPool(new ThreadFactory() {
        AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, String.format("Thread %s", counter.incrementAndGet()));
        }
    });

    private static ScheduledExecutorService RUNNABLE_POOL = Executors.newScheduledThreadPool(3, new ThreadFactory() {
        private AtomicInteger counter = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "Thread " + counter.incrementAndGet());
        }
    });

    public static void schedule(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        RUNNABLE_POOL.scheduleAtFixedRate(runnable, initialDelay, delay, unit);
    }

    static void runAsync(Runnable runnable) {
        POOL.execute(runnable);
    }

    public static int getTotal() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) Multithreading.POOL;
        return tpe.getActiveCount();
    }

}