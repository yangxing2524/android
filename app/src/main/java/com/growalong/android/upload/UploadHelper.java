package com.growalong.android.upload;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.growalong.android.model.ApiException;
import com.growalong.android.model.UploadModel;
import com.growalong.android.util.LogUtil;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by gangqing on 2016/12/20.
 */

public class UploadHelper {
    private static final int SUCCESS = 1;
    private static final int FAIL = 2;
    private UploadService mUploadService;
    private static UploadHelper mInstance;
    //    private Handler mHandler;
//    private ITopicApis mTopicApis;

    private boolean isBind = false;

    private Map<String, IUploadCallBack> mUploadCallBackMap = new HashMap<>();
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mUploadService = ((UploadService.MyBinder) service).getService();
            LogUtil.d("bindService_" + (isBind ? "1" : "2") + "_onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mUploadService = null;
            LogUtil.d("bindService_" + (isBind ? "1" : "2") + "_onServiceDisconnected");
        }
    };

    private Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            UploadModel uploadModel = (UploadModel) msg.obj;
            IUploadCallBack uploadCallBack = mUploadCallBackMap.get(uploadModel.getTopicId());
            switch (msg.what) {
                case SUCCESS:
                    if (uploadCallBack != null) {
                        uploadCallBack.onSuccess(uploadModel);
                    }
                    break;
                case FAIL:
                    if (uploadCallBack != null) {
                        uploadCallBack.onFailure(uploadModel, null);
                    }
                    break;
            }
        }
    };

    private UploadHelper() {
//        new HandlerThread("upload") {
//            @Override
//            public void run() {
//                Looper.prepare();
//                mHandler = new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        uploadToService((UploadModel) msg.obj);
//                    }
//                };
//                Looper.loop();
//            }
//        }.start();
    }

    private void uploadToService(UploadModel uploadModel) {
//        if (mTopicApis == null) {
//            mTopicApis = BaseRetrofitClient.getInstance().create(ITopicApis.class);
//        }
//        String content;
//        if (UploadModel.TYPE_MIC_TEXT.equals(uploadModel.getFileType()) || UploadModel.TYPE_TEXT.equals(uploadModel.getFileType()) || UploadModel.TYEP_VIDEO.equals(uploadModel.getFileType())) {
//            content = uploadModel.getContent();
//        } else {
//            content = uploadModel.getOssFilePath();
//            if (TextUtils.isEmpty(content)) {
////                DbHelper.getInstance().delUploadFileSync(String.valueOf(uploadModel.getId()));
//                return;
//            }
//        }

//        TopicSendContentParams topicSendContentParams = new TopicSendContentParams(
//                "", content, "N",
//                String.valueOf(uploadModel.getSecond()), uploadModel.getTopicId(), uploadModel.getFileId(),
//                uploadModel.getFileType(), uploadModel.getUniqueId(), uploadModel.getLiveId());
//        BaseParams baseParams = new BaseParams(topicSendContentParams);
//        try {
//            final BaseGenericModel<BaseModel> model = mTopicApis.sendTopicMessage(baseParams).execute().body();
//            if (model == null) {
//                stopAllUpload();
//                Message obtain = Message.obtain();
//                obtain.obj = uploadModel;
//                obtain.what = FAIL;
//                mMainHandler.sendMessage(obtain);
//                return;
//            }
//            int code = model.getState().getCode();
//            if (code == HttpStatusCode.STATUS_SUCCESS) {
////                DbHelper.getInstance().delUploadFileSync(String.valueOf(uploadModel.getId()));
//                uploadModel.setServerId(model.getData().getId());
//                Message obtain = Message.obtain();
//                obtain.obj = uploadModel;
//                obtain.what = SUCCESS;
//                mMainHandler.sendMessage(obtain);
//            } else if (HttpStatusCode.STATUS_NO_LOGIN == code || HttpStatusCode.STATUS_SID_LOST == code || HttpStatusCode.STATUS_ALREADY_LOGIN == code) {
//                startLogin();
//                stopAllUpload();
//                Message obtain = Message.obtain();
//                obtain.obj = uploadModel;
//                obtain.what = FAIL;
//                mMainHandler.sendMessage(obtain);
//            } else if (STATUS_CLOSE == code) {
//                MyApplication.runOnUIThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (model.getState() != null) {
//                            ToastUtil.shortShow(model.getState().getMsg());
//                        } else {
//                            ToastUtil.shortShow("为响应国家监管政策，更进一步保证直播平台的内容安全\\n凌晨0：00-7:00暂不支持内容发布");
//                        }
//                    }
//                });
//                stopAllUpload();
//                Message obtain = Message.obtain();
//                obtain.obj = uploadModel;
//                obtain.what = FAIL;
//                mMainHandler.sendMessage(obtain);
//            } else {
////                if (model.getState() != null) {
////                    ToastUtil.shortShow(model.getState().getMsg());
////                } else {
////                    ToastUtil.shortShow("发送失败");
////                }
//                stopAllUpload();
//                Message obtain = Message.obtain();
//                obtain.obj = uploadModel;
//                obtain.what = FAIL;
//                mMainHandler.sendMessage(obtain);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            boolean isNetError = e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof SocketException;
//            if (!isNetError) {
//                LogModel logModel = LogModel.getLogModel("NewTopicDetailActivity", System.currentTimeMillis() + "", "upload_file_service---" + e.getMessage(),
//                        new BaseParams<>(new NoDataParams()), "");
//                ErrorIncludeModel model = new ErrorIncludeModel(logModel);
//                FileManager.writeLog(MyApplication.getInstance().context, new Gson().toJson(model) + ",", "error.log");
//            }
//            stopAllUpload();
//            Message obtain = Message.obtain();
//            obtain.obj = uploadModel;
//            obtain.what = FAIL;
//            mMainHandler.sendMessage(obtain);
//        }
//        LogUtil.e("uploadDetail:Service");
    }

    public synchronized static UploadHelper getInstance() {
        if (mInstance == null) {
            mInstance = new UploadHelper();
        }
        return mInstance;
    }

    public void upload(List<UploadModel> uploadModelList) {
        if (uploadModelList != null) {
            for (UploadModel uploadModel : uploadModelList) {
                upload(uploadModel);
            }
        }
    }


    /**
     * 上传
     */
    public void upload(final UploadModel uploadModel) {
        //去重
        if (mUploadService != null) {
            //等待队列
            LinkedBlockingQueue linkedBlockingQueue = mUploadService.getQueue();
            Iterator iterator = linkedBlockingQueue.iterator();
            while (iterator.hasNext()) {
                UploadTask uploadTask = (UploadTask) iterator.next();
                UploadModel uploadModel1 = uploadTask.getUploadModel();
                if (uploadModel.getUniqueId().equals(uploadModel1.getUniqueId())) {
                    return;
                }
            }
            //正在上传
            UploadTask uploadTask = mUploadService.getRunnableTask();
            if (uploadTask != null && uploadTask.getUploadModel().getUniqueId().equals(uploadModel.getUniqueId())) {
                return;
            }
        }

        int state = uploadModel.getState();
        IUploadCallBack uploadCallBack = mUploadCallBackMap.get(uploadModel.getTopicId());
//        if (state == UploadModel.UPLOAD_STATE_WAIT) {
//            //如果没保存到过数据库，则先保存到数据库
//            int id = DbHelper.getInstance().saveUploadFileSync(uploadModel);
//            if (id != -1) {
//                uploadModel.setId(id);
//
//                if (uploadCallBack != null) {
//                    uploadCallBack.onStart(uploadModel);
//                }
//                uploadOss(uploadModel);
//            } else {
//                Exception e = new SQLException("插入数据库失败");
//                uploadCallBack.onFailure(uploadModel, e);
//            }
//        } else {
            uploadOss(uploadModel);
//        }
    }

    private void uploadOss(UploadModel uploadModel) {
        if (mUploadService != null) {
            mUploadService.startUpload(uploadModel, new IUploadCallBack() {
                @Override
                public void onStart(UploadModel uploadModel) {

                }

                @Override
                public void onProgress(UploadModel uploadModel, long currentSize, long totalSize) {
//                    DbHelper.getInstance().updateUploadFileState(String.valueOf(uploadModel.getId()), UploadModel.UPLOAD_STATE_UPLOADING);
                    IUploadCallBack uploadCallBack = mUploadCallBackMap.get(uploadModel.getTopicId());
                    if (uploadCallBack != null) {
                        uploadCallBack.onProgress(uploadModel, currentSize, totalSize);
                    }
                }

                @Override
                public void onSuccess(UploadModel uploadModel) {
                    uploadModel.setState(UploadModel.UPLOAD_STATE_SERVICE_FAIL);
//                    DbHelper.getInstance().updateUploadFileState(String.valueOf(uploadModel.getId()), UploadModel.UPLOAD_STATE_SERVICE_FAIL, uploadModel.getOssFilePath());
//                    Message message = Message.obtain();
//                    message.obj = uploadModel;
//                    message.what = uploadModel.getId();
//                    mHandler.sendMessage(message);
                    uploadToService(uploadModel);
                }

                @Override
                public void onFailure(UploadModel uploadModel, Exception e) {
                    boolean isNetError = e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof SocketException;
//                    if (!isNetError) {
//                        LogModel logModel = LogModel.getLogModel("NewTopicDetailActivity", System.currentTimeMillis() + "", "upload_file_oss---" + e.getMessage(),
//                                new BaseParams<>(new NoDataParams()), "");
//                        ErrorIncludeModel model = new ErrorIncludeModel(logModel);
//                        FileManager.writeLog(MyApplication.getInstance().context, new Gson().toJson(model) + ",", "error.log");
//                    }
                    if (e instanceof ApiException) {
                        int status = ((ApiException) e).getStatus();
                        if (status == UploadOssHelper.ERROR_STATUS_FILE_PATH_EMPTY
                                || status == UploadOssHelper.ERROR_STATUS_IMAGE_FILE_SIZE
                                || status == UploadOssHelper.ERROR_STATUS_VIDEO_FILE_SIZE) {
//                            DbHelper.getInstance().delUploadFileSync(String.valueOf(uploadModel.getId()));
                            IUploadCallBack uploadCallBack = mUploadCallBackMap.get(uploadModel.getTopicId());
                            if (uploadCallBack != null) {
                                uploadCallBack.onFailure(uploadModel, e);
                            }
                            return;
                        }
                    }
                    if (uploadModel.getState() != UploadModel.UPLOAD_STATE_SERVICE_FAIL)
//                        DbHelper.getInstance().updateUploadFileState(String.valueOf(uploadModel.getId()), UploadModel.UPLOAD_STATE_OSS_FAIL);
                    stopAllUpload();
                    IUploadCallBack uploadCallBack = mUploadCallBackMap.get(uploadModel.getTopicId());
                    if (uploadCallBack != null) {
                        uploadCallBack.onFailure(uploadModel, e);
                        uploadCallBack.onStopAllUpload(mUploadService.stopAllUpload());
                    }
                }

                @Override
                public void onStopAllUpload(List<UploadModel> uploadModelList) {

                }
            });
        }
    }

    /**
     * 撤回
     */
    public void stopUpload(int id) {
        //在OSS上传队列里撤回
        if (mUploadService != null) {
            mUploadService.removeUpload(id);
        }
        //在Handler上传队列里撤回
//        mHandler.removeMessages(id);
    }

    /**
     * 取消所有上传
     */
    public void stopAllUpload() {
        if (mUploadService != null) {
            mUploadService.stopAllUpload();
        }
//        mHandler.removeCallbacksAndMessages(null);
        mMainHandler.removeCallbacksAndMessages(null);
    }

    private void startService(Context context) {
        Intent intent = new Intent(context, UploadService.class);
        context.startService(intent);
    }

    public void bindService(Context context) {
        startService(context);
        Intent intent = new Intent(context, UploadService.class);
        isBind = context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LogUtil.d("bindService_" + (isBind ? "1" : "2"));

    }

    public void setUploadCallBack(String topicId, IUploadCallBack uploadCallBack) {
        mUploadCallBackMap.put(topicId, uploadCallBack);
    }

    public void stopService(Context context, String topicId) {
        unBindService(context, topicId);
        Intent intent = new Intent(context, UploadService.class);
        context.stopService(intent);
    }

    public void unBindService(Context context, String topicId) {
        if (mServiceConnection != null && isBind) {
            context.unbindService(mServiceConnection);
            isBind = false;
        }
        mUploadCallBackMap.remove(topicId);
    }



}
