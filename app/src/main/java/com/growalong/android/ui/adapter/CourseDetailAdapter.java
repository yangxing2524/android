package com.growalong.android.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.model.CourseDetailInfoModel;
import com.growalong.android.model.CourseDetailModel;
import com.growalong.android.ui.customview.ScaleImageView;
import com.growalong.android.util.GlideUtils;
import com.growalong.android.util.Utils;

public class CourseDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_INTRODUCTION_TITLE = 10;  //简介标题
    public static final int VIEW_TYPE_INTRODUCTION_TEXT = 11;   //文字简介
    public static final int VIEW_TYPE_INTRODUCTION_IMAGE = 12;  //图片简介
    public static final int VIEW_TYPE_VIDEO = 5;    //视频简介
    public static final int VIEW_TYPE_DEFAULT = 7;    //
    public static final int VIEW_TYPE_FILE = 6;    //文件
    private final CourseDetailModel mData;
    private final Context mContext;

    public CourseDetailAdapter(Context context, CourseDetailModel courseDetailModel) {
        mData = courseDetailModel;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new TopViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_course_detail_top, parent, false));
        } else if (viewType == VIEW_TYPE_INTRODUCTION_TITLE) {
            return new TitleViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_holder_view_title_home_channel, parent, false));
        } else if (viewType == VIEW_TYPE_INTRODUCTION_TEXT) {
            return new IntroductionTextViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_holder_view_text_introduction_channel, parent, false));
        } else if (viewType == VIEW_TYPE_INTRODUCTION_IMAGE) {
            return new IntroductionImageViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_holder_view_image_introduction_channel, parent, false));
        } else if (viewType == VIEW_TYPE_VIDEO) {
            //视频简介
            return new ChannelVideoViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_holder_view_video_channel, parent, false));
        } else {
            return new TitleViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_holder_view_title_home_channel, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TopViewHolder) {
            TopViewHolder topViewHolder = (TopViewHolder) holder;
            topViewHolder.setData(mData.getCourseDetailInfoModel());
        } else if (holder instanceof TitleViewHolder) {
            String content = mData.getMaterialModelList().get(position - 1).getContent();
            TitleViewHolder viewHolder = (TitleViewHolder) holder;
            viewHolder.setData(content, R.mipmap.icon_channel_home_title_left);
        } else if (holder instanceof IntroductionTextViewHolder) {
            String content = mData.getMaterialModelList().get(position - 1).getContent();
            IntroductionTextViewHolder viewHolder = (IntroductionTextViewHolder) holder;
            viewHolder.setData(content);
        } else if (holder instanceof IntroductionImageViewHolder) {
            String content = mData.getMaterialModelList().get(position - 1).getContent();
            IntroductionImageViewHolder viewHolder = (IntroductionImageViewHolder) holder;
            viewHolder.setData(content, mData.getMaterialModelList().get(position - 1).getTitle());
        } else if (holder instanceof ChannelVideoViewHolder) {
            String content = mData.getMaterialModelList().get(position - 1).getContent();
            ChannelVideoViewHolder viewHolder = (ChannelVideoViewHolder) holder;
            viewHolder.setData(content, mData.getMaterialModelList().get(position - 1).getTitle());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            String type = mData.getMaterialModelList().get(position - 1).getType();
            if ("file".equals(type)) {
                return VIEW_TYPE_FILE;
            } else if ("video".equals(type)) {
                return VIEW_TYPE_VIDEO;
            } else if ("image".equals(type)) {
                return VIEW_TYPE_INTRODUCTION_IMAGE;
            } else if ("text".equals(type)) {
                return VIEW_TYPE_INTRODUCTION_TEXT;
            } else {
                return VIEW_TYPE_DEFAULT;
            }
        }
    }

    @Override
    public int getItemCount() {
        if(mData.getCourseDetailInfoModel() == null){
            return 0;
        }
        return mData.getMaterialModelList() != null && mData.getMaterialModelList().size() != 0 ? 1 + mData.getMaterialModelList().size() : 1;
    }

    class TopViewHolder extends RecyclerView.ViewHolder {
        ScaleImageView scaleImageView;
        TextView title, time;

        public TopViewHolder(View itemView) {
            super(itemView);
            scaleImageView = itemView.findViewById(R.id.course_bg);
            title = itemView.findViewById(R.id.title);
            time = itemView.findViewById(R.id.time);
        }


        public void setData(CourseDetailInfoModel courseDetailInfoModel) {
            Glide.with(mContext).load(courseDetailInfoModel.getImgUrl()).asBitmap().into(scaleImageView);
            title.setText(courseDetailInfoModel.getTitle());
            String str = Utils.stampToDate(courseDetailInfoModel.getCreateTime(), "yyyy-MM-dd") + mContext.getResources().getString(R.string.update);
            time.setText(str);
        }
    }


    public class TitleViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;

        public TitleViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
        }

        public void setData(String title, int resId) {
            if (!TextUtils.isEmpty(title)) {
                mTitle.setText(title);
            }
            Drawable mDrawableLeft = mContext.getResources().getDrawable(resId);
            mDrawableLeft.setBounds(0, 0, mDrawableLeft.getMinimumWidth(), mDrawableLeft.getMinimumHeight());
            mTitle.setCompoundDrawables(mDrawableLeft, null, null, null);
        }
    }

    private class IntroductionTextViewHolder extends RecyclerView.ViewHolder {
        private TextView mText;

        public IntroductionTextViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.text);
        }

        public void setData(String data) {
            if (!TextUtils.isEmpty(data)) {
                mText.setText(data);
            }
        }
    }

    private class IntroductionImageViewHolder extends RecyclerView.ViewHolder {
        private View mRootView;
        private SubsamplingScaleImageView mImage;
        private Context mContext;
        private TextView mTitle;

        public IntroductionImageViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mRootView = itemView.findViewById(R.id.root_view);
            mImage = (SubsamplingScaleImageView) itemView.findViewById(R.id.image);
            mTitle = itemView.findViewById(R.id.text);
        }

        public void setData(String url, String title) {
            if (!TextUtils.isEmpty(url)) {
                int width = MyApplication.getInstance().getScreenWidth() - mRootView.getPaddingLeft() - mRootView.getPaddingLeft();
                GlideUtils.FitXY(mContext, Utils.compressQualityOSSImageUrl(url), mImage, width);
            }
            mTitle.setText(title);
        }
    }
}
