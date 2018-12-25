package com.growalong.android.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.bigkoo.pickerview.TimePickerView;
import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.app.AppManager;
import com.growalong.android.app.MyApplication;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.UploadModel;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.model.request.TypeParams;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.ILoginApis;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.NewBasePresenter;
import com.growalong.android.present.UserPresenter;
import com.growalong.android.ui.fragment.NewBaseFragment;
import com.growalong.android.upload.IUploadCallBack;
import com.growalong.android.upload.UploadOssHelper;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.RxUtil;
import com.growalong.android.util.ToastUtil;
import com.growalong.android.util.Utils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMUserProfile;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.presenter.FriendshipManagerPresenter;
import com.tencent.qcloud.presentation.viewfeatures.FriendInfoView;
import com.tencent.qcloud.ui.LineControllerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * 设置页面
 */
public class SettingFragment extends NewBaseFragment implements FriendInfoView {
    private LineControllerView ChineseName, EnglishName;

    @BindView(R.id.location)
    public LineControllerView location;
    @BindView(R.id.gender)
    public LineControllerView gender;
    @BindView(R.id.age)
    public LineControllerView age;
    @BindView(R.id.favorite)
    public LineControllerView favorite;

    private static final String TAG = SettingFragment.class.getSimpleName();
    private TextView name;
    private final int REQ_CHANGE_CHINESE = 1000;
    private final int REQ_CHANGE_ENGLISH = 1001;
    private final int REQ_CHANGE_GENDER = 1002;
    private final int REQ_CHANGE_FAVOURITE = 1003;
    private final int REQ_CHANGE_LOCATION = 1004;
    private final int REQ_CHANGE_AGE = 1005;

    CommonPhotoSelectorDialog photoSelectorDialog;
    private ImageView headView;

    private UploadOssHelper mUploadOssHelper;
    private UserInfoModel userInfoModel;

    //时间选择器
    private TimePickerView mPvTime;
    private boolean hasChangeInfo;

    public SettingFragment() {
        // Required empty public constructor
        mUploadOssHelper = new UploadOssHelper();

    }

