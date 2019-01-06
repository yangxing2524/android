package com.tencent.qcloud.ui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 发送语音提示控件
 */
public class VoiceSendingView extends RelativeLayout {


    private final ImageView img;
    private final TextView chatUpFinger;
    private AnimationDrawable frameAnimation;

    public VoiceSendingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.voice_sending, this);
        img = (ImageView) findViewById(R.id.microphone);
        chatUpFinger = (TextView) findViewById(R.id.chatUpFinger);
        img.setBackgroundResource(R.drawable.animation_voice);
        frameAnimation = (AnimationDrawable) img.getBackground();

    }


    public void showRecording() {
        if (frameAnimation != null) {
            frameAnimation.start();
        }
    }

    public void showCancel() {
        if (frameAnimation != null) {
            frameAnimation.stop();
        }
    }

    public void release() {
        img.setBackgroundResource(R.drawable.animation_voice);
        frameAnimation = (AnimationDrawable) img.getBackground();
        chatUpFinger.setText(R.string.chat_up_finger);
        if (frameAnimation != null) {
            frameAnimation.stop();
        }
    }

    public void showCancelVoiceView() {
        chatUpFinger.setText(R.string.chat_up_cancel);
        img.setBackgroundResource(R.drawable.icon_audio_cancel);
        frameAnimation = null;
    }

    public void showRecordVoiceView() {
        img.setBackgroundResource(R.drawable.animation_voice);
        frameAnimation = (AnimationDrawable) img.getBackground();
        chatUpFinger.setText(R.string.chat_up_finger);
        showRecording();
    }

}
