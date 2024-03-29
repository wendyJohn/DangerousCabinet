package com.sanleng.dangerouscabinet.ui.activity;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.MyApplication;
import com.sanleng.dangerouscabinet.Presenter.AccessRequest;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.fid.entity.Balance;
import com.sanleng.dangerouscabinet.fid.entity.EPC;
import com.sanleng.dangerouscabinet.fid.entity.Lock;
import com.sanleng.dangerouscabinet.fid.serialportapi.ReaderServiceImpl;
import com.sanleng.dangerouscabinet.fid.service.CallBack;
import com.sanleng.dangerouscabinet.fid.service.CallBackStopReadCard;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;
import com.sanleng.dangerouscabinet.fid.tool.ReaderUtil;
import com.sanleng.dangerouscabinet.fid.util.DataFilter;
import com.sanleng.dangerouscabinet.model.StorckModel;
import com.sanleng.dangerouscabinet.ui.adapter.StockAdapter;
import com.sanleng.dangerouscabinet.ui.adapter.StorageAdapter;
import com.sanleng.dangerouscabinet.ui.adapter.TipsAdapter;
import com.sanleng.dangerouscabinet.ui.adapter.WeighAdapter;
import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;
import com.sanleng.dangerouscabinet.utils.MessageEvent;
import com.sanleng.dangerouscabinet.utils.PreferenceUtils;
import com.sanleng.dangerouscabinet.utils.StockData;
import com.sanleng.dangerouscabinet.utils.TTSUtils;
import com.sanleng.dangerouscabinet.utils.TipsDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作&&记录
 */
