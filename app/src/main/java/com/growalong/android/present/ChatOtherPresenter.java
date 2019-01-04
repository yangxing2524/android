package com.growalong.android.present;

import com.google.gson.JsonElement;
import com.growalong.android.R;
import com.growalong.android.app.AppManager;
import com.growalong.android.app.MyApplication;
import com.growalong.android.listener.OkCancelListener;
import com.growalong.android.model.ApiException;
import com.growalong.android.model.CollectModel;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.ui.dialog.RequestedVideoCallDialog;
import com.growalong.android.util.ToastUtil;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMGroupMemberInfo;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSoundElem;
import com.tencent.imsdk.TIMValueCallBack;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangxing on 2018/12/1.
 */
public class ChatOtherPresenter {

    private final QLActivity mActivity;
    private final UserPresenter userPresenter;
    private RequestedVideoCallDialog dialog;

    public ChatOtherPresenter(QLActivity chatActivity) {
        mActivity = chatActivity;
        userPresenter = new UserPresenter();
    }

    public void getUserInfos(String groupId) {
        //创建回调
        TIMValueCallBack<List<TIMGroupMemberInfo>> cb = new TIMValueCallBack<List<TIMGroupMemberInfo>>() {
            @Override
            public void onError(int code, String desc) {
            }

            @Override
            public void onSuccess(List<TIMGroupMemberInfo> infoList) {//参数返回群组成员信息
                List<String> list = new ArrayList<>();
                for (TIMGroupMemberInfo info : infoList) {
                    list.add(info.getUser());
                }
                userPresenter.getUsersInfos(list).subscribe(new CommSubscriber<List<UserInfoModel>>() {
                    @Override
                    public void onSuccess(List<UserInfoModel> userInfoModels) {
                        for (UserInfoModel userInfoModel : userInfoModels) {
                            AppManager.userHeadMap.put(userInfoModel.getId() + "", userInfoModel);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                    }
                });
            }
        };

        //获取群组成员信息
        TIMGroupManagerExt.getInstance().getGroupMembers(
                groupId, //群组 ID
                cb);     //回调
    }


    public void requestVideoChat(String callerHeadUrl, String callerName, OkCancelListener listener) {
        dialog = new RequestedVideoCallDialog();
        dialog.setCallerHeadUrl(callerHeadUrl);
        dialog.setCallerName(callerName);
        dialog.setClickListener(listener);
        dialog.show(mActivity.getFragmentManager(), "");

    }

    public boolean isDialogShow() {
        return !(dialog == null);
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
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
            } else if (elemType == TIMElemType.UGC) {
                collectItem.setType("video");
                collectItem.setTitle(titleStr);
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
