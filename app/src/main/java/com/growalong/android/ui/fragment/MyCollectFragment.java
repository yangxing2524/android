package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.growalong.android.R;
import com.growalong.android.model.CollectModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.UserPresenter;
import com.growalong.android.ui.adapter.CollectAdapter;
import com.growalong.android.ui.recyclerview.ISuperRefreshView;
import com.growalong.android.ui.recyclerview.SuperRecyclerView;
import com.growalong.android.util.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yangxing on 2018/11/17.
 */
public class MyCollectFragment extends NewBaseListFragment {
    @BindView(R.id.recyclerview)
    public SuperRecyclerView mRecyclerView;

    private UserPresenter userPresenter = new UserPresenter();

    private int mPage = 1;

    private List<CollectModel> mData = new ArrayList<>();

    @Override
    public void setupView(Bundle savedInstanceState, View view) {
        loadData(false);
        int padding = DensityUtil.dip2px(activity, 15);
        view.setPadding(padding, padding / 2, padding, padding / 2);
    }


    public void loadData(final boolean isMore) {
        int page = 1;

        if (isMore) {
            page = mPage;
        }
        userPresenter.getMyCollect(page).subscribe(new CommSubscriber<List<CollectModel>>() {
            @Override
            public void onSuccess(List<CollectModel> collectModels) {
                if (!isMore) {
                    mPage = 2;
                } else {
                    mPage++;
                }

                if (!isMore) {
                    mData.clear();
                }

                mData.addAll(collectModels);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
            }
        });
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return new CollectAdapter(mData, activity);
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
        loadData(true);
    }
}
