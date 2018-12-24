package com.growalong.android.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.model.CourseDownloadModel;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.OpenFileUtil;
import com.growalong.android.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yangxing on 2018/11/18.
 */
public class MyDownloadedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CourseDownloadModel> mData;

    private QLActivity context;

    public MyDownloadedAdapter(List<CourseDownloadModel> mData, QLActivity context) {
        this.mData = mData;
        this.context = context;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new TextDownloadItemViewHolder(layoutInflater.inflate(R.layout.item_download_viewholder_, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CourseDownloadModel download = mData.get(position);
        BaseCollectItemViewHolder baseDownloadItemViewHolder = (BaseCollectItemViewHolder) holder;
        baseDownloadItemViewHolder.setData(download, position);
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    private abstract class BaseCollectItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView size, time;
        int position;

        public BaseCollectItemViewHolder(View view) {
            super(view);
            size = view.findViewById(R.id.frome);
            time = view.findViewById(R.id.time);
            view.setOnCreateContextMenuListener(this);
        }

        public abstract void setData(CourseDownloadModel string, int position);

        public void setSizeAndTime(String sizeStr, String timeStr, String fileName, int position) {
            size.setText(sizeStr);
            time.setText(timeStr);
            this.position = position;
            itemView.setTag(R.id.tag_first, fileName);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(position, 1, Menu.NONE, context.getResources().getString(R.string.download_del));
        }
    }

    private class TextDownloadItemViewHolder extends BaseCollectItemViewHolder {
        TextView contentTv;

        public TextDownloadItemViewHolder(View view) {
            super(view);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CourseDownloadModel courseDownloadModel = (CourseDownloadModel) v.getTag(R.id.tag_first);
                    OpenFileUtil.openFile(context, new File(courseDownloadModel.getPath()));
                }
            };
            contentTv = view.findViewById(R.id.content);
            contentTv.setOnClickListener(onClickListener);
            itemView.setOnClickListener(onClickListener);
        }

        public void setData(CourseDownloadModel courseDownloadModel, int position) {
            setSizeAndTime(Utils.FormetFileSize(courseDownloadModel.getSize()), (new SimpleDateFormat("yyyy-MM-dd")).format(courseDownloadModel.getTime()), courseDownloadModel.getName(), position);
            String nam = courseDownloadModel.getName();
            if (nam != null) {
                contentTv.setText(nam);
            } else {
                LogUtil.e("collect text content is wrong");
            }
            itemView.setTag(R.id.tag_first, courseDownloadModel);
            contentTv.setTag(R.id.tag_first, courseDownloadModel);
        }
    }

}
