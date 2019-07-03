package com.sanleng.dangerouscabinet.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sanleng.dangerouscabinet.R;

public class DetailsDialog extends Dialog {
    private Context context;
    private TextView rfid;
    private TextView name;
    private TextView equation;
    private TextView balancedata;
    private TextView describe;
    private String rfiddata;
    private String namedata;
    private String equationdata;
    private String balancedatadata;
    private String describedata;

    public DetailsDialog(Context context, String rfiddata, String namedata, String equationdata, String balancedatadata, String describedata) {
        super(context);
        this.context = context;
        this.rfiddata = rfiddata;
        this.namedata = namedata;
        this.equationdata = equationdata;
        this.balancedatadata = balancedatadata;
        this.describedata = describedata;
    }

    public DetailsDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        this.setContentView(R.layout.detailsdialog);
//        this.setCancelable(false);// 设置点击屏幕Dialog不消失
        rfid = findViewById(R.id.rfid);
        name = findViewById(R.id.name);
        equation = findViewById(R.id.equation);
        balancedata = findViewById(R.id.balancedata);
        describe = findViewById(R.id.describe);

        rfid.setText("试剂RFID  |  " + rfiddata);
        name.setText("化学品名称  |  " + namedata);
        equation.setText("化学品方程式  |  " + equationdata);
        balancedata.setText("剩余容量  |  " + balancedatadata);
        describe.setText("化学品详情:" + "\n" + describedata);

    }

}