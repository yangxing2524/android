package com.growalong.android.ui.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.app.AppManager;
import com.growalong.android.model.UserInfoModel;

/**
 * Created by yangxing on 2018/11/29.
 */
public class RequestedVideoCallDialog extends DialogFragment implements View.OnClickListener {

    private ImageView headview, receive, refuse, background;
    private TextView name;
    private String callerHeadUrl, callerName;

    public void setCallerHeadUrl(String callerHeadUrl) {
        this.callerHeadUrl = callerHeadUrl;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_requested_video_call, container, false);
        receive = view.findViewById(R.id.receive);
        receive.setOnClickListener(this);
        refuse = view.findViewById(R.id.refuse);
        refuse.setOnClickListener(this);


        headview = view.findViewById(R.id.headview);
        if (callerHeadUrl != null) {
            Glide.with(getActivity()).load(callerHeadUrl).into(headview);
        }

        background = view.findViewById(R.id.background);
        UserInfoModel userInfoModel = AppManager.getInstance().getUserInfoModel();
        if (userInfoModel.getHeadImgUrl() != null) {
            Glide.with(getActivity()).load(userInfoModel.getHeadImgUrl()).into(background);
        }

        name = view.findViewById(R.id.name);
        name.setText(callerName);
        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refuse:
                break;
            case R.id.receive:
                break;

        }
    }
}
