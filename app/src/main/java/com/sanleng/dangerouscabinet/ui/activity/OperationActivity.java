package com.sanleng.dangerouscabinet.ui.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hb.dialog.myDialog.MyImageMsgDialog;
import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.MyApplication;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.fid.entity.Balance;
import com.sanleng.dangerouscabinet.fid.entity.EPC;
import com.sanleng.dangerouscabinet.fid.serialportapi.ReaderServiceImpl;
import com.sanleng.dangerouscabinet.fid.service.CallBack;
import com.sanleng.dangerouscabinet.fid.service.CallBackStopReadCard;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;
import com.sanleng.dangerouscabinet.fid.tool.ReaderUtil;
import com.sanleng.dangerouscabinet.fid.util.DataFilter;
import com.sanleng.dangerouscabinet.ui.adapter.WeighAdapter;
import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;
import com.sanleng.dangerouscabinet.ui.view.CodeEditView;
import com.sanleng.dangerouscabinet.utils.MessageEvent;
import com.sanleng.dangerouscabinet.utils.TTSUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作&&记录
 */
public class OperationActivity extends AppCompatActivity implements View.OnClickListener {
    ReaderService readerService = new ReaderServiceImpl();
    private List<EPC> listEPC;
    private List<String> listEpc;
    private TextView back;
    public CountDownTimer countdowntimer;
    private long advertisingTime = 90 * 1000;//90S退出识别认证
    private TextView countdown;
    private DBHelpers mOpenHelper;
    private ImageView fans;
    private TextView weighhints;//称重提示
    private RelativeLayout weigh;//称重界面
    private RelativeLayout returnhints;//还取提示界面
    private FrameLayout fragment;//还取提示与称重界面
    private ListView weighlistview;//称重数据展示界面
    private List<DangerousChemicals> dataList = new ArrayList<>();//存储数据
    private WeighAdapter weighAdapter;//ListView的数据适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        initView();
        hideBottomUIMenu();
    }

    //初始化
    private void initView() {
        EventBus.getDefault().register(this);
        mOpenHelper = new DBHelpers(OperationActivity.this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        countdown = findViewById(R.id.countdown);
        countdown.setOnClickListener(this);
        weighhints = findViewById(R.id.weighhints);//称重提示
        weigh = findViewById(R.id.weigh);//称重界面
        returnhints = findViewById(R.id.returnhints);//还取提示界面
        fragment = findViewById(R.id.fragment);//还取提示界面
        fans = findViewById(R.id.fan);
        fans.setOnClickListener(this);
        Animation fananim = AnimationUtils.loadAnimation(this,
                R.anim.rotate_circle_anim);
        fans.startAnimation(fananim);// 开始动画
        weighlistview = findViewById(R.id.weighlistview);

    }


    @Override
    protected void onResume() {
        super.onResume();
        //电子秤连接
        Balance.getInstance().init();
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


    /**
     * 接收EventBus返回数据
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void backData(MessageEvent messageEvent) {
        switch (messageEvent.getTAG()) {
            case MyApplication.MESSAGE_BANLANCEDATA:
                weighhints.setVisibility(View.VISIBLE);//称重提示打开
                weigh.setVisibility(View.VISIBLE);//称重界面打开
                returnhints.setVisibility(View.GONE);//还取提示界面关闭
                String data = messageEvent.getMessage();
                String str = data.replaceAll(" ", "");
                String balancedata = str.substring(str.indexOf("+") + 1);
                invOnces(balancedata.trim());
                break;
        }
    }


    //获取电子秤上的物资信息
    public void invOnces(final String balancedata) {
        mOpenHelper = new DBHelpers(OperationActivity.this);
        listEPC = new ArrayList<>();
        listEpc = new ArrayList<>();
        if (null == ReaderUtil.readers) {
            System.out.println("请先连接设备");
            return;
        }
        listEPC.removeAll(listEPC);
        listEpc.removeAll(listEpc);
        readerService.invOnceV2(ReaderUtil.readers, new ReadData());
        new Handler().postDelayed(new Runnable() {
            public void run() {
                System.out.println("=======EPC大小========" + listEPC.size());
                if (listEPC.size() <= 0) {
                    readerService.stopInvV2(ReaderUtil.readers, new StopReadData());
                    return;
                }
                // 等待2000毫秒后获取卡号，并比对。
                for (int i = 0; i < listEPC.size(); i++) {
                    String epc = listEPC.get(i).getEpc();
                    String myant = listEPC.get(i).getAnt();
                    if (myant.equals("4")) {
                        listEpc.add(epc);
                    }
                }
                String epc = listEpc.get(0);
                System.out.println("=======EPC=======" + epc);
                Cursor cursor = mOpenHelper.query("select * from materialtable where Epc=" + "'" + epc + "'", null);
                while (cursor.moveToNext()) {
                    String epcs = cursor.getString(cursor.getColumnIndex("Epc"));
                    String Name = cursor.getString(cursor.getColumnIndex("Name"));

                    TTSUtils.getInstance().speak("本次称重物品是" + Name + "重量为" + balancedata);
                    //更新本地重量并提交服务器,生成过秤记录
                    mOpenHelper.updatebalancedata(epcs, balancedata);//更新数据重量并提交更新服务器数据

                    mOpenHelper.insertweigh(epcs,Name,balancedata);//生成过秤记录。
                    DangerousChemicals bean=new DangerousChemicals();
                    bean.setName(Name);
                    bean.setBalancedata(balancedata);
                    dataList.add(bean);
                    //通知ListView更改数据源
                    if (weighAdapter != null) {
                        weighAdapter.notifyDataSetChanged();
                        weighlistview.setSelection(dataList.size() - 1);//设置显示列表的最后一项
                    } else {
                        weighAdapter = new WeighAdapter(OperationActivity.this, dataList);
                        weighlistview.setAdapter(weighAdapter);
                        weighlistview.setSelection(dataList.size() - 1);
                    }
                }
                cursor.close();
            }
        }, 2000);
    }



    /**
     * 定时关闭密码认证功能
     */
    public void startTime() {
        if (countdowntimer == null) {
            countdowntimer = new CountDownTimer(advertisingTime, 1000l) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!OperationActivity.this.isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);
                        System.out.println("=========倒计时=========" + remainTime + "S");
                        countdown.setText(remainTime + "秒");
                    }
                }

                @Override
                public void onFinish() { //定时完成后的操作
                    finish();//关闭密码认证
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
                Intent intent = new Intent(OperationActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
            case R.id.fan:
                weighhints.setVisibility(View.VISIBLE);//称重提示
                weigh.setVisibility(View.VISIBLE);//称重界面
                returnhints.setVisibility(View.GONE);//还取提示界面
                break;
            case R.id.countdown:
                fragment.setVisibility(View.GONE);
                break;

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
    class ReadData implements CallBack {
        @Override
        public void readData(String data, String antNo) {
            addToList(listEPC, data, antNo);
        }

        @Override
        public void readData(String data, String rssi, String antNo, String deviceNo, String direction) {
            addToList(listEPC, data, rssi, antNo, deviceNo, direction);
        }
    }

    class StopReadData implements CallBackStopReadCard {
        @Override
        public void stopReadCard(final boolean result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (result) {
                        System.out.println("======停止读卡成功=======");
                    } else {
                        System.out.println("======停止读卡失败=======");
                    }
                }
            });
        }
    }

    private void addToList(final List<EPC> listEPC2, final String epc, final String ant) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataFilter.dataFilter(listEPC2, epc, ant);

            }
        });
    }

    private void addToList(final List<EPC> listEPC2, final String epc, final String rssi, final String ant, final String deviceNo, final String direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DataFilter.dataFilter(listEPC2, epc, rssi, ant, deviceNo, direction);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时停止定时
        if (countdowntimer != null) {
            countdowntimer.cancel();
        }

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
