package com.willing.android.timeofgun.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Willing on 2016/3/15.
 */
public class EventBmob extends BmobObject
{
    private long startTime;
    private long stopTime;
    private long catelogId;

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public long getCatelogId() {
        return catelogId;
    }

    public void setCatelogId(long catelogId) {
        this.catelogId = catelogId;
    }
}
