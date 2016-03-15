package com.willing.android.timeofgun.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Willing on 2016/3/15.
 */
public class CatelogBmob extends BmobObject
{
    private long catelogId;
    private String catelogName;
    private int catelogColor;
    private String userId;

    public long getCatelogId() {
        return catelogId;
    }

    public void setCatelogId(long catelogId) {
        this.catelogId = catelogId;
    }

    public String getCatelogName() {
        return catelogName;
    }

    public void setCatelogName(String catelogName) {
        this.catelogName = catelogName;
    }

    public int getCatelogColor() {
        return catelogColor;
    }

    public void setCatelogColor(int catelogColor) {
        this.catelogColor = catelogColor;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
