package com.growalong.android.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.model.CourseListItemModel;
import com.growalong.android.ui.CourseDetailActivity;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.ui.widget.RecyclerViewHolder;

import java.util.List;

/**
 */
public class CourseOverFragmentAdapter extends CourseBaseFragmentAdapter {

    public CourseOverFragmentAdapter(List<CourseListItemModel> list, QLActivity activity) {
        super(list, activity);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_new_homepage_course_item, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CourseListItemModel model = (CourseListItemModel) v.getTag(R.id.tag_first);
                CourseDetailActivity.startThis(model.getId(), mContext);
            }
        });
        return new RecyclerViewHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        ImageView imageView = recyclerViewHolder.getView(R.id.img);
        CourseListItemModel courseListItemModel = mData.get(position);
        Glide.with(mContext).load(courseListItemModel.getImgUrl()).asBitmap().into(imageView);
        recyclerViewHolder.setText(R.id.title, courseListItemModel.getTitle());
        recyclerViewHolder.setText(R.id.intro, courseListItemModel.getDescription());
        holder.itemView.setTag(R.id.tag_first, courseListItemModel);
    }
}
