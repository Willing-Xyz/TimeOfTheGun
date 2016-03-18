package com.willing.android.timeofgun.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.willing.android.timeofgun.event.AddCatelogEvent;
import com.willing.android.timeofgun.event.AddEventEvent;
import com.willing.android.timeofgun.model.Catelog;
import com.willing.android.timeofgun.model.Event;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Willing on 2016/3/15.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "TimeOfGun";
    private static final int DATABASE_VERSION = 1;

    public static final String MAIN_TABLE_NAME = "main";
    public static final String CATELOG_TABLE_NAME = "catelog";

    public static final String START_TIME = "startTime";
    public static final String STOP_TIME = "stopTime";
    public static final String CATELOG_ID = "catelog_id";
    public static final String CATELOG_NAME = "cagelogName";
    public static final String CATELOG_COLOR = "catelogColor";

    public DbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String mainTable = "CREATE TABLE IF NOT EXISTS " + MAIN_TABLE_NAME
                + " ("
                + BaseColumns._ID + " integer primary key, "
                + START_TIME + " integer, "
                + STOP_TIME + " integer, "
                + CATELOG_ID + " integer, "
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
                + CATELOG_ID + " integer not null "
                + " );";
        db.execSQL(catelogTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 获取所有的Catelog
    public static Cursor getAllCatelog(Context context)
    {
        DbHelper helper = new DbHelper(context);
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
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(DbHelper.CATELOG_TABLE_NAME, new String[]{BaseColumns._ID},
                DbHelper.CATELOG_NAME + "=?", new String[]{name}, null, null, null);


        if (cursor != null && cursor.getCount() > 0)
        {
            return true;
        }
        return false;
    }

    public static void addCatelog(Context context, Catelog catelog) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        db.insert(CATELOG_TABLE_NAME, null, getCatelogContentValues(catelog));

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

        DbHelper helper = new DbHelper(context);
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

        return catelog;
    }

    // 增加事件
    public static void addEvent(Context context, Event event) {

        long startTime = event.getStartTime();
        long stopTime = event.getStopTime();
        long catelogId = event.getCatelogId();

        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.START_TIME, startTime);
        values.put(DbHelper.STOP_TIME, stopTime);
        values.put(DbHelper.CATELOG_ID, catelogId);

        db.insert(DbHelper.MAIN_TABLE_NAME, null, values);

        EventBus.getDefault().postSticky(new AddEventEvent());
    }

    public static Cursor queryEvent(Context context, long startDate, long stopDate) {
        DbHelper helper = new DbHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String largeStartDateSql = "select * from "
                + MAIN_TABLE_NAME + " inner join " + CATELOG_TABLE_NAME + " on "
                + MAIN_TABLE_NAME + "." + CATELOG_ID + "=" + CATELOG_TABLE_NAME + "." + CATELOG_ID
                + " where " + START_TIME + " >= " + startDate;

        String sql = "select * from " + "(" + largeStartDateSql + ")"
                + " where " + STOP_TIME + " < " + stopDate
                + ";";

        return db.rawQuery(sql, null);
    }

    public static Cursor queryEventByCatelog(Context context, long startDate, long stopDate,  long catelogId)
    {
        DbHelper helper = new DbHelper(context);
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
        SQLiteDatabase db = new DbHelper(context).getWritableDatabase();

        ContentValues cateVals = new ContentValues();

        cateVals.put(CATELOG_NAME, catelog.getName());
        cateVals.put(CATELOG_COLOR, catelog.getColor());

        // 更新catelog table
        db.update(CATELOG_TABLE_NAME, cateVals, BaseColumns._ID + "=" + catelog.getId(), null);
    }
}
