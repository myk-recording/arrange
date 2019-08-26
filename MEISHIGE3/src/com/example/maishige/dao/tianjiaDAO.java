package com.example.maishige.dao;



import java.util.ArrayList;
import java.util.List;

import com.example.meishige.model.*;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class tianjiaDAO {
	private DBOPENHELPER helper;
	private SQLiteDatabase db;// 创建SQLiteDatabase对象
	
	public tianjiaDAO(Context context)// 定义构造函数
	{
		helper = new DBOPENHELPER(context);
	}
	
	public void add(Tb_tianjia tb_tianjia) {
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		
		db.execSQL(
				"insert into tb_tianjia (_id,tupian,zhuti,time,didian,ganchu) values (?,?,?,?,?,?)",
				new Object[] { tb_tianjia.getid(), tb_tianjia.gettupian(),
						tb_tianjia.getzhuti(), tb_tianjia.gettime(),
						tb_tianjia.getdidian(), tb_tianjia.getganchu() });
	}
	
	public void update(Tb_tianjia tb_tianjia) {
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		
		db.execSQL(
				"update tb_tianjia set tupian = ?,zhuti = ?,time = ?,didian = ?,ganchu = ? where _id = ?",
				new Object[] { tb_tianjia.gettupian(), tb_tianjia.getzhuti(),
						tb_tianjia.gettime(), tb_tianjia.getdidian(),
						tb_tianjia.getganchu(), tb_tianjia.getid()});
	}
	
	public Tb_tianjia find(int id) {
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		Cursor cursor = db
				.rawQuery(
						"select _id,tupian,zhuti,time,didian,ganchu from tb_tianjia where _id = ?",
						new String[] { String.valueOf(id) });
		if (cursor.moveToNext())
		{
			
			return new Tb_tianjia(
					cursor.getInt(cursor.getColumnIndex("_id")),
					cursor.getString(cursor.getColumnIndex("tupian")),
					cursor.getString(cursor.getColumnIndex("zhuti")), 
					cursor.getString(cursor.getColumnIndex("time")),
					cursor.getString(cursor.getColumnIndex("didian")), 
					cursor.getString(cursor.getColumnIndex("ganchu")));
		}
		return null;// 如果没有信息，则返回null
	}
	
	public void detele(Integer... ids) {
		if (ids.length > 0)// 判断是否存在要删除的id
		{
			StringBuffer sb = new StringBuffer();// 创建StringBuffer对象
			for (int i = 0; i < ids.length; i++)// 遍历要删除的id集合
			{
				sb.append('?').append(',');// 将删除条件添加到StringBuffer对象中
			}
			sb.deleteCharAt(sb.length() - 1);// 去掉最后一个“,“字符
			db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
			
			db.execSQL("delete from tb_tianjia where _id in (" + sb + ")",
					(Object[]) ids);
		}
	}
	
	public List<Tb_tianjia> getScrollData(int start, int count) {
		List<Tb_tianjia> tb_tianjia = new ArrayList<Tb_tianjia>();// 创建集合对象
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		
		Cursor cursor = db.rawQuery("select * from tb_tianjia limit ?,?",
				new String[] { String.valueOf(start), String.valueOf(count) });
		while (cursor.moveToNext())
		{
			
			tb_tianjia.add(new Tb_tianjia(cursor.getInt(cursor
					.getColumnIndex("_id")), cursor.getString(cursor
					.getColumnIndex("tupian")), cursor.getString(cursor
					.getColumnIndex("zhuti")), cursor.getString(cursor
							.getColumnIndex("time")), cursor.getString(cursor
									.getColumnIndex("didian")), cursor.getString(cursor
											.getColumnIndex("ganchu"))));
		}
		return tb_tianjia;// 返回集合
	}
	
	public long getCount() {
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		Cursor cursor = db
				.rawQuery("select count(_id) from tb_tianjia", null);
		if (cursor.moveToNext())// 判断Cursor中是否有数据
		{
			return cursor.getLong(0);// 返回总记录数
		}
		return 0;// 如果没有数据，则返回0
	}
	
	public int getMaxId() {
		db = helper.getWritableDatabase();// 初始化SQLiteDatabase对象
		Cursor cursor = db.rawQuery("select max(_id) from tb_tianjia", null);
		while (cursor.moveToLast()) {// 访问Cursor中的最后一条数据
			return cursor.getInt(0);// 获取访问到的数据，即最大编号
		}
		return 0;// 如果没有数据，则返回0
	}


}
