package com.growalong.android.model.request;

/**
 * Created by yangxing on 2018/11/17.
 */
public class CourseIdPageParams {
    private String courseId = "0";
    private PageParams page;

    public CourseIdPageParams(PageParams page) {
        this.page = page;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public PageParams getPage() {
        return page;
    }

    public void setPage(PageParams page) {
        this.page = page;
    }
}
