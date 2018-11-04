package com.growalong.android.ui.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.ui.recyclerview.swipe.SwipeDismissRecyclerViewTouchListener;

/**
 * Created by murphy on 10/9/16.
 */

public class SuperRecyclerView extends FrameLayout implements ISuperRefreshView<RecyclerView.Adapter> {
    protected int ITEM_LEFT_TO_LOAD_MORE = 10;

    protected RecyclerView mRecycler;
    protected ViewStub mProgress;
    protected ViewStub mMoreProgress;
    protected ViewStub mEmpty;
    protected View mProgressView;
    protected View mMoreProgressView;
    protected View mEmptyView;
    protected TextView tv_loading;
    protected RefreshHeaderView swipe_refresh_header;

    protected boolean mClipToPadding;
    protected int mPadding;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mScrollbarStyle;
    protected int mEmptyId;
    protected int mMoreProgressId;
    protected int mBackgroundResId;

    protected LAYOUT_MANAGER_TYPE layoutManagerType;

    protected RecyclerView.OnScrollListener mInternalOnScrollListener;
    //    private RecyclerView.OnScrollListener mSwipeDismissScrollListener;
    protected RecyclerView.OnScrollListener mExternalOnScrollListener;

    protected OnMoreListener mOnMoreListener;
    protected boolean isHasMore;
    protected SwipeToLoadLayout swipeToLoadLayout;

    protected int mSuperRecyclerViewMainLayout;
    private int mProgressId;

    private int[] lastScrollPositions;

    private LinearLayoutManager mLinearLayoutManager;

    private boolean isShowEmpty = false;
    boolean move = false;
    int mPosition = 0;

    public ScrollChangePositionCallback mScrollChangePositionCallback;
    private OnScrollChangeListener mOnScrollChangeListener;
    private boolean mShowNoMore = true;

    public SwipeToLoadLayout getSwipeToRefresh() {
        return swipeToLoadLayout;
    }

    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    public SuperRecyclerView(Context context) {
        super(context);
        initView();
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superrecyclerview);
        try {
            mSuperRecyclerViewMainLayout = a.getResourceId(R.styleable.superrecyclerview_mainLayoutId, R.layout.layout_progress_recyclerview);
            mClipToPadding = a.getBoolean(R.styleable.superrecyclerview_recyclerClipToPadding, false);
            mShowNoMore = a.getBoolean(R.styleable.superrecyclerview_recycler_show_no_more, true);
            mPadding = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.superrecyclerview_recyclerPaddingRight, 0.0f);
            mScrollbarStyle = a.getInt(R.styleable.superrecyclerview_scrollbarStyle, -1);
            mEmptyId = a.getResourceId(R.styleable.superrecyclerview_layout_empty, 0);
            mMoreProgressId = a.getResourceId(R.styleable.superrecyclerview_layout_moreProgress, R.layout.layout_more_progress);
            mProgressId = a.getResourceId(R.styleable.superrecyclerview_layout_progress, R.layout.layout_progress);
            mBackgroundResId = a.getResourceId(R.styleable.superrecyclerview_recyclerBackground, R.color.white);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        View v = LayoutInflater.from(getContext()).inflate(mSuperRecyclerViewMainLayout, this);
        swipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setEnabled(false);
        //设置刷新默认蓝色
//        mPtrLayout.setColorSchemeResources(R.color.color_f73657);
        mProgress = (ViewStub) v.findViewById(android.R.id.progress);

        mProgress.setLayoutResource(mProgressId);
        mProgressView = mProgress.inflate();
        tv_loading = mProgressView.findViewById(R.id.tv_loading);

        mMoreProgress = (ViewStub) v.findViewById(R.id.more_progress);
        mMoreProgress.setLayoutResource(mMoreProgressId);
        if (mMoreProgressId != 0)
            mMoreProgressView = mMoreProgress.inflate();
        mMoreProgress.setVisibility(View.GONE);

        mEmpty = (ViewStub) v.findViewById(R.id.empty);
        mEmpty.setLayoutResource(mEmptyId);
        if (mEmptyId != 0) {
            mEmptyView = mEmpty.inflate();
        }
        mEmpty.setVisibility(View.GONE);


