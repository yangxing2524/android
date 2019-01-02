package com.growalong.android.net.retrofit.download;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangxing on 2018/3/23.
 */

public class DownloadThreadPoolExecutor extends ThreadPoolExecutor {
    private List<Runnable> runningThreadList = new ArrayList<>();

    private IAfterExecute iAfterExecute;

    public DownloadThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public DownloadThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public DownloadThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public DownloadThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        super.beforeExecute(t, r);
        runningThreadList.add(r);
    }

    public List<Runnable> getRunningThread() {
        return runningThreadList;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        runningThreadList.remove(r);
        if (iAfterExecute != null) {
            iAfterExecute.afterExecute(r, t);
        }
    }

    public interface IAfterExecute {
        void afterExecute(Runnable r, Throwable t);
    }

    public void setiAfterExecute(IAfterExecute iAfterExecute) {
        this.iAfterExecute = iAfterExecute;
    }
}
