package com.growalong.android.net.retrofit.download;

import com.growalong.android.app.MyApplication;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.IDownloadApis;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yangxing on 2018/3/19.
 * 话题维度的下载，话题有多种
 */

public abstract class BaseTopicDownloadTask implements Runnable {
    private static final String TEM = ".tem";
    private IDownloadApis updateApis;

    protected DownloadTopicCallback listener;//全局监听下载进度的
    protected boolean isPauseItemDownloading;//是否暂停单个item的下载

    protected String unitId;

    public BaseTopicDownloadTask(String unitId) {
        this.unitId = unitId;
        listener = generateListenerWithCallBack();
    }

    /**
     * 子类新建回调接口来监听下载状态，并且需要调用外部回调
     *
     * @return
     */
    protected abstract DownloadTopicCallback generateListenerWithCallBack();


    public String getUnitId() {
        return unitId;
    }

    @Override
    final public void run() {
        if (updateApis == null) {
            updateApis = BaseRetrofitClient.getInstance().getOtherRetrofit(ProgressHelper.addProgress(null).build()).create(IDownloadApis.class);
        }
        realRun();
    }

    protected abstract void realRun();


    protected boolean isDeleteExitTemFile() {
        return true;
    }

    /**
     * 把读取到的数据写入文件
     *
     * @param response
     * @param filePath
     * @return 是否下载完成
     */
    private boolean writeResponseBodyToFile(ResponseBody response, final String filePath, final String id, long hasDownload) {
        LogUtil.e("download topic-> writeResponseBodyToFile start id : " + id);
        File file = new File(filePath + TEM);
        if ((file.exists() && isDeleteExitTemFile()))
            file.deleteOnExit();

        try {
            boolean mkdirs = file.getParentFile().mkdirs();
            boolean newFile = file.createNewFile();
            InputStream is = response.byteStream();
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[8192];
            int len;

            long preTime = System.currentTimeMillis();
            HasDownloadNotifyRunable runnableItemDownloading = new HasDownloadNotifyRunable() {
                @Override
                public void run() {
                    listener.onItemDownloading(id, filePath, notifyhasDownload);
                }
            };
            while (!isPauseItemDownloading && (len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
                hasDownload += len;
                if (listener != null) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - preTime > 1000) {
                        preTime = currentTime;
                        runnableItemDownloading.setHasDownload(hasDownload);
                        MyApplication.runOnUIThread(runnableItemDownloading);
                    }
                }
            }
            fos.close();
            bis.close();
            is.close();
        } catch (final IOException e) {
            e.printStackTrace();

            if (listener != null) {
                MyApplication.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailed(unitId, filePath, e);
                    }
                });
            }
            return false;
        } finally {
            response.close();

            final long finalHasDownload = hasDownload;
            LogUtil.e("downloading size all: " + finalHasDownload);
            if (listener != null) {
                MyApplication.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onItemDownloading(id, filePath, finalHasDownload);
                    }
                });
            }

            LogUtil.e("download topic-> writeResponseBodyToFile finish + id : " + id);
        }


        if (isPauseItemDownloading) {//取消下载的回调
            if (listener != null) {
                MyApplication.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.cancel(id);
                    }
                });

            }
            return false;
        }

        //下载完成后的重命名
        File des = new File(filePath);
        if (des.exists())
            des.deleteOnExit();
        boolean b = file.renameTo(des);
        if (!b) {
            if (listener != null) {
                MyApplication.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        listener.onFailed(unitId, filePath, new IOException("rename file failed"));
                    }
                });
            }
            LogUtil.e("rename file failed");
            return false;
        }
        return true;
    }

    /**
     * 下载单条语音
     *
     * @param url
     * @param savePath
     */
    protected void downloadItem(final String url, final String savePath, final String id, final long hasDownload) {
        if (savePath == null) {
            LogUtil.e("savePath is null");
            return;
        }
        //开始下载
        Observable<ResponseBody> responseBodyObservable;
        if (hasDownload == 0) {
            responseBodyObservable = updateApis.downloadObsv(url);
        } else {
            String range = "bytes=" + hasDownload + "-";
            responseBodyObservable = updateApis.downloadObsvResume(url, range);
        }
        Func1<ResponseBody, Boolean> writeFunc = new Func1<ResponseBody, Boolean>() {
            @Override
            public Boolean call(ResponseBody responseBody) {
                return writeResponseBodyToFile(responseBody, savePath, id, hasDownload);
            }
        };
        CommSubscriber<Boolean> commSubscriber = new CommSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean o) {
                //单个语音下载完成不用切换到主线程
                if (listener != null && o) {
                    listener.onItemDownloadFinishIO(id, url, savePath, o);
                }
            }

            @Override
            public void onFailure(final Throwable e) {
                super.onFailure(e);
                pause();
                if (listener != null) {
                    MyApplication.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailed(unitId, savePath, e);
                        }
                    });

                }
            }
        };
        responseBodyObservable.map(writeFunc).subscribe(commSubscriber);

    }

    public abstract boolean pause();

    /**
     * 下载回调除了onItemDownloadFinishIO都在主线程， 成功或者取消会调用finish，结束了异常会调用onFailed但是不会调用finish
     * unitId 是语音id
     */
    public interface DownloadTopicCallback {

        void cancel(String itemId);

        /**
         *
         * @param itemId
         * @param savePath
         * @param e
         */
        void onFailed(String itemId, String savePath, Throwable e);

        void onItemDownloading(String itemId, String savePath, long hasDownload);

        void onItemDownloadFinishIO(String itemId, String url, String savePath, boolean isSuccess);

    }

    /**
     * 下载音频互动或者音频图文的回调
     */
    public interface DownloadNormalTopicCallback extends DownloadTopicCallback {
        void finish(String itemId, boolean isSuccess);

        void onItemDownloadFinish(String itemId, String url, String savePath);
    }

    public interface DownloadAudioTopicCallback extends DownloadTopicCallback {
        void changeUrl(String url);

        void finish(String itemId, boolean isSuccess);
    }

    public abstract class HasDownloadNotifyRunable implements Runnable {
        long notifyhasDownload;

        public void setHasDownload(long hasDownload) {
            this.notifyhasDownload = hasDownload;
        }
    }
}
