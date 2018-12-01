package com.growalong.android.present;

import com.growalong.android.ui.QLActivity;
import com.growalong.android.ui.dialog.RequestedVideoCallDialog;

/**
 * Created by yangxing on 2018/12/1.
 */
public class ChatOtherPresenter {

    private final QLActivity mActivity;

    public ChatOtherPresenter(QLActivity chatActivity) {
        mActivity = chatActivity;
    }

    public void requestVideoChat(String callerHeadUrl, String callerName) {
        RequestedVideoCallDialog dialog = new RequestedVideoCallDialog();
        dialog.setCallerHeadUrl(callerHeadUrl);
        dialog.setCallerName(callerName);
        dialog.show(mActivity.getFragmentManager(), "");
    }
}