        initRecyclerView(v);
    }

    /**
     * Implement this method to customize the AbsListView
     */
    protected void initRecyclerView(View view) {
        View recyclerView = view.findViewById(R.id.swipe_target);
        swipe_refresh_header = view.findViewById(R.id.swipe_refresh_header);

        if (recyclerView instanceof RecyclerView)
            mRecycler = (RecyclerView) recyclerView;
        else
            throw new IllegalArgumentException("SuperRecyclerView works with a RecyclerView!");


        mRecycler.setClipToPadding(mClipToPadding);
        mRecycler.setBackgroundResource(mBackgroundResId);
        mInternalOnScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                processOnMore();

                if (mExternalOnScrollListener != null)
                    mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);

                if (move) {
                    move = false;
                    int n = mPosition - mLinearLayoutManager.findFirstVisibleItemPosition();
                    if (0 <= n && n < mRecycler.getChildCount()) {
                        int top = mRecycler.getChildAt(n).getTop();
                        mRecycler.scrollBy(0, top);
                    }
                }
                if (mScrollChangePositionCallback != null) {
                    mScrollChangePositionCallback.onScroll(mLinearLayoutManager.findFirstVisibleItemPosition(),
                            mLinearLayoutManager.findLastVisibleItemPosition());
                }
//                if (mSwipeDismissScrollListener != null)
//                    mSwipeDismissScrollListener.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mExternalOnScrollListener != null)
                    mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);
                if (mScrollChangePositionCallback != null) {
                    mScrollChangePositionCallback.onScrollStateChanged(recyclerView, newState);
                }
                if (move && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    move = false;
                    int n = mPosition - mLinearLayoutManager.findFirstVisibleItemPosition();
                    if (0 <= n && n < mRecycler.getChildCount()) {
                        int top = mRecycler.getChildAt(n).getTop();
                        mRecycler.smoothScrollBy(0, top);
                    }

                }
//                if (mSwipeDismissScrollListener != null)
//                    mSwipeDismissScrollListener.onScrollStateChanged(recyclerView, newState);
            }
        };
        mRecycler.addOnScrollListener(mInternalOnScrollListener);

        if (!FloatUtil.compareFloats(mPadding, -1.0f)) {
            mRecycler.setPadding(mPadding, mPadding, mPadding, mPadding);
        } else {
            mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }

        if (mScrollbarStyle != -1) {
            mRecycler.setScrollBarStyle(mScrollbarStyle);
        }
    }

    public void setmOnScrollChangeListener(OnScrollChangeListener mOnScrollChangeListener) {
        this.mOnScrollChangeListener = mOnScrollChangeListener;
    }

    public void setRecyclerPadding(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        mPaddingLeft = paddingLeft;
        mPaddingTop = paddingTop;
        mPaddingRight = paddingRight;
        mPaddingBottom = paddingBottom;
        if (!FloatUtil.compareFloats(mPadding, -1.0f)) {
            mRecycler.setPadding(mPadding, mPadding, mPadding, mPadding);
        } else {
            mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }
    }

    private void processOnMore() {
        RecyclerView.LayoutManager layoutManager = mRecycler.getLayoutManager();
        int lastVisibleItemPosition = getLastVisibleItemPosition(layoutManager);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();

        if (((totalItemCount - lastVisibleItemPosition) <= ITEM_LEFT_TO_LOAD_MORE ||
                (totalItemCount - lastVisibleItemPosition) == 0 && totalItemCount > visibleItemCount)
                && isHasMore) {

            isHasMore = false;
            if (mOnMoreListener != null) {
                mMoreProgress.setVisibility(View.VISIBLE);
                mOnMoreListener.onMoreAsked(mRecycler.getAdapter().getItemCount(), ITEM_LEFT_TO_LOAD_MORE, lastVisibleItemPosition);
            }
        }
    }

    private int getLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        int lastVisibleItemPosition = -1;
        if (layoutManagerType == null) {
            if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
            } else if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                lastVisibleItemPosition = caseStaggeredGrid(layoutManager);
                break;
        }
        return lastVisibleItemPosition;
    }

    private int caseStaggeredGrid(RecyclerView.LayoutManager layoutManager) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        if (lastScrollPositions == null)
            lastScrollPositions = new int[staggeredGridLayoutManager.getSpanCount()];

        staggeredGridLayoutManager.findLastVisibleItemPositions(lastScrollPositions);
        return findMax(lastScrollPositions);
    }


    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    /**
     * @param adapter                       The new adapter to set, or null to set no adapter
     * @param compatibleWithPrevious        Should be set to true if new adapter uses the same {@android.support.v7.widget.RecyclerView.ViewHolder}
     *                                      as previous one
     * @param removeAndRecycleExistingViews If set to true, RecyclerView will recycle all existing Views. If adapters
     *                                      have stable ids and/or you want to animate the disappearing views, you may
     *                                      prefer to set this to false
     */
    private void setAdapterInternal(RecyclerView.Adapter adapter, boolean compatibleWithPrevious,
                                    boolean removeAndRecycleExistingViews) {
        if (compatibleWithPrevious)
            mRecycler.swapAdapter(adapter, removeAndRecycleExistingViews);
        else
            mRecycler.setAdapter(adapter);

//        mProgress.setVisibility(View.Gone);
        //加载动画默认开启
        MyApplication.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mProgress != null && tv_loading != null) {
                    tv_loading.setText("网速有点慢，努力加载中");
                }
            }
        }, 10000);

        mRecycler.setVisibility(View.VISIBLE);
        swipeToLoadLayout.setRefreshing(false);
        if (null != adapter)
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    super.onItemRangeChanged(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    super.onItemRangeRemoved(positionStart, itemCount);
                    update();
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                    update();
                }

                @Override
                public void onChanged() {
                    super.onChanged();
                    update();
                }

                private void update() {
                    mProgress.setVisibility(View.GONE);
                    mMoreProgress.setVisibility(View.GONE);
                    swipeToLoadLayout.setRefreshing(false);
                    if (mRecycler.getAdapter().getItemCount() == 0 && mEmptyId != 0 && isShowEmpty) {
                        mEmpty.setVisibility(View.VISIBLE);
                        mRecycler.setVisibility(GONE);
                    } else if (mEmptyId != 0) {
                        mEmpty.setVisibility(View.GONE);
                        mRecycler.setVisibility(VISIBLE);
                    }
                }
            });

