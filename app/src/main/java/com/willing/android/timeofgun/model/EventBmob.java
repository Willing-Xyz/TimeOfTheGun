package com.willing.android.timeofgun.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Willing on 2016/3/15.
 */
public class EventBmob extends BmobObject
{
    public static final String USERID = "userId";
    private Long startTime;
    private Long stopTime;
    private Long catelogId;
    private Long eventId;
    private String userId;

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String id)
    {
        userId = id;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getStopTime() {
        return stopTime;
    }

    public void setStopTime(Long stopTime) {
        this.stopTime = stopTime;
    }

    public Long getCatelogId() {
        return catelogId;
    }

    public void setCatelogId(Long catelogId) {
        this.catelogId = catelogId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
