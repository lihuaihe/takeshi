package com.takeshi.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 线程相关工具类.
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class TakeshiThreadUtil {

    /**
     * 构造函数
     */
    private TakeshiThreadUtil() {
    }

    /**
     * sleep等待,单位为毫秒
     *
     * @param milliseconds 等待时间（毫秒）
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * 停止线程池
     * 先使用shutdown, 停止接收新任务并尝试完成所有已存在任务.
     * 如果超时, 则调用shutdownNow, 取消在workQueue中Pending的任务,并中断所有阻塞函数.
     * 如果仍然超時，則強制退出.
     * 另对在shutdown时线程本身被调用中断做了处理.
     *
     * @param pool    pool
     * @param timeout 定时任务关闭的最大超时时间（单位：秒）
     */
    public static void shutdownAndAwaitTermination(ExecutorService pool, long timeout) {
        if (pool != null && !pool.isShutdown()) {
            log.info("Close the background task in the task thread pool...");
            //            if (pool instanceof ScheduledThreadPoolExecutor scheduledThreadPoolExecutor) {
            //                BlockingQueue<Runnable> queue = scheduledThreadPoolExecutor.getQueue();
            //                queue.removeIf(item -> {
            //                    if (item instanceof RunnableScheduledFuture<?> runnableScheduledFuture) {
            //                        long delay = runnableScheduledFuture.getDelay(TimeUnit.SECONDS);
            //                        return delay > timeout;
            //                    }
            //                    return false;
            //                });
            //            }
            pool.shutdown();
            try {
                if (!pool.awaitTermination(timeout, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                    if (!pool.awaitTermination(timeout, TimeUnit.SECONDS)) {
                        log.info("Pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 打印线程异常信息
     *
     * @param r r
     * @param t t
     */
    public static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            log.error(t.getMessage(), t);
        }
    }

}
