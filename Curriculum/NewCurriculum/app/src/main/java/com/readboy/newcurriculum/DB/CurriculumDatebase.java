package com.readboy.newcurriculum.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;


public class CurriculumDatebase extends SQLiteOpenHelper {
    private static final String TAG = "CurriculumDatebase";

    private static final String CREATE_ALARM = "create table if not exists tb_alarm(id integer primary key autoincrement," +
            "alarm_time timestamp,subject text,course_name text,tackle text,set_up_flag bit)";

    private CurriculumDatebase(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    private static CurriculumDatebase instance;
    private static int count;
    private static SQLiteDatabase db;

    public static CurriculumDatebase getInstance(@Nullable Context context, @Nullable String name
            , @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        if (instance == null) {
            synchronized (CurriculumDatebase.class) {
                if (instance == null) {
                    instance = new CurriculumDatebase(context,name,factory,version);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: db.execSQL+++++++");
        db.execSQL(CREATE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists tb_alarm");
        db.execSQL(CREATE_ALARM);
    }

    public synchronized SQLiteDatabase openDb() {
        if (count == 0) {
            db = instance.getWritableDatabase();
        }
        count++;
        return db;
    }

    public synchronized void closeDb(SQLiteDatabase database) {
        count--;
        if (count == 0) {
            database.close();
        }
    }
}
