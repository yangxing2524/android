package com.growalong.android.trtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.trtc.adapter.ChatMsgAdapter;
import com.growalong.android.trtc.model.ConfigInfo;
import com.growalong.android.trtc.model.MessageObservable;
import com.growalong.android.trtc.model.RoomTipsInfo;
import com.growalong.android.trtc.view.DlgMgr;
import com.growalong.android.trtc.view.MsgListView;
import com.growalong.android.trtc.view.RadioGroupDialog;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.data.ILiveMessage;
import com.tencent.ilivesdk.data.msg.ILiveTextMessage;
import com.tencent.ilivesdk.listener.ILiveEventHandler;
import com.tencent.ilivesdk.listener.ILiveMessageListener;
import com.tencent.ilivesdk.tools.quality.ILiveQualityData;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.liteav.beauty.TXCVideoPreprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by tencent on 2018/5/21.
 */
public class RoomActivity extends Activity implements ILiveMessageListener, View.OnClickListener {
    private final static String TAG = "RoomActivity";
    private AVRootView avRootView;
    private TextView tvRoomId;
    private ImageView ivSwitch, ivBeauty, ivMic, ivLog;
    private EditText etMsg;
    private MsgListView lvChatMsg;
    private ChatMsgAdapter msgAdapter;
    private boolean bFirstBackPress = true;
    private ArrayList<ILiveMessage> chatMsg = new ArrayList<>();

    private boolean bFrontCamera = true, bBeautyEnable = true, bMicEnable = true, bLogEnable = false, bChatEnable = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化UI
        initView();

        // 初始化对话框
        initRoleDialog();
        initFeedBackDialog();
        // 初始化美颜
        enableBeauty(bBeautyEnable);

