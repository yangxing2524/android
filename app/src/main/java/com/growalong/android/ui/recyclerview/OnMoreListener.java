package com.growalong.android.ui.recyclerview;

/**
 * Created by murphy on 10/9/16.
 */

public interface OnMoreListener {
    /**
     * @param overallItemsCount
     * @param itemsBeforeMore
     * @param maxLastVisiblePosition for staggered grid this is max of all spans
     */
    void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition);
}
