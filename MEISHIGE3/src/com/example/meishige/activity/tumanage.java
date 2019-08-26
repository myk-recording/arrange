package com.example.meishige.activity;

import java.io.FileNotFoundException;

import com.example.maishige.dao.tianjiaDAO;
import com.example.meishige.activity.R;
import com.example.meishige.model.Tb_tianjia;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class tumanage extends Activity {
	public static final String FLAG2 = "xs";
	TextView txzhuti, txtime, txdidian, txganchu;;// 创建四个TextView对象
	ImageView tu;
	
	Button btnc, btnd,btng;// 创建两个Button对象
	String strid;
	
	tianjiaDAO tjDAO=new tianjiaDAO(tumanage.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tumanage);// 设置布局文件
		tu=(ImageView)findViewById(R.id.imageView2);
		txzhuti = (TextView) findViewById(R.id.txtgozhuti);
		txtime = (TextView) findViewById(R.id.txtgotime);
		txdidian = (TextView) findViewById(R.id.txtgodidian);
		txganchu = (TextView) findViewById(R.id.txtgoganchu);
		btnc = (Button) findViewById(R.id.btn_chance);
		btnd = (Button) findViewById(R.id.btn_del);
		btng = (Button) findViewById(R.id.gohome);
		
		Intent intent = getIntent();// 创建Intent对象
		strid = intent.getStringExtra("id");
		Tb_tianjia tb_tianjia=tjDAO.find(Integer
					.parseInt(strid));
		
		Uri uri_tu=Uri.parse((String)tb_tianjia.gettupian());
		Bitmap bitmap = null;
        bitmap = decodeUriAsBitmap(uri_tu);
        tu.setImageBitmap(bitmap);  
        
        txzhuti.setText(tb_tianjia.getzhuti());
        txtime.setText(tb_tianjia.gettime());
        txdidian.setText(tb_tianjia.getdidian());
        txganchu.setText(tb_tianjia.getganchu());
        
        btnc.setOnClickListener(new OnClickListener() {// 为修改按钮设置监听事件
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(tumanage.this, xiugai.class);// 创建Intent对象
				intent.putExtra(FLAG2, strid);// 设置传递数据
				startActivity(intent);// 执行Intent操作
				finish();
			}
        });
        
        btnd.setOnClickListener(new OnClickListener() {// 为删除按钮设置监听事件
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tjDAO.detele(Integer.parseInt(strid));
				Toast.makeText(tumanage.this, "删除成功！", Toast.LENGTH_SHORT)
				.show();
				Intent intent = new Intent(tumanage.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
        });
        
        btng.setOnClickListener(new OnClickListener() {// 为返回按钮设置监听事件
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(tumanage.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
        });
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
