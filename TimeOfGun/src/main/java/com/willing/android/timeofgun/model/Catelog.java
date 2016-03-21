package com.willing.android.timeofgun.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Willing on 2015/11/17 0017.
 */
public class Catelog implements Parcelable
{
    private int mId;
    private String mName = "";
    private int mColor = -1;
    private long mCatelogId;

    public Catelog()
    {
    }

    public Catelog(String name, int color, long catelogId)
    {
        mName = name;
        mColor = color;
        mCatelogId = catelogId;
    }

    public Catelog(int id, String name, int color, long catelogId)
    {
        mId = id;
        mName = name;
        mColor = color;
        mCatelogId = catelogId;
    }

    public int getId()
    {
        return mId;
    }

    public void setId(int id)
    {
        mId = id;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public int getColor()
    {
        return mColor;
    }

    public void setColor(int color)
    {
        mColor = color;
    }

    public long getCatelogId()
    {
        return mCatelogId;
    }

    public void setCatelogId(long catelogId)
    {
        mCatelogId = catelogId;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeInt(mColor);
        dest.writeLong(mCatelogId);
    }

    public static final Creator<Catelog> CREATOR= new Creator<Catelog>(){

        @Override
        public Catelog createFromParcel(Parcel source)
        {
            return new Catelog(source.readInt(), source.readString(), source.readInt(), source.readLong());
        }

        @Override
        public Catelog[] newArray(int size)
        {
            return new Catelog[size];
        }
    };

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Catelog catelog = (Catelog) o;


        return mName.equals(catelog.getName());

    }

    @Override
    public int hashCode()
    {
        return mName.hashCode();
    }
}
