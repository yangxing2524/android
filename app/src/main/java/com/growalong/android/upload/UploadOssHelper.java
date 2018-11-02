package com.growalong.android.upload;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;
import com.growalong.android.app.Constants;
import com.growalong.android.app.MyApplication;
import com.growalong.android.model.ApiException;
import com.growalong.android.model.UploadModel;
import com.growalong.android.util.FileUtils;
import com.growalong.android.util.ToastUtil;
import com.growalong.android.util.UniqueUtils;

import java.io.File;

/**
 * Created by gangqing on 2016/12/15.
 */

public class UploadOssHelper {
    private static final String LIVE_IMAGE_URL = "growalong/image/";
    private static final String LIVE_AUDIO_URL = "growalong/audio/";
    public static final int ERROR_STATUS_FILE_PATH_EMPTY = -1;
    public static final int ERROR_STATUS_IMAGE_FILE_SIZE = -2;
    public static final int ERROR_STATUS_VIDEO_FILE_SIZE = -3;
    //分片上传，每片的大小 128 * 1024 = 128k
    private static final long PART_SIZE = 1310720;
    //上传图片的最大限制 5 * 1024 * 1024 = 5m
    private static final long MAX_SIZE_IMAGE = 5242880;
    //上传音频的最大限制 30 * 1024 * 1024 = 30m
    private static final long MAX_SIZE_AUDIO = 31457280;
    //断点记录的位置
    private static final String recordDirectory = MyApplication.getInstance().context.getFilesDir() + "/oss_record/";
    private OSSAsyncTask mTask;

    //图片地址
    public static String imageHttp = "https://img.grow-along.com/";
    //语音地址
    public static String audioHttp = "https://media.qlchat.com/";

