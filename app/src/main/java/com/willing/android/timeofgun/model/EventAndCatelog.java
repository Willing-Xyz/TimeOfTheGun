package com.willing.android.timeofgun.model;

/**
 * Created by Willing on 2016/3/16.
 */
public class EventAndCatelog
{
    private long startTime;
    private long stopTime;
    private int id;
    private Catelog catelog;

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

    public Catelog getCatelog() {
        return catelog;
    }

    public void setCatelog(Catelog catelog) {
        this.catelog = catelog;
    }
}
