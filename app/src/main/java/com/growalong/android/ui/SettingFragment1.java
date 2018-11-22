package com.growalong.android.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.model.UploadModel;
import com.growalong.android.upload.IUploadCallBack;
import com.growalong.android.upload.UploadOssHelper;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.ToastUtil;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFriendAllowType;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;
import com.tencent.qcloud.ui.LineControllerView;
import com.tencent.qcloud.ui.ListPickerDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置页面
 */
public class SettingFragment1 extends Fragment implements FriendInfoView {

    private static final String TAG = SettingFragment1.class.getSimpleName();
    private View view;
    private FriendshipManagerPresenter friendshipManagerPresenter;
    private TextView id,name;
    private LineControllerView nickName, friendConfirm;
    private final int REQ_CHANGE_NICK = 1000;
    private Map<String, TIMFriendAllowType> allowTypeContent;

    CommonPhotoSelectorDialog photoSelectorDialog;
    private View headView;

    UploadOssHelper mUploadOssHelper;

    public SettingFragment1() {
        // Required empty public constructor
        mUploadOssHelper = new UploadOssHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null){
            photoSelectorDialog = new CommonPhotoSelectorDialog(getActivity());
            view = inflater.inflate(R.layout.fragment_setting2, container, false);
            id = (TextView) view.findViewById(R.id.idtext);
            name = (TextView) view.findViewById(R.id.name);
            friendshipManagerPresenter = new FriendshipManagerPresenter(this);
            friendshipManagerPresenter.getMyProfile();
            TextView logout = (TextView) view.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginBusiness.logout(new TIMCallBack() {
                        @Override
                        public void onError(int i, String s) {
                            if (getActivity() != null){
                                Toast.makeText(getActivity(), getResources().getString(R.string.setting_logout_fail), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onSuccess() {
                            if (getActivity() != null && getActivity() instanceof MainActivity){
                                ((MainActivity) getActivity()).logout();
                            }
                        }
                    });
                }
            });
            nickName = (LineControllerView) view.findViewById(R.id.nickName);
            nickName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditActivity.navToEdit(SettingFragment1.this, getResources().getString(R.string.setting_nick_name_change), name.getText().toString(), REQ_CHANGE_NICK, new EditActivity.EditInterface() {
                        @Override
                        public void onEdit(String text, TIMCallBack callBack) {
                            FriendshipManagerPresenter.setMyNick(text, callBack);
                        }
                    }, 20);

                }
            });
            allowTypeContent = new HashMap<>();
            allowTypeContent.put(getString(R.string.friend_allow_all), TIMFriendAllowType.TIM_FRIEND_ALLOW_ANY);
            allowTypeContent.put(getString(R.string.friend_need_confirm), TIMFriendAllowType.TIM_FRIEND_NEED_CONFIRM);
            allowTypeContent.put(getString(R.string.friend_refuse_all), TIMFriendAllowType.TIM_FRIEND_DENY_ANY);
            final String[] stringList = allowTypeContent.keySet().toArray(new String[allowTypeContent.size()]);
            friendConfirm = (LineControllerView) view.findViewById(R.id.friendConfirm);
            friendConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new ListPickerDialog().show(stringList, getFragmentManager(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, final int which) {
                            FriendshipManagerPresenter.setFriendAllowType(allowTypeContent.get(stringList[which]), new TIMCallBack() {
                                @Override
                                public void onError(int i, String s) {
                                    Toast.makeText(getActivity(), getString(R.string.setting_friend_confirm_change_err), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess() {
                                    friendConfirm.setContent(stringList[which]);
                                }
                            });
                        }
                    });
                }
            });
            LineControllerView messageNotify = (LineControllerView) view.findViewById(R.id.messageNotify);
            messageNotify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MessageNotifySettingActivity.class);
                    startActivity(intent);
                }
            });
            LineControllerView blackList = (LineControllerView) view.findViewById(R.id.blackList);
            blackList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), BlackListActivity.class);
                    startActivity(intent);
