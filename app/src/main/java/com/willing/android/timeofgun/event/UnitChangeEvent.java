package com.willing.android.timeofgun.event;

import com.willing.android.timeofgun.model.DateUnit;

/**
 * Created by Willing on 2016/3/16.
 */
public class UnitChangeEvent
{
    private DateUnit unit;

    public DateUnit getUnit() {
        return unit;
    }

    public void setUnit(DateUnit unit) {
        this.unit = unit;
    }
}
