package com.growalong.android.model;

import java.io.Serializable;

/**
 * Created by murphy on 10/10/16.
 */

public class BaseGenericModel<T> implements Serializable {
    private String id;
    private StateModel state;
    private String etag;
    private T data;

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public StateModel getState() {
        return state;
    }

    public void setState(StateModel state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
