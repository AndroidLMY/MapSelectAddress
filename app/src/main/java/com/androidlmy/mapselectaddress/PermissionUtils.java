package com.androidlmy.mapselectaddress;

import android.Manifest;

/**
 * @功能:存放app所有权限组
 * @Creat 2019/07/16 18:04
 * @User Lmy
 * @By Android Studio
 */
public class PermissionUtils {
    /*******************************app所有权限组**********************************/
    //日历
    public static final String CALENDAR = Manifest.permission.READ_CALENDAR;
    //相机
    public static final String CAMERA = Manifest.permission.CAMERA;
    //联系人
    public static final String CONTACTS = Manifest.permission.READ_CONTACTS;
    //定位
    public static final String LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    //麦克风
    public static final String MICROPHONE = Manifest.permission.RECORD_AUDIO;
    //电话
    public static final String PHONE = Manifest.permission.READ_PHONE_STATE;
    //传感器
    public static final String SENSORS = Manifest.permission.BODY_SENSORS;
    //短信
    public static final String SMS = Manifest.permission.SEND_SMS;
    //数据存储
    public static final String STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
}
