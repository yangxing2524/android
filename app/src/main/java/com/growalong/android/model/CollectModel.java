package com.growalong.android.model;

/**
 * Created by yangxing on 2018/11/17.
 */
public class CollectModel {
    private long id;    //课程id
    private String title;   //素材标题
    private String type;    //素材类型：’file’,’audio’,’video’,’image’,’text’
    private String content; //素材内容

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