        // 添加事件监听回调
        ILiveSDK.getInstance().addEventHandler(eventHandler);
        MessageObservable.getInstance().addObserver(this);
    }

    private Context getContext() {
        return isFinishing() ? null : this;
    }

    //////////////////////////////////    TRTC主要流程   ////////////////////////////////////////

    /** 加入音视频房间 */
    private void enterTrtcRoom(int roomId){
        // Step 3: 设置渲染控件
        ILiveRoomManager.getInstance().initAvRootView(avRootView);
        customRootView(avRootView);     // 定制渲染控件

        // Step 4: 加入音视频房间
        ILiveRoomManager.getInstance().joinRoom(roomId, new ILiveRoomOption()
            .controlRole("user"));
    }

    /** 处理TRTC 事件 */
    private ILiveEventHandler eventHandler = new ILiveEventHandler(){
        @Override
        public void onForceOffline(String userId, String module, int errCode, String errMsg) {
            DlgMgr.showMsg(getContext(), "帐号被踢下线: " + module + "|" + errCode + "|" + errMsg);
            finish();
        }

        @Override
        public void onCreateRoomSuccess(int roomId, String groupId) {
            DlgMgr.showToast(getContext(), "创建房间成功");
        }

        @Override
        public void onCreateRoomFailed(int roomId, String module, int errCode, String errMsg) {
            DlgMgr.showMsg(getContext(), "创建房间失败: " + module + "|" + errCode + "|" + errMsg);
        }

        @Override
        public void onJoinRoomSuccess(int roomId, String groupId) {
            DlgMgr.showToast(getContext(), "加入房间成功");
        }

        @Override
        public void onJoinRoomFailed(int roomId, String module, int errCode, String errMsg) {
            if (module.equals(ILiveConstants.Module_IMSDK) && (10010 == errCode || 10015 == errCode)){
                ILiveRoomManager.getInstance().createRoom(roomId, new ILiveRoomOption()
                        .controlRole("user"));
            }else {
                DlgMgr.showMsg(getContext(), "加入房间失败: " + module + "|" + errCode + "|" + errMsg);
            }
        }

        @Override
        public void onQuitRoomSuccess(int roomId, String groupId) {
            finish();
        }

        @Override
        public void onQuitRoomFailed(int roomId, String module, int errCode, String errMsg) {
            DlgMgr.showMsg(getContext(), "退出房间失败: " + module + "|" + errCode + "|" + errMsg);
        }
    };

    //////////////////////////////////    UI处理   ////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (null != intent){
            int roomId = intent.getIntExtra("roomId", 0);
            if (0 != roomId) {
                enterTrtcRoom(roomId);
                tvRoomId.setText("" + roomId);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ILiveSDK.getInstance().clearEventHandler();
        MessageObservable.getInstance().deleteObserver(this);
    }

    @Override
    public void onBackPressed() {
        if (bChatEnable){
            changeChatStatus(false);
            return;
        }
        if (bFirstBackPress) {
            bFirstBackPress = false;
            ILiveRoomManager.getInstance().quitRoom();
        }else{
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_switch){
            bFrontCamera = !bFrontCamera;
            ILiveRoomManager.getInstance().switchCamera(bFrontCamera ? ILiveConstants.FRONT_CAMERA : ILiveConstants.BACK_CAMERA);
            ivSwitch.setImageResource(bFrontCamera ? R.mipmap.camera : R.mipmap.camera2);
        }else if (v.getId() == R.id.ll_voice){
            bMicEnable = !bMicEnable;
            ILiveRoomManager.getInstance().enableMic(bMicEnable);
            ivMic.setImageResource(bMicEnable ? R.mipmap.mic : R.mipmap.mic2);
        }else if (v.getId() == R.id.ll_log){
            changeLogStatus(!bLogEnable);
        }else if (v.getId() == R.id.ll_beauty){
            bBeautyEnable = !bBeautyEnable;
            enableBeauty(bBeautyEnable);
            ivBeauty.setImageResource(bBeautyEnable ? R.mipmap.beauty : R.mipmap.beauty2);
        }else if (v.getId() == R.id.ll_role){
            if (null != roleDialog)
                roleDialog.show();
        }else if (v.getId() == R.id.ll_chat){
            changeChatStatus(!bChatEnable);
        }else if (v.getId() == R.id.ll_feedback){
            if (null != feedDialog) {
                feedDialog.clearCheck();
                if (null != inputDlg && inputDlg.isShowing()){
                    inputDlg.dismiss();
                    inputDlg = null;
                }
                feedDialog.show();
            }
        }
    }

    private LinearLayout initClickableLayout(int resId) {
        LinearLayout layout = (LinearLayout) findViewById(resId);
        layout.setOnClickListener(this);
        return layout;
    }

    private void initView() {
        setContentView(R.layout.room_activity);
        avRootView = (AVRootView) findViewById(R.id.av_root_view);
        tvRoomId = (TextView) findViewById(R.id.tv_room_id);
        initClickableLayout(R.id.ll_chat);
        initClickableLayout(R.id.ll_switch);
        initClickableLayout(R.id.ll_beauty);
        initClickableLayout(R.id.ll_voice);
        initClickableLayout(R.id.ll_log);
        initClickableLayout(R.id.ll_role);
        initClickableLayout(R.id.ll_feedback);
        ivSwitch = (ImageView) findViewById(R.id.iv_switch);
        ivBeauty = (ImageView) findViewById(R.id.iv_beauty);
        ivMic = (ImageView) findViewById(R.id.iv_mic);
        ivLog = (ImageView) findViewById(R.id.iv_log);
        lvChatMsg = (MsgListView) findViewById(R.id.lv_chat_msg);
        etMsg = (EditText) findViewById(R.id.et_msg);
        etMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    if (TextUtils.isEmpty(v.getText().toString())) {
                        return false;
                    }
                    sendGroupMessage(v.getText().toString());
                    v.setText("");
                }
                return false;
            }
        });

        msgAdapter = new ChatMsgAdapter(this, chatMsg);
        lvChatMsg.setAdapter(msgAdapter);
    }


    private void notifyChatUpdate(){
        msgAdapter.notifyDataSetChanged();
    }

    //////////////////////////////////    收发消息   ////////////////////////////////////////
    private void changeChatStatus(boolean enable) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        bChatEnable = enable;
        if (bChatEnable) {
            etMsg.setVisibility(View.VISIBLE);
            etMsg.requestFocus();
            //打开软键盘
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            //关闭软键盘
            imm.hideSoftInputFromWindow(etMsg.getWindowToken(), 0);
            etMsg.clearFocus();
            etMsg.setVisibility(View.GONE);
        }
    }

    /** 发送群组消息 */
    private void sendGroupMessage(final String msg){
        ILiveTextMessage textMessage = new ILiveTextMessage(msg);
        ILiveRoomManager.getInstance().sendGroupMessage(textMessage, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveTextMessage textMessage = new ILiveTextMessage(msg);
                textMessage.setSender(ILiveLoginManager.getInstance().getMyUserId());
                chatMsg.add(textMessage);
                notifyChatUpdate();
                changeChatStatus(false);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                DlgMgr.showToast(getContext(), "发送消息失败: " + module + "|" + errCode + "|" + errMsg);
                changeChatStatus(false);
            }
        });
    }

    public void onNewMessage(ILiveMessage message) {
        ILiveLog.ki(TAG, "onNewMessage", new ILiveLog.LogExts().put("msgType", message.getMsgType()).put("conversationType",
                message.getConversationType()).put("sender", message.getSender()));
        switch (message.getMsgType()) {
            case ILiveMessage.ILIVE_MSG_TYPE_TEXT:
                // 文本消息
                chatMsg.add(message);
                notifyChatUpdate();
                break;
            default:
                ILiveLog.w(TAG, "onNewMessage-> message type: " + message.getMsgType());
                break;
        }
    }

    //////////////////////////////////    渲染定制   ////////////////////////////////////////

    private void customRootView(final AVRootView rootView){
        rootView.setGravity(AVRootView.LAYOUT_GRAVITY_RIGHT);     // 右侧展示小窗口
        rootView.setBackground(R.mipmap.com_bg);                  // 设置背景图片
        rootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
            @Override
            public void onSubViewCreated() {
                for (int i=1; i<ILiveConstants.MAX_AV_VIDEO_NUM; i++){
                    final int index = i;
                    final AVVideoView videoView = rootView.getViewByIndex(i);
                    videoView.setDragable(true);        // 可拖动
                    videoView.setGestureListener(new GestureDetector.SimpleOnGestureListener(){
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {        // 小屏点击交换
                            rootView.swapVideoView(0, index);
                            return super.onSingleTapConfirmed(e);
                        }
                    });
                }
            }
        });
    }

    //////////////////////////////////    美颜    ////////////////////////////////////////

    private TXCVideoPreprocessor mTxcFilter;
    private void enableBeauty(boolean enable){
        if (null == mTxcFilter){
            mTxcFilter = new TXCVideoPreprocessor(avRootView.getContext(), false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

                mTxcFilter.setBeautyStyle(0);           // 设置美颜风格，0: 光滑 1: 自然 2: 朦胧
                mTxcFilter.setBeautyLevel(5);           // 设置美颜级别,范围 0～10
                mTxcFilter.setWhitenessLevel(3);        // 设置美白级别,范围 0～10
                mTxcFilter.setRuddyLevel(2);            // 设置红润级别，范围 0～10
            }
        }
        if (enable) {
            ((AVVideoCtrl)ILiveSDK.getInstance().getVideoEngine().getVideoObj()).setAfterPreviewListener(new AVVideoCtrl.AfterPreviewListener(){
                @Override
                public void onFrameReceive(AVVideoCtrl.VideoFrame var1) {
                    // 回调的数据，传递给 ilivefilter processFrame 接口处理;
                    if (null != mTxcFilter && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        mTxcFilter.processFrame(var1.data, var1.width, var1.height, var1.rotate, var1.videoFormat, var1.videoFormat);
                    }
                }
            });
        }else{
            ((AVVideoCtrl)ILiveSDK.getInstance().getVideoEngine().getVideoObj()).setAfterPreviewListener(null);
        }
    }

    //////////////////////////////////    视频信息    ////////////////////////////////////////

    private Runnable infoRun = new Runnable() {
        @Override
        public void run() {
            if (!bLogEnable)
                return;
            ILiveQualityData qualityData = ILiveRoomManager.getInstance().getQualityData();
            if (null != qualityData) {
                RoomTipsInfo tipInfo = new RoomTipsInfo(((AVRoomMulti) ILiveSDK.getInstance().getContextEngine().getRoomObj()).getQualityTips());
                String info = "发送速率:\t" + qualityData.getSendKbps() + "kbps\t"
                        + "丢包率:\t" + qualityData.getSendLossRate() / 100 + "%\n"
                        + "接收速率:\t" + qualityData.getRecvKbps() + "kbps\t"
                        + "丢包率:\t" + qualityData.getRecvLossRate() / 100 + "%\n"
                        + "应用CPU:\t" + qualityData.getAppCPURate() + "%\t"
                        + "系统CPU:\t" + qualityData.getSysCPURate() + "%\n";

                info += "角色: "+tipInfo.getCurRole() + "\n";
                info += "SDKAPPID: " + ILiveSDK.getInstance().getAppId() + "\nVersion:" + ILiveSDK.getInstance().getVersion();
                ((TextView) findViewById(R.id.tv_status)).setText(info);
            }
            ILiveSDK.getInstance().runOnMainThread(this, 2000);
        }
    };

    private void changeLogStatus(boolean enable){
        bLogEnable = enable;
        findViewById(R.id.tv_status).setVisibility(bLogEnable ? View.VISIBLE : View.GONE);
        ivLog.setImageResource(bLogEnable ? R.mipmap.log2 : R.mipmap.log);

        if (bLogEnable){
            ILiveSDK.getInstance().runOnMainThread(infoRun, 0);
        }
    }

    //////////////////////////////////    角色切换    ////////////////////////////////////////

    // 角色对话框
    private RadioGroupDialog roleDialog;

    private void initRoleDialog() {
        HashMap<String, String> roleMap = ConfigInfo.getInstance().getRoleMap();
        final String[] roles = new String[roleMap.size()];
        final String[] values = new String[roleMap.size()];
        int pos = 0;
        for (Map.Entry entry : roleMap.entrySet()){
            roles[pos] = (String)entry.getKey();
            values[pos] = roles[pos]+"("+entry.getValue()+")";
            pos ++;
        }
        roleDialog = new RadioGroupDialog(this, values);
        roleDialog.setTitle(R.string.str_set_role);
        //roleDialog.setSelected(2);
        roleDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                ILiveRoomManager.getInstance().changeRole(roles[position], null);
            }
        });
    }

    //////////////////////////////////    反馈信息    ////////////////////////////////////////
    // 反馈对话框
    private RadioGroupDialog feedDialog;
    private void initFeedBackDialog() {
        final String[] problems = new String[]{"视频卡顿", "画面不清晰", "音质较差", "其他"};
        feedDialog = new RadioGroupDialog(this, problems);
        feedDialog.setTitle(R.string.str_set_problem);
        feedDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(final int position) {
                feedDialog.dismiss();
                if (position == 3) {
                    showInputDialog();
                }
            }
        });
    }

    private AlertDialog inputDlg;
    private void showInputDialog() {
        if (null != inputDlg && inputDlg.isShowing()){
            inputDlg.dismiss();
            inputDlg = null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.full_dlg);
        builder.setTitle(R.string.str_problem_other);

        final EditText input = new EditText(getContext());
        input.setSingleLine(false);
        input.setTextColor(Color.BLACK);
        builder.setView(input);

        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        inputDlg = builder.create();
        inputDlg.setCanceledOnTouchOutside(true);
        inputDlg.show();
    }
}
