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
	TextView txzhuti, txtime, txdidian, txganchu;;// �����ĸ�TextView����
	ImageView tu;
	
	Button btnc, btnd,btng;// ��������Button����
	String strid;
	
	tianjiaDAO tjDAO=new tianjiaDAO(tumanage.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tumanage);// ���ò����ļ�
		tu=(ImageView)findViewById(R.id.imageView2);
		txzhuti = (TextView) findViewById(R.id.txtgozhuti);
		txtime = (TextView) findViewById(R.id.txtgotime);
		txdidian = (TextView) findViewById(R.id.txtgodidian);
		txganchu = (TextView) findViewById(R.id.txtgoganchu);
		btnc = (Button) findViewById(R.id.btn_chance);
		btnd = (Button) findViewById(R.id.btn_del);
		btng = (Button) findViewById(R.id.gohome);
		
		Intent intent = getIntent();// ����Intent����
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
        
        btnc.setOnClickListener(new OnClickListener() {// Ϊ�޸İ�ť���ü����¼�
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(tumanage.this, xiugai.class);// ����Intent����
				intent.putExtra(FLAG2, strid);// ���ô�������
				startActivity(intent);// ִ��Intent����
				finish();
			}
        });
        
        btnd.setOnClickListener(new OnClickListener() {// Ϊɾ����ť���ü����¼�
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tjDAO.detele(Integer.parseInt(strid));
				Toast.makeText(tumanage.this, "ɾ���ɹ���", Toast.LENGTH_SHORT)
				.show();
				Intent intent = new Intent(tumanage.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
        });
        
        btng.setOnClickListener(new OnClickListener() {// Ϊ���ذ�ť���ü����¼�
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
