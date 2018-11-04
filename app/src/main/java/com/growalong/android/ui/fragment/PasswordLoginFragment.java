package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.app.MyApplication;
import com.growalong.android.model.ApiException;
import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.LoginBean;
import com.growalong.android.model.NetLoginBean;
import com.growalong.android.model.PasswordLoginParams;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.ILoginApis;
import com.growalong.android.rxevent.NotificationEvent;
import com.growalong.android.ui.HomeActivity;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.RxUtil;
import com.growalong.android.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by gangqing on 2016/8/15.
 */

public class PasswordLoginFragment extends NewBaseFragment implements View.OnClickListener {
    @BindView(R.id.et_phone_number)
    EditText mPhoneNumberEdit;
    @BindView(R.id.et_password)
    EditText mPasswordEdit;
    @BindView(R.id.btn_login)
    TextView mLogin;
    @BindView(R.id.iv_delete_mobile)
    View iv_delete_mobile;
    @BindView(R.id.iv_delete_password)
    View iv_delete_password;
    @BindView(R.id.iv_eyes)
    ImageView iv_eyes;
    private String mPhoneNumber;
    private String mPassword;
    private boolean isShowPassward = false;

    @Override
    protected void initEventAndData(Bundle savedInstanceState, View mView) {
        mPhoneNumberEdit.addTextChangedListener(getEditTextWatcher());
        mPasswordEdit.addTextChangedListener(getEditTextWatcher());
        mLogin.setOnClickListener(this);
        iv_delete_mobile.setOnClickListener(this);
        iv_delete_password.setOnClickListener(this);
        iv_eyes.setOnClickListener(this);
        mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());

        mILoginApis = BaseRetrofitClient.getInstance().create(ILoginApis.class);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_password_login_layout;
    }

    public void showError(String msg) {
        hideLoadingDialog();
        ToastUtil.shortShow(msg);
    }

    public void setLoginBtnView(boolean clickable, int drawableRes) {
        mLogin.setClickable(clickable);
        mLogin.setBackgroundResource(drawableRes);
    }

    public void onAfterTextChanged() {
        mPhoneNumber = mPhoneNumberEdit.getText().toString().trim();
        mPassword = mPasswordEdit.getText().toString().trim();

        iv_delete_mobile.setVisibility(mPhoneNumber.length() == 0 ? View.GONE : View.VISIBLE);
        iv_delete_password.setVisibility(mPassword.length() == 0 ? View.GONE : View.VISIBLE);
        checkoutData(mPhoneNumber, mPassword);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                showLoadingDialog();
                loginForPassword(mPhoneNumber, mPassword);
                break;
            case R.id.iv_delete_mobile:
                mPhoneNumberEdit.setText("");
                break;
            case R.id.iv_delete_password:
                mPasswordEdit.setText("");
                break;
            case R.id.iv_eyes:
                if (isShowPassward) {
                    iv_eyes.setImageResource(R.mipmap.landing_icon_unsee);
                    mPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mPasswordEdit.setSelection(mPasswordEdit.getText().length());
                    isShowPassward = false;
                } else {
                    mPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isShowPassward = true;
                    mPasswordEdit.setSelection(mPasswordEdit.getText().length());
                    iv_eyes.setImageResource(R.mipmap.landing_icon_see);
                }
                break;
        }
    }


    private ILoginApis mILoginApis;

    /**
     * 密码登录
     */
    public void loginForPassword(String phoneNumber, String password) {
        LogUtil.e("hunxiao", phoneNumber + "---" + password);
        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 8) {
            showError(MyApplication.getInstance().context.getString(R.string.login_checkout_password_hint));
            return;
        }
        PasswordLoginParams dataBean = new PasswordLoginParams(phoneNumber, password);
        BaseParams<PasswordLoginParams> baseParams = new BaseParams<>(dataBean);
        Observable<BaseGenericModel<NetLoginBean>> observable = mILoginApis.loginForPassword(baseParams);
        Subscription subscribe = observable.compose(RxUtil.<NetLoginBean>handleResult())
                .subscribe(new Action1<NetLoginBean>() {
                    @Override
                    public void call(NetLoginBean netLoginBean) {
                        LoginBean user = netLoginBean.getUser();
                        AccountManager.getInstance().setVisitor(false);
                        AccountManager.getInstance().saveAccountInfoFormLocate(user);
                        EventBus.getDefault().post(NotificationEvent.LOGIN_SUCCESS);

                        hideLoadingDialog();
                        HomeActivity.startThis(getActivity());
                        ToastUtil.shortShow(getResources().getString(R.string.login_succ));
                        getActivity().finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        EventBus.getDefault().post(NotificationEvent.LOGIN_FAIL);
                        if (throwable instanceof ApiException) {
                            showError(throwable.getMessage());
                        } else {
                            LogUtil.e("", throwable.getMessage());
                            showError("登录失败");
                        }
                    }
                });
        addSubscribe(subscribe);
    }

    public void checkoutData(String phoneNumber, String password) {
        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(password)) {
            setLoginBtnView(false, R.drawable.bg_weixin_unlogin);
        } else {
            setLoginBtnView(true, R.drawable.bg_weixin_login);
        }
    }

    public EditTextWatcher getEditTextWatcher() {
        return new EditTextWatcher();
    }

    private class EditTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            onAfterTextChanged();
        }
    }
}
