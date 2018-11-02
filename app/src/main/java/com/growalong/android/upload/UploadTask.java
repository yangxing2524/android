package com.growalong.android.upload;

import com.growalong.android.model.UploadModel;
import com.growalong.android.util.LogUtil;

import java.util.List;

/**
 * Created by gangqing on 2016/12/20.
 */

public class UploadTask implements Runnable {
    private boolean shouldContinue = true;
    private UploadModel mUploadModel;
    private IUploadCallBack mCallBack;

    public UploadTask(UploadModel uploadModel, IUploadCallBack callBack) {
        mUploadModel = uploadModel;
        mCallBack = callBack;
    }

    public UploadModel getUploadModel() {
        return mUploadModel;
    }

    @Override
    public void run() {
        if (shouldContinue) {
            LogUtil.e("uploadDetail:OSS");
            String fileType = mUploadModel.getFileType();
            int state = mUploadModel.getState();
            //文字不需要上传到阿里云，视频的通用代码也是文字
            if (UploadModel.TYPE_MIC_TEXT.equals(fileType) || UploadModel.TYPE_TEXT.equals(fileType) || UploadModel.TYEP_VIDEO.equals(fileType) || state == UploadModel.UPLOAD_STATE_SERVICE_FAIL) {
                if (mCallBack != null) {
                    mCallBack.onSuccess(mUploadModel);
                }
            } else {
                new UploadOssHelper().resumeUpload(mUploadModel, new IUploadCallBack() {
                    @Override
                    public void onStart(UploadModel uploadModel) {

                    }

                    @Override
                    public void onProgress(UploadModel uploadModel, long currentSize, long totalSize) {
                        if (shouldContinue) {
                            UploadTask.this.cancel();
                        } else if (mCallBack != null) {
                            mCallBack.onProgress(mUploadModel, currentSize, totalSize);
                        }
                    }

                    @Override
                    public void onSuccess(UploadModel uploadModel) {
                        if (mCallBack != null) {
                            mCallBack.onSuccess(mUploadModel);
                        }
                    }

                    @Override
                    public void onFailure(UploadModel uploadModel, Exception e) {
                        if (mCallBack != null) {
                            mCallBack.onFailure(mUploadModel, e);
                        }
                    }

                    @Override
                    public void onStopAllUpload(List<UploadModel> uploadModelList) {

                    }
                });
            }
        }
    }

    public final void cancel() {
        this.shouldContinue = false;
    }
}
