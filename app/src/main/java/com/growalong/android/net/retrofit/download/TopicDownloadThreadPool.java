package com.growalong.android.net.retrofit.download;

import com.growalong.android.util.LogUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by yangxing on 2018/3/21.
 */

public class TopicDownloadThreadPool {

    private DownloadThreadPoolExecutor threadPool;

    private HashMap<String, BaseTopicDownloadTask> runnablePool = new HashMap<>();

    public TopicDownloadThreadPool() {
        threadPool = new DownloadThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        threadPool.setiAfterExecute(new DownloadThreadPoolExecutor.IAfterExecute() {
            @Override
            public void afterExecute(Runnable r, Throwable t) {
                if (t != null) {
                    LogUtil.e("download topic->" + t.getMessage());
                }
                Iterator iter = runnablePool.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = (String) entry.getKey();
                    BaseTopicDownloadTask val = (BaseTopicDownloadTask) entry.getValue();
                    if (val == r) {
                        runnablePool.remove(key);
                        return;
                    }
                }
            }
        });
    }

    /**
     * 判断是否存在unitId相同的task
     *
     * @param unitId
     * @return
     */
    public boolean isContainTask(String unitId) {
        if (unitId == null) {
            return false;
        }
        return runnablePool.get(unitId) != null;
    }

    public void execute(BaseTopicDownloadTask runnable) {
        synchronized (this) {
            runnablePool.put(runnable.getUnitId(), runnable);
        }
        LogUtil.e("download topic-> runnablePool size : " + runnablePool.size());
        threadPool.execute(runnable);
    }

    /**
     * 返回正在执行的下载，不包括等待队列
     *
     * @return
     */
    public BaseTopicDownloadTask getRunningTask() {
        List<Runnable> runningThread = threadPool.getRunningThread();
        if (runningThread != null && runningThread.size() > 0) {
            return (BaseTopicDownloadTask) runningThread.get(0);
        } else {
            return null;
        }
    }

    public void stopAll() {
        runnablePool.clear();
        for (Runnable runnable : threadPool.getQueue()) {
            threadPool.remove(runnable);
        }
        if (getRunningTask() != null) {
            getRunningTask().pause();
        }
        LogUtil.e("download topic-> stopAll runnablePool size : " + runnablePool.size());
    }

    public void removeTask(String unitId) {
        runnablePool.remove(unitId);
        LogUtil.e("download topic-> removeTask runnablePool size : " + runnablePool.size());
    }
}
