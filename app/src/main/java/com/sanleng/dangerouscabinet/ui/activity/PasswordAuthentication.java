package com.sanleng.dangerouscabinet.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.dialog.myDialog.MyImageMsgDialog;
import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.Presenter.PassVerificationRequest;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.fid.entity.Lock;
import com.sanleng.dangerouscabinet.model.PassModel;
import com.sanleng.dangerouscabinet.ui.view.CodeEditView;
import com.sanleng.dangerouscabinet.utils.PreferenceUtils;
import com.sanleng.dangerouscabinet.utils.TTSUtils;

/**
 * 密码认证
 */
public class PasswordAuthentication extends AppCompatActivity implements View.OnClickListener, PassModel {

    private CodeEditView mCodeEditViewa;
    private CodeEditView mCodeEditViewb;
    private String passworda = "暂无密码";
    private String passwordb = "暂无密码";
    private TextView back;
    public CountDownTimer countdowntimer;
    private long advertisingTime = 90 * 1000;//90S退出识别认证
    private TextView countdown;
    private DBHelpers mOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordauthentication);
        initView();
        startTime();
    }

    //初始化
    private void initView() {
        TTSUtils.getInstance().speak("请输入密码");
        PreferenceUtils.setString(PasswordAuthentication.this, "chioUserName1", "1号暂无名称");
        PreferenceUtils.setString(PasswordAuthentication.this, "chioUserCode1", "1号暂无ID");
        PreferenceUtils.setString(PasswordAuthentication.this, "chioUserName2", "2号暂无名称");
        PreferenceUtils.setString(PasswordAuthentication.this, "chioUserCode2", "2号暂无ID");
        mOpenHelper = new DBHelpers(PasswordAuthentication.this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        countdown = findViewById(R.id.countdown);
        mCodeEditViewa = findViewById(R.id.codeEditViewa);
        mCodeEditViewb = findViewById(R.id.codeEditViewb);
        mCodeEditViewa.setOnInputEndCallBack(new CodeEditView.inputEndListener() {
            @Override
            public void input(String text) { //输入完毕后的回调
                passworda = text;
                if (passworda.equals(passwordb)) {
                    TTSUtils.getInstance().speak("密码已被验证，请重新输入新密码");
                    Toast.makeText(PasswordAuthentication.this, "密码已被验证，请重新输入新密码", Toast.LENGTH_SHORT).show();
                } else {
                    PassVerificationRequest.GetPassVerification(PasswordAuthentication.this, getApplicationContext(), passworda, "1号");
                }
            }

            @Override
            public void afterTextChanged(String text) { //输入内容改变后的回调
            }
        });
        mCodeEditViewb.setOnInputEndCallBack(new CodeEditView.inputEndListener() {
            @Override
            public void input(String text) { //输入完毕后的回调
                passwordb = text;

                if (passwordb.equals(passworda)) {
                    TTSUtils.getInstance().speak("密码已被验证，请重新输入新密码");
                    Toast.makeText(PasswordAuthentication.this, "密码已被验证，请重新输入新密码", Toast.LENGTH_SHORT).show();
                } else {
                    PassVerificationRequest.GetPassVerification(PasswordAuthentication.this, getApplicationContext(), passwordb, "2号");
                }
            }

            @Override
            public void afterTextChanged(String text) { //输入内容改变后的回调
            }
        });
    }

    //验证密码
    private void Verification(String passworda, String passwordb) {
        if (!"暂无密码".equals(passworda) && !"暂无密码".equals(passwordb)) {
            TTSUtils.getInstance().speak("密码认证成功");
            MyImageMsgDialog myImageMsgDialog = new MyImageMsgDialog(PasswordAuthentication.this).builder()
                    .setImageLogo(getResources().getDrawable(R.mipmap.ic_launcher))
                    .setMsg("双人密码认证成功，开锁中...");
            ImageView logoImg = myImageMsgDialog.getLogoImg();
            logoImg.setImageResource(R.drawable.connect_animation);
            AnimationDrawable connectAnimation = (AnimationDrawable) logoImg.getDrawable();
            connectAnimation.start();
            myImageMsgDialog.show();
            //此处进行开锁
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    // 等待2000毫秒后销毁此页面，并开门
                    Lock.getInstance().sendA();
                    mOpenHelper.deleterecords();
                    Intent intent = new Intent(PasswordAuthentication.this, OperationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //当activity不在前台是停止定时
        if (countdowntimer != null) {
            countdowntimer.cancel();
            System.out.println("==================" + "取消定时");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时停止定时
        if (countdowntimer != null) {
            countdowntimer.cancel();
        }
    }

    /**
     * 定时关闭密码认证功能
     */
    public void startTime() {
        if (countdowntimer == null) {
            countdowntimer = new CountDownTimer(advertisingTime, 1000l) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!PasswordAuthentication.this.isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);
                        System.out.println("=========倒计时=========" + remainTime + "秒");
                        countdown.setText(remainTime + "秒");
                    }
                }

                @Override
                public void onFinish() { //定时完成后的操作
                    Intent intent = new Intent(PasswordAuthentication.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            };
            countdowntimer.start();
        } else {
            countdowntimer.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent(PasswordAuthentication.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
        }
    }

    @Override
    public void PassSuccess(String msg, String user_code, String user_name, String type) {
        if (type.equals("1号")) {
            if (msg.equals("1号验证成功")) {
                Verification(passworda, passwordb);
                mCodeEditViewb.requestFocus();//获取焦点 光标出现
                if (passwordb.equals("暂无密码")) {
                    TTSUtils.getInstance().speak("1号密码正确，请输入2号密码");
                }
                PreferenceUtils.setString(PasswordAuthentication.this, "chioUserName1", user_name);
                PreferenceUtils.setString(PasswordAuthentication.this, "chioUserCode1", user_code);
            } else {
                mCodeEditViewa.clearText();
                passworda = "暂无密码";
                TTSUtils.getInstance().speak("1号输入密码错误，请重新输入");
            }
        } else {
            if (msg.equals("2号验证成功")) {
                Verification(passworda, passwordb);
                mCodeEditViewa.requestFocus();//获取焦点 光标出现
                if (passworda.equals("暂无密码")) {
                    TTSUtils.getInstance().speak("2号密码正确，请输入1号密码");
                }
                PreferenceUtils.setString(PasswordAuthentication.this, "chioUserName2", user_name);
                PreferenceUtils.setString(PasswordAuthentication.this, "chioUserCode2", user_code);
            } else {
                mCodeEditViewb.clearText();
                passwordb = "暂无密码";
                TTSUtils.getInstance().speak("2号输入密码错误，请重新输入");
            }
        }
    }

    @Override
    public void PassFailed() {

    }
}
