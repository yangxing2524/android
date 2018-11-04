package com.growalong.android.ui.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by yangxing on 2017/7/21.
 */

public class HorSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private boolean isLeftRightPaddingFull;
    private int space;

    public HorSpaceItemDecoration(int space) {
        this.space = space;
    }

    public HorSpaceItemDecoration setLeftRightPaddingFull(boolean b) {
        isLeftRightPaddingFull = b;
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.right = space / 2;
            if (isLeftRightPaddingFull)
                outRect.left = space;
            else
                outRect.left = 0;
        } else if (parent.getChildAdapterPosition(view) != parent.getChildCount() - 1) {
            outRect.left = space / 2;
            if (isLeftRightPaddingFull)
                outRect.right = space;
            else
                outRect.right = 0;
        } else {
            outRect.right = space / 2;
            outRect.left = space / 2;
        }
    }
}