//        if (mEmptyId != 0) {
//            mEmpty.setVisibility(null != adapter && adapter.getItemCount() > 0
//                    ? View.GONE
//                    : View.VISIBLE);
//        }
    }

    public void setIsShowEmpty(boolean isShow) {
        this.isShowEmpty = isShow;
    }

    /**
     * Set the layout manager to the recycler
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mLinearLayoutManager = (LinearLayoutManager) manager;
        mRecycler.setLayoutManager(mLinearLayoutManager);
    }

    /**
     * Set the adapter to the recycler
     * Automatically hide the progressbar
     * Set the refresh to false
     * If adapter is empty, then the emptyview is shown
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        setAdapterInternal(adapter, false, true);
    }

    /**
     * @param adapter                       The new adapter to , or null to set no adapter.
     * @param removeAndRecycleExistingViews If set to true, RecyclerView will recycle all existing Views. If adapters
     *                                      have stable ids and/or you want to animate the disappearing views, you may
     *                                      prefer to set this to false.
     */
    public void swapAdapter(RecyclerView.Adapter adapter, boolean removeAndRecycleExistingViews) {
        setAdapterInternal(adapter, true, removeAndRecycleExistingViews);
    }

    public void setupSwipeToDismiss(final SwipeDismissRecyclerViewTouchListener.DismissCallbacks listener) {
        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(mRecycler, new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(int position) {
                        return listener.canDismiss(position);
                    }

                    @Override
                    public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        listener.onDismiss(recyclerView, reverseSortedPositions);
                    }
                });
