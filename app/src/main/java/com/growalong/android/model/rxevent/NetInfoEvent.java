package com.growalong.android.model.rxevent;

/**
 * Created by murphy on 21/10/2016.
 */

public class NetInfoEvent {
    private boolean isConnected;

    public NetInfoEvent(boolean b){
        this.isConnected = b;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
