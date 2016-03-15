package com.willing.android.timeofgun.utils;

import java.util.Date;

/**
 * Created by Willing on 2016/3/15.
 */
public class DateUtils
{
    // 根据当前时间和开始时间得到字符串表示
    public static String formatDistanceTime(long startTime)
    {
        Date nowDate = new Date();

        long distance = nowDate.getTime() - startTime;

        distance /= 1000;
        int second = (int) (distance % 60);
        distance /= 60;
        int minute = (int) (distance % 60);
        int hour = (int) (distance /= 60);

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

}