//        mSwipeDismissScrollListener = touchListener.makeScrollListener();
        mRecycler.setOnTouchListener(touchListener);
    }

    /**
     * Remove the adapter from the recycler
     */
    public void clear() {
        mRecycler.setAdapter(null);
    }

    /**
     * Show the progressbar
     */
    public void showProgress() {
        hideRecycler();
        if (mEmptyId != 0) mEmpty.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
        MyApplication.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mProgress != null && tv_loading != null && mProgress.getVisibility() == View.VISIBLE) {
                    tv_loading.setText("网速有点慢，努力加载中");
                }
            }
        }, 10000);
    }

    /**
     * Hide the progressbar and show the recycler
     */
    public void showRecycler() {
        hideProgress();
        if (mRecycler.getAdapter().getItemCount() == 0 && mEmptyId != 0) {
            mEmpty.setVisibility(View.VISIBLE);
        } else if (mEmptyId != 0) {
            mEmpty.setVisibility(View.GONE);
        }
        mRecycler.setVisibility(View.VISIBLE);
    }

    public void showMoreProgress() {
        mMoreProgress.setVisibility(View.VISIBLE);
    }

    public void hideMoreProgress() {
        mMoreProgress.setVisibility(View.GONE);
    }

    public boolean isRefreshing() {
        return swipeToLoadLayout.isRefreshing();
    }

    public void setRefreshing(boolean refreshing) {
        swipeToLoadLayout.setRefreshing(refreshing);
    }

    /**
     * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
     */
    public void setRefreshListener(OnRefreshListener listener) {
        swipeToLoadLayout.setEnabled(true);
        swipeToLoadLayout.setOnRefreshListener(listener);
    }

    public void setRefreshEnable(boolean isEnable) {
        swipeToLoadLayout.setRefreshEnabled(isEnable);
    }

    @Override
    public void setRefreshingColorResources(int... colorResources) {
//        mPtrLayout.setColorSchemeResources(colorResources);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     */
    public void setRefreshingColorResources(@ColorRes int colRes1, @ColorRes int colRes2, @ColorRes int colRes3, @ColorRes int colRes4) {
//        mPtrLayout.setColorSchemeResources(colRes1, colRes2, colRes3, colRes4);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     */
    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
//        mPtrLayout.setColorSchemeColors(col1, col2, col3, col4);
    }

    /**
     * Hide the progressbar
     */
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);
    }

    public void setRecyclerHeadUI(String text) {
        swipe_refresh_header.setText(text);
    }

    /**
     * Hide the recycler
     */
    public void hideRecycler() {
        mRecycler.setVisibility(View.GONE);
    }

    /**
     * Set the scroll listener for the recycler
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mExternalOnScrollListener = listener;
    }

    /**
     * Add the onItemTouchListener for the recycler
     */
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.addOnItemTouchListener(listener);
    }

    /**
     * Remove the onItemTouchListener for the recycler
     */
    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.removeOnItemTouchListener(listener);
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecycler.getAdapter();
    }

    /**
     * Sets the More listener
     *
     * @param max Number of items before loading more
     */
    public void setupMoreListener(OnMoreListener onMoreListener, int max) {
        mOnMoreListener = onMoreListener;
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    @Override
    public void setLoadComplete(boolean isLoadComplete) {
        this.setRefreshing(!isLoadComplete);
    }


    public void setOnMoreListener(OnMoreListener onMoreListener) {
        mOnMoreListener = onMoreListener;
    }

    public void setNumberBeforeMoreIsCalled(int max) {
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    public boolean isHasMore() {
        return isHasMore;
    }

    /**
     * Enable/Disable the More event
     */
    public void setHasMore(boolean isLoadingMore) {
        this.isHasMore = isLoadingMore;
    }

    /**
     * Remove the moreListener
     */
    public void removeMoreListener() {
        mOnMoreListener = null;
    }


    public void setOnTouchListener(OnTouchListener listener) {
        mRecycler.setOnTouchListener(listener);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.removeItemDecoration(itemDecoration);
    }

    /**
     * @return inflated progress view or null
     */
    public View getProgressView() {
        return mProgressView;
    }

    /**
     * @return inflated more progress view or null
     */
    public View getMoreProgressView() {
        return mMoreProgressView;
    }

    /**
     * @return inflated empty view or null
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    /**
     * Animate a scroll by the given amount of pixels along either axis.
     *
     * @param dx Pixels to scroll horizontally
     * @param dy Pixels to scroll vertically
     */
    public void smoothScrollBy(int dx, int dy) {
        mRecycler.smoothScrollBy(dx, dy);
    }

//    public void setICanScroll(NewChannelHomeFragment_C.ICanScroll iCanScroll) {
//        if (mRecycler instanceof JudgeNestedSuperRecycler) {
//            JudgeNestedSuperRecycler judgeNestedSuperRecycler = (JudgeNestedSuperRecycler) mRecycler;
//            judgeNestedSuperRecycler.setICanScroll(iCanScroll);
//        }
//    }

    public enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    public void moveToPosition(int position) {
        mPosition = position;
        mRecycler.stopScroll();
        int firstItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLinearLayoutManager.findLastVisibleItemPosition();
        if (position <= firstItem) {
            mRecycler.scrollToPosition(position);
        } else if (position <= lastItem) {
            int top = mRecycler.getChildAt(position - firstItem).getTop();
            mRecycler.scrollBy(0, top);
        } else {
            mRecycler.scrollToPosition(position);
            move = true;
        }
    }


    public void showEmptyView(int mEmptyId) {
        this.mEmptyId = mEmptyId;
        if (mEmptyId != 0) {
            mEmpty.setLayoutResource(mEmptyId);
            mEmpty.setVisibility(VISIBLE);
        }
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        int action=ev.getAction();
//        if(action==MotionEvent.ACTION_DOWN){
//            return false;
//        } else {
//            return true;
//        }
////        return super.onInterceptTouchEvent(ev);
//    }

    public interface ScrollChangePositionCallback {
        void onScroll(int firstPosition, int lastPosition);

        void onScrollStateChanged(RecyclerView recyclerView, int newState);
    }

    public void setScrollChangePositionCallback(ScrollChangePositionCallback mScrollChangePositionCallback) {
        this.mScrollChangePositionCallback = mScrollChangePositionCallback;
    }
}
