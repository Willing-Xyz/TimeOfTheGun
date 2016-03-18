package com.willing.android.timeofgun.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Willing on 2016/3/16.
 */
public class EventAndCatelog implements Parcelable
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeLong(startTime);
        dest.writeLong(stopTime);
        dest.writeParcelable(catelog, 0);
    }

    public static final Parcelable.Creator<EventAndCatelog> CREATOR = new Parcelable.Creator<EventAndCatelog>(){

        @Override
        public EventAndCatelog createFromParcel(Parcel source)
        {
            EventAndCatelog event = new EventAndCatelog();

            event.setId(source.readInt());
            event.setStartTime(source.readLong());
            event.setStopTime(source.readLong());
            event.setCatelog((Catelog)source.readParcelable(getClass().getClassLoader()));

            return event;
        }

        @Override
        public EventAndCatelog[] newArray(int size)
        {
            return new EventAndCatelog[size];
        }
    };

}
