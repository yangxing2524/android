package com.growalong.android.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.growalong.android.R;
import com.growalong.android.ui.widget.NonOffSlidingTabLayout;
import com.growalong.android.ui.widget.NonOffscreenViewPager;

import butterknife.BindView;

/**
 */
public class CourseMainFragment extends NewBaseFragment {

    @BindView(R.id.viewPager)
    NonOffscreenViewPager mViewPager;
    @BindView(R.id.tabLayout)
    NonOffSlidingTabLayout mTablayout;
    private PagerAdapter mAdapter;

    private String[] tiltes = {"已完成", "进行中", "我的消息"};
    @Override
    protected void initEventAndData(Bundle savedInstanceState, View view) {

        mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                Fragment fragment;
                if(position == 0) {
                   fragment =  CourseOverFragment.newInstance();
                }else if(position == 1){
                    fragment = CourseStartingFragment.newInstance();
                }else{
                    fragment = CourseStartingFragment.newInstance();
                }
                return fragment;
            }

            @Override
            public int getCount() {
                return 3;
            }


            @Override
            public CharSequence getPageTitle(int position) {
                return tiltes[position];
            }
        };
        mViewPager.setAdapter(mAdapter);
        setupFrame();
    }

    private void setupFrame() {
        mTablayout.setDividerColors(Color.TRANSPARENT);
        mTablayout.setSelectedIndicatorColors(R.color.color_f0f0f0);
        mTablayout.setCustomTabView(R.layout.tab_custom_view, R.id.item_content);
        mTablayout.setViewPager(mViewPager);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_course_main;
    }
}
