package com.growalong.android.model;

import java.util.List;

/**
 */
public class CourseDetailModel {
    private CourseDetailInfoModel courseDetailInfoModel;
    private List<CourseMaterialModel> materialModelList;

    public CourseDetailInfoModel getCourseDetailInfoModel() {
        return courseDetailInfoModel;
    }

    public void setCourseDetailInfoModel(CourseDetailInfoModel courseDetailInfoModel) {
        this.courseDetailInfoModel = courseDetailInfoModel;
    }

    public List<CourseMaterialModel> getMaterialModelList() {
        return materialModelList;
    }

    public void setMaterialModelList(List<CourseMaterialModel> materialModelList) {
        this.materialModelList = materialModelList;
    }

}
