package com.growalong.android.ui.recyclerview;

import android.view.View;

import com.aspsine.swipetoloadlayout.OnRefreshListener;

/**
 * Created by murphy on 16/1/7.
 */
public interface ISuperRefreshView<T> {
    void setAdapter(T t);

    void setRefreshListener(OnRefreshListener listener);

    void setRefreshingColorResources(int... colorResources);

    void setupMoreListener(OnMoreListener onMoreListener, int max);

    void setLoadComplete(boolean isLoadComplete);

    void showProgress();

    void hideProgress();

    View getEmptyView();

    void setHasMore(boolean isLoadingMore);

    void moveToPosition(int position);

    void setRefreshing(boolean b);
}
