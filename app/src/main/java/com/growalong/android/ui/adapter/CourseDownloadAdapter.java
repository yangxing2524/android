package com.growalong.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.utils.FileUtil;
import com.growalong.android.model.CourseMaterialModel;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.download.ProgressHelper;
import com.growalong.android.net.retrofit.service.IDownloadApis;
import com.growalong.android.util.ToastUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by murphy on 2017/11/8.
 */

public class CourseDownloadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<CourseMaterialModel> mList = new ArrayList<>();

    IDownloadApis updateApis;

    public CourseDownloadAdapter(Context context, List<CourseMaterialModel> list) {
        mContext = context;
        mList.clear();
        mList.addAll(list);
        updateApis = BaseRetrofitClient.getInstance().getOtherRetrofit(ProgressHelper.addProgress(null).build()).create(IDownloadApis.class);
    }

    public void setList(List<CourseMaterialModel> list) {
        if (mList != null) {
            mList.clear();
            mList.addAll(list);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_download, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        ViewHolder viewHolder1 = (ViewHolder) viewHolder;
        viewHolder1.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTv;
        public TextView downloadTv;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title);
            downloadTv = itemView.findViewById(R.id.download);
            downloadTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CourseMaterialModel courseMaterialModel = (CourseMaterialModel) v.getTag(R.id.tag_first);
                    final String url = courseMaterialModel.getContent();
                    downloadTv.setText(mContext.getResources().getString(R.string.downloading));
                    downloadTv.setTag(R.id.tag_first, "");
                    final File file = new File(FileUtil.getDownloadFileFromUrl(url, courseMaterialModel.getTitle()));
                    if (!TextUtils.isEmpty(url)) {
                        Call<ResponseBody> call = updateApis.downloadFile(url);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    InputStream is = response.body().byteStream();
                                    file.getParentFile().mkdirs();
                                    file.createNewFile();
                                    FileOutputStream fos = new FileOutputStream(file);
                                    BufferedInputStream bis = new BufferedInputStream(is);
                                    byte[] buffer = new byte[1024];
                                    int len;
                                    while ((len = bis.read(buffer)) != -1) {
                                        fos.write(buffer, 0, len);
                                        fos.flush();
                                    }
                                    fos.close();
                                    bis.close();
                                    is.close();

                                    MyApplication.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            downloadTv.setText(mContext.getResources().getString(R.string.has_download));
                                            downloadTv.setTag(R.id.tag_first, "");
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    call.cancel();
                                    file.deleteOnExit();
                                    ToastUtil.shortShow(mContext.getResources().getString(R.string.download_fail));
                                    MyApplication.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            downloadTv.setText(mContext.getResources().getString(R.string.download));
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                call.cancel();
                                ToastUtil.shortShow(mContext.getResources().getString(R.string.download_fail));
                                file.deleteOnExit();
                                MyApplication.runOnUIThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        downloadTv.setText(mContext.getResources().getString(R.string.download));
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }

        public void setData(CourseMaterialModel courseMaterialModel) {
            titleTv.setText(courseMaterialModel.getTitle());
            if (FileUtil.isDownloadFileFromUrlExist(courseMaterialModel.getContent(), courseMaterialModel.getTitle())) {
                downloadTv.setText(mContext.getResources().getString(R.string.has_download));
                downloadTv.setTag(R.id.tag_first, "");
            } else {
                downloadTv.setText(mContext.getResources().getString(R.string.download));
                downloadTv.setTag(R.id.tag_first, courseMaterialModel);
            }
        }
    }

}
