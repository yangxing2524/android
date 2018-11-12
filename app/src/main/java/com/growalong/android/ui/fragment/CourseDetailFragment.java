package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.growalong.android.R;
import com.growalong.android.model.CourseDetailInfoModel;
import com.growalong.android.model.CourseDetailModel;
import com.growalong.android.model.CourseMaterialModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.CoursePresenter;
import com.growalong.android.ui.adapter.CourseDetailAdapter;
import com.growalong.android.ui.recyclerview.ISuperRefreshView;
import com.growalong.android.ui.recyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.BindView;

/**
 */
public class CourseDetailFragment extends NewBaseListFragment {
    @BindView(R.id.recyclerview)
    public SuperRecyclerView mRecyclerView;

    private long courseId;

    private CoursePresenter coursePresenter = new CoursePresenter();

    private CourseDetailModel mCourseDetailModel = new CourseDetailModel();

    @Override
    public void setupView(Bundle savedInstanceState, View view) {

    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new CourseDetailAdapter(activity,
                mCourseDetailModel);
    }

    @Override
    public ISuperRefreshView<RecyclerView.Adapter> getRefreshView() {
        return mRecyclerView;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.comm_list;
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void initData() {
        coursePresenter.getCourseDetail(courseId).subscribe(new CommSubscriber<CourseDetailInfoModel>() {
            @Override
            public void onSuccess(CourseDetailInfoModel courseDatailInfoModel) {
                mCourseDetailModel.setCourseDetailInfoModel(courseDatailInfoModel);
                mAdapter.notifyDataSetChanged();
                coursePresenter.getCourseMaterial(courseId, 0).subscribe(new CommSubscriber<List<CourseMaterialModel>>() {
                    @Override
                    public void onSuccess(List<CourseMaterialModel> courseMaterialModels) {
                        mCourseDetailModel.setMaterialModelList(courseMaterialModels);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
            }
        });
    }
}
