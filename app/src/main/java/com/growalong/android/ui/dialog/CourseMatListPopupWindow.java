package com.growalong.android.ui.dialog;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;

import com.growalong.android.R;
import com.growalong.android.model.CourseMaterialModel;
import com.growalong.android.ui.adapter.CourseDownloadAdapter;
import com.growalong.android.ui.customview.popupwindow.BasePopupWindow;

import java.util.List;

/**
 * Created by murphy on 2017/11/11.
 */

public class CourseMatListPopupWindow extends BasePopupWindow {

    RecyclerView mRecyclerView;
    CourseDownloadAdapter mCouponsAdapter;
    View ivClose, downloadAll;
    private Context mContext;

    public CourseMatListPopupWindow(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        mRecyclerView = (RecyclerView) findViewById(R.id.coupon_recycler_view);
        ivClose = findViewById(R.id.ivClose);
        downloadAll = findViewById(R.id.downloadAll);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        downloadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mCouponsAdapter.
            }
        });
    }

    public void setList(List<CourseMaterialModel> list) {

        if (mCouponsAdapter == null) {
            mCouponsAdapter = new CourseDownloadAdapter(mContext, list);
            mRecyclerView.setAdapter(mCouponsAdapter);
        } else {
            mCouponsAdapter.setList(list);
            mCouponsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.view_course_download_popup);
    }

    @Override
    public View initAnimaView() {
        return null;
    }

    @Override
    protected Animation initShowAnimation() {
        return null;
    }

    @Override
    public View getClickToDismissView() {
        return getPopupWindowView();
    }

}
