package com.example.maishige.dao;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOPENHELPER extends SQLiteOpenHelper {
	
	private static final int VERSION = 1;
	private static final String DBNAME = "meishi.db";
	
	public DBOPENHELPER(Context context){
		
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table tb_tianjia (_id integer primary key,tupian varchar(200),zhuti varchar(20),time varchar(10),"
				+ "didian varchar(100),ganchu varchar(200))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