public class OperationActivity extends AppCompatActivity implements View.OnClickListener ,StorckModel {
    ReaderService readerService = new ReaderServiceImpl();
    private List<EPC> listEPC;
    private List<String> listEpc;
    private TextView back;
    public CountDownTimer countdowntimers;
    private long advertisingTimes = 600 * 1000;//关门后无操作时返回首页
    private TextView countdown;
    private DBHelpers mOpenHelper;
    private ImageView fans;
    private TextView weighhints;//称重提示
    private RelativeLayout weigh;//称重界面
    private RelativeLayout tipsview;//未称重界面
    private RelativeLayout returnhints;//还取提示界面
    private FrameLayout fragment;//还取提示与称重界面
    private ListView weighlistview;//称重数据展示界面
    private ListView storagelistview;//存放数据展示界面
    private ListView inventorylistview;//库存数据展示界面
    private ListView tipslistview;//未称重数据展示界面
    private List<DangerousChemicals> dataList = new ArrayList<>();//存储数据
    private WeighAdapter weighAdapter;//ListView的数据适配器
    private RelativeLayout inventory;//盘点记录界面
    private Button accessrecords;//盘点记录界面
    private Button inventoryrecords;//盘点记录界面
    private Button rescaling;//开门重新过秤
    private LinearLayout linears;//存放tab选项界面
    private LinearLayout linearb;//库存tab选项界面
    private TextView allrecords;//全部记录
    private TextView storagerecords;//存放记录
    private TextView removerecords;//取出记录
    private TextView inventory_in;//柜内库存
    private TextView inventory_out;//柜外库存
    private JSONArray Depositarray = new JSONArray();;
    private List<DangerousChemicals> list = new ArrayList<>();
    private List<DangerousChemicals> depositlist = new ArrayList<>();
    private List<DangerousChemicals> taskuotlist = new ArrayList<>();
    private List<DangerousChemicals> tipslist;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initView();
        hideBottomUIMenu();
    }

    //初始化
    private void initView() {
        Lock.getInstance().checkstatus();
        EventBus.getDefault().register(this);
        mOpenHelper = new DBHelpers(OperationActivity.this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        countdown = findViewById(R.id.countdown);
        weighhints = findViewById(R.id.weighhints);//称重提示
        weigh = findViewById(R.id.weigh);//称重界面
        tipsview = findViewById(R.id.tips);//未称重界面
        returnhints = findViewById(R.id.returnhints);//还取提示界面
        fragment = findViewById(R.id.fragment);//还取提示界面
        inventory = findViewById(R.id.inventory);//盘点界面
        fans = findViewById(R.id.fan);
        Animation fananim = AnimationUtils.loadAnimation(this,
                R.anim.rotate_circle_anim);
        fans.startAnimation(fananim);// 开始动画
        weighlistview = findViewById(R.id.weighlistview);
        tipslistview = findViewById(R.id.tipslistview);
        storagelistview = findViewById(R.id.storagelistview);//存放数据展示界面
        inventorylistview = findViewById(R.id.inventorylistview);//库存数据展示界面
        accessrecords = findViewById(R.id.accessrecords);//打开盘点记录界面
        accessrecords.setOnClickListener(this);
        rescaling = findViewById(R.id.rescaling);//开门重新过秤
        rescaling.setOnClickListener(this);
        inventoryrecords = findViewById(R.id.inventoryrecords);//打开盘点记录界面
        inventoryrecords.setOnClickListener(this);
        linears = findViewById(R.id.linears);//存放tab选项界面
        linearb = findViewById(R.id.linearb);//库存tab选项界面
        allrecords = findViewById(R.id.allrecords);//全部记录
        storagerecords = findViewById(R.id.storagerecords);//存放记录
        removerecords = findViewById(R.id.removerecords);//取出记录
        inventory_in = findViewById(R.id.inventory_in);//柜内库存
        inventory_out = findViewById(R.id.inventory_out);//柜外库存
        allrecords.setOnClickListener(this);
        storagerecords.setOnClickListener(this);
        removerecords.setOnClickListener(this);
        inventory_in.setOnClickListener(this);
        inventory_out.setOnClickListener(this);
        mediaPlayer = new MediaPlayer();//这个我定义了一个成员函数
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.seekTo(0);
            }
        });
        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
        try {
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            mediaPlayer.prepare();
        } catch (IOException ioe) {
            mediaPlayer = null;
        }

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
        if (countdowntimers != null) {
            countdowntimers.cancel();
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
                String data = messageEvent.getMessage();
                String str = data.replaceAll(" ", "");
                String balancedata = str.substring(str.indexOf("+") + 1);
                String weigh = balancedata.replace("g", ""); //得到新的字符串
                invOnces(weigh.trim());
                break;
            case MyApplication.MESSAGE_LOCKSTATE:
                TTSUtils.getInstance().speak("请注意当前门未关起");
                mediaPlayer.start();
                break;
            case MyApplication.MESSAGE_LOCKDATA:
                invOnce();
                Lock.getInstance().closestatus();
                break;

        }
    }


    //获取电子秤上的物资信息
    public void invOnces(final String balancedata) {
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
                    TipsDialog tipsDialog = new TipsDialog(OperationActivity.this, "危化品信息获取失败，请重新获取", "重新获取危化品信息");
                    tipsDialog.show();
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
                    weighhints.setVisibility(View.VISIBLE);//称重提示打开
                    weigh.setVisibility(View.VISIBLE);//称重界面打开
                    tipsview.setVisibility(View.GONE);//未称重界面关闭
                    returnhints.setVisibility(View.GONE);//还取提示界面关闭
                    inventory.setVisibility(View.GONE);//盘点界面关闭
                    fragment.setVisibility(View.VISIBLE);
                    TTSUtils.getInstance().speak("本次称重物品是" + Name + "重量为" + balancedata);
                    //更新本地重量并提交服务器,生成过秤记录
                    mOpenHelper.updatebalancedata(epcs, balancedata,"已过秤");//更新数据重量并提交更新服务器数据
                    mOpenHelper.insertweigh(epcs, Name, balancedata);//生成过秤记录。
                    DangerousChemicals bean = new DangerousChemicals();
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

    //正常盘点数据
    public void invOnce() {
        listEPC = new ArrayList<>();
        listEpc = new ArrayList<>();
        tipslist = new ArrayList<>();
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
                    //提示盘点失败，请重新盘点
                    TipsDialog tipsDialog = new TipsDialog(OperationActivity.this, "盘点失败请重新盘点", "重新盘点");
                    tipsDialog.show();
                    return;
                }
                // 等待2000毫秒后获取卡号，并比对。
                for (int i = 0; i < listEPC.size(); i++) {
                    String epc = listEPC.get(i).getEpc();
                    String myant = listEPC.get(i).getAnt();
//                    if (myant.equals("1") || myant.equals("2") || myant.equals("3")) {
                    listEpc.add(epc);
//                    }
                }
                //操作记录的数组
                Cursor cursor = mOpenHelper.query("select * from materialtable", null);
                while (cursor.moveToNext()) {
                    String epcs = cursor.getString(cursor.getColumnIndex("Epc"));
                    String staus = cursor.getString(cursor.getColumnIndex("Staus"));
                    String ids = cursor.getString(cursor.getColumnIndex("Ids"));
                    String StationName = cursor.getString(cursor.getColumnIndex("StationName"));
                    String StationId = cursor.getString(cursor.getColumnIndex("StationId"));
                    String Name = cursor.getString(cursor.getColumnIndex("Name"));
                    String Balancedata = cursor.getString(cursor.getColumnIndex("Balancedata"));
                    String Equation = cursor.getString(cursor.getColumnIndex("Equation"));
                    String Acidbase = cursor.getString(cursor.getColumnIndex("Acidbase"));
                    String Type = cursor.getString(cursor.getColumnIndex("Type"));
                    String CurrentWeight = cursor.getString(cursor.getColumnIndex("CurrentWeight"));
                    String Manufacturer = cursor.getString(cursor.getColumnIndex("Manufacturer"));
                    String chioUnitCode = cursor.getString(cursor.getColumnIndex("ChioUnitCode"));
                    String chioBuildCode = cursor.getString(cursor.getColumnIndex("ChioBuildCode"));
                    String chioFloorCode = cursor.getString(cursor.getColumnIndex("ChioFloorCode"));
                    String chioRoomCode = cursor.getString(cursor.getColumnIndex("ChioRoomCode"));
                    String chioUnitName = cursor.getString(cursor.getColumnIndex("ChioUnitName"));
                    String chioBuildName = cursor.getString(cursor.getColumnIndex("ChioBuildName"));
                    String chioFloorName = cursor.getString(cursor.getColumnIndex("ChioFloorName"));
                    String chioRoomName = cursor.getString(cursor.getColumnIndex("ChioRoomName"));
                    String tips = cursor.getString(cursor.getColumnIndex("Tips"));

                    JSONObject object = new JSONObject();
                    DangerousChemicals bean = new DangerousChemicals();
                    Boolean exists = ((List) listEpc).contains(epcs);
                    if (exists) {
                        System.out.println(epcs + "有卡号信息！");//当有卡时判断状态，如果是出库状态则认为现在为入库。
                        if (staus.equals("1")) {
                            if (tips.equals("未过秤")) {
                                bean.setRfid(epcs);
                                bean.setName(Name);
                                bean.setBalancedata("未过秤");
                                tipslist.add(bean);
                            } else {
                                System.out.println(epcs + "物资入库");
                                mOpenHelper.update(epcs, "2", "");
                                bean.setRfid(epcs);
                                bean.setName(Name);
                                bean.setState("in");
                                bean.setEquation(Equation);
                                bean.setAcidbase(Acidbase);
                                bean.setType(Type);
                                bean.setBalancedata(Balancedata);
                                bean.setSpecifications(CurrentWeight);
                                bean.setManufacturer(Manufacturer);
                                bean.setUsernamea(PreferenceUtils.getString(OperationActivity.this, "chioUserName1"));
                                bean.setUsernameb(PreferenceUtils.getString(OperationActivity.this, "chioUserName2"));
                                list.add(bean);
                                depositlist.add(bean);
                                //统计本次物品入库的操作记录
                                try {
                                    object.put("chioRfid", epcs);//RFID
                                    object.put("chioType", "2");//危化品状态
                                    object.put("chioSubstanceCode", ids);//危化品ID
                                    object.put("chioSubstanceName", Name);//危化品名称
                                    object.put("chioSubstanceFormula", Equation);//危化品方程式
                                    object.put("chioNum", Balancedata);//危化品当前重量
                                    object.put("chioChemicalStoreCode", StationId);//站点ID
                                    object.put("chioChemicalStoreName", StationName);//站点名称
                                    object.put("chioUserName1", PreferenceUtils.getString(OperationActivity.this, "chioUserName1"));//操作人A
                                    object.put("chioUserName2", PreferenceUtils.getString(OperationActivity.this, "chioUserName2"));//操作人B
                                    object.put("chioUserCode1", PreferenceUtils.getString(OperationActivity.this, "chioUserCode1"));//操作人CODEA
                                    object.put("chioUserCode2", PreferenceUtils.getString(OperationActivity.this, "chioUserCode2"));//操作人CODEB
                                    object.put("chioUnitCode", chioUnitCode);
                                    object.put("chioBuildCode", chioBuildCode);
                                    object.put("chioFloorCode", chioFloorCode);
                                    object.put("chioRoomCode", chioRoomCode);
                                    object.put("chioUnitName", chioUnitName);
                                    object.put("chioBuildName", chioBuildName);
                                    object.put("chioFloorName", chioFloorName);
                                    object.put("chioRoomName", chioRoomName);
                                    Depositarray.put(object);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //当无卡时则认为现在为出库，并修改物资为出库状态。
                    else {
                        System.out.println(epcs + "无卡号信息！");
                        if (staus.equals("2")) {
                            System.out.println(epcs + "物资出库");//更新物资状态，并修改为未过秤状态
                            mOpenHelper.update(epcs, "1", "未过秤");
                            bean.setRfid(epcs);
                            bean.setName(Name);
                            bean.setState("out");
                            bean.setEquation(Equation);
                            bean.setAcidbase(Acidbase);
                            bean.setType(Type);
                            bean.setBalancedata(Balancedata);
                            bean.setSpecifications(CurrentWeight);
                            bean.setManufacturer(Manufacturer);
                            bean.setUsernamea(PreferenceUtils.getString(OperationActivity.this, "chioUserName1"));
                            bean.setUsernameb(PreferenceUtils.getString(OperationActivity.this, "chioUserName2"));
                            list.add(bean);
                            taskuotlist.add(bean);
                            //统计本次出库的物资操作记录。
                            try {
                                object.put("chioRfid", epcs);//RFID
                                object.put("chioType", "1");//危化品状态
                                object.put("chioSubstanceCode", ids);//危化品ID
                                object.put("chioSubstanceName", Name);//危化品名称
                                object.put("chioSubstanceFormula", Equation);//危化品方程式
                                object.put("chioNum", Balancedata);//危化品当前重B
                                object.put("chioChemicalStoreCode", StationId);//站点ID
                                object.put("chioChemicalStoreName", StationName);//站点名称
                                object.put("chioUserName1", PreferenceUtils.getString(OperationActivity.this, "chioUserName1"));//操作人A
                                object.put("chioUserName2", PreferenceUtils.getString(OperationActivity.this, "chioUserName2"));//操作人B
                                object.put("chioUserCode1", PreferenceUtils.getString(OperationActivity.this, "chioUserCode1"));//操作人CODEA
                                object.put("chioUserCode2", PreferenceUtils.getString(OperationActivity.this, "chioUserCode2"));//操作人CODE
                                object.put("chioUnitCode", chioUnitCode);
                                object.put("chioBuildCode", chioBuildCode);
                                object.put("chioFloorCode", chioFloorCode);
                                object.put("chioRoomCode", chioRoomCode);
                                object.put("chioUnitName", chioUnitName);
                                object.put("chioBuildName", chioBuildName);
                                object.put("chioFloorName", chioFloorName);
                                object.put("chioRoomName", chioRoomName);
                                Depositarray.put(object);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                cursor.close();
                if (tipslist.size() > 0) {
                    TTSUtils.getInstance().speak("以下是您本次未过秤记录,请重新过秤");
                    weighhints.setText("以下是您本次未过秤记录");
                    weighhints.setVisibility(View.VISIBLE);//称重提示打开
                    weigh.setVisibility(View.GONE);//称重界面打开
                    tipsview.setVisibility(View.VISIBLE);//未称重界面关闭
                    returnhints.setVisibility(View.GONE);//还取提示界面关闭
                    inventory.setVisibility(View.GONE);//盘点界面关闭
                    fragment.setVisibility(View.VISIBLE);
                    TipsAdapter tipsAdapter = new TipsAdapter(OperationActivity.this, tipslist);
                    tipslistview.setAdapter(tipsAdapter);
                    return;
                } else {
                    //提交盘点数据,并展示。
                    if (Depositarray.length() > 0) {
                        fragment.setVisibility(View.GONE);//归还提示和称重界面关闭
                        inventory.setVisibility(View.VISIBLE);//盘点界面打开
                        StorageAdapter storageAdapter = new StorageAdapter(OperationActivity.this, list);
                        storagelistview.setAdapter(storageAdapter);
                        AccessRequest.GetAccessRecords(OperationActivity.this, Depositarray);
                    }
                    startTime();//启动界面监听
                }
            }
        }, 2000);
    }

    /**
     * 门未关的时间的监听
     */
    public void startTime() {
        if (countdowntimers == null) {
            countdowntimers = new CountDownTimer(advertisingTimes, 1000l) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!OperationActivity.this.isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);
                        System.out.println("=========倒计时=========" + remainTime + "S");
                        countdown.setText(remainTime + "S");
                    }
                }

                @Override
                public void onFinish() { //定时完成后的操作
                    //跳转到页面
                    Intent intent = new Intent(OperationActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            };
            countdowntimers.start();
        } else {
            countdowntimers.start();
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
                Lock.getInstance().closestatus();
                break;
            //打开盘点记录界面
            case R.id.accessrecords:
                linears.setVisibility(View.VISIBLE);
                linearb.setVisibility(View.GONE);
                storagelistview.setVisibility(View.VISIBLE);
                inventorylistview.setVisibility(View.GONE);
                break;
            //打开库存记录界面
            case R.id.inventoryrecords:
                linears.setVisibility(View.GONE);
                linearb.setVisibility(View.VISIBLE);
                storagelistview.setVisibility(View.GONE);
                inventorylistview.setVisibility(View.VISIBLE);
                StockData.Stock(getApplicationContext(), OperationActivity.this, "2");
                break;
            //打开全部记录界面
            case R.id.allrecords:
                StorageAdapter storageAdapter = new StorageAdapter(OperationActivity.this, list);
                storagelistview.setAdapter(storageAdapter);
                linears.setBackground(OperationActivity.this.getResources().getDrawable(R.mipmap.taba));
                allrecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.white));
                storagerecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                removerecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                break;
            //打开存放记录界面
            case R.id.storagerecords:
                StorageAdapter storageAdaptera = new StorageAdapter(OperationActivity.this, depositlist);
                storagelistview.setAdapter(storageAdaptera);
                linears.setBackground(OperationActivity.this.getResources().getDrawable(R.mipmap.tabb));
                allrecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                storagerecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.white));
                removerecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                break;
            //打开取出记录界面
            case R.id.removerecords:
                StorageAdapter storageAdapterb = new StorageAdapter(OperationActivity.this, taskuotlist);
                storagelistview.setAdapter(storageAdapterb);
                linears.setBackground(OperationActivity.this.getResources().getDrawable(R.mipmap.tabc));
                allrecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                storagerecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                removerecords.setTextColor(OperationActivity.this.getResources().getColor(R.color.white));
                break;
            //打开柜内库存
            case R.id.inventory_in:
                linearb.setBackground(OperationActivity.this.getResources().getDrawable(R.mipmap.inventory_taba));
                inventory_out.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                inventory_in.setTextColor(OperationActivity.this.getResources().getColor(R.color.white));
                StockData.Stock(getApplicationContext(), OperationActivity.this, "2");
                break;
            //打开柜外库存
            case R.id.inventory_out:
                linearb.setBackground(OperationActivity.this.getResources().getDrawable(R.mipmap.inventory_tabb));
                inventory_in.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                inventory_out.setTextColor(OperationActivity.this.getResources().getColor(R.color.white));
                StockData.Stock(getApplicationContext(), OperationActivity.this, "1");
                break;
            //开门重新过秤
            case R.id.rescaling:
                Lock.getInstance().sendA();//开门
                Lock.getInstance().checkstatus();//查询门的状态
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

    @Override
    public void StokSuccess(String state, List<DangerousChemicals> stocklist) {
        if (state.equals("1")) {
            StockAdapter stockAdapter = new StockAdapter(OperationActivity.this, stocklist);
            inventorylistview.setAdapter(stockAdapter);
        } else {
            StockAdapter stockAdapter = new StockAdapter(OperationActivity.this, stocklist);
            inventorylistview.setAdapter(stockAdapter);
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
        if (countdowntimers != null) {
            countdowntimers.cancel();
        }
        Lock.getInstance().closestatus();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }
}