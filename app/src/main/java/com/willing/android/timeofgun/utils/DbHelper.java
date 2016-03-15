package com.willing.android.timeofgun.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.willing.android.timeofgun.event.AddCatelogEvent;
import com.willing.android.timeofgun.model.Catelog;

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
}
