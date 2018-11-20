package com.growalong.android.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.model.CollectModel;

import java.util.List;

/**
 * Created by yangxing on 2018/11/18.
 */
public class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FILE = 1;
    private static final int AUDIO = 2;
    private static final int VIDEO = 3;
    private static final int IMAGE = 4;
    private static final int TEXT = 5;

    private List<CollectModel> mData;

    private Context context;

    public CollectAdapter(List<CollectModel> mData, Context context) {
        this.mData = mData;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == FILE) {
            return new FileCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_file, parent, false));
        } else if (viewType == AUDIO) {
            return new FileCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_file, parent, false));
        } else if (viewType == VIDEO) {
            return new FileCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_file, parent, false));
        } else if (viewType == IMAGE) {
            return new FileCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_file, parent, false));
        } else {
            return new FileCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_file, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }


    @Override
    public int getItemViewType(int position) {
        if ("file".equals(mData.get(position).getType())) {
            return FILE;
        } else if ("audio".equals(mData.get(position).getType())) {
            return AUDIO;
        } else if ("video".equals(mData.get(position).getType())) {
            return VIDEO;
        } else if ("image".equals(mData.get(position).getType())) {
            return IMAGE;
        } else {
            return TEXT;
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    private class BaseCollectItemViewHolder extends RecyclerView.ViewHolder {
        TextView from, time;
        public BaseCollectItemViewHolder(View view) {
            super(view);
            from = view.findViewById(R.id.frome);
            time = view.findViewById(R.id.time);
        }

        public void setData(String fromStr, String timeStr){
            from.setText(fromStr);
            time.setText(timeStr);
        }
    }

    private class FileCollectItemViewHolder extends BaseCollectItemViewHolder{
        TextView title;
        public FileCollectItemViewHolder(View view) {
            super(view);
        }

        public void setData(CollectModel collectModel){
            setData(collectModel.getContent(), collectModel.getContent());
            title.setText(collectModel.getTitle());
        }

    }
}
