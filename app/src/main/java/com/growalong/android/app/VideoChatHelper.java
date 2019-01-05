package com.growalong.android.app;

import android.text.TextUtils;

import com.growalong.android.im.model.FriendProfile;
import com.growalong.android.im.model.FriendshipInfo;
import com.growalong.android.im.model.GroupInfo;
import com.growalong.android.im.model.TextMessage;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.ui.ChatActivity;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by yangxing on 2019/1/5.
 */
public class VideoChatHelper implements Observer {
    private static final String TAG = "ConversationPresenter";

    private static VideoChatHelper instance;

    private VideoChatHelper() {
        //注册消息监听
        MessageEvent.getInstance().addObserver(this);
        //注册刷新监听
        RefreshEvent.getInstance().addObserver(this);
        //注册好友关系链监听
        FriendshipEvent.getInstance().addObserver(this);
        //注册群关系监听
        GroupEvent.getInstance().addObserver(this);
    }

    public static VideoChatHelper getInstance() {
        if (instance == null) {
            instance = new VideoChatHelper();
        }
        return instance;
    }

    @Override
    public void update(Observable observable, Object data) {
        if (observable instanceof MessageEvent) {
            if (data instanceof TIMMessage) {
                TIMMessage msg = (TIMMessage) data;

                if (msg.getElement(0).getType() == TIMElemType.Text) {
                    TextMessage textMessage = new TextMessage(msg);
                    final String content = textMessage.getContent();
                    if (content.startsWith(ChatActivity.VIDEO_CHAT_REQUEST)) {
                        UserInfoModel userInfoModel = AppManager.getInstance().getUserInfoModel();
                        if (!TextUtils.equals(userInfoModel.getId() + "", textMessage.getSender())) {
                            //非自己发送的
                            if (!(AppManager.getInstance().currentActivity() instanceof ChatActivity)) {
                                TIMUserProfile senderProfile = msg.getSenderProfile();
                                String id = content.substring(ChatActivity.VIDEO_CHAT_REQUEST.length());
                                String chatName;
                                if (senderProfile != null) {
                                    String groupName = GroupInfo.getInstance().getGroupName(id);
                                    TIMConversationType type;
                                    if (!TextUtils.isEmpty(groupName)) {
                                        type = TIMConversationType.Group;
                                        chatName = groupName;
                                    } else {
                                        type = TIMConversationType.C2C;
                                        FriendProfile profile = FriendshipInfo.getInstance().getProfile(id);
                                        chatName = profile == null ? id : profile.getName();
                                    }
                                    ChatActivity.startThis(AppManager.getInstance().currentActivity(),
                                            id, type, chatName, content, msg.getSender(), senderProfile.getNickName(), senderProfile.getFaceUrl());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
