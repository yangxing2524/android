package com.growalong.android.ui.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.growalong.android.R;

/**
 * Created by yangxing on 2017/3/13.
 */

public class ScaleImageView extends android.support.v7.widget.AppCompatImageView {
    private double mScale = -1;

    public ScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyle, 0);
        mScale = a.getFloat(R.styleable.RoundImageView_scale_height_width, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mScale <= 0)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        else {
            int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                    - getPaddingRight();
            int height = (int) (width * mScale);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 设置宽长比例
     *
     * @param mScale 小于1
     */
    public void setmScale(double mScale) {
        this.mScale = mScale;
    }
}

