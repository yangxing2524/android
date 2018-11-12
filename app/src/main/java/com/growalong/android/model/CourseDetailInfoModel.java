package com.growalong.android.model;

/**
 */
public class CourseDetailInfoModel extends CourseListItemModel {
    private String categoryName;//课程所属分类名称

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
