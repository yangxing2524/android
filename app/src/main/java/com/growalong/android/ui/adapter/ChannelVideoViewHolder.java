package com.growalong.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.growalong.android.R;
import com.growalong.android.ui.TopicDetailVideoActivity;

/**
 * Created by gangqing on 2017/2/22.
 */

public class ChannelVideoViewHolder extends RecyclerView.ViewHolder {
    private ImageView mImage;
    private Context mContext;

    public ChannelVideoViewHolder(View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        mImage = (ImageView) itemView.findViewById(R.id.image);
    }

    public void setData(final String url) {

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(url)) {
                    TopicDetailVideoActivity.startThisActivity(mContext, url);
                }
            }
        });
    }
}
