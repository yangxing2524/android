package com.growalong.android.ui.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.growalong.android.util.LogUtil;

/**
 * Created by yangxing on 2018/6/22.
 */
public class JudgeNestedSuperRecycler extends RecyclerView {
    private boolean isNeedScroll = true;
    private float xDistance, yDistance, xLast, yLast;
    private int scaledTouchSlop;
    private float x1;
    private float y1;
    private ICanScroll mICanScroll;

    public JudgeNestedSuperRecycler(Context context) {
        super(context, null);
    }

    public JudgeNestedSuperRecycler(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public JudgeNestedSuperRecycler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }
    public interface ICanScroll {
        boolean isCanScroll();

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                if(yDistance <= 10){
                    return super.onInterceptTouchEvent(ev);
                }
                boolean shang = curY - yLast > 0;
                xLast = curX;
                yLast = curY;

                boolean b = (isNeedScroll || shang);
                if (mICanScroll != null && !isNeedScroll) {
                    b = b && mICanScroll.isCanScroll();
                }
                LogUtil.e("ssss b : " + b);
                LogUtil.e("ssss isNeedScroll : " + isNeedScroll);

                return (!(xDistance > yDistance || yDistance < scaledTouchSlop) && b);

        }
        return super.onInterceptTouchEvent(ev);
    }

    /*
    改方法用来处理NestedScrollView是否拦截滑动事件
     */
    public void setNeedScroll(boolean isNeedScroll) {
        this.isNeedScroll = isNeedScroll;
    }

    public boolean isNeedScroll() {
        return isNeedScroll;
    }

    public void setICanScroll(ICanScroll ICanScroll) {
        this.mICanScroll = ICanScroll;
    }
}
