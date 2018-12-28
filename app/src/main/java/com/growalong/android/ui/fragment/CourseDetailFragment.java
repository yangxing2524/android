package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.im.utils.FileUtil;
import com.growalong.android.model.CourseDetailInfoModel;
import com.growalong.android.model.CourseDetailModel;
import com.growalong.android.model.CourseMaterialModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.CoursePresenter;
import com.growalong.android.ui.adapter.CourseDetailAdapter;
import com.growalong.android.ui.dialog.CourseMatListPopupWindow;
import com.growalong.android.ui.recyclerview.ISuperRefreshView;
import com.growalong.android.ui.recyclerview.SuperRecyclerView;

import java.util.List;

import butterknife.BindView;

/**
 */
public class CourseDetailFragment extends NewBaseListFragment {
    @BindView(R.id.recyclerview)
    public SuperRecyclerView mRecyclerView;
    @BindView(R.id.download)
    public TextView mDownloadView;

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
        initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_course_detail;
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
            public void onSuccess(final CourseDetailInfoModel courseDatailInfoModel) {
                mCourseDetailModel.setCourseDetailInfoModel(courseDatailInfoModel);
                mAdapter.notifyDataSetChanged();
                coursePresenter.getCourseMaterial(courseId, 0).subscribe(new CommSubscriber<List<CourseMaterialModel>>() {
                    @Override
                    public void onSuccess(List<CourseMaterialModel> courseMaterialModels) {
                        //普通素材
                        mCourseDetailModel.setMaterialModelList(courseMaterialModels);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                    }
                });
                coursePresenter.getCourseMaterial(courseId, 2).subscribe(new CommSubscriber<List<CourseMaterialModel>>() {
                    @Override
                    public void onSuccess(final List<CourseMaterialModel> courseMaterialModels) {
                        //课程简介素材
                        if (courseMaterialModels != null && courseMaterialModels.size() > 0) {

                            boolean hasDownload = true;
                            for (CourseMaterialModel model : courseMaterialModels) {
                                if (!FileUtil.isDownloadFileFromUrlExist(model.getContent(), model.getTitle())) {
                                    hasDownload = false;break;
                                }
                            }

                            if (hasDownload) {
                                mDownloadView.setText(activity.getResources().getString(R.string.has_download));
                            }
                            mDownloadView.setVisibility(View.VISIBLE);
                            mDownloadView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CourseMatListPopupWindow popupWindow = new CourseMatListPopupWindow(activity);
                                    popupWindow.setList(courseMaterialModels);
                                    popupWindow.showPopupWindow();
                                }
                            });
                        }
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
