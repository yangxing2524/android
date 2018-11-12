package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.growalong.android.R;
import com.growalong.android.model.CourseListItemModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.CoursePresenter;
import com.growalong.android.ui.adapter.CourseOverFragmentAdapter;
import com.growalong.android.ui.recyclerview.ISuperRefreshView;
import com.growalong.android.ui.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 */
public class CourseStartingFragment extends NewBaseListFragment {

    private static final int STARTING_COURSE = 1;
    @BindView(R.id.recyclerview)
    public SuperRecyclerView mRecyclerView;

    private CoursePresenter presenter = new CoursePresenter();
    private int mPage = 1;

    private List<CourseListItemModel> mData = new ArrayList<>();

    public static Fragment newInstance() {
        return new CourseStartingFragment();
    }

    @Override
    public void setupView(Bundle savedInstanceState, View view) {
        mRecyclerView.setRefreshEnable(false);
        loadData(false, false);
    }

    public void loadData(final boolean isMore, boolean showLoading) {
        int page = 1;

        if (isMore) {
            page = mPage;
        }
        presenter.getCourList(STARTING_COURSE, page).subscribe(new CommSubscriber<List<CourseListItemModel>>() {
            @Override
            public void onSuccess(List<CourseListItemModel> courseListItemModels) {
                if (courseListItemModels.size() >= PAGE_SIZE) {
                    mRecyclerView.setHasMore(true);
                } else {
                    mRecyclerView.setHasMore(false);
                }
                if (!isMore) {
                    mPage = 2;
                } else {
                    mPage++;
                }

                if (!isMore) {
                    mData.clear();
                }
                mData.addAll(courseListItemModels);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new CourseOverFragmentAdapter(mData, activity);
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
        loadData(true, false);
    }
}
