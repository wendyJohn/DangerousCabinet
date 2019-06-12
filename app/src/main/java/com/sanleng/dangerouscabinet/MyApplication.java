package com.sanleng.dangerouscabinet;

import android.app.Application;
import android.util.Log;
import com.sanleng.dangerouscabinet.utils.GetMac;


public class MyApplication extends Application {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 0x1231232;
    public static String MAC = "";
    public static boolean isFirst;
    public static MyApplication instance;
    public synchronized MyApplication getInstance() {
        if (instance == null) {
            synchronized (MyApplication.class) {
                instance = new MyApplication();
            }
        }
        return instance;
    }

    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        achieveMac();
        isFirst = true;
    }

    public static String getMac() {
        return MAC;
    }

    /**
     * 获取本地mac地址
     * 初始化socket
     */
    public void achieveMac() {
        MAC = GetMac.getMacAddress().replaceAll(":", "");
        Log.i("MAC", MAC);
    }

    public void setIsFirst(Boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean getIsFirst() {
        return this.isFirst;
    }
}

