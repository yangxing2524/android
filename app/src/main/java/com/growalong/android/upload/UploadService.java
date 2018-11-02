package com.growalong.android.upload;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.growalong.android.model.UploadModel;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class UploadService extends Service {
    private MyBinder myBinder;
    private UploadThreadPoolHelper mUploadImageHelper;

    public UploadService() {
        mUploadImageHelper = new UploadThreadPoolHelper();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myBinder = new MyBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public LinkedBlockingQueue getQueue() {
        return mUploadImageHelper.getmQueue();
    }

    public UploadTask getRunnableTask() {
        return mUploadImageHelper.getmRunningTask();
    }

    /**
     * 上传
     */
    public void startUpload(UploadModel uploadModel, IUploadCallBack callBack) {
        mUploadImageHelper.startUpload(uploadModel, callBack);
    }

    /**
     * 撤回
     */
    public void removeUpload(UploadModel uploadModel) {
        mUploadImageHelper.removeUpload(uploadModel);
    }
    /**
     * 撤回
     */
    public void removeUpload(int id) {
        mUploadImageHelper.removeUpload(id);
    }

    /**
     * 取消所有上传
     */
    public List<UploadModel> stopAllUpload() {
        return mUploadImageHelper.stopAllUpload();
    }

    public class MyBinder extends Binder {
        public UploadService getService() {
            return UploadService.this;
        }
    }
}
