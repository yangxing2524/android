package com.growalong.android.agora.openvcall.model;

import io.agora.rtc.video.VideoEncoderConfiguration;

public class AgoraEngineConfig {
    VideoEncoderConfiguration.VideoDimensions mVideoDimension;

    public int mUid;

    public String mChannel;

    public void reset() {
        mChannel = null;
    }

    AgoraEngineConfig() {
    }
}
