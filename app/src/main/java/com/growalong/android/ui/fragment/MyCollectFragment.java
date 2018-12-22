package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonElement;
import com.growalong.android.R;
import com.growalong.android.model.CollectModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.UserPresenter;
import com.growalong.android.ui.adapter.CollectAdapter;
import com.growalong.android.ui.recyclerview.ISuperRefreshView;
import com.growalong.android.ui.recyclerview.SuperRecyclerView;
import com.growalong.android.util.DensityUtil;
import com.growalong.android.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Subscription;

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
//        registerForContextMenu(mRecyclerView);
    }


    public void loadData(final boolean isMore) {
        int page = 1;

        if (isMore) {
            page = mPage;
        }
        Subscription subscribe = userPresenter.getMyCollect(page, activity.doOnSubscribe, activity.doOnTerminate).subscribe(new CommSubscriber<List<CollectModel>>() {
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
        addSubscribe(subscribe);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        CollectModel message = null;
        for (CollectModel model : mData) {
            if (model.getId() == item.getGroupId()) {
                message = model;
            }
        }
        if (message == null) {
            return super.onContextItemSelected(item);
        }
        final CollectModel finalMessage = message;
        Subscription subscribe = userPresenter.removeCollect(message.getId()).subscribe(new CommSubscriber<JsonElement>() {
            @Override
            public void onSuccess(JsonElement jsonElement) {
                mData.remove(finalMessage);
                mAdapter.notifyDataSetChanged();
                ToastUtil.shortShow(getResources().getString(R.string.remove_collect_success));
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
                ToastUtil.shortShow(getResources().getString(R.string.remove_collect_failed));
            }
        });
        addSubscribe(subscribe);
        return super.onContextItemSelected(item);
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
