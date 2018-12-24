package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.utils.FileUtil;
import com.growalong.android.model.CourseDownloadModel;
import com.growalong.android.ui.adapter.MyDownloadedAdapter;
import com.growalong.android.ui.recyclerview.ISuperRefreshView;
import com.growalong.android.ui.recyclerview.SuperRecyclerView;
import com.growalong.android.util.DensityUtil;
import com.growalong.android.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by yangxing on 2018/11/17.
 */
public class MyDownloadFragment extends NewBaseListFragment {
    @BindView(R.id.recyclerview)
    public SuperRecyclerView mRecyclerView;

    private List<CourseDownloadModel> mData = new ArrayList<>();

    @Override
    public void setupView(Bundle savedInstanceState, View view) {
        int padding = DensityUtil.dip2px(activity, 15);
        view.setPadding(padding, padding / 2, padding, padding / 2);
        mRecyclerView.setRefreshEnable(false);
//        registerForContextMenu(mRecyclerView);
    }


    /**
     * 目前是通过文件夹中的文件来获取课程的下载资料，参考{@link FileUtil#getDownloadFileFromUrl(String, String)}
     */
    public void loadData() {
        activity.showLoadingDialog("");
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(FileUtil.COURSE_DIRECTORY);
                File[] list = file.listFiles();
                if (list == null || list.length == 0) {
                    return;
                }
                for (File s : list) {
                    if (s.getName().contains("&&")) {
                        String tem = s.getName().substring(s.getName().lastIndexOf("&&") + 2);
                        CourseDownloadModel courseDownloadModel = new CourseDownloadModel();
                        courseDownloadModel.setName(tem);
                        courseDownloadModel.setPath(s.getPath());
                        courseDownloadModel.setSize(s.length());
                        courseDownloadModel.setTime(s.lastModified());
                        mData.add(courseDownloadModel);
                    }
                }
                MyApplication.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                activity.hideLoadingDialog();
            }
        }).start();

    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int position = item.getGroupId();
        CourseDownloadModel remove = mData.remove(position);
        FileUtils.delete(remove.getPath());
        mAdapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        mAdapter = new MyDownloadedAdapter(mData, activity);
        loadData();
        return mAdapter;
    }

    @Override
    public ISuperRefreshView<RecyclerView.Adapter> getRefreshView() {
        return mRecyclerView;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.comm_list;
    }

    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
    }
}
