package com.growalong.android.ui.recyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Created by yangxing on 2018/6/29.
 */
public class JudgeNestedSuperRecyclerCallBack {
    private JudgeNestedSuperRecycler.ICanScroll ICanScroll;
    private RecyclerView.OnScrollListener onScrollListener;
    int[] child = new int[2];
    int[] parent = new int[2];

    public JudgeNestedSuperRecyclerCallBack() {
        ICanScroll = new JudgeNestedSuperRecycler.ICanScroll() {

            @Override
            public boolean isCanScroll() {
                return child[1] == parent[1] && child[1] != 0;
            }

        };
        onScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (recyclerView.getChildCount() > 0) {

                    recyclerView.getChildAt(0).getLocationOnScreen(child);
                    recyclerView.getLocationOnScreen(parent);

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }

    public RecyclerView.OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public JudgeNestedSuperRecycler.ICanScroll getICanScroll() {
        return ICanScroll;
    }
}
