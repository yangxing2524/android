package com.growalong.android.ui.adapter;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.utils.FileUtil;
import com.growalong.android.model.CollectModel;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.download.DownloadProgressHandler;
import com.growalong.android.net.retrofit.download.ProgressHelper;
import com.growalong.android.net.retrofit.service.IDownloadApis;
import com.growalong.android.ui.FullImageActivity;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.ui.VideoActivity;
import com.growalong.android.util.LogUtil;
import com.growalong.android.util.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yangxing on 2018/11/18.
 */
public class CollectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FILE = 1;
    private static final int AUDIO = 2;
    private static final int VIDEO = 3;
    private static final int IMAGE = 4;
    private static final int TEXT = 5;
//    private final View.OnLongClickListener longClickListener;

    private List<CollectModel> mData;

    private QLActivity context;

    IDownloadApis updateApis;

    public CollectAdapter(List<CollectModel> mData, QLActivity context) {
        this.mData = mData;
        this.context = context;
        updateApis = BaseRetrofitClient.getInstance().getOtherRetrofit(ProgressHelper.addProgress(null).build()).create(IDownloadApis.class);
//        longClickListener = new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                return false;
//            }
//        };
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == FILE) {
            return new FileCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_file, parent, false));
        }
//        else if (viewType == AUDIO) {
//            return new AudioCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_audio, parent, false));
//        }
        else if (viewType == VIDEO) {
            return new VideoCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_video, parent, false));
        } else if (viewType == IMAGE) {
            return new ImageCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_image, parent, false));
        } else if (viewType == TEXT) {
            return new TextCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_text, parent, false));
        } else {
            return new UnknowCollectItemViewHolder(layoutInflater.inflate(R.layout.layout_collect_viewholder_text, parent, false));
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

    private abstract class BaseCollectItemViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView from, time;

        public BaseCollectItemViewHolder(View view) {
            super(view);
            from = view.findViewById(R.id.frome);
            time = view.findViewById(R.id.time);
//            view.setOnLongClickListener(longClickListener);
            view.setOnCreateContextMenuListener(this);
        }

        public abstract void setData(CollectModel collectModel);

        public void setTimeAndFrom(String fromStr, String timeStr, int id) {
            from.setText(fromStr);
            time.setText(timeStr);
            itemView.setTag(R.id.tag_first, id);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add((Integer) v.getTag(R.id.tag_first), 1, Menu.NONE, context.getResources().getString(R.string.collect_del));
        }
    }

    private class FileCollectItemViewHolder extends BaseCollectItemViewHolder {
        TextView titleTv;

        public FileCollectItemViewHolder(View view) {
            super(view);
            titleTv = view.findViewById(R.id.title);
        }

        public void setData(CollectModel collectModel) {
            setTimeAndFrom(collectModel.getContent(), collectModel.getContent(), (int) collectModel.getId());
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

            setTimeAndFrom(collectModel.getGroupName(), (new SimpleDateFormat("yyyy-MM-dd")).format(collectModel.getCreateTime()), (int) collectModel.getId());
            String[] content = Utils.getIMTextOrigString(collectModel.getContent());
            if (content != null) {
                contentTv.setText(content[0]);
            } else {
                LogUtil.e("collect text content is wrong");
            }
        }
    }

    private class UnknowCollectItemViewHolder extends BaseCollectItemViewHolder {
        TextView contentTv;

        public UnknowCollectItemViewHolder(View view) {
            super(view);
            contentTv = view.findViewById(R.id.content);
            contentTv.setText(context.getResources().getText(R.string.unknow_type));
        }

        public void setData(CollectModel collectModel) {

            setTimeAndFrom(collectModel.getGroupName(), (new SimpleDateFormat("yyyy-MM-dd")).format(collectModel.getCreateTime()), (int) collectModel.getId());
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
            setTimeAndFrom(collectModel.getGroupName(), (new SimpleDateFormat("yyyy-MM-dd")).format(collectModel.getCreateTime()), (int) collectModel.getId());
            imageView.setTag(R.id.tag_first, collectModel.getContent());
        }
    }

