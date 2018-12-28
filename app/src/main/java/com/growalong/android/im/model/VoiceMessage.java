package com.growalong.android.im.model;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.adapters.ChatAdapter;
import com.growalong.android.im.utils.FileUtil;
import com.growalong.android.im.utils.MediaUtil;
import com.growalong.android.util.DensityUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;

import java.io.File;
import java.io.FileInputStream;

/**
 * 语音消息数据
 */
public class VoiceMessage extends Message {

    private static final String TAG = "VoiceMessage";
    private boolean isPlaying = false;

    public VoiceMessage(TIMMessage message) {
        this.message = message;
    }


    /**
     * 语音消息构造方法
     *
     * @param duration 时长
     * @param filePath 语音数据地址
     */
    public VoiceMessage(long duration, String filePath) {
        message = new TIMMessage();
        TIMSoundElem elem = new TIMSoundElem();
        elem.setPath(filePath);
        elem.setDuration(duration);  //填写语音时长
        message.addElement(elem);
    }

    @Override
    public String getContent() {
        TIMSoundElem timSoundElem = (TIMSoundElem) message.getElement(0);
        return timSoundElem.getPath();
    }

    @Override
    public String[] getInfo() {
        String[] strings = new String[1];
        strings[0] = ((TIMSoundElem) message.getElement(0)).getDuration() + "";
        return strings;
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        if (checkRevoke(viewHolder)) return;
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_im_voice, null, false);
        ImageView voiceIcon = linearLayout.findViewById(R.id.image);
        voiceIcon.setBackgroundResource(message.isSelf() ? R.drawable.right_voice : R.drawable.left_voice);
        final AnimationDrawable frameAnimatio = (AnimationDrawable) voiceIcon.getBackground();

        TextView tv = linearLayout.findViewById(R.id.text);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setTextColor(MyApplication.getContext().getResources().getColor(isSelf() ? R.color.white : R.color.black));
        int duration = (int) ((TIMSoundElem) message.getElement(0)).getDuration();
        String time;
        if (duration > 60) {
            int min = duration / 60;
            int seconde = duration % 60;
            time = min + "''" + seconde + "'";
        } else {
            time = duration + "'";
        }
        if (duration > 17) {
            tv.getLayoutParams().width = DensityUtil.dip2px(context, 100);
        } else {
            tv.getLayoutParams().width = DensityUtil.dip2px(context, 40);
        }
        tv.setText(time);
        clearView(viewHolder);
        getBubbleView(viewHolder).addView(linearLayout);
        getBubbleView(viewHolder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    stopAudio(frameAnimatio);
                } else {
                    VoiceMessage.this.playAudio(frameAnimatio);
                }
            }
        });
        showStatus(viewHolder);
    }


    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) return str;
        return MyApplication.getContext().getString(R.string.summary_voice);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    private void stopAudio(final AnimationDrawable frameAnimatio) {
        frameAnimatio.stop();
        frameAnimatio.selectDrawable(0);
        MediaUtil.getInstance().stop();
    }

    private void playAudio(final AnimationDrawable frameAnimatio) {
        TIMSoundElem elem = (TIMSoundElem) message.getElement(0);
        final File tempAudio = FileUtil.getTempFile(FileUtil.FileType.AUDIO);
        elem.getSoundToFile(tempAudio.getAbsolutePath(), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess() {
                try {
                    FileInputStream fis = new FileInputStream(tempAudio);
                    MediaUtil.getInstance().play(fis);
                    frameAnimatio.start();
                    isPlaying = true;
                    MediaUtil.getInstance().setEventListener(new MediaUtil.EventListener() {
                        @Override
                        public void onStop() {
                            frameAnimatio.stop();
                            frameAnimatio.selectDrawable(0);
                            isPlaying = false;
                        }
                    });
                } catch (Exception e) {

                }

            }
        });

    }
}
