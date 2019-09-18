package org.apache.rocketmq.proxy.utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolFactory {
    public static ThreadPoolExecutor createThreadPoolExecutor(int core, int max, final String threadName) {
        return new ThreadPoolExecutor(core, max,
            10 * 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1000), new ThreadFactory() {

            private AtomicInteger seq = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                seq.incrementAndGet();
                Thread t = new Thread(r, threadName + seq.get());
                t.setDaemon(true);
                return t;
            }
        });
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(int core, int max, final String threadName,
        final boolean isDaemon) {
        return new ThreadPoolExecutor(core, max,
            10 * 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1000), new ThreadFactory() {

            private AtomicInteger seq = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                seq.incrementAndGet();
                Thread t = new Thread(r, threadName + seq.get());
                t.setDaemon(isDaemon);
                return t;
            }
        });
    }

    public static ThreadPoolExecutor createThreadPoolExecutor(int core, int max, BlockingQueue blockingQueue,
        final String threadName, final boolean isDaemon) {
        return new ThreadPoolExecutor(core, max,
            10 * 1000, TimeUnit.MILLISECONDS, blockingQueue, new ThreadFactory() {

            private AtomicInteger seq = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                seq.incrementAndGet();
                Thread t = new Thread(r, threadName + seq.get());
                t.setDaemon(isDaemon);
                return t;
            }
        });
    }

    public static ScheduledExecutorService createSingleScheduledExecutor(final String threadName) {
        return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            private AtomicInteger ai = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, threadName + ai.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public static ScheduledExecutorService createScheduledExecutor(int core, final String threadName) {
        return Executors.newScheduledThreadPool(core, new ThreadFactory() {
            private AtomicInteger ai = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, threadName + ai.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public static ScheduledExecutorService createScheduledExecutor(int core, final String threadName,
        final boolean isDaemon) {
        return Executors.newScheduledThreadPool(core, new ThreadFactory() {
            private AtomicInteger ai = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, threadName + ai.incrementAndGet());
                thread.setDaemon(isDaemon);
                return thread;
            }
        });
    }
}
