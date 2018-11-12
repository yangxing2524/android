package com.growalong.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.File;

/**
 * Created by gangqing on 2017/2/24.
 */

public class GlideUtils {
    public static void FitXY(Context context, String url, final ImageView image, int screenWidth) {
        final int maxWidth;
        if (screenWidth <= 0) {
            maxWidth = image.getWidth();
        } else {
            maxWidth = screenWidth;
        }
        Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                int width = resource.getWidth();
                int height = resource.getHeight();
                ViewGroup.LayoutParams para = image.getLayoutParams();

                para.width = maxWidth;
                para.height = maxWidth * height / width;
                image.setLayoutParams(para);
                //图片宽度超过600PX都拉伸等比适配展示
//                if (width >= 600 || width > maxWidth) {
//                    para.width = maxWidth;
//                    para.height = maxWidth * height / width;
//                    image.setLayoutParams(para);
//                    resource = zoomBitmap(resource, para.width, para.height);
//                    image.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                } else {
//                    para.width = width;
//                    para.height = height;
//                    image.setScaleType(ImageView.ScaleType.FIT_XY);
//                    image.setLayoutParams(para);
//                }

                image.setImageBitmap(resource);
            }
        });
    }

    public static void FitXY(Context context, String url, final SubsamplingScaleImageView image, int screenWidth) {
        final int maxWidth;
        if (screenWidth <= 0) {
            maxWidth = image.getWidth();
        } else {
            maxWidth = screenWidth;
        }
        Glide.with(context).load(url).downloadOnly(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                BitmapFactory.Options option = new BitmapFactory.Options();
                option.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(resource.getPath(), option);
                float scale;
                int width = option.outWidth;
                int height = option.outHeight;
                ViewGroup.LayoutParams para = image.getLayoutParams();

                para.width = maxWidth;
                para.height = maxWidth * height / width;
                image.setLayoutParams(para);
                scale = ((float) maxWidth) / ((float) width);

//                //图片宽度超过600PX都拉伸等比适配展示
//                if (width >= 600 || width > maxWidth) {
//                    para.width = maxWidth;
//                    para.height = maxWidth * height / width;
//                    image.setLayoutParams(para);
//
//                    scale = ((float) maxWidth) / ((float) width);
//                } else {
//                    para.width = width;
//                    para.height = height;
//                    image.setLayoutParams(para);
//
//                    scale = 1.0f;
//                }

                image.setMinScale(scale);//最小显示比例
                image.setMaxScale(scale);//最大显示比例（太大了图片显示会失真，因为一般微博长图的宽度不会太宽）
                // 将保存的图片地址给SubsamplingScaleImageView,这里注意设置ImageViewState设置初始显示比例
                image.setImage(ImageSource.uri(Uri.fromFile(resource)), new ImageViewState(scale, new PointF(0, 0), 0));
            }
        });
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap == null || width <= 0 || height <= 0)
            return null;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
//        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleWidth);

        Bitmap newbmp = null;
        try {
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
            try {
                newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            } catch (OutOfMemoryError error1) {
                error1.printStackTrace();
            }
        }
        if (newbmp == null) {
            newbmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
        }
        return newbmp;
    }
}
