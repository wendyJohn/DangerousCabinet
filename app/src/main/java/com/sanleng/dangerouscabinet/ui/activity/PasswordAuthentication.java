package com.sanleng.dangerouscabinet.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

import com.hb.dialog.myDialog.MyImageMsgDialog;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.ui.view.PwdEditText;

/**
 * 密码认证
 */
public class PasswordAuthentication extends BaseActivity {

    private PwdEditText et_pwd;
    private PwdEditText et_pwds;
    private String passworda = "暂无密码";
    private String passwordb = "暂无密码";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordauthentication);
        et_pwd = findViewById(R.id.et_pwd);
        et_pwds = findViewById(R.id.et_pwds);
        et_pwd.setOnInputFinishListener(new PwdEditText.OnInputFinishListener() {
            @Override
            public void onInputFinish(String password) {
                Toast.makeText(PasswordAuthentication.this, password, Toast.LENGTH_LONG).show();
                passworda = password;
                Verification(passworda, passwordb);
            }
        });
        et_pwds.setOnInputFinishListener(new PwdEditText.OnInputFinishListener() {
            @Override
            public void onInputFinish(String password) {
                Toast.makeText(PasswordAuthentication.this, password, Toast.LENGTH_LONG).show();
                passwordb = password;
                Verification(passworda, passwordb);
            }
        });
    }

    //验证密码
    private void Verification(String passworda, String passwordb) {
        if (!"暂无密码".equals(passworda) && !"暂无密码".equals(passwordb)) {
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
                    Intent intent=new Intent(PasswordAuthentication.this,ReturnOperation.class);
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
}
