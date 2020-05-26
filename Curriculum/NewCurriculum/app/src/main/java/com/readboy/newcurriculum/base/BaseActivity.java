package com.readboy.newcurriculum.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;

import com.readboy.newcurriculum.Utils.SkinEngine;
import com.readboy.newcurriculum.Utils.SkinFactory;

import java.io.File;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity {
        private static final String TAG = "BaseActivity";
    private SkinFactory skinFactory;

    private boolean allowChangeSkinFlag = true;
    protected static String currentSkin = null;
    protected static int skinIndex;
    protected static String[] skins = new String[]{"curriculumSkin.apk", "curriculumSkin2.apk"};


    //    if (currentSkin == null) {
//        currentSkin = skins[0];
//        File file = new File(Environment.getExternalStorageDirectory(),currentSkin);
//        SkinEngine.getInstance().load(file.getAbsolutePath());
//        skinFactory.changeSkin();
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO: 关键点1：hook（劫持）系统创建view的过程
        if (allowChangeSkinFlag) {
            skinFactory = new SkinFactory();
            skinFactory.setDelegate(getDelegate());
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            Log.d("layoutInflaterTag", layoutInflater.toString());
            layoutInflater.setFactory2(skinFactory);
        }

        super.onCreate(savedInstanceState);
    }

    protected void initSkin() {
        SharedPreferences preferences = getSharedPreferences("skin", Context.MODE_PRIVATE);
        if (currentSkin == null) {
            currentSkin = preferences.getString("current_skin",null);
            if (currentSkin == null) {
                currentSkin = skins[0];
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("current_skin",currentSkin);
                editor.apply();
            }
        }
//        Log.d(TAG, "initSkin: ++++++" + currentSkin);

        File skinFile = new File(Environment.getExternalStorageDirectory(), currentSkin);
        SkinEngine.getInstance().load(skinFile.getAbsolutePath());
        skinFactory.changeSkin();
        skinIndex = preferences.getInt("skin_index",0);
    }



    /**
     * 创建完成但是还不可以交互
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 等控件创建完成并且可交互之后，再换肤
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("changeTag", null == currentSkin ? "currentSkin是空" : currentSkin);

//        if (null != currentSkin)
//            changeSkin(currentSkin); // 换肤操作必须在setContentView之后
    }

    /**
     * 做一个切换方法
     *
     * @return
     */
    protected String getPath() {
        String path;
        SharedPreferences preferences = getSharedPreferences("skin",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (null == currentSkin) {
            path = skins[0];
            skinIndex = 0;

        } else if (skins[0].equals(currentSkin)) {
            path = skins[1];
            skinIndex = 1;
        } else if (skins[1].equals(currentSkin)) {
            path = skins[0];
            skinIndex = 0;
        } else {
            return "unknown skin";
        }

        editor.putString("current_skin",path);
        editor.putInt("skin_index",skinIndex);
        editor.apply();
        return path;
    }

    protected int getSkinIndex() {
        return skinIndex;
    }

    protected void changeSkin(String path) {
        if (allowChangeSkinFlag) {
            File skinFile = new File(Environment.getExternalStorageDirectory(), path);
            SkinEngine.getInstance().load(skinFile.getAbsolutePath());
            skinFactory.changeSkin();
            currentSkin = path;
        }
    }

}
