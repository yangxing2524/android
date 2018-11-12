package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.growalong.android.R;
import com.growalong.android.ui.recyclerview.ISuperRefreshView;
import com.growalong.android.ui.recyclerview.SuperRecyclerView;

import butterknife.BindView;

/**
 */
public class CourseRunningFragment extends NewBaseListFragment {

    @BindView(R.id.recyclerview)
    public SuperRecyclerView mRecyclerView;

    @Override
    public void setupView(Bundle savedInstanceState, View view) {

    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return null;
    }

    @Override
    public ISuperRefreshView<RecyclerView.Adapter> getRefreshView() {
        return null;
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
}
