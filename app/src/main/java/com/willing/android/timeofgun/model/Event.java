package com.willing.android.timeofgun.model;

/**
 * Created by Willing on 2016/3/15.
 */
public class Event
{
    private long startTime;
    private long stopTime;
    private long catelogId;
    private int id;

    public Event()
    {}

    public Event(long startTime, long stopTime, long catelogId) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.catelogId = catelogId;
    }

    public Event(long startTime, long stopTime, long catelogId, int id) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.catelogId = catelogId;
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

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