    @Override
    protected void initEventAndData(Bundle savedInstanceState, View view) {

        mPvTime = new TimePickerView(activity, TimePickerView.Type.YEAR_MONTH_DAY);
        final SimpleDateFormat formatter = new SimpleDateFormat(getResources().getString(R.string.data));
        //时间选择后回调
        mPvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                try {
                    int age = Utils.getAge(date);
                    SettingFragment.this.age.setContent(age + "岁");
                    userInfoModel.setAge(age);
                    userInfoModel.setBirthday((new SimpleDateFormat("yyyy-MM-dd")).format(date));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        userInfoModel = AppManager.getInstance().getUserInfoModel();
        photoSelectorDialog = new CommonPhotoSelectorDialog(getActivity());
        name = (TextView) view.findViewById(R.id.name);
        setOnClickListener(view);
        headView = view.findViewById(R.id.headview);
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneDialog();
            }
        });
        Glide.with(getActivity()).load(userInfoModel.getHeadImgUrl()).into(headView);

        setChineseName(userInfoModel.getName());
        gender.setContent(userInfoModel.getGender() == 0 ? "女" : "男");

        if (userInfoModel.getHobby() != null) {
            favorite.setContent(userInfoModel.getHobby());
        }
        if (userInfoModel.getAge() > 0) {
            age.setContent(userInfoModel.getAge() + "岁");
        }

        if (userInfoModel.getAddress() != null) {
            location.setContent(userInfoModel.getAddress());
        }
        if (userInfoModel.getEnName() != null) {
            EnglishName.setContent(userInfoModel.getEnName());
        }
        if (userInfoModel.getCnName() != null) {
            ChineseName.setContent(userInfoModel.getCnName());
        }

    }

    private void setOnClickListener(View view) {
        TextView logout = (TextView) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginBusiness.logout(new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        if (getActivity() != null) {
//                            Toast.makeText(getActivity(), getResources().getString(R.string.setting_logout_fail), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onSuccess() {

                    }
                });

                ILoginApis iLoginApis = BaseRetrofitClient.getInstance().create(ILoginApis.class);
                BaseParams<TypeParams> baseParams = new BaseParams<>(new TypeParams(MyApplication.TYPE));
                iLoginApis.logout(baseParams).compose(RxUtil.<JsonElement>handleResult())
                        .compose(NewBasePresenter.<JsonElement>asyAndMainResponseTransformer())
                        .subscribe(new CommSubscriber<JsonElement>() {
                            @Override
                            public void onSuccess(JsonElement jsonElement) {
                                AccountManager.getInstance().logout();
                                AppManager.getInstance().logout();
                                LoginMainActivity.startThis(activity);
                                activity.finish();
                                AppManager.getInstance().finishActivity(MainActivity.class);
                                ToastUtil.shortShow(getResources().getString(R.string.logout_success));
                            }

                            @Override
                            public void onFailure(Throwable e) {
                                super.onFailure(e);
                                ToastUtil.shortShow(e.getMessage());
                            }
                        });
            }
        });
        ChineseName = (LineControllerView) view.findViewById(R.id.ChineseName);
        ChineseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(SettingFragment.this, getResources().getString(R.string.setting_chinese_name_change), ChineseName.getContent(), REQ_CHANGE_CHINESE, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                        FriendshipManagerPresenter.setMyNick(text, callBack);
                    }
                }, 20);

            }
        });

        EnglishName = (LineControllerView) view.findViewById(R.id.EnglishName);
        EnglishName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(SettingFragment.this, getResources().getString(R.string.setting_english_name_change), EnglishName.getContent(), REQ_CHANGE_ENGLISH, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                        FriendshipManagerPresenter.setMyNick(text, callBack);
                    }
                }, 20);

            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(SettingFragment.this, getResources().getString(R.string.setting_favorite_change), favorite.getContent(), REQ_CHANGE_FAVOURITE, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                    }
                }, 20);

            }
        });
        age.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birStr = userInfoModel.getBirthday();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = format.parse(birStr);
                    mPvTime.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mPvTime.show();
            }
        });
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(SettingFragment.this, getResources().getString(R.string.setting_gender_change), gender.getContent(), REQ_CHANGE_GENDER, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                    }
                }, 20);

            }
        });
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.navToEdit(SettingFragment.this, getResources().getString(R.string.setting_location_change), location.getContent(), REQ_CHANGE_LOCATION, new EditActivity.EditInterface() {
                    @Override
                    public void onEdit(String text, TIMCallBack callBack) {
                    }
                }, 20);

            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }


    //    @NeedsPermission(value = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    private void showPhoneDialog() {
        photoSelectorDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CHANGE_CHINESE) {
            if (resultCode == getActivity().RESULT_OK) {
                setChineseName(data.getStringExtra(EditActivity.RETURN_EXTRA));
                userInfoModel.setCnName(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }

        if (requestCode == REQ_CHANGE_ENGLISH) {
            if (resultCode == getActivity().RESULT_OK) {
                userInfoModel.setEnName(data.getStringExtra(EditActivity.RETURN_EXTRA));
                EnglishName.setContent(userInfoModel.getEnName());
            }
        }
        if (requestCode == REQ_CHANGE_GENDER) {
            if (resultCode == getActivity().RESULT_OK) {
                String genderStr = data.getStringExtra(EditActivity.RETURN_EXTRA);
                userInfoModel.setGender("女".equals(genderStr) ? 0 : 1);
                gender.setContent(userInfoModel.getGender() == 0 ? "女" : "男");
            }
        }
        if (requestCode == REQ_CHANGE_AGE) {
            if (resultCode == getActivity().RESULT_OK) {
//                setChineseName(data.getStringExtra(EditActivity.RETURN_EXTRA));
            }
        }
        if (requestCode == REQ_CHANGE_LOCATION) {
            if (resultCode == getActivity().RESULT_OK) {
                userInfoModel.setAddress(data.getStringExtra(EditActivity.RETURN_EXTRA));
                location.setContent(userInfoModel.getAddress());
            }
        }
        if (requestCode == REQ_CHANGE_FAVOURITE) {
            if (resultCode == getActivity().RESULT_OK) {
                userInfoModel.setHobby(data.getStringExtra(EditActivity.RETURN_EXTRA));
                favorite.setContent(userInfoModel.getHobby());
            }
        } else {
            hasChangeInfo = changeHeadView(requestCode, data);
        }

        if (resultCode == getActivity().RESULT_OK) {
            hasChangeInfo = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hasChangeInfo) {
            UserPresenter userPresenter = new UserPresenter();
            userPresenter.updateUserInfo(userInfoModel).subscribe(new CommSubscriber<JsonElement>() {
                @Override
                public void onSuccess(JsonElement jsonObject) {
                    ToastUtil.shortShow("信息修改成功");
                }

                @Override
                public void onFailure(Throwable e) {
                    super.onFailure(e);
                    ToastUtil.shortShow("信息修改失败");
                }
            });
        }
    }

    @SuppressLint("NewApi")
    private boolean changeHeadView(int requestCode, Intent data) {
        String mUploadPath = null;
        if (data == null) {
            return false;
        }
        if (requestCode == 99) {
            mUploadPath = data.getStringExtra(CameraProtectActivity.IMAGE_PATH);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String fileDir = (Environment.getExternalStorageDirectory() + mUploadPath).replace("/external_storage_root", "");
                mUploadPath = fileDir;
            }
//            mPresenter.showLocalImage(mUploadPath, dvHeader);
            Glide.with(this).load(mUploadPath).into(headView);
            newUploadImage(mUploadPath);
            return true;
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
                Glide.with(this).load(mUploadPath).into(headView);
                newUploadImage(mUploadPath);
            } catch (Exception e) {
                Log.e("EditAccountInfoActivity", e.toString());
            }
            return true;
        }
        return false;
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

                userInfoModel.setHeadImgUrl(uploadModel.getOssFilePath());
                hasChangeInfo = true;

                FriendshipManagerPresenter.setModifyFaceUrl(uploadModel.getOssFilePath());
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

    private void setChineseName(String name) {
        if (name == null) return;
        this.name.setText(name);
        ChineseName.setContent(name);
    }


    /**
     * 显示用户信息
     *
     * @param users 资料列表
     */
    @Override
    public void showUserInfo(List<TIMUserProfile> users) {
        setChineseName(users.get(0).getNickName());
    }
}
