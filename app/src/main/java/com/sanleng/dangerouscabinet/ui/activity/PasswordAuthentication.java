package com.sanleng.dangerouscabinet.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.dialog.myDialog.MyImageMsgDialog;
import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.ui.view.PwdEditText;
import com.sanleng.dangerouscabinet.utils.TTSUtils;

/**
 * 密码认证
 */
public class PasswordAuthentication extends BaseActivity implements View.OnClickListener {

    private PwdEditText et_pwd;
    private PwdEditText et_pwds;
    private String passworda = "暂无密码";
    private String passwordb = "暂无密码";
    private TextView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordauthentication);
        TTSUtils.getInstance().speak("请输入密码");
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        et_pwd = findViewById(R.id.et_pwd);
        et_pwds = findViewById(R.id.et_pwds);
        et_pwd.setOnInputFinishListener(new PwdEditText.OnInputFinishListener() {
            @Override
            public void onInputFinish(String password) {
                passworda = password;
                if (passworda.equals("1234")) {
                    Verification(passworda, passwordb);
                    et_pwds.requestFocus();//获取焦点 光标出现
                    TTSUtils.getInstance().speak("1号密码正确，请输入2号密码");
                } else {
                    et_pwd.setText("");
                    passworda = "暂无密码";
                    TTSUtils.getInstance().speak("1号输入密码错误，请重新输入");
                }

            }
        });
        et_pwds.setOnInputFinishListener(new PwdEditText.OnInputFinishListener() {
            @Override
            public void onInputFinish(String password) {
                passwordb = password;
                if (passwordb.equals("1234")) {
                    Verification(passworda, passwordb);
                } else {
                    et_pwds.setText("");
                    passwordb = "暂无密码";
                    TTSUtils.getInstance().speak("2号输入密码错误，请重新输入");
                }
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
//                    Lock.getInstance().sendA();
                    Intent intent = new Intent(PasswordAuthentication.this, ReturnOperation.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_passwordauthentication;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
        }
    }
}
