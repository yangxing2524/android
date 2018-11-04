package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.growalong.android.ui.recyclerview.ISuperRefreshView;
import com.growalong.android.ui.recyclerview.OnMoreListener;
import com.growalong.android.ui.recyclerview.SuperRecyclerView;

/**
 * Created by murphy on 10/9/16.
 */

public abstract class NewBaseListFragment extends NewBaseFragment implements OnRefreshListener, OnMoreListener {
    private RecyclerView.LayoutManager mLayoutManager;

    protected RecyclerView.Adapter mAdapter;

    private ISuperRefreshView<RecyclerView.Adapter> iSuperRefreshView;


    public final void initEventAndData(Bundle savedInstanceState, View rootView) {
        this.setupView(savedInstanceState, rootView);
        iSuperRefreshView = getRefreshView();
        if (null == iSuperRefreshView)
            throw new RuntimeException("RecyclerView could not be null");
        mLayoutManager = getLayoutManager();
        mAdapter = getAdapter();
        iSuperRefreshView.setAdapter(mAdapter);
        iSuperRefreshView.setRefreshListener(this);
        if (iSuperRefreshView instanceof SuperRecyclerView) {
            SuperRecyclerView iSuperRefreshView = (SuperRecyclerView) this.iSuperRefreshView;
            if (iSuperRefreshView.getRecyclerView().getLayoutManager() == null) {
                ((SuperRecyclerView) this.iSuperRefreshView).setLayoutManager(mLayoutManager);
            }
        }
        iSuperRefreshView.setupMoreListener(this, 1);
        initRefreshAfter();
    }

    public void initRefreshAfter(){

    }

    public abstract void setupView(Bundle savedInstanceState, View view);

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getContext());
    }

    public abstract RecyclerView.Adapter getAdapter();

    public abstract ISuperRefreshView<RecyclerView.Adapter> getRefreshView();
}