//                    Intent intent = new Intent(getActivity(), TCVideoRecordActivity.class);
//                    startActivity(intent);
                }
            });
            headView = view.findViewById(R.id.headview);
            headView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPhoneDialog();
                }
            });
            LineControllerView about = (LineControllerView) view.findViewById(R.id.about);
            about.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AboutActivity.class);
                    startActivity(intent);
                }
            });

        }
        return view ;
    }


//    @NeedsPermission(value = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    private void showPhoneDialog() {
        photoSelectorDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CHANGE_NICK){
            if (resultCode == getActivity().RESULT_OK){
                setNickName(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }else{
            dealResult(requestCode, data);
        }

    }

    @SuppressLint("NewApi")
    private void dealResult(int requestCode, Intent data) {
        String mUploadPath = null;
        if(data == null){
            return;
        }
        if (requestCode == 99) {
            mUploadPath = data.getStringExtra(CameraProtectActivity.IMAGE_PATH);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String fileDir = (Environment.getExternalStorageDirectory() + mUploadPath).replace("/external_storage_root", "");
                mUploadPath = fileDir;
            }
//            mPresenter.showLocalImage(mUploadPath, dvHeader);
            Glide.with(this).load(mUploadPath).into((ImageView) headView);
            newUploadImage(mUploadPath);
        } else if (requestCode == CommonPhotoSelectorDialog.PHOTOREQUESTCODE) {
            try {
                Uri originalUri = data.getData();
                final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
                if (isKitKat && DocumentsContract.isDocumentUri(getActivity(), originalUri)) {
                    String wholeID = DocumentsContract.getDocumentId(originalUri);
                    String id = wholeID.split(":")[1];
                    String[] column = {MediaStore.Images.Media.DATA};
                    String sel = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor = getActivity().getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
                    int columnIndex = cursor.getColumnIndex(column[0]);
                    if (cursor.moveToFirst()) {
                        mUploadPath = cursor.getString(columnIndex);
                    }
                    cursor.close();
                } else if ("content".equalsIgnoreCase(originalUri.getScheme())) {
                    mUploadPath = getDataColumn(getActivity(), originalUri, null, null);
                } // File
                else if ("file".equalsIgnoreCase(originalUri.getScheme())) {
                    mUploadPath = originalUri.getPath();
                }
//                mPresenter.showLocalImage(mUploadPath, dvHeader);
                Glide.with(this).load(mUploadPath).into((ImageView) headView);
                newUploadImage(mUploadPath);
            } catch (Exception e) {
                Log.e("EditAccountInfoActivity", e.toString());
            }
        }
    }


    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private void newUploadImage(String path) {
        ((QLActivity)getActivity()).showLoadingDialog("保存中");
        UploadModel uploadModel = new UploadModel();
        uploadModel.setLocalFilePath(path);
        mUploadOssHelper.uploadObject(uploadModel, new IUploadCallBack() {
            @Override
            public void onStart(UploadModel uploadModel) {

            }

            @Override
            public void onProgress(UploadModel uploadModel, long currentSize, long totalSize) {

            }

            @Override
            public void onSuccess(UploadModel uploadModel) {
//                mPresenter.updateInfo(mName, uploadModel.getOssFilePath());
                LogUtil.e("file path :" + uploadModel.getOssFilePath());
                ((QLActivity)getActivity()).hideLoadingDialog();
            }

            @Override
            public void onFailure(UploadModel uploadModel, Exception e) {
                ToastUtil.shortShow(e.getMessage());
                ((QLActivity)getActivity()).hideLoadingDialog();
            }

            @Override
            public void onStopAllUpload(List<UploadModel> uploadModelList) {

            }
        });
    }
    private void setNickName(String name){
        if (name == null) return;
        this.name.setText(name);
        nickName.setContent(name);
    }


    /**
     * 显示用户信息
     *
     * @param users 资料列表
     */
    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        id.setText(users.get(0).getIdentifier());
        setNickName(users.get(0).getNickName());
        for (String item : allowTypeContent.keySet()){
            if (allowTypeContent.get(item) == users.get(0).getAllowType()){
                friendConfirm.setContent(item);
                break;
            }
        }

    }
}
