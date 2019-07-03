package com.sanleng.dangerouscabinet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.utils.TTSUtils;

/**
 * 归与还操作
 */
public class ReturnOperation extends BaseActivity implements View.OnClickListener {
    private TextView returnitems;
    private TextView removeitems;
    private TextView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returnoperation);
        initView();
        TTSUtils.getInstance().speak("请选择归还或取出操作");
    }

    //初始化
    private void initView() {
        returnitems = findViewById(R.id.returnitems);
        removeitems = findViewById(R.id.removeitems);
        back = findViewById(R.id.back);
        returnitems.setOnClickListener(this);
        removeitems.setOnClickListener(this);
        back.setOnClickListener(this);

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_returnoperation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //归还物资
            case R.id.returnitems:
                Intent intentreturnitems = new Intent(context, ReturnItems.class);
                startActivity(intentreturnitems);
                break;
            //取出物资
            case R.id.removeitems:
                Intent intentgetoutitems = new Intent(context, GetoutItems.class);
                startActivity(intentgetoutitems);
                break;
            //返回首页
            case R.id.back:
                Intent intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
        }
    }
}