//    private class AudioCollectItemViewHolder extends BaseCollectItemViewHolder {
//        ImageView mSound;
//        private int mCurrentSoundType = -1;
//
//        public AudioCollectItemViewHolder(View view) {
//            super(view);
//            mSound = view.findViewById(R.id.imageView);
//            mSound.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    CollectModel collectModel = (CollectModel) v.getTag(R.id.tag_first);
//                    playAudio(collectModel.getContent());
//                }
//            });
//        }
//
//        /**
//         * @param type 0：不显示    1:暂停播放  2：播放中
//         */
//        private void setSoundStatus(int type) {
//            if (type == mCurrentSoundType)
//                return;
//            mCurrentSoundType = type;
//            switch (type) {
//                case 0:
//                    break;
//                case 1:
//                    Drawable drawable = mSound.getDrawable();
//                    if (drawable instanceof AnimationDrawable) {
//                        AnimationDrawable animation = (AnimationDrawable) drawable;
//                        if (animation.isRunning()) {
//                            animation.stop();
//                        }
//                    }
//                    mSound.setImageResource(R.mipmap.icon_topic_red_sound_3);
//                    break;
//                case 2:
//                    mSound.setImageResource(R.drawable.icon_topic_red_sound);
//                    AnimationDrawable animationDrawable = (AnimationDrawable) mSound.getDrawable();
//                    animationDrawable.start();
//                    break;
//            }
//        }
//
//        public void setData(CollectModel collectModel) {
//            Glide.with(context).load(collectModel.getContent()).asBitmap().into(mSound);
//            setSizeAndTime(collectModel.getGroupName(), (new SimpleDateFormat("yyyy-MM-dd")).format(collectModel.getCreateTime()));
//            mSound.setTag(R.id.tag_first, collectModel);
//        }
//
//        private void playAudio(String url) {
//            final File tempAudio = FileUtil.getTempFile(FileUtil.FileType.AUDIO);
//
//            Call<ResponseBody> call = updateApis.downloadFile(url);
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    try {
//                        InputStream is = response.body().byteStream();
//                        File file = new File(Environment.getExternalStorageDirectory(), "new_version.apk");
//                        FileOutputStream fos = new FileOutputStream(file);
//                        BufferedInputStream bis = new BufferedInputStream(is);
//                        byte[] buffer = new byte[1024];
//                        int len;
//                        while ((len = bis.read(buffer)) != -1) {
//                            fos.write(buffer, 0, len);
//                            fos.flush();
//                        }
//                        fos.close();
//                        bis.close();
//                        is.close();
//
//                        try {
//                            FileInputStream fis = new FileInputStream(tempAudio);
//                            MediaUtil.getInstance().play(fis);
//                            setSoundStatus(2);
//                            MediaUtil.getInstance().setEventListener(new MediaUtil.EventListener() {
//                                @Override
//                                public void onStop() {
//                                    setSoundStatus(1);
//                                }
//                            });
//                        } catch (Exception e) {
//
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        call.cancel();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    call.cancel();
//                }
//            });
//
//        }
//    }

    private class VideoCollectItemViewHolder extends BaseCollectItemViewHolder {
        ImageView imageView;
        View play;
        TextView progress;

        public VideoCollectItemViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            play = view.findViewById(R.id.layout_video);
            progress = view.findViewById(R.id.progress);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CollectModel collectModel = (CollectModel) play.getTag(R.id.tag_first);

                    final String path = FileUtil.getCacheFilePath(collectModel.getTitle());
                    File file = new File(path);
                    boolean b = true;
                    try {
                        b = file.length() >= Long.valueOf(collectModel.getOtherInfo());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (file.exists() && b) {
                        //文件如果存在，直接就播放
                        startVideo(path, collectModel.getOtherInfo());
                    } else {
                        ProgressHelper.setProgressHandler(new DownloadProgressHandler() {
                            @Override
                            protected void onProgress(long bytesRead, long contentLength, boolean done) {
                                String p = bytesRead * 100 / contentLength + "%";
                                progress.setText(p);
                                if (progress.getVisibility() == View.GONE) {
                                    progress.setVisibility(View.VISIBLE);
                                    play.setVisibility(View.GONE);
                                }
                            }
                        });

                        Call<ResponseBody> call = updateApis.downloadFile(collectModel.getContent());
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    InputStream is = response.body().byteStream();
                                    File file = new File(Environment.getExternalStorageDirectory(), "new_version.apk");
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
                                            progress.setVisibility(View.GONE);
                                            play.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    startVideo(path, collectModel.getOtherInfo());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    call.cancel();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                call.cancel();
                            }
                        });

                    }
                }
            });
        }

        public void setData(CollectModel collectModel) {
            Glide.with(context).load(collectModel.getOtherInfo()).asBitmap().into(imageView);
            setTimeAndFrom(collectModel.getGroupName(), (new SimpleDateFormat("yyyy-MM-dd")).format(collectModel.getCreateTime()), (int) collectModel.getId());
            play.setTag(R.id.tag_first, collectModel);
        }
    }

    private void startVideo(String path, String fileSize) {
        File file = new File(path);
//        Log.d(TAG, "file size " + file.length() + " ucg size " + e.getVideo().getSize());
//        QLog.d(TAG, QLog.USR, "file size " + file.length() + " ucg size " + e.getVideo().getSize());

        try {
            if (file.length() < Long.valueOf(fileSize)) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("path", path);
        context.startActivity(intent);
    }
}
