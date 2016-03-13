package com.willing.android.timeofgun;

import android.app.Application;

import cn.bmob.v3.Bmob;

/**
 * Created by Willing on 2016/3/13.
 */
public class App extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Bmob.initialize(this, "06d8fd4a25e1636b602397f71d281f57");
    }
}
