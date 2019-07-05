package com.sanleng.dangerouscabinet.ui.activity;

import android.content.Intent;
import android.database.Cursor;
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
import com.sanleng.dangerouscabinet.fid.serialportapi.ReaderServiceImpl;
import com.sanleng.dangerouscabinet.fid.service.CallBack;
import com.sanleng.dangerouscabinet.fid.service.CallBackStopReadCard;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;
import com.sanleng.dangerouscabinet.fid.tool.ReaderUtil;
import com.sanleng.dangerouscabinet.fid.util.DataFilter;
import com.sanleng.dangerouscabinet.ui.adapter.StockAdapter;
import com.sanleng.dangerouscabinet.ui.adapter.StorageAdapter;
import com.sanleng.dangerouscabinet.ui.adapter.WeighAdapter;
import com.sanleng.dangerouscabinet.ui.bean.DangerousChemicals;
import com.sanleng.dangerouscabinet.utils.MessageEvent;
import com.sanleng.dangerouscabinet.utils.PreferenceUtils;
import com.sanleng.dangerouscabinet.utils.TTSUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private ListView storagelistview;//存放数据展示界面
    private ListView inventorylistview;//库存数据展示界面
    private List<DangerousChemicals> dataList = new ArrayList<>();//存储数据
    private WeighAdapter weighAdapter;//ListView的数据适配器
    private RelativeLayout inventory;//盘点记录界面
    private Button accessrecords;//盘点记录界面
    private Button inventoryrecords;//盘点记录界面
    private LinearLayout linears;//存放tab选项界面
    private LinearLayout linearb;//库存tab选项界面
    private TextView allrecords;//全部记录
    private TextView storagerecords;//存放记录
    private TextView removerecords;//取出记录
    private TextView inventory_in;//柜内库存
    private TextView inventory_out;//柜外库存
    JSONArray Depositarray = new JSONArray();
    List<DangerousChemicals> list = new ArrayList<>();
    List<DangerousChemicals> depositlist = new ArrayList<>();
    List<DangerousChemicals> taskuotlist = new ArrayList<>();
    List<DangerousChemicals> stocklista;//柜内
    List<DangerousChemicals> stocklistb;//柜外

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
        weighhints = findViewById(R.id.weighhints);//称重提示
        weigh = findViewById(R.id.weigh);//称重界面
        returnhints = findViewById(R.id.returnhints);//还取提示界面
        fragment = findViewById(R.id.fragment);//还取提示界面
        inventory = findViewById(R.id.inventory);//盘点界面
        fans = findViewById(R.id.fan);
        fans.setOnClickListener(this);
        Animation fananim = AnimationUtils.loadAnimation(this,
                R.anim.rotate_circle_anim);
        fans.startAnimation(fananim);// 开始动画
        weighlistview = findViewById(R.id.weighlistview);
        storagelistview = findViewById(R.id.storagelistview);//存放数据展示界面
        inventorylistview = findViewById(R.id.inventorylistview);//库存数据展示界面

        accessrecords = findViewById(R.id.accessrecords);//打开盘点记录界面
        accessrecords.setOnClickListener(this);
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
                String data = messageEvent.getMessage();
                String str = data.replaceAll(" ", "");
                String balancedata = str.substring(str.indexOf("+") + 1);
                String weigh = balancedata.replace("g",""); //得到新的字符串
                System.out.println("=====重量AAA======" + weigh);
                invOnces(weigh.trim());
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
                    returnhints.setVisibility(View.GONE);//还取提示界面关闭
                    inventory.setVisibility(View.GONE);//盘点界面关闭
                    fragment.setVisibility(View.VISIBLE);//归还提示和称重界面关闭

                    TTSUtils.getInstance().speak("本次称重物品是" + Name + "重量为" + balancedata);
                    //更新本地重量并提交服务器,生成过秤记录
                    mOpenHelper.updatebalancedata(epcs, balancedata);//更新数据重量并提交更新服务器数据
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
//                    if (myant.equals("1") || myant.equals("2") || myant.equals("3")) {
                    listEpc.add(epc);
