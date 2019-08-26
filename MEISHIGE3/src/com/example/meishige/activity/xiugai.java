package com.example.meishige.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

import com.example.maishige.dao.tianjiaDAO;
import com.example.meishige.activity.R;
import com.example.meishige.model.Tb_tianjia;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class xiugai extends Activity{
	protected static final int DATE_DIALOG_ID = 0;// 创建日期对话框常�?
	EditText txzhuti, txtime, txdidian, txganchu;;// 创建四个TextView对象
	ImageView tu;
	Button btns, btnb;// 创建两个Button对象
	String strid;
	
	private int mYear;// �?
	private int mMonth;// �?
	private int mDay;// �?
	
	private ImageView mAcountHeadIcon;
	
	tianjiaDAO tjDAO=new tianjiaDAO(xiugai.this);
	
	public static final String ACCOUNT_DIR = Environment.getExternalStorageDirectory().getPath()
			+ "/meishi/";
	public static final String ACCOUNT_MAINTRANCE_ICON_CACHE = "icon_cache/";
	private static final String IMGPATH = ACCOUNT_DIR + ACCOUNT_MAINTRANCE_ICON_CACHE;
	
	Uri uri_tu=null;
	String bstr=null;
	
	public static final int TAKE_A_PICTURE = 10;
	public static final int SELECT_A_PICTURE = 20;
	public static final int SET_PICTURE = 30;
	public static final int SET_ALBUM_PICTURE_KITKAT = 40;
	public static final int SELECET_A_PICTURE_AFTER_KIKAT = 50;
	
	private String mAlbumPicturePath = null;
	final boolean mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xiugai);// 设置布局文件
		tu=(ImageView)findViewById(R.id.imageView3);
		txzhuti = (EditText) findViewById(R.id.txtzhuti);
		txtime = (EditText) findViewById(R.id.txttime);
		txdidian = (EditText) findViewById(R.id.txtdidian);
		txganchu = (EditText) findViewById(R.id.txtganchu);
		btns = (Button) findViewById(R.id.btn_save);
		btnb = (Button) findViewById(R.id.btn_back);
		
		mAcountHeadIcon = (ImageView) findViewById(R.id.imageView3);
		Intent intent = getIntent();// 创建Intent对象
		strid = intent.getStringExtra("xs");
		Tb_tianjia tb_tianjia=tjDAO.find(Integer
					.parseInt(strid));
		bstr=tb_tianjia.gettupian();
		uri_tu=Uri.parse((String)bstr);
		Bitmap bitmap = null;
        bitmap = decodeUriAsBitmap(uri_tu);
        tu.setImageBitmap(bitmap); 
        
        txzhuti.setText(tb_tianjia.getzhuti());
        txtime.setText(tb_tianjia.gettime());
        txdidian.setText(tb_tianjia.getdidian());
        txganchu.setText(tb_tianjia.getganchu());
        
        mAcountHeadIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(xiugai.this).setTitle("获得美食")
				.setNegativeButton("取存货", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mIsKitKat) {
							selectImageUriAfterKikat();
						} else {
							cropImageUri();
						}
					}
				}).setPositiveButton("现烹饪", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT,uri_tu);
						startActivityForResult(intent, TAKE_A_PICTURE);
					}
				}).show();
			}
		});
        
        txtime.setOnClickListener(new OnClickListener() {// 为时间文本框设置单击监听事件
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);// 显示日期选择对话�?
			}
		});
        
        btns.setOnClickListener(new OnClickListener() {// 为修改按钮设置监听事件
			@Override
			public void onClick(View arg0) {
				
					Tb_tianjia tb_tianjia = new Tb_tianjia();
					tb_tianjia.setid(Integer.parseInt(strid));
					tb_tianjia.settupian(bstr);
					tb_tianjia.setzhuti(txzhuti.getText().toString());
					tb_tianjia.settime(txtime.getText().toString());
					tb_tianjia.setdidian(txdidian.getText().toString());
					tb_tianjia.setganchu(txganchu.getText().toString());
					tjDAO.update(tb_tianjia);
					// 弹出信息提示
					Toast.makeText(xiugai.this, "修改成功！",
							Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(xiugai.this, MainActivity.class);
					startActivity(intent);
					finish();
				
			}
        });
        
        btnb.setOnClickListener(new OnClickListener() {// 为取消按钮设置监听事事件
			@Override
			public void onClick(View arg0) {
					Intent intent = new Intent(xiugai.this, MainActivity.class);
					startActivity(intent);
					finish();
				
			}
        });
        
        final Calendar c = Calendar.getInstance();// 获取当前系统日期
		mYear = c.get(Calendar.YEAR);// 获取年份
		mMonth = c.get(Calendar.MONTH);// 获取月份
		mDay = c.get(Calendar.DAY_OF_MONTH);// 获取天数
		updateDisplay();// 显示当前系统时间
        
	}
	
	private void updateDisplay() {
		// 显示设置的时间
		txtime.setText(new StringBuilder().append(mYear).append("-")
				.append(mMonth + 1).append("-").append(mDay));
	}
	
	@Override
	protected Dialog onCreateDialog(int id)// 重写onCreateDialog方法
	{
		switch (id) {
		case DATE_DIALOG_ID:// 弹出日期选择对话框
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;// 为年份赋值
			mMonth = monthOfYear;// 为月份赋值
			mDay = dayOfMonth;// 为天赋值
			updateDisplay();// 显示设置的日期
		}
	};
	
	
	/**  
     * <br>功能�?�?:4.4以下从相册�?�照片并剪切 
     */ 
	private void cropImageUri() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,uri_tu);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, SELECT_A_PICTURE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SELECT_A_PICTURE) {
			if (resultCode == RESULT_OK && null != data) {
				
				bstr=uri_tu.toString();
				Bitmap bitmap = decodeUriAsBitmap(uri_tu);
				mAcountHeadIcon.setImageBitmap(bitmap);
			} 
		} else if (requestCode == SELECET_A_PICTURE_AFTER_KIKAT) {
			if (resultCode == RESULT_OK && null != data) {
//				//4.4以上
				//取得的是当前app�?使用的application,在当前app的任意位置使用这个函数得到的是同�?个Context;
				mAlbumPicturePath = getPath(getApplicationContext(), data.getData());
				cropImageUriAfterKikat(Uri.fromFile(new File(mAlbumPicturePath)));
			} 
		} else if (requestCode == SET_ALBUM_PICTURE_KITKAT) {
			
			bstr=uri_tu.toString();
			Bitmap bitmap = decodeUriAsBitmap(uri_tu);
			mAcountHeadIcon.setImageBitmap(bitmap);
			
		} else if (requestCode == TAKE_A_PICTURE) {
			if (resultCode == RESULT_OK) {
				cameraCropImageUri(uri_tu);
			}
		} else if (requestCode == SET_PICTURE) {
			Bitmap bitmap = null;
			
			if (resultCode == RESULT_OK && null != data) {
				
				bstr=uri_tu.toString();
				bitmap = decodeUriAsBitmap(uri_tu);
				mAcountHeadIcon.setImageBitmap(bitmap);
			} 
		
		}
	}
	
	/**  
     * <br>功能�?�?: 4.4及以上拍照后剪切方法 
     */ 
	private void cameraCropImageUri(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/jpeg");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, SET_PICTURE);
	}
	
	/**  
     * <br>功能�?�?: 4.4及以上�?�取照片后剪切方�? 
     */ 
	private void cropImageUriAfterKikat(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/jpeg");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 640);
		intent.putExtra("outputY", 640);
		intent.putExtra("scale", true);// 去黑�?
		intent.putExtra("return-data", false);
		//存储获取的图片信�? 
		intent.putExtra(MediaStore.EXTRA_OUTPUT,uri_tu);
		//图片压缩
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, SET_ALBUM_PICTURE_KITKAT);
	}
	
	//根据不同类型区分对待
		@TargetApi(Build.VERSION_CODES.KITKAT)
		public static String getPath(final Context context, final Uri uri) {

			final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

			// DocumentProvider
			if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/" + split[1];
					}
				}
				// DownloadsProvider
				else if (isDownloadsDocument(uri)) {

					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

					return getDataColumn(context, contentUri, null, null);
				}
				// MediaProvider
				else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] { split[1] };

					return getDataColumn(context, contentUri, selection, selectionArgs);
				}
			}
			// MediaStore (and general)
			else if ("content".equalsIgnoreCase(uri.getScheme())) {

				// Return the remote address
				if (isGooglePhotosUri(uri))
					return uri.getLastPathSegment();

				return getDataColumn(context, uri, null, null);
			}
			// File
			else if ("file".equalsIgnoreCase(uri.getScheme())) {
				return uri.getPath();
			}

			return null;
		}
		
		public static boolean isExternalStorageDocument(Uri uri) {
			return "com.android.externalstorage.documents".equals(uri.getAuthority());
		}
		
		public static boolean isDownloadsDocument(Uri uri) {
			return "com.android.providers.downloads.documents".equals(uri.getAuthority());
		}
		
		public static boolean isMediaDocument(Uri uri) {
			return "com.android.providers.media.documents".equals(uri.getAuthority());
		}
		
		public static boolean isGooglePhotosUri(Uri uri) {
			return "com.google.android.apps.photos.content".equals(uri.getAuthority());
		}
		
		public static String getDataColumn(Context context, Uri uri, String selection,
				String[] selectionArgs) {

			Cursor cursor = null;
			final String column = "_data";
			final String[] projection = { column };

			try {
				//直接在UI线程中查询数�?
				cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
						null);
				if (cursor != null && cursor.moveToFirst()) {
					//从零�?始返回指定列名称，如果不存在将抛出IllegalArgumentException 异常�?
					final int index = cursor.getColumnIndexOrThrow(column);
					return cursor.getString(index);
				}
			} finally {
				if (cursor != null)
					cursor.close();
			}
			return null;
		}
	
	/**  
     * <br>功能�?�?:4.4及以上从相册选择照片 
     */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void selectImageUriAfterKikat() {
		//打开图片选择�?
		Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		//用来指示�?个GET_CONTENT意图只希望ContentResolver.openInputStream能够打开URI  
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		startActivityForResult(intent, SELECET_A_PICTURE_AFTER_KIKAT);
	}
	
	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

}
