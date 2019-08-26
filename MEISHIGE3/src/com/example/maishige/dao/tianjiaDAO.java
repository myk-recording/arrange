package com.example.maishige.dao;



import java.util.ArrayList;
import java.util.List;

import com.example.meishige.model.*;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class tianjiaDAO {
	private DBOPENHELPER helper;
	private SQLiteDatabase db;// ����SQLiteDatabase����
	
	public tianjiaDAO(Context context)// ���幹�캯��
	{
		helper = new DBOPENHELPER(context);
	}
	
	public void add(Tb_tianjia tb_tianjia) {
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		
		db.execSQL(
				"insert into tb_tianjia (_id,tupian,zhuti,time,didian,ganchu) values (?,?,?,?,?,?)",
				new Object[] { tb_tianjia.getid(), tb_tianjia.gettupian(),
						tb_tianjia.getzhuti(), tb_tianjia.gettime(),
						tb_tianjia.getdidian(), tb_tianjia.getganchu() });
	}
	
	public void update(Tb_tianjia tb_tianjia) {
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		
		db.execSQL(
				"update tb_tianjia set tupian = ?,zhuti = ?,time = ?,didian = ?,ganchu = ? where _id = ?",
				new Object[] { tb_tianjia.gettupian(), tb_tianjia.getzhuti(),
						tb_tianjia.gettime(), tb_tianjia.getdidian(),
						tb_tianjia.getganchu(), tb_tianjia.getid()});
	}
	
	public Tb_tianjia find(int id) {
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
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
		return null;// ���û����Ϣ���򷵻�null
	}
	
	public void detele(Integer... ids) {
		if (ids.length > 0)// �ж��Ƿ����Ҫɾ����id
		{
			StringBuffer sb = new StringBuffer();// ����StringBuffer����
			for (int i = 0; i < ids.length; i++)// ����Ҫɾ����id����
			{
				sb.append('?').append(',');// ��ɾ��������ӵ�StringBuffer������
			}
			sb.deleteCharAt(sb.length() - 1);// ȥ�����һ����,���ַ�
			db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
			
			db.execSQL("delete from tb_tianjia where _id in (" + sb + ")",
					(Object[]) ids);
		}
	}
	
	public List<Tb_tianjia> getScrollData(int start, int count) {
		List<Tb_tianjia> tb_tianjia = new ArrayList<Tb_tianjia>();// �������϶���
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		
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
		return tb_tianjia;// ���ؼ���
	}
	
	public long getCount() {
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		Cursor cursor = db
				.rawQuery("select count(_id) from tb_tianjia", null);
		if (cursor.moveToNext())// �ж�Cursor���Ƿ�������
		{
			return cursor.getLong(0);// �����ܼ�¼��
		}
		return 0;// ���û�����ݣ��򷵻�0
	}
	
	public int getMaxId() {
		db = helper.getWritableDatabase();// ��ʼ��SQLiteDatabase����
		Cursor cursor = db.rawQuery("select max(_id) from tb_tianjia", null);
		while (cursor.moveToLast()) {// ����Cursor�е����һ������
			return cursor.getInt(0);// ��ȡ���ʵ������ݣ��������
		}
		return 0;// ���û�����ݣ��򷵻�0
	}


}
