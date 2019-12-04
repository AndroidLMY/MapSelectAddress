package com.androidlmy.mapselectaddress;

import android.app.Application;

import org.litepal.LitePal;

/**
 * @功能:
 * @Creat 2019/12/4 10:15
 * @User Lmy
 * @Compony zaituvideo
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
