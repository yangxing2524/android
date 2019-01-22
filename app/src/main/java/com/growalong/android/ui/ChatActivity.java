package com.growalong.android.ui;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.growalong.android.BuildConfig;
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
import com.growalong.android.image.ImagePagerActivity;
import com.growalong.android.listener.OkCancelListener;
import com.growalong.android.model.CourseListItemModel;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.present.ChatOtherPresenter;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.CoursePresenter;
import com.growalong.android.present.InitPresenter;
import com.growalong.android.ui.dialog.CommonAffirmDialog;
import com.growalong.android.ui.fragment.CourseRuningFragment;
import com.growalong.android.util.BitmapUtils;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.SharedPreferenceUtil;
import com.growalong.android.util.ToastUtil;
import com.growalong.android.util.TranslateHelper;
import com.growalong.android.util.Utils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMCustomElem;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMMessageStatus;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.imsdk.ext.group.TIMGroupManagerExt;
import com.tencent.imsdk.ext.message.TIMMessageDraft;
import com.tencent.imsdk.ext.message.TIMMessageExt;
import com.tencent.imsdk.ext.message.TIMMessageLocator;
import com.tencent.imsdk.ext.ugc.TIMUGCElem;
import com.tencent.qcloud.presentation.presenter.ChatPresenter;
import com.tencent.qcloud.presentation.viewfeatures.ChatView;
import com.tencent.qcloud.ui.ChatInput;
import com.tencent.qcloud.ui.TemplateTitle;
import com.tencent.qcloud.ui.VoiceSendingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatActivity extends QLActivity implements ChatView {

    public static final String LANGUAGE_TRANSLATE = "language_translate";
    @BindView(R.id.courseLayout)
    public View courseLayout;

    private static final String TAG = "ChatActivity";
    public static final String TRANSLATE_TAG = " &translate& ";

    private int mFamillyType = 0;//0代表的是中方家庭, 1代表英方

    public static final String VIDEO_CHAT_REQUEST = "&video_chat_request&";
    public static final String VIDEO_CHAT_FAILED = "&video_chat_failed&";//通话已结束
    public static final String VIDEO_CHAT_REFUSE = "&video_chat_refused&";
    public static final String VIDEO_CHAT_OVER = "&video_chat_over&";//通话已结束

    private List<Message> messageList = new ArrayList<>();
    private List<String> imageUrlList = new ArrayList<>();//图片
    private List<Message> sourceList = new ArrayList<>();
    private ChatAdapter adapter;
    private ListView listView;
    private ChatPresenter presenter;

    private ChatOtherPresenter chatOtherPresenter;

    private String mGroupName, temGroupName;
    private ChatInput input;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = CommonPhotoSelectorDialog.PHOTOREQUESTCODE1;
    public static final int VIDEO_CHAT_REQUEST_CODE_SENDER = 22;//视频发起者
    public static final int VIDEO_CHAT_REQUEST_CODE_RECEIVER = 21;//视频接收者
    public static final int VIDEO_CHAT_TWO_BREAKE = 23;//视频只剩两个人，然后自己主动挂断
    private static final int IMAGE_STORE = 200;
    private static final int FILE_CODE = 300;
    public static final int IMAGE_PREVIEW = 400;
    private static final int VIDEO_RECORD = 500;
    private VoiceSendingView voiceSendingView;
    private String identify;
    private RecorderUtil recorder = new RecorderUtil();
    private TIMConversationType type;
    private Handler handler = new Handler();

    public static ShowTranslate showTranslate = ShowTranslate.Normal;

    public enum ShowTranslate {
        Chinese,
        English,
        ChineseAndEnglish,
        Normal
    }

    public static void startThis(Context context, String identify, TIMConversationType type, String name) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        context.startActivity(intent);
    }

    public static void startThis(Context context, String identify, TIMConversationType type, String name,
                                 final String content, String senderId, String nickName, String faceUrl) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("identify", identify);
        intent.putExtra("type", type);
        intent.putExtra("name", name);
        intent.putExtra("content", content);
        intent.putExtra("senderId", senderId);
        intent.putExtra("nickName", nickName);
        intent.putExtra("faceUrl", faceUrl);
        intent.putExtra("isRequestedVideochat", true);
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
        int languageIndex = SharedPreferenceUtil.getInstance(this).getInt(LANGUAGE_TRANSLATE, 0);
        showTranslate = ShowTranslate.values()[languageIndex];
        checkSelfPermissions();
        initVideo();
        int nation = AppManager.getInstance().getUserInfoModel().getNation();
        if (nation == 1) {
            mFamillyType = 0;
        } else if (nation == 2) {
            mFamillyType = 1;
        }

        identify = getIntent().getStringExtra("identify");
        mGroupName = getIntent().getStringExtra("name");

        type = (TIMConversationType) getIntent().getSerializableExtra("type");
        if (TextUtils.isEmpty(mGroupName)) {
            if (type == TIMConversationType.Group) {
                mGroupName = GroupInfo.getInstance().getGroupName(identify);
                if (mGroupName.equals("")) mGroupName = identify;
            } else {
                FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
                mGroupName = profile == null ? identify : profile.getName();
            }
        }

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

        //选择显示语言
        final TemplateTitle title = findViewById(R.id.chat_title);
        title.showMoreImg();
        title.setTitleText(mGroupName);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ChatActivity.this, title.getTvMore());
                int id = R.menu.menu_select_translate;
                if (MyApplication.TYPE == MyApplication.TYPE_B || BuildConfig.DEVELOPMENT_ENV) {
                    id = R.menu.menu_select_translate_b;
                }
                popup.getMenuInflater().inflate(id, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Resources resources = getResources();
                        if (resources.getString(R.string.showChinese).equals(item.getTitle())) {
                            showTranslate = ShowTranslate.Chinese;
                        } else if (resources.getString(R.string.showEnglish).equals(item.getTitle())) {
                            showTranslate = ShowTranslate.English;
                        } else if (resources.getString(R.string.showChinaAndEnglish).equals(item.getTitle())) {
                            showTranslate = ShowTranslate.ChineseAndEnglish;
                        } else if (resources.getString(R.string.modify_group_name).equals(item.getTitle())) {
                            //修改群名
                            CommonAffirmDialog.Builder(3).setEditText(mGroupName).setIAffirmDialogInput(new CommonAffirmDialog.IAffirmDialogInput() {
                                @Override
                                public void input(String str) {
                                    temGroupName = str;
                                }
                            }).setIAffirmDialogClick(new CommonAffirmDialog.IAffirmDialogClick() {
                                @Override
                                public void onOkClick() {
                                    TIMGroupManagerExt.ModifyGroupInfoParam param = new TIMGroupManagerExt.ModifyGroupInfoParam(identify);
                                    param.setGroupName(temGroupName);
                                    TIMGroupManagerExt.getInstance().modifyGroupInfo(param, new TIMCallBack() {
                                        @Override
                                        public void onError(int code, String desc) {
                                            LogUtil.e("modify group info failed, code:" + code + "|desc:" + desc);
                                        }

                                        @Override
                                        public void onSuccess() {
                                            mGroupName = temGroupName;
                                            TemplateTitle title = (TemplateTitle) findViewById(R.id.chat_title);
                                            title.setTitleText(mGroupName);
                                            LogUtil.e("modify group info succ");
                                        }
                                    });

                                }

                                @Override
                                public void onCancelClick() {

                                }
                            }).show(getSupportFragmentManager(), "");
                        } else {
                            showTranslate = ShowTranslate.Normal;
                        }
                        SharedPreferenceUtil.getInstance(ChatActivity.this).setInt(LANGUAGE_TRANSLATE, showTranslate.ordinal());
                        adapter.notifyDataSetChanged();
                        adapter.notifyDataSetInvalidated();
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method
        switch (type) {
            case C2C:
//                title.setMoreImg(R.drawable.btn_person);
//                if (FriendshipInfo.getInstance().isFriend(identify)) {
//                    title.setMoreImgAction(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
//                            intent.putExtra("identify", identify);
//                            startActivity(intent);
//                        }
//                    });
//                    FriendProfile profile = FriendshipInfo.getInstance().getProfile(identify);
//                    title.setTitleText(titleStr = profile == null ? identify : profile.getName());
//                } else {
//                    title.setMoreImgAction(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent person = new Intent(ChatActivity.this, AddFriendActivity.class);
//                            person.putExtra("id", identify);
//                            person.putExtra("name", identify);
//                            startActivity(person);
//                        }
//                    });
//                    title.setTitleText(titleStr = identify);
//                }
                break;
            case Group:
                CoursePresenter coursePresenter = new CoursePresenter();
                coursePresenter.getCourList(CourseRuningFragment.STARTING_COURSE, 1, null, null).subscribe(new CommSubscriber<List<CourseListItemModel>>() {
                    @Override
                    public void onSuccess(final List<CourseListItemModel> courseListItemModels) {
                        courseLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CourseDetailActivity.startThis(courseListItemModels.get(0).getId(), ChatActivity.this);
                            }
                        });
                        if (courseListItemModels == null || courseListItemModels.size() == 0) {
                            courseLayout.setVisibility(View.GONE);
                        } else {
                            courseLayout.setVisibility(View.VISIBLE);
                            ((TextView) courseLayout.findViewById(R.id.courseTitle)).setText(courseListItemModels.get(0).getTitle());
                        }
                    }
                });
