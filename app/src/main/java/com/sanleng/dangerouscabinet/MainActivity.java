package com.sanleng.dangerouscabinet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Group;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.GlobalSet;
import com.baidu.idl.facesdk.utils.PreferencesUtil;
import com.sanleng.dangerouscabinet.face.activity.MainsActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int TYPE_RGB_DEPTH_LIVENSS = 4;
    public static final String TYPE_LIVENSS = "TYPE_LIVENSS";
    private TextView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();//初始化
        initFaceeSDK();//初始化人脸；
        addGroup();//强制添加分组；
        checkPermission();//7.0以上添加存储与相机的权限
        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_DEPTH_LIVENSS);//设置摄像头样式；
        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECATLAS);//设置摄像头样式；
    }

    private void initView(){
        test=findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,MainsActivity.class);
                startActivity(intent);
            }
        });
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
            System.out.println("==============" + "添加" + (ret ? "成功" : "失败"));
            return;
        }
    }

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
}
