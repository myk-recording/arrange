package com.readboy.newcurriculum.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.lang.reflect.Method;

public class SkinEngine {
    private final static SkinEngine instance = new SkinEngine();

    public static SkinEngine getInstance() {
        return instance;
    }

    private SkinEngine() {
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    private Resources mOutResource;// 资源管理器
    private Context mContext;//上下文
    private String mOutPkgName;// 外部资源包的packageName

    /**
     *  加载外部资源包
     */
    public void load(final String path) {//path 是外部传入的apk文件名
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        //取得PackageManager引用
        PackageManager mPm = mContext.getPackageManager();
        //拿到这个apk的包信息
        PackageInfo mInfo = mPm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        mOutPkgName = mInfo.packageName;//先把包名存起来
        AssetManager assetManager;//资源管理器
        try {
            //通过反射获取AssetManager 用来加载外面的资源包
            assetManager = AssetManager.class.newInstance();//类内部的addAssetPath方法是hide状态，需使用反射
            //addAssetPath方法可以加载外部的资源包
            Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);//是hide的,不直接对外开放，只能反射调用
            addAssetPath.invoke(assetManager, path);//反射执行方法
            mOutResource = new Resources(assetManager,//参数1，资源管理器
                    mContext.getResources().getDisplayMetrics(),//屏幕参数
                    mContext.getResources().getConfiguration());//资源配置
            //"外部资源包"mOutResource ，可以加载外部的资源文件
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 提供外部资源包里面的颜色
     * @param resId
     * @return
     */
    public int getColor(int resId) {
        if (mOutResource == null) {
            return resId;
        }
        String resName = mOutResource.getResourceEntryName(resId);
        int outResId = mOutResource.getIdentifier(resName, "color", mOutPkgName);
        if (outResId == 0) {
            return resId;
        }
        return mOutResource.getColor(outResId);
    }

    /**
     * 提供外部资源包里的图片资源
     * @param resId
     * @return
     */
    public Drawable getDrawable(int resId) {//获取图片
        if (mOutResource == null) {
            return ContextCompat.getDrawable(mContext, resId);
        }
        String resName = mOutResource.getResourceEntryName(resId);
        int outResId = mOutResource.getIdentifier(resName, "drawable", mOutPkgName);
        if (outResId == 0) {
            return ContextCompat.getDrawable(mContext, resId);
        }
        return mOutResource.getDrawable(outResId);
    }

}
