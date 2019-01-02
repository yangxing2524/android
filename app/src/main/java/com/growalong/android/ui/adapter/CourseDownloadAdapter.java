package com.growalong.android.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.utils.FileUtil;
import com.growalong.android.model.CourseMaterialModel;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.download.ProgressHelper;
import com.growalong.android.net.retrofit.service.IDownloadApis;
import com.growalong.android.util.OpenFileUtil;
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
        private static final int DOWNLOADING = 1;
        private static final int DOWNLOADED = 2;
        public TextView titleTv;
        public TextView downloadTv;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title);
            downloadTv = itemView.findViewById(R.id.download);
            image = itemView.findViewById(R.id.image);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadTv.getTag(R.id.tag_second) != null &&
                            ((int) downloadTv.getTag(R.id.tag_second)) == DOWNLOADING) {
                        return;
                    }
                    final CourseMaterialModel courseMaterialModel = (CourseMaterialModel) downloadTv.getTag(R.id.tag_first);

                    if ("image".equals(courseMaterialModel.getType())) {
                        //图片
                        List<String> imgUrlList = new ArrayList<>();
                        for (int i = 0; i < mList.size(); i++) {
                            CourseMaterialModel courseMaterialModel1 = mList.get(i);
                            if ("image".equals(courseMaterialModel1.getType())) {
                                imgUrlList.add(courseMaterialModel1.getContent());
                            }
                        }
                        String[] strings = new String[imgUrlList.size()];
                        int k = 0;
                        for (int j = 0; j < imgUrlList.size(); j++) {
                            strings[j] = imgUrlList.get(j);
                            if (courseMaterialModel.getContent().equals(strings[j])) {
                                k = j;
                            }
                        }
                        OpenFileUtil.openImages(mContext, strings, k, false);
                        return;
                    }

                    final String url = courseMaterialModel.getContent();
                    final File file = new File(FileUtil.getDownloadFileFromUrl(url, courseMaterialModel.getTitle()));
                    boolean isFileExist = FileUtil.isDownloadFileFromUrlExist(courseMaterialModel.getContent(), courseMaterialModel.getTitle());
                    if (!isFileExist) {
                        downloadTv.setText(mContext.getResources().getString(R.string.downloading));
                        downloadTv.setTag(R.id.tag_second, DOWNLOADING);
                    } else {
                        OpenFileUtil.openFile(mContext, file);
                    }

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

                                    downloadTv.setTag(R.id.tag_second, DOWNLOADED);
                                    MyApplication.runOnUIThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            downloadTv.setText(mContext.getResources().getString(R.string.open));
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
                                downloadTv.setTag(R.id.tag_second, 0);
                            }
                        });
                    }
                }
            };
            downloadTv.setOnClickListener(onClickListener);
            itemView.setOnClickListener(onClickListener);
        }

        public void setData(CourseMaterialModel courseMaterialModel) {
            titleTv.setText(courseMaterialModel.getTitle());
            if ("image".equals(courseMaterialModel.getType()) || FileUtil.isDownloadFileFromUrlExist(courseMaterialModel.getContent(), courseMaterialModel.getTitle())) {
                downloadTv.setText(mContext.getResources().getString(R.string.open));
            } else {
                downloadTv.setText(mContext.getResources().getString(R.string.download));
            }

            if("image".equals(courseMaterialModel.getType())){
                image.setImageResource(R.mipmap.icon_image_small);
            }else if("video".equals(courseMaterialModel.getType())){
                image.setImageResource(R.mipmap.icon_video_small);
            }else {
                image.setImageResource(R.mipmap.icon_file_small);
            }
            downloadTv.setTag(R.id.tag_first, courseMaterialModel);
        }
    }

}
