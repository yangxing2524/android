package com.growalong.android.net.retrofit.download;

/**
 * Created by murphy on 14/11/2016.
 */

public interface ProgressListener {
    void onProgress(long progress, long total, boolean done);
}
