package com.growalong.android.upload;

import com.growalong.android.model.UploadModel;

import java.util.List;

/**
 * Created by gangqing on 2016/12/20.
 */

public interface IUploadCallBack {
    void onStart(UploadModel uploadModel);

    void onProgress(UploadModel uploadModel, long currentSize, long totalSize);

    void onSuccess(UploadModel uploadModel);

    void onFailure(UploadModel uploadModel, Exception e);

    void onStopAllUpload(List<UploadModel> uploadModelList);

}
