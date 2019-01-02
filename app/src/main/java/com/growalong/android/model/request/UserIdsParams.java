package com.growalong.android.model.request;

import java.util.List;

/**
 * Created by yangxing on 2018/11/14.
 */
public class UserIdsParams {
    private List<String> userIds;

    public UserIdsParams(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}
