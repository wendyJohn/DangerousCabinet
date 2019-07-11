package com.sanleng.dangerouscabinet;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.sanleng.dangerouscabinet.utils.GetMac;


public class MyApplication extends Application {
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 0x1231232;
    public static final int MESSAGE_BANLANCEDATA = 0x1231542;
    public static final int MESSAGE_LOCKDATA = 0x12312356;
    public static final int MESSAGE_LOCKSTATE = 0x12332356;
    public static final int MESSAGE_TIME = 0x1254542;
    public static final String BROADCAST_ACTION_DISC = "com.permissions.myf_broadcast";
    public static final String BROADCAST_PERMISSION_DISC = "com.permissions.MYF_BROADCAST";
    public static String MAC = "";
    public static boolean isFirst;
    public static MyApplication instance;
    private static Context context = null;

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
        context = getApplicationContext();
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

    public static Context getContext() {
        return context;
    }

    public void setIsFirst(Boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean getIsFirst() {
        return this.isFirst;
    }
}

