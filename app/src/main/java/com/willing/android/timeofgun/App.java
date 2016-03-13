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

        Bmob.initialize(this, "54ff894c23dc62a014df713cc3b043c1");
    }
}
