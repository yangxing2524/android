package com.growalong.android.model.request;

/**
 */
public class RequestCourseItemList {
    private int status;
    private PageParams page;

    public RequestCourseItemList(int status, int pageIndex) {
        this.status = status;
        page = new PageParams(pageIndex, 20);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public PageParams getPage() {
        return page;
    }

    public void setPage(PageParams page) {
        this.page = page;
    }
}
