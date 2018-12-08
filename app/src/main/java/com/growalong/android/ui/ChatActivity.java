package com.growalong.android.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.growalong.android.R;
import com.growalong.android.agora.openvcall.model.AgoraConstantApp;
import com.growalong.android.agora.openvcall.ui.AgoraChatActivity;
import com.growalong.android.app.AppManager;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.adapters.ChatAdapter;
import com.growalong.android.im.model.CustomMessage;
import com.growalong.android.im.model.FileMessage;
import com.growalong.android.im.model.FriendProfile;
import com.growalong.android.im.model.FriendshipInfo;
import com.growalong.android.im.model.GroupInfo;
import com.growalong.android.im.model.ImageMessage;
import com.growalong.android.im.model.Message;
import com.growalong.android.im.model.MessageFactory;
import com.growalong.android.im.model.TextMessage;
import com.growalong.android.im.model.UGCMessage;
import com.growalong.android.im.model.VideoMessage;
import com.growalong.android.im.model.VoiceMessage;
import com.growalong.android.im.utils.FileUtil;
import com.growalong.android.im.utils.MediaUtil;
import com.growalong.android.im.utils.RecorderUtil;
import com.growalong.android.listener.OkCancelListener;
import com.growalong.android.model.CollectModel;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.present.ChatOtherPresenter;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.InitPresenter;
import com.growalong.android.present.UserPresenter;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.ToastUtil;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.ext.message.TIMMessageDraft;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.qcloud.presentation.presenter.ChatPresenter;
import com.tencent.qcloud.presentation.viewfeatures.ChatView;
import com.tencent.qcloud.ui.ChatInput;
import com.tencent.qcloud.ui.TemplateTitle;
import com.tencent.qcloud.ui.VoiceSendingView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends QLActivity implements ChatView {

    private static final String TAG = "ChatActivity";

    public static final String VIDEO_CHAT_REQUEST = "&video_chat_request&";
    public static final String VIDEO_CHAT_FAILED = "&video_chat_failed&";//通话已结束
    public static final String VIDEO_CHAT_REFUSE = "&video_chat_refused&";
    public static final String VIDEO_CHAT_OVER = "&video_chat_over&";//通话已结束

    private List<Message> messageList = new ArrayList<>();
    private List<Message> sourceList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    private ChatPresenter presenter;

    private ChatOtherPresenter chatOtherPresenter;

    private ChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = CommonPhotoSelectorDialog.PHOTOREQUESTCODE1;
    public static final int VIDEO_CHAT_REQUEST_CODE_SENDER = 22;//视频发起者
    public static final int VIDEO_CHAT_REQUEST_CODE_RECEIVER = 21;//视频接收者
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    private static final int IMAGE_PREVIEW = 400;
    private static final int VIDEO_RECORD = 500;
    private Uri fileUri;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private String titleStr;
    private Handler handler = new Handler();

    private UserPresenter userPresenter;

    public static void navToChat(Context context, String identify, TIMConversationType type) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }


    private void initVideo() {
        InitPresenter initPresenter = new InitPresenter();
        initPresenter.getRoom().subscribe(new CommSubscriber<List<String>>() {
            @Override
            public void onSuccess(List<String> strings) {
                LogUtil.e(strings.toString());
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //退出聊天界面时输入框有内容，保存草稿
        if (input.getText().length() > 0) {
            TextMessage message = new TextMessage(input.getText());
            presenter.saveDraft(message.getMessage());
        } else {
            presenter.saveDraft(null);
        }
//        RefreshEvent.getInstance().onRefresh();
        presenter.readMessages();
        MediaUtil.getInstance().stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.stop();
    }

    public boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestCode);
            return false;
        }

        if (Manifest.permission.CAMERA.equals(permission)) {
            MyApplication.getInstance().initWorkerThread();
        }
        return true;
    }

    private boolean checkSelfPermissions() {
        return checkSelfPermission(Manifest.permission.RECORD_AUDIO, AgoraConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO) &&
                checkSelfPermission(Manifest.permission.CAMERA, AgoraConstantApp.PERMISSION_REQ_ID_CAMERA) &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AgoraConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case AgoraConstantApp.PERMISSION_REQ_ID_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.CAMERA, AgoraConstantApp.PERMISSION_REQ_ID_CAMERA);
                } else {
                    finish();
                }
                break;
            }
            case AgoraConstantApp.PERMISSION_REQ_ID_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AgoraConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE);
                    MyApplication.getInstance().initWorkerThread();
                } else {
                    finish();
                }
                break;
            }
            case AgoraConstantApp.PERMISSION_REQ_ID_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    finish();
                }
                break;
            }
        }
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {
        checkSelfPermissions();
        initVideo();
        userPresenter = new UserPresenter();
        identify = getIntent().getStringExtra("identify");
        type = (TIMConversationType) getIntent().getSerializableExtra("type");
        presenter = new ChatPresenter(this, identify, type);
        chatOtherPresenter = new ChatOtherPresenter(this);
        input = (ChatInput) findViewById(R.id.input_panel);
        input.setChatView(this);
        adapter = new ChatAdapter(this, R.layout.item_message, messageList);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        input.setInputMode(ChatInput.InputMode.NONE);
                        break;
                }
                return false;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int firstItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && firstItem == 0) {
                    //如果拉到顶端读取更多消息
                    presenter.getMessage(sourceList.size() > 0 ? sourceList.get(0).getMessage() : null);

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;
            }
        });
        registerForContextMenu(listView);
        TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
        switch (type) {
            case C2C:
                title.setMoreImg(R.drawable.btn_person);
                if (FriendshipInfo.getInstance().isFriend(identify)) {
                    title.setMoreImgAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                            intent.putExtra("identify", identify);
                            startActivity(intent);
                        }
                    });
                    FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
                    title.setTitleText(titleStr = profile == null ? identify : profile.getName());
                } else {
                    title.setMoreImgAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent person = new Intent(ChatActivity.this, AddFriendActivity.class);
                            person.putExtra("id", identify);
                            person.putExtra("name", identify);
                            startActivity(person);
                        }
                    });
                    title.setTitleText(titleStr = identify);
                }
                break;
            case Group:
                title.setMoreImg(R.drawable.btn_group);
                title.setMoreImgAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, GroupProfileActivity.class);
                        intent.putExtra("identify", identify);
                        startActivity(intent);
                    }
                });
                title.setTitleText(GroupInfo.getInstance().getGroupName(identify));
                break;

        }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        presenter.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }


    /**
     * 显示消息，收到了新消息
     *
     * @param message
     */
    @Override
    public void showMessage(TIMMessage message) {
        if (message == null) {
            adapter.notifyDataSetChanged();
        } else {
            Message mMessage = MessageFactory.getMessage(message);
            if (mMessage != null) {
                if (mMessage instanceof CustomMessage) {
                    CustomMessage.Type messageType = ((CustomMessage) mMessage).getType();
                    switch (messageType) {
                        case TYPING:
                            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
                            title.setTitleText(getString(R.string.chat_typing));
                            handler.removeCallbacks(resetTitle);
                            handler.postDelayed(resetTitle, 3000);
                            break;
                        default:
                            break;
                    }
                } else {
//                    //
                    TextMessage textMessage = (TextMessage) mMessage;
                    final String content = textMessage.getContent();
                    if (content.startsWith(VIDEO_CHAT_REQUEST)) {
                        UserInfoModel userInfoModel = AppManager.getInstance().getUserInfoModel();
                        if (!TextUtils.equals(userInfoModel.getId() + "", textMessage.getSender())) {
                            //非自己发送的
                            OkCancelListener okCancelListener = new OkCancelListener() {
                                @Override
                                public void clickOk(Object o) {
                                    AgoraChatActivity.startThis(ChatActivity.this, content, VIDEO_CHAT_REQUEST_CODE_RECEIVER);
                                }

                                @Override
                                public void clickCancel(Object o) {
                                    sendTextMsg(VIDEO_CHAT_REFUSE);
                                }
                            };
                            chatOtherPresenter.requestVideoChat(message.getSenderProfile().getFaceUrl(), message.getSenderProfile().getNickName(), okCancelListener);

                        }

                        mMessage = setVideoSenderTip(message);
                        if (mMessage == null) {
                            return;
                        }
                    } else if (TextUtils.equals(content, VIDEO_CHAT_FAILED) ||
                            TextUtils.equals(content, VIDEO_CHAT_OVER)) {
                        mMessage = setVideoOverTip();
                        if (mMessage == null) {
                            return;
                        }
                    } else if (TextUtils.equals(content, VIDEO_CHAT_REFUSE)) {
                        return;
                    }

                    if (messageList.size() == 0) {
                        mMessage.setHasTime(null);
                    } else {
                        mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
                    }
                    messageList.add(mMessage);
                    adapter.notifyDataSetChanged();
                    listView.setSelection(adapter.getCount() - 1);
                }

            }
        }

    }

    /**
     * 构造显示通话结束的msg
     *
     * @return
     */
    private Message setVideoOverTip() {
        TIMMessage message1 = new TIMMessage();
        //添加文本内容
        TIMCustomElem elem1 = new TIMCustomElem();
        elem1.setDesc(getResources().getString(R.string.video_chat_over));
        //将elem添加到消息
        if (message1.addElement(elem1) != 0) {
            LogUtil.d("addElement failed");
            return null;
        }
        Message mMessage = MessageFactory.getMessage(message1);
        ((CustomMessage) mMessage).setType(null);
        return mMessage;
    }

    /**
     * 构造显示谁发起了视频通话的msg
     *
     * @param message
     * @return
     */
    private Message setVideoSenderTip(TIMMessage message) {
        //开始视频请求
        TIMMessage message1 = new TIMMessage();
        //添加文本内容
        TIMCustomElem elem1 = new TIMCustomElem();
        TIMUserProfile senderProfile = message.getSenderProfile();
        String name ;
        if (senderProfile != null) {
            name = senderProfile.getNickName();
        } else {
            name = message.isSelf() ? getResources().getString(R.string.mine) : null;
        }
        if (name == null) {
            return null;
        }
        elem1.setDesc(name + getResources().getString(R.string.start_video_chat_request));
        //将elem添加到消息
        if (message1.addElement(elem1) != 0) {
            LogUtil.d("addElement failed");
            return null;
        }
        Message msg = MessageFactory.getMessage(message1);
        ((CustomMessage) msg).setType(null);
        return msg;
    }

    /**
     * 显示消息,刚进入的时候显示消息
     *
     * @param messages
     */
    @Override
    public void showMessage(List<TIMMessage> messages) {
        int newMsgNum = 0;
        for (int i = 0; i < messages.size(); ++i) {
            Message mMessage = MessageFactory.getMessage(messages.get(i));
            Message mMessage1 = mMessage;
            try {
                if (mMessage instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) mMessage;
                    if (textMessage.getContent().startsWith("&video_chat_")) {
                        final String content = textMessage.getContent();
                        if (content.startsWith(VIDEO_CHAT_REQUEST)) {
                            mMessage = setVideoSenderTip(messages.get(i));
                            if (mMessage == null) {
                                continue;
                            }
                        } else if (TextUtils.equals(content, VIDEO_CHAT_FAILED) ||
                                TextUtils.equals(content, VIDEO_CHAT_OVER)) {
                            mMessage = setVideoOverTip();
                            if (mMessage == null) {
                                continue;
                            }
                        } else if (TextUtils.equals(content, VIDEO_CHAT_REFUSE)) {
                            continue;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (mMessage == null || messages.get(i).status() == TIMMessageStatus.HasDeleted)
                continue;
            if (mMessage instanceof CustomMessage && (((CustomMessage) mMessage).getType() == CustomMessage.Type.TYPING ||
                    ((CustomMessage) mMessage).getType() == CustomMessage.Type.INVALID)) continue;
            ++newMsgNum;
            if (i != messages.size() - 1) {
                mMessage.setHasTime(messages.get(i + 1));
                messageList.add(0, mMessage);
            } else {
                mMessage.setHasTime(null);
                messageList.add(0, mMessage);
            }
            sourceList.add(0, mMessage1);
        }
        adapter.notifyDataSetChanged();
        listView.setSelection(newMsgNum);
    }

    @Override
    public void showRevokeMessage(TIMMessageLocator timMessageLocator) {
        for (Message msg : messageList) {
            TIMMessageExt ext = new TIMMessageExt(msg.getMessage());
            if (ext.checkEquals(timMessageLocator)) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 清除所有消息，等待刷新
     */
    @Override
    public void clearAllMessage() {
        messageList.clear();
    }

    /**
     * 发送消息成功
     *
     * @param message 返回的消息
     */
    @Override
    public void onSendMessageSuccess(TIMMessage message) {
        showMessage(message);
    }

    /**
     * 发送消息失败
     *
     * @param code 返回码
     * @param desc 返回描述
     */
    @Override
    public void onSendMessageFail(int code, String desc, TIMMessage message) {
        long id = message.getMsgUniqueId();
        for (Message msg : messageList) {
            if (msg.getMessage().getMsgUniqueId() == id) {
                switch (code) {
                    case 80001:
                        //发送内容包含敏感词
                        msg.setDesc(getString(R.string.chat_content_bad));
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }

        adapter.notifyDataSetChanged();

    }

    /**
     * 发送图片消息
     */
    @Override
    public void sendImage() {
        Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
        intent_album.setType("image/*");
        startActivityForResult(intent_album, IMAGE_STORE);
    }

    /**
     * 发送照片消息
     */
    @Override
    public void sendPhoto() {
//        Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent_photo.resolveActivity(getPackageManager()) != null) {
//            File tempFile = FileUtil.getTempFile(FileUtil.FileType.IMG);
//            if (tempFile != null) {
//                fileUri = Uri.fromFile(tempFile);
//            }
//            intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            startActivityForResult(intent_photo, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//        }

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                CameraProtectActivity.startThisActivityForResult(this);
            } catch (Exception e) {
                ToastUtil.shortShow("没有找到储存目录");
            }
        } else {
            ToastUtil.shortShow("没有储存卡");
        }
    }

    /**
     * 发送文本消息
     */
    @Override
    public void sendText() {
        Message message = new TextMessage(input.getText());
        presenter.sendMessage(message.getMessage());
        input.setText("");
    }

    /**
     * 发送文件
     */
    @Override
    public void sendFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_CODE);
    }


    /**
     * 开始发送语音消息
     */
    @Override
    public void startSendVoice() {
        voiceSendingView.setVisibility(View.VISIBLE);
        voiceSendingView.showRecording();
        recorder.startRecording();

    }

    /**
     * 结束发送语音消息
     */
    @Override
    public void endSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
        if (recorder.getTimeInterval() < 1) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_short), Toast.LENGTH_SHORT).show();
        } else if (recorder.getTimeInterval() > 60) {
            Toast.makeText(this, getResources().getString(R.string.chat_audio_too_long), Toast.LENGTH_SHORT).show();
        } else {
            Message message = new VoiceMessage(recorder.getTimeInterval(), recorder.getFilePath());
            presenter.sendMessage(message.getMessage());
        }
    }

    /**
     * 发送小视频消息
     *
     * @param fileName 文件名
     */
    @Override
    public void sendVideo(String fileName) {
        Message message = new VideoMessage(fileName);
        presenter.sendMessage(message.getMessage());
    }


    /**
     * 结束发送语音消息
     */
    @Override
    public void cancelSendVoice() {

    }

    /**
     * 正在发送
     */
    @Override
    public void sending() {
        if (type == TIMConversationType.C2C) {
            Message message = new CustomMessage(CustomMessage.Type.TYPING);
            presenter.sendOnlineMessage(message.getMessage());
        }
    }

    /**
     * 显示草稿
     */
    @Override
    public void showDraft(TIMMessageDraft draft) {
        input.getText().append(TextMessage.getString(draft.getElems(), this));
    }

    @Override
    public void videoAction() {
        Intent intent = new Intent(this, TCVideoRecordActivity.class);
        startActivityForResult(intent, VIDEO_RECORD);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    //请求视频
    public void openVideo() {
        String channel = VIDEO_CHAT_REQUEST + System.currentTimeMillis();
//        String channel = VIDEO_CHAT_REQUEST;
        sendTextMsg(channel);


//        Intent intent = new Intent(this, AgoraMainActivity.class);
//        intent.putExtra("id", id);
//        startActivity(intent);

        MyApplication.mVideoSettings.mChannelName = channel;

//        EditText v_encryption_key = (EditText) findViewById(R.id.encryption_key);
//        String encryption = v_encryption_key.getText().toString();
//        vSettings().mEncryptionKey = encryption;

        AgoraChatActivity.startThis(this, channel, VIDEO_CHAT_REQUEST_CODE_SENDER);
    }

    private void sendTextMsg(String msg) {
        TIMMessage message = new TIMMessage();
        //添加文本内容
        TIMTextElem elem = new TIMTextElem();
        elem.setText(msg);
        //将elem添加到消息
        if (message.addElement(elem) != 0) {
            LogUtil.d("addElement failed");
            return;
        }
        presenter.sendMessage(message);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, 1, Menu.NONE, getString(R.string.chat_del));
        if (message.isSendFail()) {
            menu.add(0, 2, Menu.NONE, getString(R.string.chat_resend));
        } else if (message.getMessage().isSelf()) {
            menu.add(0, 4, Menu.NONE, getString(R.string.chat_pullback));
        }
        if (message instanceof ImageMessage || message instanceof FileMessage) {
            menu.add(0, 3, Menu.NONE, getString(R.string.chat_save));
        }
        if (message instanceof ImageMessage || message instanceof FileMessage) {
            menu.add(0, 5, Menu.NONE, getString(R.string.chat_collect));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                message.remove();
                messageList.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case 2:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
                break;
            case 3:
                message.save();
                break;
            case 4:
                presenter.revokeMessage(message.getMessage());
                break;
            case 5:
                collect(message.getMessage(), titleStr, message.getContent());
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void collect(TIMMessage msg, String titleStr, String content) {
        String id = msg.getMsgId();
        for (int i = 0; i < msg.getElementCount(); ++i) {
            TIMElem elem = msg.getElement(i);

            //获取当前元素的类型
            TIMElemType elemType = elem.getType();
            if (elemType == TIMElemType.Text) {
                //处理文本消息
            } else if (elemType == TIMElemType.Image) {
                //处理图片消息
            } else if (elemType == TIMElemType.Sound) {

            } else if (elemType == TIMElemType.File) {

            } else if (elemType == TIMElemType.Video) {

            }
        }
        CollectModel collectItem = new CollectModel();
        collectItem.setContent(content);

        userPresenter.addCollect(collectItem).subscribe(new CommSubscriber<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                ToastUtil.shortShow(getResources().getString(R.string.collect_success));
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
                ToastUtil.shortShow(getResources().getString(R.string.collect_failed));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String stringExtra = data.getStringExtra(CameraProtectActivity.IMAGE_PATH);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    String fileDir = (Environment.getExternalStorageDirectory() + stringExtra).replace("/external_storage_root", "");
                    stringExtra = fileDir;
                }
                showImagePreview(stringExtra);
            }
        } else if (requestCode == VIDEO_CHAT_REQUEST_CODE_SENDER) {
            //视频聊天结束
            sendTextMsg(VIDEO_CHAT_OVER);
        } else if (requestCode == IMAGE_STORE) {
            if (resultCode == RESULT_OK && data != null) {
                showImagePreview(FileUtil.getFilePath(this, data.getData()));
            }

        } else if (requestCode == FILE_CODE) {
            if (resultCode == RESULT_OK) {
                sendFile(FileUtil.getFilePath(this, data.getData()));
            }
        } else if (requestCode == IMAGE_PREVIEW) {
            if (resultCode == RESULT_OK) {
                boolean isOri = data.getBooleanExtra("isOri", false);
                String path = data.getStringExtra("path");
                File file = new File(path);
                if (file.exists()) {
                    final BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);
                    if (file.length() == 0 && options.outWidth == 0) {
                        Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                    } else {
                        if (file.length() > 1024 * 1024 * 10) {
                            Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
                        } else {
                            Message message = new ImageMessage(path, isOri);
                            presenter.sendMessage(message.getMessage());
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == VIDEO_RECORD) {
            if (resultCode == RESULT_OK) {
                String videoPath = data.getStringExtra("videoPath");
                String coverPath = data.getStringExtra("coverPath");
                long duration = data.getLongExtra("duration", 0);
                Message message = new UGCMessage(videoPath, coverPath, duration);
                presenter.sendMessage(message.getMessage());
            }
        }

    }


    private void showImagePreview(String path) {
        if (path == null) return;
        Intent intent = new Intent(this, ImagePreviewActivity.class);
        intent.putExtra("path", path);
        startActivityForResult(intent, IMAGE_PREVIEW);
    }

    private void sendFile(String path) {
        if (path == null) return;
        File file = new File(path);
        if (file.exists()) {
            if (file.length() > 1024 * 1024 * 1024) {
                Toast.makeText(this, getString(R.string.chat_file_too_large), Toast.LENGTH_SHORT).show();
            } else {
                Message message = new FileMessage(path);
                presenter.sendMessage(message.getMessage());
            }
        } else {
            Toast.makeText(this, getString(R.string.chat_file_not_exist), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 将标题设置为对象名称
     */
    private Runnable resetTitle = new Runnable() {
        @Override
        public void run() {
            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
            title.setTitleText(titleStr);
        }
    };


}
