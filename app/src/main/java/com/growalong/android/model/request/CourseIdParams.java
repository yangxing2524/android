package com.growalong.android.model.request;

/**
 */
public class CourseIdParams {
    private long courseId;

    public CourseIdParams(long courseId) {
        this.courseId = courseId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }
}
