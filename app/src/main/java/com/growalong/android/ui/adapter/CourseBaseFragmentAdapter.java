package com.growalong.android.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import com.growalong.android.model.CourseListItemModel;
import com.growalong.android.ui.QLActivity;

import java.util.List;

/**
 */
public abstract class CourseBaseFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final List<CourseListItemModel> mData;

    protected final LayoutInflater mLayoutInflater;
    protected final QLActivity mContext;

    public CourseBaseFragmentAdapter(List<CourseListItemModel> list, QLActivity activity) {
        mData = list;
        mContext = activity;
        mLayoutInflater = LayoutInflater.from(activity);
    }


    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }
}
