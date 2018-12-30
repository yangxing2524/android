package com.growalong.android.image;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.growalong.android.R;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.ui.dialog.CommonAffirmDialog;
import com.growalong.android.util.ImageUtil;
import com.growalong.android.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.fragment.ImagePagerFragment;

/**
 * Created by chenjiawei on 2016/10/26.
 */

public class ImagePagerActivity extends QLActivity {

    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static final int MODEFY_IMG = 11;
    private ArrayList<String> mSelectPhotos = null;
    private ImagePagerFragment mImagePagerFragment;
    private int[] mScreenLocation = new int[2];
    private int mIndex = 1;//显示的图片，从1开始
    private int mWidth = 0;
    private int mHeight = 0;
    private int mPhotoSize;

    @BindView(R.id.iv_back)
    ImageView mBackView;
    @BindView(R.id.tv_indicator)
    TextView mIndicatorView;
    @BindView(R.id.rl_all)
    View rootView;
    @BindView(R.id.container)
    InterceptLongClickLayout container;

    SavePicturePopupWindow mSavePicturePopupWindow;

    public static void startThisActivity(Context context, String[] array, int index, boolean showDelete) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
        Bundle bundle = new Bundle();
        bundle.putStringArray(ImagePagerActivity.EXTRA_IMAGE_URLS, array);
        bundle.putInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, index);
        bundle.putBoolean("isDelete", showDelete);
        intent.putExtras(bundle);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, 1);
        } else {
            context.startActivity(intent);
        }
    }

    public static void startThisActivity(Context context, String[] array, int index) {
        startThisActivity(context, array, index, false);
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {
        initData();
        initUI();
    }

    private void initData() {
        Bundle bundle = this.getIntent().getExtras();
        String[] selectPathArr = bundle.getStringArray(EXTRA_IMAGE_URLS);
        this.mSelectPhotos = new ArrayList<>(Arrays.asList(selectPathArr));
        this.mIndex = bundle.getInt(EXTRA_IMAGE_INDEX, 0) + 1;
        this.mPhotoSize = mSelectPhotos.size();
    }

    private void initUI() {
        if (getIntent().getBooleanExtra("isDelete", false)) {
            View view = findViewById(R.id.delete);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonAffirmDialog.Builder(2).setContent("是否删除图片").setIAffirmDialogClick(new CommonAffirmDialog.IAffirmDialogClick() {
                        @Override
                        public void onOkClick() {
                            mSelectPhotos.remove(mIndex - 1);
                            mImagePagerFragment.getPaths().remove(mIndex - 1);
                            mImagePagerFragment.getViewPager().getAdapter().notifyDataSetChanged();
                            mPhotoSize--;
                            if (mIndex > mSelectPhotos.size()) {
                                mIndex--;
                                mIndex = Math.max(mIndex, 0);
                            }

                            setResult(MODEFY_IMG, new Intent().putStringArrayListExtra("img_result", mSelectPhotos));

                            if (mPhotoSize == 0) {
                                finish();
                            }
                            String indicatorStr = getResources().getString(R.string.viewpager_indicator);
                            String result = String.format(indicatorStr, mIndex, mPhotoSize);
                            mIndicatorView.setText(result);

                        }

                        @Override
                        public void onCancelClick() {

                        }
                    }).show(getSupportFragmentManager(), "");
                }
            });
        }
        String indicatorStr = getResources().getString(R.string.viewpager_indicator);
        String result = String.format(indicatorStr, mIndex, mPhotoSize);
        mIndicatorView.setText(result);
        final ImagePagerFragment imagePagerFragment =
                ImagePagerFragment.newInstance(mSelectPhotos, mIndex - 1, mScreenLocation, mWidth,
                        mHeight);
        this.addImagePagerFragment(imagePagerFragment);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mImagePagerFragment.getViewPager() != null) {
                    mImagePagerFragment.getViewPager().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            mIndex = position + 1;
                            String indicatorStr = getResources().getString(R.string.viewpager_indicator);
                            String result = String.format(indicatorStr, mIndex, mPhotoSize);
                            mIndicatorView.setText(result);
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        container.setOnLongClickLister(new InterceptLongClickLayout.OnLongClickLister() {
            @Override
            public void onLongClick() {
                if (mSavePicturePopupWindow == null) {
                    mSavePicturePopupWindow = new SavePicturePopupWindow(ImagePagerActivity.this, new SavePicturePopupWindow.OnSaveClickListener() {
                        @Override
                        public void save() {
                            String state = Environment.getExternalStorageState();
                            if (!state.equals(Environment.MEDIA_MOUNTED)) {
                                ToastUtil.shortShow("保存失败");
                                return;
                            }
                            int current = mImagePagerFragment.getViewPager().getCurrentItem();
                            if (current < mSelectPhotos.size()) {

                                String url = mSelectPhotos.get(current);
                                if (url.indexOf("gif") > -1) {
                                    ToastUtil.shortShow("暂不支持gif文件保存");
                                } else {
                                    showLoadingDialog("保存中");
                                    Glide.with(ImagePagerActivity.this).load(url).into(bmTarget);
                                }
                            }
                        }
                    });
                    mSavePicturePopupWindow.setPopupGravity(Gravity.BOTTOM);
                }
                mSavePicturePopupWindow.showPopupWindow();
            }
        });
    }

    private SimpleTarget bmTarget = new SimpleTarget<GlideBitmapDrawable>() {
        @Override
        public void onResourceReady(GlideBitmapDrawable drawable, GlideAnimation glideAnimation) {
            hideLoadingDialog();
            mSavePicturePopupWindow.dismiss();
            if (drawable == null) {
                ToastUtil.shortShow("保存失败");
                return;
            }
            Bitmap bitmap = drawable.getBitmap();
            if (bitmap != null) {

                ImageUtil.saveImageToGallery(ImagePagerActivity.this, bitmap);
                ToastUtil.shortShow("已保存到系统相册");
            }

        }
    };

    public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
        this.mImagePagerFragment = imagePagerFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .add(me.iwf.photopicker.R.id.container, this.mImagePagerFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.image_detail_pager;
    }

    @OnClick(R.id.iv_back)
    public void back() {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
