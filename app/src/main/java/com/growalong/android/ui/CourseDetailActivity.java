package com.growalong.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.growalong.android.R;
import com.growalong.android.ui.fragment.CourseDetailFragment;

/**
 */
public class CourseDetailActivity extends QLActivity {

    public static void startThis(long courseId, Context context) {
        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra("courseId", courseId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {
        long courseId = getIntent().getLongExtra("courseId", 0);
        CourseDetailFragment courseDetailFragment = (CourseDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        courseDetailFragment.setCourseId(courseId);
        courseDetailFragment.initData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_course_detail;
    }
}
