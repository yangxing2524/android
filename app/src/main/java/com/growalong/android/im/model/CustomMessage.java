package com.growalong.android.im.model;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.growalong.android.im.adapters.ChatAdapter;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 自定义消息
 */
public class CustomMessage extends Message {


    private String TAG = getClass().getSimpleName();

    private final int TYPE_TYPING = 14;

    private Type type;
    private String desc;
    private String data;

    public CustomMessage(TIMMessage message) {
        this.message = message;
        TIMCustomElem elem = (TIMCustomElem) message.getElement(0);
        parse(elem.getData());

    }

    public CustomMessage(Type type) {
        message = new TIMMessage();
        String data = "";
        JSONObject dataJson = new JSONObject();
        try {
            switch (type) {
                case TYPING:
                    dataJson.put("userAction", TYPE_TYPING);
                    dataJson.put("actionParam", "EIMAMSG_InputStatus_Ing");
                    data = dataJson.toString();
            }
        } catch (JSONException e) {
            Log.e(TAG, "generate json error");
        }
        TIMCustomElem elem = new TIMCustomElem();
        elem.setData(data.getBytes());
        message.addElement(elem);
    }


    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private void parse(byte[] data) {
        type = Type.INVALID;
        try {
            String str = new String(data, "UTF-8");
            JSONObject jsonObj = new JSONObject(str);
            int action = jsonObj.getInt("userAction");
            switch (action) {
                case TYPE_TYPING:
                    type = Type.TYPING;
                    this.data = jsonObj.getString("actionParam");
                    if (this.data.equals("EIMAMSG_InputStatus_End")) {
                        type = Type.INVALID;
                    }
                    break;
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "parse json error");

        }
    }

    @Override
    public String getContent() {
        return null;
    }

    /**
     * 显示消息
     *
     * @param viewHolder 界面样式
     * @param context    显示消息的上下文
     */
    @Override
    public void showMessage(ChatAdapter.ViewHolder viewHolder, Context context) {
        viewHolder.leftPanel.setVisibility(View.GONE);
        viewHolder.rightPanel.setVisibility(View.GONE);
        viewHolder.systemMessage.setVisibility(View.VISIBLE);
        viewHolder.systemMessage.setText(getSummary());
    }

    /**
     * 获取消息摘要
     * TIMMessage message1 = new TIMMessage();
     * //添加文本内容
     * TIMCustomElem elem1 = new TIMCustomElem();
     * elem1.setDesc(getResources().getString(R.string.video_chat_over));
     * //将elem添加到消息
     * if (message1.addElement(elem1) != 0) {
     * LogUtil.d("addElement failed");
     * return;
     * }
     * mMessage = MessageFactory.getMessage(message1);
     */
    @Override
    public String getSummary() {
        try {
            return ((TIMCustomElem) getMessage().getElement(0)).getDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    public enum Type {
        TYPING,
        INVALID,
    }
}
