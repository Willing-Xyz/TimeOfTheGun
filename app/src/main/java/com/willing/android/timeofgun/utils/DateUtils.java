package com.willing.android.timeofgun.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Willing on 2016/3/15.
 */
public class DateUtils {

    public static String formatDistanceTime(long startTime, long stopTime)
    {
        long distance = stopTime - startTime;

        distance /= 1000;
        int second = (int) (distance % 60);
        distance /= 60;
        int minute = (int) (distance % 60);
        int hour = (int) (distance /= 60);

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    // 根据当前时间和开始时间得到字符串表示
    public static String formatDistanceTime(long startTime) {
        return formatDistanceTime(startTime, new Date().getTime());
    }

    // 获取一天的开始时间
    public static long getDayBegin(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        return cal.getTimeInMillis();
    }

    // 获取一天的结束时间
    public static long getDayEnd(long timeInMillis) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);


        return cal.getTimeInMillis();
    }

    public static String createText(long startTime, long stopTime) {
        StringBuilder builder = new StringBuilder();

        builder.append(formatDate(startTime)).append(",");

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        if (hour < 10) {
            builder.append("0");
        }
        builder.append(hour);
        builder.append(":");
        if (minute < 10) {
            builder.append("0");
        }
        builder.append(minute);
        builder.append("-");

        cal.setTimeInMillis(stopTime);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        if (hour < 10) {
            builder.append("0");
        }
        builder.append(hour);
        builder.append(":");
        if (minute < 10) {
            builder.append("0");
        }
        builder.append(minute);

        builder.append(" (");

        minute = (int) ((stopTime - startTime) / 1000 / 60);
        hour = minute / 60;
        minute = minute % 60;

        if (hour < 10) {
            builder.append("0");
        }
        builder.append(hour);
        builder.append(":");
        if (minute < 10) {
            builder.append("0");
        }
        builder.append(minute);
        builder.append(")");

        return builder.toString();
    }

    // TODO: 2016/3/16 待删除
    public static String formatDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date(time);
        return format.format(date);
    }

    public static String formatDateAndWeek(long timeInMillis) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);

        StringBuilder builder = new StringBuilder();
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String week = formatWeek(cal.get(Calendar.DAY_OF_WEEK));

        builder.append(year).append("-");

        if (month.length() == 1) {
            builder.append("0");
        }
        builder.append(month).append("-");
        if (day.length() == 1) {
            builder.append("0");
        }
        builder.append(day).append(" ")
                .append(week);

        return builder.toString();
    }

    private static String formatWeek(int week) {
        String weekStr = null;
        switch (week) {
            case Calendar.MONDAY:
                weekStr = "周一";
                break;
            case Calendar.TUESDAY:
                weekStr = "周二";
                break;
            case Calendar.WEDNESDAY:
                weekStr = "周三";
                break;
            case Calendar.THURSDAY:
                weekStr = "周四";
                break;
            case Calendar.FRIDAY:
                weekStr = "周五";
                break;
            case Calendar.SATURDAY:
                weekStr = "周六";
                break;
            case Calendar.SUNDAY:
                weekStr = "周日";
                break;
        }
        return weekStr;
    }

    public static String formatDateAndTime(long time) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date = new Date(time);
        return format.format(date);
    }

    public static long getWeekBegin(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);

        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        return cal.getTimeInMillis();
    }

    public static long getWeekEnd(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);

        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);


        return cal.getTimeInMillis();
    }

    public static long getMonthBegin(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        return cal.getTimeInMillis();
    }


    public static long getMonthEnd(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);


        return cal.getTimeInMillis();
    }

    public static long getYearBegin(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);

        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        return cal.getTimeInMillis();
    }

    public static long getYearEnd(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);

        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);


        return cal.getTimeInMillis();
    }

    public static float convertToHour(long time) {
        return time / (1000 * 60 * 60f);
    }
}
