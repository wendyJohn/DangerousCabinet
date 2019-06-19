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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Group;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.GlobalSet;
import com.baidu.aip.utils.PreferencesUtil;
import com.sanleng.dangerouscabinet.broadcast.Receiver;
import com.sanleng.dangerouscabinet.face.activity.LivenessSettingActivity;
import com.sanleng.dangerouscabinet.face.activity.OrbbecProVideoIdentifyActivity;
import com.sanleng.dangerouscabinet.face.activity.RgbVideoIdentityActivity;
import com.sanleng.dangerouscabinet.fid.serialportapi.ReaderServiceImpl;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;
import com.sanleng.dangerouscabinet.fid.tool.ReaderUtil;
import com.sanleng.dangerouscabinet.ui.activity.PasswordAuthentication;
import com.sanleng.dangerouscabinet.ui.activity.SearchActivity;
import com.sanleng.dangerouscabinet.utils.Utils;

import java.util.ArrayList;
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
    private ReaderService readerService = new ReaderServiceImpl();
    private Receiver receiver;
    public static final int TYPE_RGB_DEPTH_LIVENSS = 4;
    public static final String TYPE_LIVENSS = "TYPE_LIVENSS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//初始化
        initFaceeSDK();//初始化人脸；
        addGroup();//强制添加分组；
        registeredBroadcasting(); //广播注册
        checkPermission();//7.0以上添加存储与相机的权限
        fid();
        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_DEPTH_LIVENSS);//设置摄像头样式；
        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECATLAS);//设置摄像头样式；

    }

    @Override
    protected void onResume() {
        super.onResume();
        new TimeThread().start();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


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
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                break;
            //人脸认证
            case R.id.faceverification:
//                Intent intent = new Intent(MainActivity.this, MainsActivity.class);
//                startActivity(intent);
                // 使用人脸1：n时使用
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
}
