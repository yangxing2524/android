package com.growalong.android.model.request;

/**
 */
public class CourseMaterialParams {
    private long courseId;
    private int pptStatus;
    private PageParams page;

    public CourseMaterialParams(long courseId, int pptStatus) {
        this.courseId = courseId;
        this.pptStatus = pptStatus;
        page = new PageParams(1, 20);
    }

    public int getPptStatus() {
        return pptStatus;
    }

    public void setPptStatus(int pptStatus) {
        this.pptStatus = pptStatus;
    }

    public PageParams getPage() {
        return page;
    }

    public void setPage(PageParams page) {
        this.page = page;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }
}
