package com.growalong.android.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.model.CollectModel;
import com.growalong.android.ui.FullImageActivity;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.Utils;

import java.text.SimpleDateFormat;
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

    private QLActivity context;

    public CollectAdapter(List<CollectModel> mData, QLActivity context) {
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
            return new ImageCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_image, parent, false));
        } else {
            return new TextCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_text, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CollectModel collectModel = mData.get(position);
        BaseCollectItemViewHolder baseCollectItemViewHolder = (BaseCollectItemViewHolder) holder;
        baseCollectItemViewHolder.setData(collectModel);
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

    private abstract class BaseCollectItemViewHolder extends RecyclerView.ViewHolder {
        TextView from, time;

        public BaseCollectItemViewHolder(View view) {
            super(view);
            from = view.findViewById(R.id.frome);
            time = view.findViewById(R.id.time);
        }

        public abstract void setData(CollectModel collectModel);

        public void setTimeAndFrom(String fromStr, String timeStr) {
            from.setText(fromStr);
            time.setText(timeStr);
        }
    }

    private class FileCollectItemViewHolder extends BaseCollectItemViewHolder {
        TextView titleTv;

        public FileCollectItemViewHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.title);
        }

        public void setData(CollectModel collectModel) {
            setTimeAndFrom(collectModel.getContent(), collectModel.getContent());
            titleTv.setText(collectModel.getTitle());
        }

    }

    private class TextCollectItemViewHolder extends BaseCollectItemViewHolder {
        TextView contentTv;

        public TextCollectItemViewHolder(View view) {
            super(view);
            contentTv = view.findViewById(R.id.content);
        }

        public void setData(CollectModel collectModel) {

            setTimeAndFrom(collectModel.getGroupName(), (new SimpleDateFormat("yyyy-MM-dd")).format(collectModel.getCreateTime()));
            String content = Utils.getIMTextString(collectModel.getContent());
            if (content != null) {
                contentTv.setText(content);
            } else {
                LogUtil.e("collect text content is wrong");
            }
        }
    }

    private class ImageCollectItemViewHolder extends BaseCollectItemViewHolder {
        ImageView imageView;

        public ImageCollectItemViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = (String) imageView.getTag(R.id.tag_first);
                    FullImageActivity.startThis(context, url);
                }
            });
        }

        public void setData(CollectModel collectModel) {
            Glide.with(context).load(collectModel.getContent()).asBitmap().into(imageView);
            setTimeAndFrom(collectModel.getGroupName(), (new SimpleDateFormat("yyyy-MM-dd")).format(collectModel.getCreateTime()));
            imageView.setTag(R.id.tag_first, collectModel.getContent());
        }
    }
}
