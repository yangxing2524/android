package com.growalong.android.trtc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.trtc.model.ConfigInfo;
import com.growalong.android.trtc.model.MessageObservable;
import com.growalong.android.trtc.view.DlgMgr;
import com.growalong.android.trtc.view.RadioGroupDialog;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomConfig;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.listener.ILiveEventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tencent on 2018/5/21.
 */
public class CreateActivity extends Activity {
    public final static int REQ_PERMISSION_CODE = 0x1000;

    private EditText etRoomId;
    private TextView tvCreate;

    private boolean bLogin = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化UI界面
        initView();

        // 读取配置文件
        loadConfigData();

        // 申请动态权限
        checkPermission();

        if (ConfigInfo.getInstance().isConfigLoad()) {
            // 显示帐户选择对话框
            showUserDlg();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ILiveLoginManager.getInstance().isLogin()){
            ILiveLoginManager.getInstance().iLiveLogout();
        }
    }

    private Context getContext(){
        return this;
    }

    //////////////////////////////////    TRTC主要流程   ////////////////////////////////////////

    /** 初始化并登录TRTC SDK */
    private void afterUserSelected(String userId){
        // Step 1: 初始化TRTC SDK
        initTrtcSDK(ConfigInfo.getInstance().getSdkAppId());

        // Step 2: 登录TRTC SDK
        loginTrtcSDK(userId, ConfigInfo.getInstance().getUserMap().get(userId));
    }

    /** 初始化TRTC SDK */
    private void initTrtcSDK(int sdkAppId){
        // 初始化ILiveSDK
        ILiveSDK.getInstance().initSdk(getContext(), sdkAppId, 0);
        // 初始化房间模块
        ILiveRoomManager.getInstance().init(new ILiveRoomConfig()
                .setRoomMsgListener(MessageObservable.getInstance()));
        //ILiveSDK.getInstance().setChannelMode(CommonConstants.E_ChannelMode.E_ChannelIMSDK);
    }

    /** 登录TRTC SDK */
    private void loginTrtcSDK(String useId, String userSig){
        // 添加事件监听
        ILiveSDK.getInstance().addEventHandler(new ILiveEventHandler(){
            // 登录成功事件
            @Override
            public void onLoginSuccess(String userId) {
                bLogin = true;
                DlgMgr.showToast(getContext(), getString(R.string.str_login_success));
            }
            // 登录失败事件
            @Override
            public void onLoginFailed(String userId, String module, int errCode, String errMsg) {
                DlgMgr.showMsg(getContext(), "登录失败: " + module + "|" + errCode + "|" + errMsg);
            }
            // 帐号被踢
            @Override
            public void onForceOffline(String userId, String module, int errCode, String errMsg) {
                bLogin = false;
                DlgMgr.showMsg(getContext(), "帐号被踢下线: " + module + "|" + errCode + "|" + errMsg);
            }
        });

        // 登录SDK
        ILiveLoginManager.getInstance().iLiveLogin(useId, userSig, null);
    }

    //////////////////////////////////    选择用户   ////////////////////////////////////////

    /** 读取配置文件 */
    private void loadConfigData(){
        try {
            ConfigInfo.getInstance().loadConfig(getContext(), R.raw.config);
        } catch (Exception e){
            DlgMgr.showMsg(getContext(), "读取配置文件失败，请在【控制台】->【快速上手】中生成配置内容复制到config.json文件");
        }
    }

    /** 显示用户登录选择对话框 */
    private void showUserDlg(){
        int pos = 0;
        int size = ConfigInfo.getInstance().getUserMap().size();
        final String userIds[] = new String[size];
        for (Map.Entry entry : ConfigInfo.getInstance().getUserMap().entrySet()){
            userIds[pos++] = (String)entry.getKey();
        }
        RadioGroupDialog dialog = new RadioGroupDialog(getContext(), userIds);
        dialog.setTitle("请选择登录的用户:");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                /** 初始化并登录TRTC SDK */
                afterUserSelected(userIds[position]);
            }
        });
        dialog.show();
    }

    //////////////////////////////////    初始化UI   ////////////////////////////////////////

    /** 初始化UI控件 */
    private void initView(){
        setContentView(R.layout.create_activity);
        etRoomId = (EditText)findViewById(R.id.et_room_name);
        tvCreate = (TextView)findViewById(R.id.tv_enter);
        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bLogin) {
                    DlgMgr.showMsg(getContext(), "请先等待登录成功");
                    return;
                }
                int roomId = Integer.valueOf(etRoomId.getText().toString());
                Intent intent = new Intent(getContext(), RoomActivity.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });
    }

    //////////////////////////////////    动态权限申请   ////////////////////////////////////////

    /** 动态权限申请 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(CreateActivity.this,permissions.toArray(new String[0]), REQ_PERMISSION_CODE);
                return false;
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                for (int ret : grantResults) {
                    if (PackageManager.PERMISSION_GRANTED != ret) {
                        DlgMgr.showMsg(getContext(), "用户没有允许需要的权限，使用可能会受到限制！");
                    }
                }
                break;
            default:
                break;
        }
    }
}
