package com.growalong.android.upload;

import com.growalong.android.model.UploadModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by gangqing on 2016/12/20.
 */

public class UploadThreadPoolHelper {
    private ThreadPoolExecutor executorService;
    private UploadTask mRunningTask;
    private LinkedBlockingQueue mQueue;

    public UploadThreadPoolHelper() {
        mQueue = new LinkedBlockingQueue<UploadTask>();
        executorService = new UploadThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                mQueue);
    }

    public UploadTask getmRunningTask() {
        return mRunningTask;
    }

    public LinkedBlockingQueue getmQueue() {
        return mQueue;
    }

    /**
     * 上传
     */
    public void startUpload(UploadModel uploadModel, IUploadCallBack callBack) {
        UploadTask task = new UploadTask(uploadModel, callBack);
        executorService.execute(task);
    }


    /**
     * 撤回
     */
    public void removeUpload(int id) {
        BlockingQueue<Runnable> queue = executorService.getQueue();
        Iterator<Runnable> iterator = queue.iterator();
        while (iterator.hasNext()) {
            UploadTask next = (UploadTask) iterator.next();
            if (id == next.getUploadModel().getId()) {
//                executorService.remove(next);
                iterator.remove();
                return;
            }
        }
        if (mRunningTask != null) {
            mRunningTask.cancel();
        }
    }

    /**
     * 撤回
     */
    public void removeUpload(UploadModel uploadModel) {
        if (uploadModel == null) {
            return;
        }
        removeUpload(uploadModel.getId());
    }

    /**
     * 取消所有上传
     */
    public List<UploadModel> stopAllUpload() {
        Iterator<Runnable> iterator = executorService.getQueue().iterator();
        List<UploadModel> uploadModelList = new ArrayList<>();
        while (iterator.hasNext()) {
            UploadTask uploadTask = (UploadTask) iterator.next();
            uploadModelList.add(uploadTask.getUploadModel());
        }
        executorService.getQueue().clear();
        if (mRunningTask != null) {
            mRunningTask.cancel();
        }
        return uploadModelList;
    }

    public class UploadThreadPoolExecutor extends ThreadPoolExecutor {
        public UploadThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            mRunningTask = (UploadTask) r;
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if (mRunningTask == r)
                mRunningTask = null;
        }
    }
}
