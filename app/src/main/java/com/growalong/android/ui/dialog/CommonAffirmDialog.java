package com.growalong.android.ui.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.app.MyApplication;

/**
 * Created by yangxing on 2017/2/20.
 */

public class CommonAffirmDialog extends DialogFragment implements View.OnClickListener {
    //    CommonAffirmDialog.Builder(1).setTitle("惠园小区").setContent("你的会员将于明天到期").show(getSupportFragmentManager(), "");
//    CommonAffirmDialog.Builder(2).setTitle("惠园小区").setContent("你的会员将于明天到期你的会员将于明天到期你的会员将于明天到期").setIAffirmDialogClick(new CommonAffirmDialog.IAffirmDialogClick() {
//        @Override
//        public void onOkClick() {
//            ToastUtil.shortShow("onOkClick");
//        }
//
//        @Override
//        public void onCancelClick() {
//            ToastUtil.shortShow("onCancelClick");
//        }
//    }).show(getSupportFragmentManager(), "");
//    CommonAffirmDialog.Builder(3).setTitle("惠园小区").setContent("你的会员将于明天到期").setIAffirmDialogInput(new CommonAffirmDialog.IAffirmDialogInput() {
//        @Override
//        public void input(String str) {
//            ToastUtil.shortShow(str);
//        }
//    }).show(getSupportFragmentManager(), "");
    private int mType;
    private TextView mTitleView, mContentView, mCancel, mOK;
    private EditText mEdit;

    public IAffirmDialogClick mIAffirmDialogClick;
    public IAffirmDialogInput mIAffirmDialogInput;
    private String mTitle;
    private String mContent;
    private int mInputType = -100;
    private String mHint;
    private String mEditTextContent;
    private String mOKText;
    private boolean isTitleBold;
    private int mContentColor = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen_Translucent);
        mType = getArguments().getInt("type");
    }

    /**
     * @param type 1:只有一个确定按钮；2：取消和确认；3：edittext输入框；4：只有提示没有title
     */
    public static CommonAffirmDialog Builder(int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        CommonAffirmDialog fragment = new CommonAffirmDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_affirm_common, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitleView = (TextView) view.findViewById(R.id.tv_title);
        mContentView = (TextView) view.findViewById(R.id.tv_content);
        mEdit = (EditText) view.findViewById(R.id.edit);
        mCancel = (TextView) view.findViewById(R.id.cancel);
        mOK = (TextView) view.findViewById(R.id.ok);
        mCancel.setOnClickListener(this);
        mOK.setOnClickListener(this);
        view.findViewById(R.id.content).getLayoutParams().width = MyApplication.getInstance().getScreenWidth() * 5 / 6;

        //1:只有一个确定按钮；2：取消和确认；3：edittext输入框
        switch (mType) {
            case 1:
                mOK.setVisibility(View.GONE);
                mCancel.setText("确认");
                mEdit.setVisibility(View.GONE);
                break;
            case 4:
                LinearLayout.LayoutParams layoutpa = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
                layoutpa.topMargin = layoutpa.topMargin * 5 / 4;
            case 2:
                mEdit.setVisibility(View.GONE);
                break;
            case 3:
                mContentView.setVisibility(View.GONE);
                mEdit.setVisibility(View.VISIBLE);
                break;
        }

        if (mTitle != null) {
            mTitleView.setText(mTitle);
            mTitleView.setVisibility(View.VISIBLE);
            if (isTitleBold) {
                TextPaint paint = mTitleView.getPaint();
                paint.setFakeBoldText(true);
            }
        }
        if (mContent != null) {
            mContentView.setText(mContent);
            if (mContentColor != -1) {
                mContentView.setTextColor(mContentColor);
            }
        }
        if (mEdit != null && mInputType != -100)
            mEdit.setInputType(mInputType);
        if (!TextUtils.isEmpty(mHint)) {
            mEdit.setHint(mHint);
        }
        if (!TextUtils.isEmpty(mEditTextContent)) {
            mEdit.setText(mEditTextContent);
            mEdit.setSelection(mEditTextContent.length());
        }
        if (!TextUtils.isEmpty(mOKText)) {
            mOK.setText(mOKText);
        }
    }

    public CommonAffirmDialog setInputType(int inputType) {
        mInputType = inputType;
        if (mEdit != null) {
            mEdit.setInputType(inputType);
        }
        return this;
    }

    public CommonAffirmDialog setEditText(String text){
        if(mEdit != null){
            mEdit.setText(text);
        }
        return this;
    }
    public CommonAffirmDialog setTextHint(String hint) {
//        mInputType = inputType;
        mHint = hint;
        if (mEdit != null) {
            mEdit.setHint(hint);
        }
        return this;
    }

    public CommonAffirmDialog setEditTextContent(String txt) {
        mEditTextContent = txt;
        if (mEdit != null) {
            mEdit.setText(txt);
            mEdit.setSelection(txt.length());
        }
        return this;
    }

    public CommonAffirmDialog setTitle(String str) {
        mTitle = str;
        if (mTitleView != null) {
            mTitleView.setText(mTitle);
            mTitleView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public CommonAffirmDialog setContent(String str) {
        mContent = str;
        if (mContentView != null)
            mContentView.setText(mContent);
        return this;
    }

    public CommonAffirmDialog setContent(String str, int color) {
        mContent = str;
        mContentColor = color;
        if (mContentView != null) {
            mContentView.setText(mContent);
            mContentView.setTextColor(color);
        }
        return this;
    }

    public CommonAffirmDialog setOkText(String str) {
        mOKText = str;
        if (mOK != null) {
            mOK.setText(str);
        }
        return this;
    }

    public CommonAffirmDialog setIAffirmDialogClick(IAffirmDialogClick iAffirmDialogClick) {
        mIAffirmDialogClick = iAffirmDialogClick;
        return this;
    }

    public CommonAffirmDialog setIAffirmDialogInput(IAffirmDialogInput iAffirmDialogInput) {
        mIAffirmDialogInput = iAffirmDialogInput;
        return this;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mIAffirmDialogClick = null;
        mIAffirmDialogInput = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                if (mIAffirmDialogInput != null)
                    mIAffirmDialogInput.input(mEdit.getText().toString());
                if (mIAffirmDialogClick != null)
                    mIAffirmDialogClick.onOkClick();
                dismiss();
                break;
            case R.id.cancel:
                if (mIAffirmDialogClick != null)
                    mIAffirmDialogClick.onCancelClick();
                dismiss();
                break;
        }
    }

    public CommonAffirmDialog setTitleBold() {
        isTitleBold = true;
        return this;
    }

    public interface IAffirmDialogClick {
        void onOkClick();

        void onCancelClick();
    }

    public interface IAffirmDialogInput {
        void input(String str);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
