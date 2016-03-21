package com.willing.android.timeofgun.utils;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.willing.android.timeofgun.model.CatelogBmob;
import com.willing.android.timeofgun.model.EventBmob;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Willing on 2016/3/19.
 */
public class BmobUtils
{
    public static void uploadDatas(Context context)
    {
        String userId = null;
        BmobUser user = BmobUser.getCurrentUser(context);
        if (user == null)
        {
            return;
        }
        else
        {
            userId = user.getObjectId();
        }
        uploadEvents(context, userId);
        uploadCatelogs(context, userId);
    }

    private static void uploadCatelogs(Context context, String userId) {
        Cursor cursor = DbHelper.getAllCatelog(context, Utils.NOUSER);

        ArrayList<BmobObject> catelogs = new ArrayList<>(cursor.getCount() > 50 ? 50 : cursor.getCount());
        int time = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            CatelogBmob catelog = new CatelogBmob();
            catelog.setCatelogId(cursor.getLong(cursor.getColumnIndex(DbHelper.CATELOG_ID)));
            catelog.setCatelogName(cursor.getString(cursor.getColumnIndex(DbHelper.CATELOG_NAME)));
            catelog.setCatelogColor(cursor.getInt(cursor.getColumnIndex(DbHelper.CATELOG_COLOR)));
            catelog.setUserId(userId);

            catelogs.add(catelog);
            time++;
            if (time == 50 || time == cursor.getCount())
            {
                new EventBmob().insertBatch(context, catelogs, new SaveListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i("test", "upload failure: " + s);
                    }
                });
                catelogs = new ArrayList<>();
                time = 0;
            }
        }

        cursor.close();
    }


    public static void uploadEvents(Context context, String userId)
    {
        Cursor cursor = DbHelper.queryAllEvent(context, Utils.NOUSER);
        ArrayList<BmobObject> events = new ArrayList<>(cursor.getCount() > 50 ? 50 : cursor.getCount());

        int time = 0;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
        {
            EventBmob event = new EventBmob();
            event.setStartTime(cursor.getLong(cursor.getColumnIndex(DbHelper.START_TIME)));
            event.setStopTime(cursor.getLong(cursor.getColumnIndex(DbHelper.STOP_TIME)));
            event.setCatelogId(cursor.getLong(cursor.getColumnIndex(DbHelper.CATELOG_ID)));
            event.setEventId(cursor.getLong(cursor.getColumnIndex(DbHelper.EVENT_ID)));
            event.setUserId(userId);

            events.add(event);
            time++;
            if (time == 50 || time == cursor.getCount())
            {
                new EventBmob().insertBatch(context, events, new SaveListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.i("test", "upload failure: " + s);
                    }
                });
                events = new ArrayList<>();
                time = 0;
            }
        }
        cursor.close();
    }

    public static void moveDatas(Context context) {

        moveCatelogs(context);
        moveEvents(context);
    }

    private static void moveEvents(Context context) {
        Cursor cursor = DbHelper.queryAllEvent(context, Utils.NOUSER);

        DbHelper.addEvent(context, Utils.getDbName(context), cursor);

        DbHelper.clearEvent(context, Utils.NOUSER);

        cursor.close();
    }

    private static void moveCatelogs(Context context) {
        Cursor cursor = DbHelper.getAllCatelog(context, Utils.NOUSER);

        DbHelper.addCatelog(context, Utils.getDbName(context), cursor);

        DbHelper.clearCatelog(context, Utils.NOUSER);

        cursor.close();
    }

    public static void downloadDatas(Context context) {

        // TODO: 2016/3/19 download
        BmobUser user = BmobUser.getCurrentUser(context);
        if (user == null)
        {
            return;
        }
        downloadCatelogs(context, user.getObjectId());
        downloadEvents(context, user.getObjectId());
    }

    private static void downloadCatelogs(final Context context, String userId) {
        BmobQuery<CatelogBmob> query = new BmobQuery<>();
        query.addWhereEqualTo(CatelogBmob.USERID, userId);
        query.findObjects(context, new FindListener<CatelogBmob>() {
            @Override
            public void onSuccess(List<CatelogBmob> list) {
                if (list == null || list.isEmpty())
                {
                    return;
                }
                DbHelper.addCatelogs(context, list);
            }

            @Override
            public void onError(int i, String s) {
                // TODO: 2016/3/19 增加失败标记，在应用启动时重新下载
            }
        });
    }

    private static void downloadEvents(final Context context, String objectId) {

        BmobQuery<EventBmob> query = new BmobQuery<>();
        query.addWhereEqualTo(EventBmob.USERID, objectId);
        query.findObjects(context, new FindListener<EventBmob>() {
            @Override
            public void onSuccess(List<EventBmob> list) {
                if (list == null || list.isEmpty())
                {
                    return;
                }
                DbHelper.addEvents(context, list);
            }

            @Override
            public void onError(int i, String s) {
                // TODO: 2016/3/19 如果失败，设置标记，并在应用启动时重新下载
            }
        });
    }
}
