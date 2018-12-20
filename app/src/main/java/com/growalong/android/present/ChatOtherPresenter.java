package com.growalong.android.present;

import com.google.gson.JsonElement;
import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.listener.OkCancelListener;
import com.growalong.android.model.ApiException;
import com.growalong.android.model.CollectModel;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.ui.dialog.RequestedVideoCallDialog;
import com.growalong.android.util.ToastUtil;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;

/**
 * Created by yangxing on 2018/12/1.
 */
public class ChatOtherPresenter {

    private final QLActivity mActivity;
    private final UserPresenter userPresenter;

    public ChatOtherPresenter(QLActivity chatActivity) {
        mActivity = chatActivity;
        userPresenter = new UserPresenter();
    }

    public void requestVideoChat(String callerHeadUrl, String callerName, OkCancelListener listener) {
        RequestedVideoCallDialog dialog = new RequestedVideoCallDialog();
        dialog.setCallerHeadUrl(callerHeadUrl);
        dialog.setCallerName(callerName);
        dialog.setClickListener(listener);
        dialog.show(mActivity.getFragmentManager(), "");
    }

    public void collect(TIMMessage msg, String titleStr, String content, String groupName, String otherInfo) {
        try {
            CollectModel collectItem = new CollectModel();
//            String id = msg.getMsgId();
            TIMElem elem = msg.getElement(0);
            //获取当前元素的类型
            TIMElemType elemType = elem.getType();
            if (elemType == TIMElemType.Text) {
                //处理文本消息
                collectItem.setType("text");
                collectItem.setTitle(content);
            } else if (elemType == TIMElemType.Image) {
                //处理图片消息
                collectItem.setType("image");
                collectItem.setTitle("image");
            } else if (elemType == TIMElemType.Sound) {
                collectItem.setType("audio");
                TIMSoundElem soundElem = (TIMSoundElem) msg.getElement(0);
                collectItem.setOtherInfo(soundElem.getDuration() + "");
                collectItem.setTitle("audio");
            } else if (elemType == TIMElemType.File) {
                collectItem.setType("file");
                collectItem.setTitle(titleStr);
            } else if (elemType == TIMElemType.Video) {
                collectItem.setType("video");
                collectItem.setTitle("video");
            }

            collectItem.setGroupName(groupName);
            collectItem.setContent(content);
            collectItem.setChatId(msg.getMsgId());
            collectItem.setOtherInfo(otherInfo);
            collectItem.setCreateTime(System.currentTimeMillis());
            userPresenter.addCollect(collectItem).subscribe(new CommSubscriber<JsonElement>() {
                @Override
                public void onSuccess(JsonElement jsonElement) {
                    ToastUtil.shortShow(MyApplication.getContext().getResources().getString(R.string.collect_success));
                }

                @Override
                public void onFailure(Throwable e) {
                    super.onFailure(e);
                    if (e instanceof ApiException) {
                        ApiException exception = (ApiException) e;
                        if (exception.getStatus() == 10001) {
                            ToastUtil.shortShow(MyApplication.getContext().getResources().getString(R.string.collect_added));
                            return;
                        }
                    }
                    ToastUtil.shortShow(MyApplication.getContext().getResources().getString(R.string.collect_failed));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
