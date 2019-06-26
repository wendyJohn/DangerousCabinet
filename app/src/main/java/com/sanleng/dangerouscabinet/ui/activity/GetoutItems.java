package com.sanleng.dangerouscabinet.ui.activity;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.fid.entity.EPC;
import com.sanleng.dangerouscabinet.fid.serialportapi.ReaderServiceImpl;
import com.sanleng.dangerouscabinet.fid.service.CallBack;
import com.sanleng.dangerouscabinet.fid.service.CallBackStopReadCard;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;
import com.sanleng.dangerouscabinet.fid.tool.ReaderUtil;
import com.sanleng.dangerouscabinet.fid.util.DataFilter;
import com.sanleng.dangerouscabinet.utils.MessageEvent;
import com.sanleng.dangerouscabinet.utils.TTSUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 取出物资
 */
public class GetoutItems extends AppCompatActivity {
    ReaderService readerService = new ReaderServiceImpl();
    private List<EPC> listEPC;
    private List<String> listEpc;
    private DBHelpers mOpenHelper;
    private TextView namea;
    private TextView nameb;
    private TextView time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getoutitems);
        initView();
        TTSUtils.getInstance().speak("请按要求取出所需危化品");
        hideBottomUIMenu();
    }

    //初始化
    private void initView() {
        EventBus.getDefault().register(this);
        namea = findViewById(R.id.namea);
        nameb = findViewById(R.id.nameb);
        time = findViewById(R.id.time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        time.setText(simpleDateFormat.format(date));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 接收EventBus返回数据
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void backData(MessageEvent messageEvent) {
        switch (messageEvent.getTAG()) {
//            case MyApplication.MESSAGE_BANLANCEDATA:
//                break;
        }
    }


    //获取被取出的物资信息
    public void invOnces(final String balancedata) {
        mOpenHelper = new DBHelpers(GetoutItems.this);
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
                Cursor cursor = mOpenHelper.query("select * from materialtable where Epc=" + "'" + epc + "'", null);
                while (cursor.moveToNext()) {
                    String epcs = cursor.getString(cursor.getColumnIndex("Epc"));
                    String staus = cursor.getString(cursor.getColumnIndex("Staus"));
                    String ant = cursor.getString(cursor.getColumnIndex("Ant"));
                    String ids = cursor.getString(cursor.getColumnIndex("Ids"));
                    String StationName = cursor.getString(cursor.getColumnIndex("StationName"));
                    String StationId = cursor.getString(cursor.getColumnIndex("StationId"));
                    String StorageLocation = cursor.getString(cursor.getColumnIndex("StorageLocation"));
                    String Name = cursor.getString(cursor.getColumnIndex("Name"));
                    String Balancedata = cursor.getString(cursor.getColumnIndex("Balancedata"));

                    //提交服务器
                }
                cursor.close();
            }
        }, 2000);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}
