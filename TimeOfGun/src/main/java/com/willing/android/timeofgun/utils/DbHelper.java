package com.willing.android.timeofgun.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.willing.android.timeofgun.event.AddCatelogEvent;
import com.willing.android.timeofgun.event.AddEventEvent;
import com.willing.android.timeofgun.event.DeleteCatelogEvent;
import com.willing.android.timeofgun.event.DeleteEventEvent;
import com.willing.android.timeofgun.event.UpdateEventEvent;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.CatelogBmob;
import com.willing.android.timeofgun.model.Event;
import com.willing.android.timeofgun.model.EventBmob;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Willing on 2016/3/15.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;

    public static final String MAIN_TABLE_NAME = "main";
    public static final String CATELOG_TABLE_NAME = "catelog";

    public static final String START_TIME = "startTime";
    public static final String STOP_TIME = "stopTime";
    public static final String CATELOG_ID = "catelog_id";
    public static final String CATELOG_NAME = "cagelogName";
    public static final String CATELOG_COLOR = "catelogColor";
    public static final String EVENT_ID = "eventId";

    public DbHelper(Context context, String name)
    {
        super(context, name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String mainTable = "CREATE TABLE IF NOT EXISTS " + MAIN_TABLE_NAME
                + " ("
                + BaseColumns._ID + " integer primary key, "
                + START_TIME + " integer , "
                + STOP_TIME + " integer, "
                + CATELOG_ID + " integer , "
                + EVENT_ID + " integer unique, "
                + "foreign key(" + CATELOG_ID + ") references " + CATELOG_TABLE_NAME + "(" + CATELOG_ID + ")"
                + ");";

        // 启用外键
        db.execSQL("PRAGMA foreign_keys = ON");
        // 创建main表
        db.execSQL(mainTable);

        String catelogTable = "CREATE TABLE IF NOT EXISTS " + CATELOG_TABLE_NAME
                + " ("
                + BaseColumns._ID + " integer primary key, "
                + CATELOG_NAME + " text unique not null, "
                + CATELOG_COLOR + " integer not null, "
                + CATELOG_ID + " integer not null unique "
                + " );";
        db.execSQL(catelogTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static Cursor getAllCatelog(Context context)
    {
        return getAllCatelog(context, Utils.getDbName(context));
    }

    // 获取所有的Catelog
    public static Cursor getAllCatelog(Context context, String name)
    {
        DbHelper helper = new DbHelper(context, name);
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = new String[]{
                BaseColumns._ID,
                DbHelper.CATELOG_NAME,
                DbHelper.CATELOG_COLOR,
                DbHelper.CATELOG_ID
        };
        Cursor cursor = db.query(DbHelper.CATELOG_TABLE_NAME, columns, null, null, null, null, null);

        return cursor;
    }

    public static boolean isCatelogNameExisted(Context context, String name)
    {
        // 检查是否在数据库中存在
        DbHelper helper = new DbHelper(context, Utils.getDbName(context));
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(DbHelper.CATELOG_TABLE_NAME, new String[]{BaseColumns._ID},
                DbHelper.CATELOG_NAME + "=?", new String[]{name}, null, null, null);

        boolean isExisted = false;
        if (cursor != null && cursor.getCount() > 0)
        {
            isExisted = true;
        }
        db.close();
        return isExisted;
    }

    public static void addCatelog(Context context, String name, Cursor cursor)
    {
        SQLiteDatabase db = new DbHelper(context,name).getWritableDatabase();

        db.beginTransaction();
        try
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                ContentValues values = new ContentValues(3);
                values.put(CATELOG_NAME, cursor.getString(cursor.getColumnIndex(CATELOG_NAME)));
                values.put(CATELOG_COLOR, cursor.getInt(cursor.getColumnIndex(CATELOG_COLOR)));
                values.put(CATELOG_ID, cursor.getLong(cursor.getColumnIndex(CATELOG_ID)));
                db.insert(CATELOG_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public static void addCatelog(Context context, Catelog catelog) {
        DbHelper helper = new DbHelper(context, Utils.getDbName(context));
        SQLiteDatabase db = helper.getWritableDatabase();

        db.insert(CATELOG_TABLE_NAME, null, getCatelogContentValues(catelog));

        db.close();

        EventBus.getDefault().postSticky(new AddCatelogEvent());
    }

    private static ContentValues getCatelogContentValues(Catelog catelog)
    {
        ContentValues values = new ContentValues();

        values.put(CATELOG_NAME, catelog.getName());
        values.put(CATELOG_COLOR, catelog.getColor());
        values.put(CATELOG_ID, catelog.getCatelogId());

        return values;
    }

    private static Catelog generateCatelog(Cursor cursor)
    {
        Catelog catelog = new Catelog();

        catelog.setName(cursor.getString(cursor.getColumnIndex(DbHelper.CATELOG_NAME)));
        catelog.setColor(cursor.getInt(cursor.getColumnIndex(DbHelper.CATELOG_COLOR)));
        catelog.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
        catelog.setCatelogId(cursor.getLong(cursor.getColumnIndex(DbHelper.CATELOG_ID)));

        return catelog;
    }

    // 从数据库中任一查找一个Catelog
    public static Catelog findAnyCatelogFromDb(Context context)
    {

        DbHelper helper = new DbHelper(context, Utils.getDbName(context));
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DbHelper.CATELOG_TABLE_NAME,
                new String[]{BaseColumns._ID, DbHelper.CATELOG_NAME, DbHelper.CATELOG_COLOR, DbHelper.CATELOG_ID},
                null, null, null, null, null);

        Catelog catelog = null;
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            catelog = generateCatelog(cursor);
        }
        db.close();

        return catelog;
    }

    public static void addEvent(Context context, String dbName, Cursor cursor) {

        SQLiteDatabase db = new DbHelper(context, dbName).getWritableDatabase();

        db.beginTransaction();
        try
        {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext())
            {
                ContentValues values = new ContentValues(3);
                values.put(START_TIME, cursor.getLong(cursor.getColumnIndex(START_TIME)));
                values.put(STOP_TIME, cursor.getLong(cursor.getColumnIndex(STOP_TIME)));
                values.put(CATELOG_ID, cursor.getLong(cursor.getColumnIndex(CATELOG_ID)));
                values.put(EVENT_ID, cursor.getLong(cursor.getColumnIndex(EVENT_ID)));
                db.insert(MAIN_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    // 增加事件
    public static void addEvent(Context context, Event event) {

        ArrayList<Event> events = partitionEvent(event);

        SQLiteDatabase db = new DbHelper(context, Utils.getDbName(context)).getWritableDatabase();

        for (int i = 0; i < events.size(); ++i) {
            ContentValues values = new ContentValues();
            values.put(DbHelper.START_TIME, events.get(i).getStartTime());
            values.put(DbHelper.STOP_TIME, events.get(i).getStopTime());
            values.put(DbHelper.CATELOG_ID, events.get(i).getCatelogId());
            values.put(EVENT_ID, events.get(i).getEventId());


            db.insert(DbHelper.MAIN_TABLE_NAME, null, values);
        }
        db.close();

        EventBus.getDefault().postSticky(new AddEventEvent());
    }

    private static ArrayList<Event> partitionEvent(Event event) {
        ArrayList<Event> events = new ArrayList<>();

        long startTime = event.getStartTime();
        long curStopTime = DateUtils.getDayEnd(startTime);
        long eventId = event.getEventId();
        while (curStopTime < event.getStopTime())
        {
            events.add(new Event(startTime, curStopTime,event.getCatelogId(), eventId++));
            startTime = curStopTime + 1;
            curStopTime = DateUtils.getDayEnd(startTime);
        }
        events.add(new Event(startTime, event.getStopTime(), event.getCatelogId(), eventId));

        return events;
    }

    public static Cursor queryEvent(Context context, long startDate, long stopDate) {
        DbHelper helper = new DbHelper(context, Utils.getDbName(context));
        SQLiteDatabase db = helper.getReadableDatabase();

        String largeStartDateSql = "select * from "
                + MAIN_TABLE_NAME + " inner join " + CATELOG_TABLE_NAME + " on "
                + MAIN_TABLE_NAME + "." + CATELOG_ID + "=" + CATELOG_TABLE_NAME + "." + CATELOG_ID
                + " where " + START_TIME + " >= " + startDate;

        String sql = "select * from " + "(" + largeStartDateSql + ")"
                + " where " + STOP_TIME + " <= " + stopDate
                + " order by " + START_TIME
                + ";";



        return db.rawQuery(sql, null);
    }

    public static Cursor queryEventByCatelog(Context context, long startDate, long stopDate,  long catelogId)
    {
        DbHelper helper = new DbHelper(context, Utils.getDbName(context));
        SQLiteDatabase db = helper.getReadableDatabase();

        String largeStartDateSql = "select * from "
                + MAIN_TABLE_NAME + " inner join " + CATELOG_TABLE_NAME + " on "
                + MAIN_TABLE_NAME + "." + CATELOG_ID + "=" + CATELOG_TABLE_NAME + "." + CATELOG_ID
                + " where " + START_TIME + " >= " + startDate;

        String sql = "select * from " + "(" + largeStartDateSql + ")"
                + " where " + STOP_TIME + " <= " + stopDate + " and " + CATELOG_ID + " = " + catelogId
                + " order by " + START_TIME
                + ";";

        return db.rawQuery(sql, null);
    }

    public static void updateCatelog(Context context, Catelog catelog) {
        SQLiteDatabase db = new DbHelper(context, Utils.getDbName(context)).getWritableDatabase();

        ContentValues cateVals = new ContentValues();

        cateVals.put(CATELOG_NAME, catelog.getName());
        cateVals.put(CATELOG_COLOR, catelog.getColor());

        // 更新catelog table
        db.update(CATELOG_TABLE_NAME, cateVals, BaseColumns._ID + "=" + catelog.getId(), null);
        db.close();
    }

    // 删除类别并删除相关联的事件
    public static void deleteCatelogs(Context context, ArrayList<Long> catelogs) {

        SQLiteDatabase db = new DbHelper(context, Utils.getDbName(context)).getWritableDatabase();

        for (int i = 0; i < catelogs.size(); ++i)
        {
            db.delete(CATELOG_TABLE_NAME, CATELOG_ID + "=?", new String[]{catelogs.get(i) + ""});
            deleteEventByCatelogId(context, catelogs.get(i));
        }
        EventBus.getDefault().post(new DeleteCatelogEvent());
        db.close();
    }

    public static void deleteEventByCatelogId(Context context, long catelogId)
    {
        SQLiteDatabase db = new DbHelper(context, Utils.getDbName(context)).getWritableDatabase();

        db.delete(MAIN_TABLE_NAME, CATELOG_ID + "=?", new String[]{catelogId + ""});
        EventBus.getDefault().post(new DeleteEventEvent());
        db.close();
    }

    public static void updateEvent(Context context, Event event) {

        ArrayList<Event> events = partitionEvent(event);

        SQLiteDatabase db = new DbHelper(context, Utils.getDbName(context)).getWritableDatabase();

        for (int i = 0; i < events.size(); ++i) {
            ContentValues values = new ContentValues();
            values.put(START_TIME, events.get(i).getStartTime());
            values.put(STOP_TIME, events.get(i).getStopTime());
            values.put(CATELOG_ID, events.get(i).getCatelogId());

            db.update(MAIN_TABLE_NAME, values, BaseColumns._ID + "=" + event.getId(), null);
        }
        db.close();

        EventBus.getDefault().post(new UpdateEventEvent());
    }

    public static void deleteEvent(Context context, int id)
    {
        SQLiteDatabase db = new DbHelper(context, Utils.getDbName(context)).getWritableDatabase();

        db.delete(MAIN_TABLE_NAME, BaseColumns._ID + "=" + id, null);

        db.close();
    }
    public static Cursor queryAllEvent(Context context)
    {
        return queryAllEvent(context, Utils.getDbName(context));
    }

    public static Cursor queryAllEvent(Context context, String name) {

        SQLiteDatabase db = new DbHelper(context, name).getReadableDatabase();

        String sql = "select " + START_TIME + "," + STOP_TIME + "," + MAIN_TABLE_NAME + "." + CATELOG_ID + "," + EVENT_ID
                + " from "
                + MAIN_TABLE_NAME + " inner join " + CATELOG_TABLE_NAME + " on "
                + MAIN_TABLE_NAME + "." + CATELOG_ID + "=" + CATELOG_TABLE_NAME + "." + CATELOG_ID;

        return db.rawQuery(sql, null);
    }

    public static void clearEvent(Context context, String name) {

        SQLiteDatabase db = new DbHelper(context, name).getWritableDatabase();

        db.delete(MAIN_TABLE_NAME, null, null);
        db.close();
    }

    public static void clearCatelog(Context context, String name) {
        SQLiteDatabase db = new DbHelper(context, name).getWritableDatabase();

        db.delete(CATELOG_TABLE_NAME, null, null);
        db.close();
    }

    public static void addCatelogs(Context context, List<CatelogBmob> list) {
        SQLiteDatabase db = new DbHelper(context, Utils.getDbName(context)).getWritableDatabase();

        db.beginTransaction();
        try
        {
            CatelogBmob catelog;
            for (int i = 0; i < list.size(); ++i)
            {
                catelog = list.get(i);
                ContentValues values = new ContentValues(3);
                values.put(CATELOG_NAME, catelog.getCatelogName());
                values.put(CATELOG_COLOR, catelog.getCatelogColor());
                values.put(CATELOG_ID, catelog.getCatelogId());
                db.insert(CATELOG_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

    public static void addEvents(Context context, List<EventBmob> list) {
        SQLiteDatabase db = new DbHelper(context, Utils.getDbName(context)).getWritableDatabase();

        db.beginTransaction();
        try
        {
            EventBmob event = null;
            for (int i = 0; i < list.size(); ++i)
            {
                event = list.get(i);
                ContentValues values = new ContentValues(4);
                values.put(START_TIME, event.getStartTime());
                values.put(STOP_TIME, event.getStopTime());
                values.put(CATELOG_ID, event.getCatelogId());
                values.put(EVENT_ID, event.getEventId());
                db.insert(MAIN_TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }
}
