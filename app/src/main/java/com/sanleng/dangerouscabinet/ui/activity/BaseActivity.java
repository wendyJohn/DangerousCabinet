package com.sanleng.dangerouscabinet.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.data.SDBHelper;
import com.sanleng.dangerouscabinet.net.URLs;
import com.sanleng.dangerouscabinet.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public abstract class BaseActivity extends AppCompatActivity {
    public CountDownTimer countDownTimer;
    private long advertisingTime = 300 * 1000;//无操作时跳转首页时间
    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏虚拟按键
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        context = this;
        hideBottomUIMenu();
        setContentView(getLayoutRes());
    }

    protected abstract int getLayoutRes();

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: //有按下动作时取消定时
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    System.out.println("==================" + "取消定时");
                }
                break;
            case MotionEvent.ACTION_UP: //抬起时启动定时
                System.out.println("==================" + "开始定时");
                startTime();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 无操作时跳转首页
     */
    public void startTime() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(advertisingTime, 1000l) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!BaseActivity.this.isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);
                        System.out.println("=========倒计时=========" + remainTime + "秒");
                    }
                }

                @Override
                public void onFinish() { //定时完成后的操作
                    //跳转到页面
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            };
            countDownTimer.start();
        } else {
            countDownTimer.start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //显示是启动定时
        startTime();
        if (Utils.foFile() == false) {
            new Thread(runnables).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //当activity不在前台是停止定时
        if (countDownTimer != null) {
            countDownTimer.cancel();
            System.out.println("==================" + "取消定时");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时停止定时
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
            System.out.println("==================" + "取消定时");
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    Runnable runnables = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            String databaseFilename = SDBHelper.DB_DIRS + File.separator + "dangerconfig.db";
            InputStream is = getResources().openRawResource(R.raw.dangerconfig);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(databaseFilename);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
