package com.growalong.android.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.growalong.android.R;

/**
 * Created by murphy on 8/31/16.
 */
public class AnimUtil {


    public static TranslateAnimation inFromRight() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(3000);
        return mHiddenAction;
    }

    public static TranslateAnimation outFromLeft() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mHiddenAction.setDuration(3000);
        return mHiddenAction;
    }

    public static ObjectAnimator shakeUpDown(View view, float shakeFactor) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, 1f),
                Keyframe.ofFloat(.2f, 1.1f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.2f),
                Keyframe.ofFloat(.5f, 1.2f),
                Keyframe.ofFloat(.6f, 1.2f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1.1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, 1f),
                Keyframe.ofFloat(.2f, 1.1f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.2f),
                Keyframe.ofFloat(.5f, 1.2f),
                Keyframe.ofFloat(.6f, 1.2f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1.1f)
        );

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -3f * shakeFactor),
                Keyframe.ofFloat(.2f, -3f * shakeFactor),
                Keyframe.ofFloat(.3f, 3f * shakeFactor),
                Keyframe.ofFloat(.4f, -3f * shakeFactor),
                Keyframe.ofFloat(.5f, 3f * shakeFactor),
                Keyframe.ofFloat(.6f, -3f * shakeFactor),
                Keyframe.ofFloat(.7f, 3f * shakeFactor),
                Keyframe.ofFloat(.8f, -3f * shakeFactor),
                Keyframe.ofFloat(.9f, 3f * shakeFactor),
                Keyframe.ofFloat(1f, 0)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate).setDuration(500);
    }

    public static ObjectAnimator shakeLeftRight(View view) {
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.space_08);

        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }

    public static ObjectAnimator shakeZoomIn(View view) {
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY).setDuration(300);
    }

    public static RotateAnimation getRotate() {
        final RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(5000);//设置动画持续时间
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        return animation;
    }

    public static Animation getNewRotate(Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.rota_anim);
        return animation;
    }

    public static void bgAnimation(View view) {
        ValueAnimator colorAnim = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.3f, 1.0f);
        colorAnim.setDuration(2000);
        colorAnim.start();
    }

    /**
     * 生成TranslateAnimation
     *
     * @param durationMillis 动画显示时间
     * @param start          初始位置
     */
    public static Animation getTranslateAnimation(int start, int end, int durationMillis) {
        Animation translateAnimation = new TranslateAnimation(0, 0, start, end);
        translateAnimation.setDuration(durationMillis);
        translateAnimation.setFillEnabled(true);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }


    /**
     * 生成ScaleAnimation
     * <p>
     * time=300
     */
    public static Animation getScaleAnimation(float fromX,
                                              float toX,
                                              float fromY,
                                              float toY,
                                              int pivotXType,
                                              float pivotXValue,
                                              int pivotYType,
                                              float pivotYValue) {
        Animation scaleAnimation = new ScaleAnimation(fromX, toX, fromY, toY, pivotXType, pivotXValue, pivotYType,
                pivotYValue
        );
        scaleAnimation.setDuration(300);
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }


    /**
     * 生成自定义ScaleAnimation
     */
    public static Animation getDefaultScaleAnimation() {
        Animation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(300);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        scaleAnimation.setFillEnabled(true);
        scaleAnimation.setFillAfter(true);
        return scaleAnimation;
    }


    /**
     * 生成默认的AlphaAnimation
     */
    public static Animation getDefaultAlphaAnimation() {
        Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setFillEnabled(true);
        alphaAnimation.setFillAfter(true);
        return alphaAnimation;
    }


    /**
     * 从下方滑动上来
     */
    public static AnimatorSet getDefaultSlideFromBottomAnimationSet(View mAnimaView) {
        AnimatorSet set = null;
        set = new AnimatorSet();
        if (mAnimaView != null) {
            set.playTogether(
                    ObjectAnimator.ofFloat(mAnimaView, "translationY", 250, 0).setDuration(400),
                    ObjectAnimator.ofFloat(mAnimaView, "alpha", 0.4f, 1).setDuration(250 * 3 / 2)
            );
        }
        return set;
    }

    public static void alphaAnimation(View view, float from, float to) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(from, to);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(100);
    }

    public static void translateAnimation(View view, final onDismissCallback callback) {

        AnimatorSet set = new AnimatorSet();

        float x = view.getTranslationX();
        float y = view.getTranslationY();
        ObjectAnimator translationXAnimation = ObjectAnimator.ofFloat(view, "translationX", x, 300, 300);
        ObjectAnimator translationYAnimation = ObjectAnimator.ofFloat(view, "translationY", x, 1000, 1000);
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.1f, 0f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.1f, 0f);
//        ObjectAnimator alphtAnimator = ObjectAnimator.ofFloat()
        set.playTogether(scaleXAnimator, scaleYAnimator, translationXAnimation, translationYAnimation);
        set.setDuration(500);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                callback.onDismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    public static void ticketTranslateAnimation(final View view, final View view2, final TextView textView) {

        final AnimatorSet set = new AnimatorSet();
        final AnimatorSet set2 = new AnimatorSet();
        ObjectAnimator translationXAnimation = ObjectAnimator.ofFloat(view2, "translationX", 200, 0, 0, 500);
        ObjectAnimator alphtAnimator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        set.playTogether(translationXAnimation);
        set.setDuration(4000);
        set2.playTogether(alphtAnimator);
        set2.setDuration(3000);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view2.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                textView.setText("✓ 已领取");
                set2.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.start();
    }

    public interface onDismissCallback {
        void onDismiss();
    }
}
