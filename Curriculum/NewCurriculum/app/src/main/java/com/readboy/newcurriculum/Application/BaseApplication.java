package com.readboy.newcurriculum.Application;

import android.app.Application;

import com.readboy.newcurriculum.Utils.SkinEngine;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinEngine.getInstance().init(this);
    }
}