    /**
     * 普通异步上传
     */
    public void uploadObject(final UploadModel uploadModel, final IUploadCallBack callBack) {
        final String path = uploadModel.getLocalFilePath();
        if (!isQualified(uploadModel, callBack)) {
            return;
        }
        final String objectKey = createObjectKey(uploadModel);
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(Constants.bucketName, objectKey, path);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                if (callBack != null) {
                    callBack.onProgress(uploadModel, currentSize, totalSize);
                }
            }
        });
        callBack.onStart(uploadModel);
        mTask = OSSManager.getInstance().getOss().asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if (callBack != null) {
                    if (isAudio(uploadModel)) {
                        if (isMP3Audio(uploadModel)) {
                            String app_oss_media_url = audioHttp;
                            uploadModel.setOssFilePath(app_oss_media_url + objectKey);
                        } else {
                            String accObjectKey = objectKey.substring(0, objectKey.lastIndexOf("."));
                            String app_oss_media_url = audioHttp;
                            uploadModel.setOssFilePath(app_oss_media_url + accObjectKey);
                        }
                        callBack.onSuccess(uploadModel);
                    } else {
                        String app_oss_img_url = imageHttp;
                        uploadModel.setOssFilePath(app_oss_img_url + objectKey);
                        callBack.onSuccess(uploadModel);
                    }
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常
                if (clientException != null) {
                    // 本地异常如网络异常等
                    clientException.printStackTrace();
                    if (callBack != null) {
                        callBack.onFailure(uploadModel, clientException);
                    }
                }
                if (serviceException != null) {
                    // 服务异常
                    if (callBack != null) {
                        callBack.onFailure(uploadModel, serviceException);
                    }
                }
            }
        });
    }

    /**
     * 同步断点续传
     */
    public void resumeUpload(final UploadModel uploadModel, final IUploadCallBack callBack) {
        String path = uploadModel.getLocalFilePath();
        if (!isQualified(uploadModel, callBack)) {
            return;
        }
        File recordDir = new File(recordDirectory);
        // 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        String objectKey = "";
        if (!TextUtils.isEmpty(uploadModel.getObjectKey())) {
            objectKey = uploadModel.getObjectKey();
        } else {
            objectKey = createObjectKey(uploadModel);
            uploadModel.setObjectKey(objectKey);
        }
        ResumableUploadRequest request = new ResumableUploadRequest(Constants.bucketName, objectKey, path, recordDirectory);
        request.setPartSize(PART_SIZE);
        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
                Log.d("resumableUpload", "currentSize: " + currentSize + " totalSize: " + totalSize);
                if (callBack != null) {
                    callBack.onProgress(uploadModel, currentSize, totalSize);
                }
            }
        });
        try {
            OSSManager.getInstance().getOss().resumableUpload(request);
            if (callBack != null) {
                if (isAudio(uploadModel)) {
                    if (isMP3Audio(uploadModel)) {
                        String app_oss_media_url = audioHttp;
                        uploadModel.setOssFilePath(app_oss_media_url + objectKey);
                    } else {
                        String accObjectKey = objectKey.substring(0, objectKey.lastIndexOf("."));
                        String app_oss_media_url = audioHttp;
                        uploadModel.setOssFilePath(app_oss_media_url + accObjectKey);
                    }
                    callBack.onSuccess(uploadModel);
                } else {
                    String app_oss_img_url = imageHttp;
                    uploadModel.setOssFilePath(app_oss_img_url + objectKey);
                    callBack.onSuccess(uploadModel);
                }
            }
        } catch (ClientException e) {
            // 本地异常如网络异常等
            e.printStackTrace();
            if (callBack != null) {
                callBack.onFailure(uploadModel, e);
            }
        } catch (ServiceException e) {
            e.printStackTrace();
            String errorCode = e.getErrorCode();
            if ("InvalidAccessKeyId".equals(errorCode)) {
                OSSManager.getInstance().update();
            }
            // 服务异常
            if (callBack != null) {
                callBack.onFailure(uploadModel, e);
            }
        }
    }

    /**
     * 异步断点续传
     */
    public void asyncResumeUpload(final UploadModel uploadModel, final IUploadCallBack callBack) {
        final String path = uploadModel.getLocalFilePath();
        if (!isQualified(uploadModel, callBack)) {
            return;
        }
        File recordDir = new File(recordDirectory);
        // 要保证目录存在，如果不存在则主动创建
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }
        String objectKey = "";
        if (!TextUtils.isEmpty(uploadModel.getObjectKey())) {
            objectKey = uploadModel.getObjectKey();
        } else {
            objectKey = createObjectKey(uploadModel);
            uploadModel.setObjectKey(objectKey);
        }

        ResumableUploadRequest request = new ResumableUploadRequest(Constants.bucketName, objectKey, path, recordDirectory);
        request.setPartSize(PART_SIZE);
        // 设置上传过程回调
        request.setProgressCallback(new OSSProgressCallback<ResumableUploadRequest>() {
            @Override
            public void onProgress(ResumableUploadRequest request, long currentSize, long totalSize) {
                Log.d("resumableUpload", "currentSize: " + currentSize + " totalSize: " + totalSize);
                if (callBack != null) {
                    callBack.onProgress(uploadModel, currentSize, totalSize);
                }
            }
        });
        final String finalObjectKey = objectKey;
        mTask = OSSManager.getInstance().getOss().asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                Log.d("resumableUpload", "success!");
                if (callBack != null) {
                    if (isAudio(uploadModel)) {
                        if (isMP3Audio(uploadModel)) {
                            String app_oss_media_url = audioHttp;
                            uploadModel.setOssFilePath(app_oss_media_url + finalObjectKey);
                        } else {
                            String accObjectKey = finalObjectKey.substring(0, finalObjectKey.lastIndexOf("."));
                            String app_oss_media_url = audioHttp;
                            uploadModel.setOssFilePath(app_oss_media_url + accObjectKey);
                        }
                        callBack.onSuccess(uploadModel);
                    } else {
                        String app_oss_img_url = imageHttp;
                        uploadModel.setOssFilePath(app_oss_img_url + finalObjectKey);
                        callBack.onSuccess(uploadModel);
                    }
                }
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常
                if (clientException != null) {
                    // 本地异常如网络异常等
                    clientException.printStackTrace();
                    if (callBack != null) {
                        callBack.onFailure(uploadModel, clientException);
                    }
                }
                if (serviceException != null) {
                    String errorCode = serviceException.getErrorCode();
                    if ("InvalidAccessKeyId".equals(errorCode)) {
                        OSSManager.getInstance().update();
                    }
                    // 服务异常
                    if (callBack != null) {
                        callBack.onFailure(uploadModel, serviceException);
                    }
                }
            }
        });
    }

    /**
     * 取消异步断点续传
     */
    public void cancelUpload() {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

    private String createObjectKey(UploadModel uploadModel) {
        StringBuilder key = new StringBuilder();
        if (isAudio(uploadModel)) {
            key.append(LIVE_AUDIO_URL);
        } else {
            key.append(LIVE_IMAGE_URL);
        }
        key.append(UniqueUtils.getImageViewUnique());
        key.append(getFileSuffix(uploadModel.getLocalFilePath()));
        return key.toString();
    }

    private String getFileSuffix(String path) {
        return path.substring(path.lastIndexOf("."), path.length()).toLowerCase();
    }

    private boolean isAudio(UploadModel uploadModel) {
        String fileType = uploadModel.getFileType();
        if (UploadModel.FILE_TYPE_AUDIO.equals(fileType)
                || UploadModel.FILE_TYPE_APP_AUDIO.equals(fileType)
                || UploadModel.FILE_TYPE_M4A_AUDIO.equals(fileType)
                || UploadModel.FILE_TYPE_MP3_AUDIO.equals(fileType)
                || UploadModel.FILE_TYPE_MIC_AUDIO.equals(fileType)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isMP3Audio(UploadModel uploadModel) {
        String fileType = uploadModel.getFileType();
        if (UploadModel.FILE_TYPE_MP3_AUDIO.equals(fileType)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isImage(UploadModel uploadModel) {
        String fileType = uploadModel.getFileType();
        if (UploadModel.FILE_TYPE_IMAGE.equals(fileType)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isQualified(final UploadModel uploadModel, final IUploadCallBack callBack) {
        String path = uploadModel.getLocalFilePath();
        if (TextUtils.isEmpty(path)) {
            if (callBack != null) {
                ApiException exception = new ApiException("文件路径为空", ERROR_STATUS_FILE_PATH_EMPTY);
                callBack.onFailure(uploadModel, exception);
            }
            return false;
        }
        long fileSize = FileUtils.getFolderSize(new File(path));
        if (isImage(uploadModel) && (fileSize > MAX_SIZE_IMAGE)) {
            MyApplication.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.shortShow("上传的图片不能超过5M");
                    if (callBack != null) {
                        ApiException exception = new ApiException("图片超过5M", ERROR_STATUS_IMAGE_FILE_SIZE);
                        callBack.onFailure(uploadModel, exception);
                    }
                }
            });
            return false;
        } else if (isAudio(uploadModel) && (fileSize > MAX_SIZE_AUDIO)) {
            MyApplication.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.shortShow("上传的音频不能超过30M");
                    if (callBack != null) {
                        ApiException exception = new ApiException("音频超过30M", ERROR_STATUS_VIDEO_FILE_SIZE);
                        callBack.onFailure(uploadModel, exception);
                    }
                }
            });
            return false;
        }
        return true;
    }
}
