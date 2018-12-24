package com.growalong.android.ui.adapter;

import com.growalong.android.model.CourseMaterialModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by murphy on 2017/11/11.
 */

public class ChangeCouponObserver {

    private static volatile ChangeCouponObserver mInstance;

    public static ChangeCouponObserver getInstance() {
        if (mInstance == null) {
            synchronized (ChangeCouponObserver.class) {
                if (mInstance == null) {
                    mInstance = new ChangeCouponObserver();
                }
            }
        }

        return  mInstance;
    }

    public List<OnChangeListener> mList = new ArrayList<>();


    public void change(CourseMaterialModel model) {
        for (OnChangeListener listener : mList) {
            listener.change(model);
        }
    }

    public void addListener(OnChangeListener listener) {
        if (!mList.contains(listener)) {
            mList.add(listener);
        }

    }

    public void removeListener(OnChangeListener listener) {
        mList.remove(listener);
    }



    public interface OnChangeListener{
        void change(CourseMaterialModel model);
    }
}