//                    }
                }
                //操作记录的数组
                Cursor cursor = mOpenHelper.query("select * from materialtable", null);
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

                    JSONObject object = new JSONObject();
                    DangerousChemicals bean = new DangerousChemicals();
                    Boolean exists = ((List) listEpc).contains(epcs);
                    String operatora = PreferenceUtils.getString(OperationActivity.this, "FaceOne");
                    String operatorb = PreferenceUtils.getString(OperationActivity.this, "FaceTwo");
                    if (exists) {
                        System.out.println(epcs + "有卡号信息！");//当有卡时判断状态，如果是出库状态则认为现在为入库。
                        if (staus.equals("1")) {
                            System.out.println(epcs + "物资入库");
                            mOpenHelper.update(epcs, "2");
                            bean.setRfid(epcs);
                            bean.setName(Name);
                            bean.setState("in");
                            bean.setEquation(Equation);
                            bean.setAcidbase(Acidbase);
                            bean.setType(Type);
                            bean.setBalancedata(Balancedata);
                            bean.setSpecifications(CurrentWeight);
                            bean.setManufacturer(Manufacturer);
                            bean.setUsernamea("11");
                            bean.setUsernameb("22");
                            list.add(bean);
                            depositlist.add(bean);
                            //危化品入库时判断是否已过秤，未过秤给予提示
//                            Cursor cursors = mOpenHelper.query("select * from operationalrecords where Epc=" + "'" + epcs + "'", null);
//                            while (cursors.moveToNext()) {
////                                String Names = cursor.getString(cursor.getColumnIndex("Name"));
////                                TTSUtils.getInstance().speak("请注意，当前您有危化品未过秤。");
//                            }
//                            cursors.close();
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
                                object.put("chioUserName1", "zhoulixing1");//操作人A
                                object.put("chioUserName2", "zhoulixing3");//操作人B
                                object.put("chioUserCode1", "0358eda87d8a4f51aec3623fb05d44f1");//操作人A
                                object.put("chioUserCode2", "a3febbdc86c548448a39d323a959b02a");//操作人B

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

                    //当无卡时则认为现在为出库，并修改物资为出库状态。
                    else {
                        System.out.println(epcs + "无卡号信息！");
                        if (staus.equals("2")) {
                            System.out.println(epcs + "物资出库");//更新物资状态，并修改为未过秤状态
                            mOpenHelper.update(epcs, "1");
                            bean.setRfid(epcs);
                            bean.setName(Name);
                            bean.setState("out");
                            bean.setEquation(Equation);
                            bean.setAcidbase(Acidbase);
                            bean.setType(Type);
                            bean.setBalancedata(Balancedata);
                            bean.setSpecifications(CurrentWeight);
                            bean.setManufacturer(Manufacturer);
                            bean.setUsernamea("11");
                            bean.setUsernameb("22");
                            list.add(bean);
                            taskuotlist.add(bean);
                            //统计本次出库的物资操作记录。
                            try {
                                object.put("chioRfid", epcs);//RFID
                                object.put("chioType", "1");//危化品状态
                                object.put("chioSubstanceCode", ids);//危化品ID
                                object.put("chioSubstanceName", Name);//危化品名称
                                object.put("chioSubstanceFormula", Equation);//危化品方程式
                                object.put("chioNum", Balancedata);//危化品当前重量
                                object.put("chioChemicalStoreCode", StationId);//站点ID
                                object.put("chioChemicalStoreName", StationName);//站点名称
                                object.put("chioUserName1", "zhoulixing1");//操作人A
                                object.put("chioUserName2", "zhoulixing3");//操作人B
                                object.put("chioUserCode1", "0358eda87d8a4f51aec3623fb05d44f1");//操作人A
                                object.put("chioUserCode2", "a3febbdc86c548448a39d323a959b02a");//操作人B

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
                //提交盘点数据,并展示。
                System.out.println("========Array大小============" + Depositarray.length());
                //提交服务器
                if (Depositarray.length() > 0) {
                    fragment.setVisibility(View.GONE);//归还提示和称重界面关闭
                    inventory.setVisibility(View.VISIBLE);//盘点界面打开
                    StorageAdapter storageAdapter = new StorageAdapter(OperationActivity.this, list);
                    storagelistview.setAdapter(storageAdapter);
                    AccessRequest.GetAccess(OperationActivity.this, Depositarray.toString());
                }
            }
        }, 2000);
    }

    //库存信息
    private void Stock(String state) {
        stocklista = new ArrayList<>();//柜内
        stocklistb = new ArrayList<>();//柜外
        Cursor cursor = mOpenHelper.query("select * from materialtable", null);
        while (cursor.moveToNext()) {
            String epcs = cursor.getString(cursor.getColumnIndex("Epc"));
            String staus = cursor.getString(cursor.getColumnIndex("Staus"));
            String Name = cursor.getString(cursor.getColumnIndex("Name"));
            String Balancedata = cursor.getString(cursor.getColumnIndex("Balancedata"));
            String Equation = cursor.getString(cursor.getColumnIndex("Equation"));
            String Acidbase = cursor.getString(cursor.getColumnIndex("Acidbase"));
            String Type = cursor.getString(cursor.getColumnIndex("Type"));
            String CurrentWeight = cursor.getString(cursor.getColumnIndex("CurrentWeight"));
            String Manufacturer = cursor.getString(cursor.getColumnIndex("Manufacturer"));
            if (staus.equals("1")) {
                DangerousChemicals bean = new DangerousChemicals();
                bean.setRfid(epcs);
                bean.setName(Name);
                bean.setEquation(Equation);
                bean.setAcidbase(Acidbase);
                bean.setType(Type);
                bean.setBalancedata(Balancedata);
                bean.setSpecifications(CurrentWeight);
                bean.setManufacturer(Manufacturer);
                stocklista.add(bean);
            } else {
                DangerousChemicals bean = new DangerousChemicals();
                bean.setRfid(epcs);
                bean.setName(Name);
                bean.setEquation(Equation);
                bean.setAcidbase(Acidbase);
                bean.setType(Type);
                bean.setBalancedata(Balancedata);
                bean.setSpecifications(CurrentWeight);
                bean.setManufacturer(Manufacturer);
                stocklistb.add(bean);
            }
        }
        cursor.close();

        if (state.equals("out")) {
            StockAdapter stockAdaptera = new StockAdapter(OperationActivity.this, stocklista);
            inventorylistview.setAdapter(stockAdaptera);
        } else {
            StockAdapter stockAdapterb = new StockAdapter(OperationActivity.this, stocklistb);
            inventorylistview.setAdapter(stockAdapterb);
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
                Stock("in");
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
                Stock("in");
                break;
            //打开柜外库存
            case R.id.inventory_out:
                linearb.setBackground(OperationActivity.this.getResources().getDrawable(R.mipmap.inventory_tabb));
                inventory_in.setTextColor(OperationActivity.this.getResources().getColor(R.color.black));
                inventory_out.setTextColor(OperationActivity.this.getResources().getColor(R.color.white));
                Stock("out");
                break;
            //测试功能
            case R.id.fan:
                invOnce();
//                weighhints.setVisibility(View.VISIBLE);//称重提示
//                weigh.setVisibility(View.VISIBLE);//称重界面
//                returnhints.setVisibility(View.GONE);//还取提示界面
//                inventory.setVisibility(View.GONE);//盘点界面打开
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
