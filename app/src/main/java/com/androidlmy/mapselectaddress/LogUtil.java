package com.androidlmy.mapselectaddress;

import android.util.Log;


/**
 * @功能: 全局控制log日志打印配置
 * @Creat 2019/07/15 16:18
 * @User Lmy
 * @By Android Studio
 */
public class LogUtil {

    public static boolean isPrintLog = true;
    private static int LOG_MAXLENGTH = 2000;
    public static String TGA = "com.androidlmy.mapselectaddress";

    public static void v(String msg) {
        v(TGA, msg);
    }

    public static void v(String tagName, String msg) {
        if (isPrintLog) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.v(TGA, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.v(TGA, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void d(String msg) {
        d("TGA", msg);
    }

    public static void d(String tagName, String msg) {
        if (isPrintLog) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.d(TGA, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.d(TGA, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void i(String msg) {
        i(TGA, msg);
    }

    public static void i(String tagName, String msg) {
        if (isPrintLog) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.i(TGA, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.i(TGA, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void w(String msg) {
        w(TGA, msg);
    }

    public static void w(String tagName, String msg) {
        if (isPrintLog) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.w(TGA, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.w(TGA, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static void e(String msg) {
        e(TGA, msg);
    }

    public static void e(String tagName, String msg) {
        if (isPrintLog) {
            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.e(TGA, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.e(TGA, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");


}