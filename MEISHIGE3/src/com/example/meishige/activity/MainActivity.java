package com.example.meishige.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.example.maishige.dao.tianjiaDAO;
import com.example.meishige.activity.R;
import com.example.meishige.model.Tb_tianjia;
import com.example.meishige.model.Tb_zhujiemian;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	public static final String FLAG = "id";
	GridView gv;
	private Uri[] uri_zjm = null;
	private String[] str_zjm = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gv=(GridView)findViewById(R.id.gridView1);
		
		showzjm();
		gv.setOnItemClickListener(new OnItemClickListener()// 为ListView添加项单击事件
				{
					// 覆写onItemClick方法
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
						TextView text=(TextView)arg1.findViewById(R.id.title);
						String strzjm = String.valueOf(text.getText());// 记录信息
						String strid = strzjm.substring(0, strzjm.indexOf("-"));// 截取编号
						Intent intent = new Intent(MainActivity.this, tumanage.class);// 创建Intent对象
						intent.putExtra(FLAG, strid);// 设置传递数据
						startActivity(intent);// 执行Intent操作
						finish();
					}
				});
	}
	
	private void showzjm() {
		
		tianjiaDAO zjm_dao = new tianjiaDAO(MainActivity.this);
		
		List<Tb_tianjia> list_zjm = zjm_dao.getScrollData(0,
				(int) zjm_dao.getCount());
		uri_zjm=new Uri[list_zjm.size()];
		str_zjm = new String[list_zjm.size()];// 设置字符串数组的长度
		int m = 0;// 定义一个开始标识
		for (Tb_tianjia tb_tianjia : list_zjm) {// 遍历List泛型集合
			uri_zjm[m]=Uri.parse((String)tb_tianjia.gettupian());
			
			str_zjm[m]=tb_tianjia.getid()+"-"+tb_tianjia.getzhuti();
			
			m++;// 标识加1
		}
		 
		ImageAdapter ia = new ImageAdapter(this);  
		gv.setAdapter(ia);
		
	}
	
	public class ImageAdapter extends BaseAdapter {  
        private Context mContext;  
        public ImageAdapter(Context context) {   
            mContext = context;  
        }  
         
        public int getCount() {   
            return str_zjm.length;  
        }  
         
        public Object getItem(int position) {  
            return position;  
        }  
         
        public long getItemId(int position) {  
            return position;  
        }  
         
        public View getView(int position, View convertView, ViewGroup parent) {  
            View view = View.inflate(MainActivity.this, R.layout.items, null);  
            LinearLayout rl = (LinearLayout)view.findViewById(R.id.items2);  
            ImageView image = (ImageView)rl.findViewById(R.id.image);  
            Bitmap bitmap = null;
            bitmap = decodeUriAsBitmap(uri_zjm[position]);
            image.setImageBitmap(bitmap);  
            TextView tv = (TextView)rl.findViewById(R.id.title);  
            tv.setText(str_zjm[position]);  
            return rl;  
        }

		
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

	 public void anjia(View view) {
		 Intent intent = new Intent(this, TianJia.class);// 创建Intent对象
		 
		 startActivity(intent);// 执行Intent操作
		 finish();
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
