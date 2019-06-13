package com.sanleng.dangerouscabinet;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Group;
import com.baidu.aip.manager.FaceSDKManager;
import com.sanleng.dangerouscabinet.broadcast.Receiver;
import com.sanleng.dangerouscabinet.face.activity.MainsActivity;
import com.sanleng.dangerouscabinet.fid.entity.Balance;
import com.sanleng.dangerouscabinet.fid.serialportapi.ReaderServiceImpl;
import com.sanleng.dangerouscabinet.fid.service.ReaderService;
import com.sanleng.dangerouscabinet.fid.tool.ReaderUtil;
import com.sanleng.dangerouscabinet.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.limlee.hipraiseanimationlib.HiPraise;
import org.limlee.hipraiseanimationlib.HiPraiseAnimationView;
import org.limlee.hipraiseanimationlib.HiPraiseWithCallback;
import org.limlee.hipraiseanimationlib.OnDrawCallback;
import org.limlee.hipraiseanimationlib.base.IPraise;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView test;
    private ReaderService readerService = new ReaderServiceImpl();
    private Receiver receiver;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int HEARDS[] = new int[]{
            R.mipmap.heart_1,
            R.mipmap.heart_1,
            R.mipmap.heart_1,
            R.mipmap.heart_1,
            R.mipmap.heart_1,
            R.mipmap.heart_1
    };
    private SparseArray<SoftReference<Bitmap>> mBitmapCacheArray = new SparseArray<>();
    private HiPraiseAnimationView mHiPraiseAnimationView;

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        Balance.getInstance().init();
        mHiPraiseAnimationView.start(); //添加点赞动画之前要先开始启动绘制
        mHandler.postDelayed(r, 1000);//延时100毫秒
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHiPraiseAnimationView.stop(); //停止绘制点赞动画
    }

    /**
     * 添加点赞动画
     */
    private void addPraise() {
        final IPraise hiPraise = new HiPraise(getHeartBitmap());
        mHiPraiseAnimationView.addPraise(hiPraise);
    }

    /**
     * 添加具有回调的点赞动画
     */
    private void addPraiseWithCallback() {
        final IPraise hiPraiseWithCallback = new HiPraiseWithCallback(getHeartBitmap(),
                new OnDrawCallback() {
                    @Override
                    public void onFinish() {
                        Log.d(TAG, "绘制完成了！");
                    }
                });
        mHiPraiseAnimationView.addPraise(hiPraiseWithCallback);
    }

    private Bitmap getHeartBitmap() {
        final int id = HEARDS[new Random().nextInt(HEARDS.length)];
        SoftReference<Bitmap> bitmapRef = mBitmapCacheArray.get(id);
        Bitmap retBitmap = null;
        if (null != bitmapRef) {
            retBitmap = bitmapRef.get();
        }
        if (null == retBitmap) {
            retBitmap = BitmapFactory.decodeResource(getResources(),
                    id);
            mBitmapCacheArray.put(id, new SoftReference<>(retBitmap));
        }
        return retBitmap;
    }


    private void initView() {
        test = findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainsActivity.class);
                startActivity(intent);
            }
        });

        EventBus.getDefault().register(this);
        System.out.println("========MAC==========" + MyApplication.getMac());
        //绑定唯一标识
        JPushInterface.setAlias(MainActivity.this, 1, MyApplication.getMac());

        mHiPraiseAnimationView = findViewById(R.id.praise_animation);
        mHiPraiseAnimationView.setOnClickListener(this);
        mHiPraiseAnimationView.performClick();
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
                System.out.println("=======秤的重量==========" + balancedata);
                test.setText("秤的重量：" + balancedata);
                break;
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
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        unregisterReceiver(receiver);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.praise_animation:
                addPraiseWithCallback();
                break;
        }
    }

    Handler mHandler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            addPraiseWithCallback();
            //每隔1s循环执行run方法
            mHandler.postDelayed(this, 500);
        }
    };
}
