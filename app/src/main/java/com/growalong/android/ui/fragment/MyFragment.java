package com.growalong.android.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.model.UploadModel;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.UserPresenter;
import com.growalong.android.ui.CameraProtectActivity;
import com.growalong.android.ui.CommonPhotoSelectorDialog;
import com.growalong.android.ui.MyCollectActivity;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.upload.IUploadCallBack;
import com.growalong.android.upload.UploadOssHelper;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.ToastUtil;

import java.util.List;

import butterknife.BindView;

/**
 * Created by yangxing on 2018/11/12.
 */
public class MyFragment extends NewBaseFragment implements View.OnClickListener {
    @BindView(R.id.headview)
    public ImageView headView;
    @BindView(R.id.name)
    public TextView name;
    @BindView(R.id.interest)
    public TextView interest;
    @BindView(R.id.location)
    public TextView location;

    @BindView(R.id.study_level)
    public View study_level;
    @BindView(R.id.my_download)
    public View my_download;
    @BindView(R.id.collect)
    public View collect;

    private UploadOssHelper mUploadOssHelper;
    private UserPresenter userPresenter = new UserPresenter();

    @Override
    protected void initEventAndData(Bundle savedInstanceState, View view) {
        mUploadOssHelper = new UploadOssHelper();
        initData();
    }

    private void initData() {
        userPresenter.getCourseDetail(AccountManager.getInstance().getAccountInfo().getUserId(), "c").subscribe(new CommSubscriber<UserInfoModel>() {
            @Override
            public void onSuccess(UserInfoModel userInfoModel) {
                name.setText(userInfoModel.getName());
                interest.setText(userInfoModel.getHobby());
                location.setText(userInfoModel.getFamilyInfo());
                Glide.with(activity).load(userInfoModel.getHeadImgUrl()).asBitmap().into(headView);
                Drawable drawable;
                if (userInfoModel.getGender() == 1) {
                    //男
                    drawable = getResources().getDrawable(R.mipmap.man);
                } else {
                    drawable = getResources().getDrawable(R.mipmap.women);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                name.setCompoundDrawables(drawable, null, null, null);

                //获取到的个人信息之后才添加点击事件
                study_level.setOnClickListener(MyFragment.this);
                interest.setOnClickListener(MyFragment.this);
                collect.setOnClickListener(MyFragment.this);
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        dealResult(requestCode, data);

    }

    @SuppressLint("NewApi")
    private void dealResult(int requestCode, Intent data) {
        String mUploadPath = null;
        if (data == null) {
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
        ((QLActivity) getActivity()).showLoadingDialog("保存中");
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
                ((QLActivity) getActivity()).hideLoadingDialog();
            }

            @Override
            public void onFailure(UploadModel uploadModel, Exception e) {
                ToastUtil.shortShow(e.getMessage());
                ((QLActivity) getActivity()).hideLoadingDialog();
            }

            @Override
            public void onStopAllUpload(List<UploadModel> uploadModelList) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_download:

                break;
            case R.id.study_level:
                //跳转到对应的webview
                break;
            case R.id.collect:
                MyCollectActivity.startThis(activity);
                break;
        }
    }
}