//                title.setMoreImg(R.drawable.btn_group);
//                title.setMoreImgAction(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(ChatActivity.this, GroupProfileActivity.class);
//                        intent.putExtra("identify", identify);
//                        startActivity(intent);
//                    }
//                });
//                title.setTitleText(GroupInfo.getInstance().getGroupName(identify));
                break;

        }
        voiceSendingView = (VoiceSendingView) findViewById(R.id.voice_sending);
        presenter.start();

        chatOtherPresenter.getUserInfos(identify);

        if (getIntent().getBooleanExtra("isRequestedVideochat", false)) {
            Bundle extras = getIntent().getExtras();
            final String content = extras.getString("content");
            final String senderId = extras.getString("senderId");
            final String nickName = extras.getString("nickName");
            final String faceUrl = extras.getString("faceUrl");
            MyApplication.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    requestVideoChat(content, senderId, nickName, faceUrl);
                }
            }, 1000);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (input.isMorePanelVisiable()) {
                input.hidMorePanel();
                return true;
            } else if (input.isEmotionVisiable()) {
                input.hidEmoticonPanel();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
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
                } else if (mMessage instanceof ImageMessage) {
                    ImageMessage imageMessage = (ImageMessage) mMessage;
                    addImageToList(imageMessage);
                    messageList.add(mMessage);
                    adapter.notifyDataSetChanged();
                    scorllToBottom(0);
                } else if (mMessage instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) mMessage;
                    final String content = textMessage.getContent();
                    if (content.startsWith(VIDEO_CHAT_REQUEST)) {
                        if (chatOtherPresenter.isDialogShow() || AppManager.getInstance().isExistActivity(AgoraChatActivity.class)) {
                            return;
                        }
                        UserInfoModel userInfoModel = AppManager.getInstance().getUserInfoModel();
                        if (!TextUtils.equals(userInfoModel.getId() + "", textMessage.getSender())) {
                            //非自己发送的
                            requestVideoChat(content, message.getSender(), message.getSenderProfile().getNickName(), message.getSenderProfile().getFaceUrl());
                        }

                        mMessage = setVideoSenderTip(message);
                        if (mMessage == null) {
                            return;
                        }
                    } else if (TextUtils.equals(content, VIDEO_CHAT_FAILED) ||
                            TextUtils.equals(content, VIDEO_CHAT_OVER)) {
                        chatOtherPresenter.dismissDialog();
                        AppManager.getInstance().finishActivity(AgoraChatActivity.class);
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
                    scorllToBottom(0);
                } else {
                    if (messageList.size() == 0) {
                        mMessage.setHasTime(null);
                    } else {
                        mMessage.setHasTime(messageList.get(messageList.size() - 1).getMessage());
                    }
                    messageList.add(mMessage);
                    adapter.notifyDataSetChanged();
                    scorllToBottom(0);
                }

            }
        }

    }

    private void requestVideoChat(final String content, String senderId, String nickName, String faceUrl) {
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
        if (AppManager.userHeadMap.get(senderId) == null) {
            return;
        }
        String url = AppManager.userHeadMap.get(senderId).getHeadImgUrl();
        if (TextUtils.isEmpty(url)) {
            url = faceUrl;
        }
        chatOtherPresenter.requestVideoChat(url, nickName, okCancelListener);
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
        String name;
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
                    final String content = textMessage.getContent();
                    if (content.startsWith("&video_chat_")) {
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
                } else if (mMessage instanceof ImageMessage) {
                    ImageMessage imageMessage = (ImageMessage) mMessage;
                    addImageToList(imageMessage);
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

    private void addImageToList(ImageMessage imageMessage) {
        final String url = imageMessage.getContent();
        imageUrlList.add(url);
        View.OnClickListener imageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = imageUrlList.indexOf(url);
                String[] list = new String[imageUrlList.size()];
                for (int j = 0; j < imageUrlList.size(); j++) {
                    list[j] = imageUrlList.get(j);
                }
                ImagePagerActivity.startThisActivity(ChatActivity.this, list, index, false);
            }
        };
        imageMessage.setOnclickListener(imageClickListener);
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
        final String text = input.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        MyApplication.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new TextMessage(text);
                                presenter.sendMessage(message.getMessage());
                                input.setText("");
                                LogUtil.e(e.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String response1 = response.body().string();
                        String content = text;
                        try {
                            JSONObject srcJson = new JSONObject(response1);
                            JSONArray jsonArray = srcJson.getJSONArray("trans_result");
                            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                            String dst = jsonObject.getString("dst");
                            String src = jsonObject.getString("src");
                            if (mFamillyType == 0) {
                                content = mFamillyType + src + TRANSLATE_TAG + dst;
                            } else {
                                content = mFamillyType + dst + TRANSLATE_TAG + src;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final String content1 = content;
                        MyApplication.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new TextMessage(content1);
                                presenter.sendMessage(message.getMessage());
                                input.setText("");
                                scorllToBottom(0);
                            }
                        });

                        Log.e("response ", "onResponse(): " + response1);
                    }
                };
                String from = mFamillyType == 0 ? TranslateHelper.TrLanguage.zh.toString() : TranslateHelper.TrLanguage.en.toString();
                String to = mFamillyType == 0 ? TranslateHelper.TrLanguage.en.toString() : TranslateHelper.TrLanguage.zh.toString();
                TranslateHelper.getTransResult(text, from, to, callback);

            }
        }).start();

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
        } else if (recorder.getTimeInterval() > 180) {
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
     * 取消发送语音消息
     */
    @Override
    public void cancelSendVoice() {
        voiceSendingView.release();
        voiceSendingView.setVisibility(View.GONE);
        recorder.stopRecording();
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
        String channel = VIDEO_CHAT_REQUEST + identify;
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

    @Override
    public void scorllToBottom(int delay) {
        MyApplication.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(adapter.getCount() - 1);
            }
        }, delay);

    }

    @Override
    public void showCancelVoiceView() {
        voiceSendingView.showCancelVoiceView();
    }

    @Override
    public void showRecordVoiceView() {
        voiceSendingView.showRecordVoiceView();
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

    final int COPY = 1, DELETE = 2, RESEND = 3, SAVE = 4, COLLECT = 5;


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Message message = messageList.get(info.position);
        menu.add(0, DELETE, Menu.NONE, getString(R.string.chat_del));
        if (message.isSendFail()) {
            menu.add(0, RESEND, Menu.NONE, getString(R.string.chat_resend));
        }
//        else if (message.getMessage().isSelf()) {
//            menu.add(0, 4, Menu.NONE, getString(R.string.chat_pullback));
//        }
        if (message instanceof ImageMessage || message instanceof FileMessage) {
            menu.add(0, SAVE, Menu.NONE, getString(R.string.chat_save));
        }
        if (message instanceof TextMessage) {
            menu.add(0, COPY, Menu.NONE, getString(R.string.copy));
        }
        if (message instanceof ImageMessage || message instanceof TextMessage || message instanceof VideoMessage) {
            menu.add(0, COLLECT, Menu.NONE, getString(R.string.chat_collect));
        }

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Message message = messageList.get(info.position);
        switch (item.getItemId()) {
            case DELETE:
                message.remove();
                messageList.remove(info.position);
                adapter.notifyDataSetChanged();
                break;
            case RESEND:
                messageList.remove(message);
                presenter.sendMessage(message.getMessage());
                break;
            case SAVE:
                message.save();
                break;
            case COPY:
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                TextMessage textMessage = (TextMessage) message;
                cmb.setText(Utils.getIMTextNormal(textMessage.getContent()));
                break;
            case COLLECT:
                TIMMessage timMessage = message.getMessage();
                if (timMessage.getElement(0) instanceof TIMUGCElem) {
                    final TIMUGCElem e = (TIMUGCElem) message.getMessage().getElement(0);
                    final String fileName = e.getFileId() + "_video";
                    chatOtherPresenter.collect(message.getMessage(), fileName, message.getContent(), mGroupName, message.getInfo()[0]);
                } else if (message instanceof TextMessage || message instanceof ImageMessage) {
                    chatOtherPresenter.collect(message.getMessage(), mGroupName, message.getContent(), mGroupName, "");
                } else if (message instanceof VoiceMessage) {
                    chatOtherPresenter.collect(message.getMessage(), mGroupName, message.getContent(), mGroupName, message.getInfo()[0]);
                }
                break;
//            presenter.revokeMessage(message.getMessage());
//                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    String stringExtra = data.getStringExtra(CameraProtectActivity.IMAGE_PATH);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        String fileDir = (Environment.getExternalStorageDirectory() + stringExtra).replace("/external_storage_root", "");
                        stringExtra = fileDir;
                    }
                    showImagePreview(stringExtra);
                }
            } else if (requestCode == VIDEO_CHAT_REQUEST_CODE_SENDER ||
                    requestCode == VIDEO_CHAT_REQUEST_CODE_RECEIVER) {
                //视频聊天结束
                if (resultCode == VIDEO_CHAT_TWO_BREAKE) {
                    sendTextMsg(VIDEO_CHAT_OVER);
                }
            } else if (requestCode == IMAGE_STORE) {
                String path = FileUtil.getFilePath(this, data.getData());

                if (path != null && path.endsWith(".mp4")) {
                    String videoPath = path;
                    long duration = data.getLongExtra("duration", 10);

                    //创建MediaMetadataRetriever对象
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//设置资源位置
//绑定资源
                    mmr.setDataSource(videoPath);
//获取第一帧图像的bitmap对象
                    Bitmap bitmap = mmr.getFrameAtTime();

                    File coverFile = BitmapUtils.saveBitmapFile(bitmap);
                    Message message = new UGCMessage(videoPath, coverFile.getPath(), duration);
                    presenter.sendMessage(message.getMessage());
                } else {
                    showImagePreview(path);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void showImagePreview(String path) {
        if (path == null)
            return;
        ImagePreviewActivity.startThis(this, path);
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
            title.setTitleText(mGroupName);
        }
    };


}
