package com.sanleng.dangerouscabinet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Group;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.GlobalSet;
import com.baidu.aip.utils.PreferencesUtil;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechUtility;
import com.sanleng.dangerouscabinet.Presenter.ChemicalRequest;
import com.sanleng.dangerouscabinet.broadcast.Receiver;
import com.sanleng.dangerouscabinet.data.SDBHelper;
import com.sanleng.dangerouscabinet.face.activity.LivenessSettingActivity;
import com.sanleng.dangerouscabinet.face.activity.OrbbecProVideoIdentifyActivity;
import com.sanleng.dangerouscabinet.face.activity.RgbVideoIdentityActivity;
import com.sanleng.dangerouscabinet.fid.serialportapi.ReaderServiceImpl;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;
import com.sanleng.dangerouscabinet.fid.tool.ReaderUtil;
import com.sanleng.dangerouscabinet.ui.activity.PasswordAuthentication;
import com.sanleng.dangerouscabinet.ui.activity.SearchActivity;
import com.sanleng.dangerouscabinet.utils.TTSUtils;
import com.sanleng.dangerouscabinet.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView passwordauthentication;
    private TextView search;
    private TextView faceverification;
    private TextView time;
    private ImageView iv_rotate;
    private ImageView iv_rotates;
    private ImageView fan;
    private ImageView net;
    private ImageView lock;
    private TextView voc;
    private TextView temperature;
    private TextView humidity;
    private TextView quality;
    private String vocdata;
    private String temperaturedata;
    private String humiditydata;
    private ImageView vocstate;
    private ImageView temperaturestate;
    private ImageView humiditystate;
    private RelativeLayout upmain;

    private ReaderService readerService = new ReaderServiceImpl();
    private Receiver receiver;
    public static final int TYPE_RGB_DEPTH_LIVENSS = 4;
    public static final String TYPE_LIVENSS = "TYPE_LIVENSS";
    private List<String> permissionList = null;
    private static final String TAG = MainActivity.class.getSimpleName();
    int a = 1;//测试界面数据

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//初始化
        initFaceeSDK();//初始化人脸；
        addGroup();//强制添加分组；
        registeredBroadcasting(); //广播注册
        checkPermission();//7.0以上添加存储与相机的权限
        fid();//RFID串口连接
        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_DEPTH_LIVENSS);//设置摄像头样式；
        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECATLAS);//设置摄像头样式；
        addData();//获取voc,温度，湿度。
        initTTS();//语音注册
        new TimeThread().start();
        ChemicalRequest.GetChemical(MainActivity.this,MyApplication.getMac());//导入最新的服务器化学品信息
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
        if (Utils.foFile() == false) {
            new Thread(runnables).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //初始化
    private void initView() {
        //绑定唯一标识
        JPushInterface.setAlias(MainActivity.this, 1, MyApplication.getMac());
        passwordauthentication = findViewById(R.id.passwordauthentication);
        search = findViewById(R.id.search);
        faceverification = findViewById(R.id.faceverification);
        time = findViewById(R.id.time);
        iv_rotate = findViewById(R.id.iv_rotate);
        iv_rotates = findViewById(R.id.iv_rotates);
        fan = findViewById(R.id.fan);
        net = findViewById(R.id.net);
        lock = findViewById(R.id.lock);
        voc = findViewById(R.id.voc);
        quality = findViewById(R.id.quality);
        temperature = findViewById(R.id.temperature);
        humidity = findViewById(R.id.humidity);
        vocstate = findViewById(R.id.vocstate);
        temperaturestate = findViewById(R.id.temperaturestate);
        humiditystate = findViewById(R.id.humiditystate);
        upmain = findViewById(R.id.upmain);

        Animation anim = AnimationUtils.loadAnimation(this,
                R.anim.rotate_circle_anim);
        iv_rotate.startAnimation(anim);// 开始动画
        Animation anims = AnimationUtils.loadAnimation(this,
                R.anim.rotate_circle_anims);
        iv_rotates.startAnimation(anims);// 开始动画
        Animation fananim = AnimationUtils.loadAnimation(this,
                R.anim.rotate_circle_anim);
        fan.startAnimation(fananim);// 开始动画
        passwordauthentication.setOnClickListener(this);
        search.setOnClickListener(this);
        faceverification.setOnClickListener(this);
    }

    //获取VOC,温度，湿度
    private void addData() {
        if (a == 1) {
            vocdata = "56";
            temperaturedata = "26";
            humiditydata = "60";
            voc.setText(vocdata);
            temperature.setText(temperaturedata);
            humidity.setText(humiditydata);
            quality.setText("优");
            vocstate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowac_icon));
            temperaturestate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowas_icon));
            humiditystate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowa_icon));
            upmain.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.upmain_icon));
            a = 2;
            return;
        }

        if (a == 2) {
            vocdata = "45";
            temperaturedata = "30";
            humiditydata = "55";
            voc.setText(vocdata);
            temperature.setText(temperaturedata);
            humidity.setText(humiditydata);
            quality.setText("良");
            vocstate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowa_icon));
            temperaturestate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowas_icon));
            humiditystate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowas_icon));
            upmain.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.upmainb_icon));
            a = 3;
            return;
        }
        if (a == 3) {
            vocdata = "66";
            temperaturedata = "40";
            humiditydata = "80";
            voc.setText(vocdata);
            temperature.setText(temperaturedata);
            humidity.setText(humiditydata);
            quality.setText("差");
            vocstate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowa_icon));
            temperaturestate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowa_icon));
            humiditystate.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.arrowa_icon));
            upmain.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.upmainc_icon));
            a = 1;
            return;
        }


    }

    //初始化人脸
    private void initFaceeSDK() {
        FaceSDKManager.getInstance().init(this, new FaceSDKManager.SdkInitListener() {
            @Override
            public void initStart() {
            }

            @Override
            public void initSuccess() {
            }

            @Override
            public void initFail(int errorCode, String msg) {
            }
        });
    }

    //强制添加一个分组
    private void addGroup() {
        // 使用人脸1：n时使用
        DBManager.getInstance().init(this);
        List<Group> groupList = FaceApi.getInstance().getGroupList(0, 1000);
        if (groupList.size() <= 0) {
            String groupId = "UserGroup";
            Group group = new Group();
            group.setGroupId(groupId);
            boolean ret = FaceApi.getInstance().groupAdd(group);
            return;
        }
    }

    //连接Fid串口
    private void fid() {
        if (ReaderUtil.readers == null) {
            ReaderUtil.readers = readerService.serialPortConnect("/dev/ttysWK1", 115200);
            if (ReaderUtil.readers != null) {
                System.out.println("FID串口连接成功");
                readerService.version(ReaderUtil.readers);
            } else {
                System.out.println("FID串口连接失败");
            }
        }
    }

    //初始化语音合成
    private void initTTS() {
        //讯飞语音播报平台
        String key = "5d0b232a";
        SpeechUtility.createUtility(this, "appid=" + key);//=号后面写自己应用的APPID
        Setting.setShowLog(true); //设置日志开关（默认为true），设置成false时关闭语音云SDK日志打印
        TTSUtils.getInstance().init(); //初始化工具类
    }

    //广播注册
    private void registeredBroadcasting() {
        receiver = new Receiver();
        IntentFilter intenta = new IntentFilter();
        intenta.addAction(MyApplication.BROADCAST_ACTION_DISC); // 只有持有相同的action的接受者才能接收此广
        registerReceiver(receiver, intenta, MyApplication.BROADCAST_PERMISSION_DISC, null);
    }

    //7.0以上视频图片存储的权限
    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissionStrs = new ArrayList<>();
            int hasWriteSdcardPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteSdcardPermission != PackageManager.PERMISSION_GRANTED) {
                permissionStrs.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            int hasCameraPermission = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
            if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                permissionStrs.add(android.Manifest.permission.CAMERA);
            }
            String[] stringArray = permissionStrs.toArray(new String[0]);
            if (permissionStrs.size() > 0) {
                requestPermissions(stringArray, MyApplication.REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addListPermission();
            boolean isGranted = false;//是否全部授权
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            Iterator<String> iterator = permissionList.iterator();
            while (iterator.hasNext()) {
                // 检查该权限是否已经获取
                int granted = ContextCompat.checkSelfPermission(this, iterator.next());
                if (granted == PackageManager.PERMISSION_GRANTED) {
                    iterator.remove();//已授权则remove
                }
            }
            if (permissionList.size() > 0) {
                // 如果没有授予该权限，就去提示用户请求
                //将List转为数组
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                // 开始提交请求权限
                ActivityCompat.requestPermissions(this, permissions, 0x10);
            } else {
                Log.i("zhh", "权限已申请");
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MyApplication.REQUEST_CODE_ASK_PERMISSIONS:
                //可以遍历每个权限设置情况
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //这里写你需要相关权限的操作

                } else {
                    Toast.makeText(MainActivity.this, "权限没有开启", Toast.LENGTH_SHORT).show();
                }
                break;
            case 0x10:
                if (grantResults.length > 0 && ifGrantResult(grantResults)) {
                    Toast.makeText(this, "同意权限申请", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "权限被拒绝了", Toast.LENGTH_SHORT).show();
//                    finish();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean ifGrantResult(int[] grants) {
        boolean isGrant = true;
        for (int grant : grants) {
            if (grant == PackageManager.PERMISSION_DENIED) {
                isGrant = false;
                break;
            }
        }
        return isGrant;
    }


    //敏感权限添加
    private void addListPermission() {
        if (null == permissionList) {
            permissionList = new ArrayList<>();
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭FID串口
        if (ReaderUtil.readers != null) {
            readerService.disconnect(ReaderUtil.readers);
            ReaderUtil.readers = null;
        }
        unregisterReceiver(receiver);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //密码认证
            case R.id.passwordauthentication:
                startActivity(new Intent(this, PasswordAuthentication.class));
                break;
            //搜索查询
            case R.id.search:
                startActivity(new Intent(this, SearchActivity.class));
//                TTSUtils.getInstance().speak("请注意，当前温度过高");
//                TTSUtils.getInstance().speak("请注意，当前门未关起，请注意，当前门未关起");
                break;
            //人脸认证
            case R.id.faceverification:
//                Intent intent = new Intent(MainActivity.this, MainsActivity.class);
//                startActivity(intent);
//                 使用人脸1：n时使用
                DBManager.getInstance().init(this);
                livnessTypeTip();
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager
                        .PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
                    return;
                }
                showSingleAlertDialog();
                break;
        }
    }


    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = MyApplication.MESSAGE_TIME;
                    mHandlertime.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandlertime = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyApplication.MESSAGE_TIME:
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat
                            .format("HH:mm", sysTime);
                    time.setText(sysTimeStr);
                    if (Utils.isNetworkAvailable(MainActivity.this) == true) {
                        net.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.networkin_icon));
                    } else {
                        net.setBackground(MainActivity.this.getResources().getDrawable(R.mipmap.networkon_icon));
                    }
                    addData();
                    break;
                default:
                    break;
            }
        }
    };

    //===============================人脸识别============================
    private String[] items;

    public void showSingleAlertDialog() {
        List<Group> groupList = FaceApi.getInstance().getGroupList(0, 1000);
        if (groupList.size() <= 0) {
            Toast.makeText(this, "还没有分组，请创建分组并添加用户", Toast.LENGTH_SHORT).show();
            return;
        }
        items = new String[groupList.size()];
        for (int i = 0; i < groupList.size(); i++) {
            Group group = groupList.get(i);
            items[i] = group.getGroupId();
        }
        choiceIdentityType(items[0]);

    }

    private void choiceIdentityType(String groupId) {
        int type = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity
                .TYPE_NO_LIVENSS);
        if (type == LivenessSettingActivity.TYPE_NO_LIVENSS) {
//            Toast.makeText(this, "当前活体策略：无活体", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, RgbVideoIdentityActivity.class);
            intent.putExtra("group_id", groupId);
            startActivity(intent);
        } else if (type == LivenessSettingActivity.TYPE_RGB_LIVENSS) {
//            Toast.makeText(this, "当前活体策略：单目RGB活体", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, RgbVideoIdentityActivity.class);
            intent.putExtra("group_id", groupId);
            startActivity(intent);
        } else if (type == LivenessSettingActivity.TYPE_RGB_DEPTH_LIVENSS) {
//            Toast.makeText(this, "当前活体策略：双目RGB+Depth活体", Toast.LENGTH_LONG).show();
            int cameraType = PreferencesUtil.getInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBEC);
            Intent intent = null;
            if (cameraType == GlobalSet.ORBBECPRO) {
                intent = new Intent(MainActivity.this, OrbbecProVideoIdentifyActivity.class);
            } else if (cameraType == GlobalSet.ORBBECPROS1) {
                intent = new Intent(MainActivity.this, OrbbecProVideoIdentifyActivity.class);
            } else if (cameraType == GlobalSet.ORBBECPRODABAI) {
                intent = new Intent(MainActivity.this, OrbbecProVideoIdentifyActivity.class);
            } else if (cameraType == GlobalSet.ORBBECPRODEEYEA) {
                intent = new Intent(MainActivity.this, OrbbecProVideoIdentifyActivity.class);
            } else if (cameraType == GlobalSet.ORBBECATLAS) {
                intent = new Intent(MainActivity.this, OrbbecProVideoIdentifyActivity.class);
            }
            if (intent != null) {
                intent.putExtra("group_id", groupId);
                startActivity(intent);
            }
        }
    }

    private void livnessTypeTip() {
        try {
            int type = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity
                    .TYPE_NO_LIVENSS);
            if (type == LivenessSettingActivity.TYPE_NO_LIVENSS) {
//            Toast.makeText(this, "当前活体策略：无活体, 请选用普通USB摄像头", Toast.LENGTH_LONG).show();
            } else if (type == LivenessSettingActivity.TYPE_RGB_LIVENSS) {
//            Toast.makeText(this, "当前活体策略：单目RGB活体, 请选用普通USB摄像头", Toast.LENGTH_LONG).show();
            } else if (type == LivenessSettingActivity.TYPE_RGB_IR_LIVENSS) {
//            Toast.makeText(this, "当前活体策略：双目RGB+IR活体, 请选用RGB+IR摄像头",
//                    Toast.LENGTH_LONG).show();
            } else if (type == LivenessSettingActivity.TYPE_RGB_DEPTH_LIVENSS) {
//            Toast.makeText(this, "当前活体策略：双目RGB+Depth活体，请选用RGB+Depth摄像头", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
