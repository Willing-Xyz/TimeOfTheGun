package com.willing.android.timeofgun.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Willing on 2016/3/15.
 */
public class CatelogBmob extends BmobObject
{
    public static final String USERID = "userId";

    private Long catelogId;
    private String catelogName;
    private Integer catelogColor;
    private String userId;

    public Long getCatelogId() {
        return catelogId;
    }

    public void setCatelogId(Long catelogId) {
        this.catelogId = catelogId;
    }

    public String getCatelogName() {
        return catelogName;
    }

    public void setCatelogName(String catelogName) {
        this.catelogName = catelogName;
    }

    public Integer getCatelogColor() {
        return catelogColor;
    }

    public void setCatelogColor(Integer catelogColor) {
        this.catelogColor = catelogColor;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
