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
    private long eventId;

    public Event()
    {}

    public Event(long startTime, long stopTime, long catelogId, long eventId) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.catelogId = catelogId;
        this.eventId = eventId;
    }

    public Event(long startTime, long stopTime, long catelogId, long eventId, int id) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.catelogId = catelogId;
        this.eventId = eventId;
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

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
}
