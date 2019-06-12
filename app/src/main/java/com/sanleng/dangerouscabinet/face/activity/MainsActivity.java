package com.sanleng.dangerouscabinet.face.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Group;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.GlobalSet;
import com.baidu.aip.utils.PreferencesUtil;
import com.sanleng.dangerouscabinet.R;

import java.util.ArrayList;
import java.util.List;

public class MainsActivity extends AppCompatActivity implements View.OnClickListener {
    private Button videoIdentifyBtn;
    private Button userGroupManagerBtn;
    private Button livenessSettingBtn;
    private Button deviceActivateBtn;
    private int count;
    private Button btMultiThread;
    private Button featureSettingBtn;

    ArrayList<String> list = new ArrayList<>();
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mains);
        initView();
        // 使用人脸1：n时使用
        DBManager.getInstance().init(this);
        // 初始化SDK
        initFaceeSDK();
        livnessTypeTip();
    }

    private void initView() {
        videoIdentifyBtn = findViewById(R.id.video_identify_faces_btn);
        userGroupManagerBtn = findViewById(R.id.user_groud_manager_btn);
        livenessSettingBtn = findViewById(R.id.liveness_setting_btn);
        deviceActivateBtn = findViewById(R.id.device_activate_btn);
        btMultiThread = findViewById(R.id.bt_multiThread);
        featureSettingBtn = findViewById(R.id.feature_setting_btn);

        videoIdentifyBtn.setOnClickListener(this);
        userGroupManagerBtn.setOnClickListener(this);
        livenessSettingBtn.setOnClickListener(this);
        deviceActivateBtn.setOnClickListener(this);
        deviceActivateBtn.setOnClickListener(this);
        btMultiThread.setOnClickListener(this);
        featureSettingBtn.setOnClickListener(this);
    }

    private void initFaceeSDK() {
        FaceSDKManager.getInstance().init(this, new FaceSDKManager.SdkInitListener() {
            @Override
            public void initStart() {
                toast("sdk init start");
            }

            @Override
            public void initSuccess() {
                toast("sdk init success");
            }

            @Override
            public void initFail(int errorCode, String msg) {
                toast("sdk init fail:" + msg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v == deviceActivateBtn) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                    .PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                return;
            }
            FaceSDKManager.getInstance().showActivation(new FaceSDKManager.SdkInitListener() {
                @Override
                public void initStart() {
                    toast("sdk init start");
                }

                @Override
                public void initSuccess() {
                    toast("sdk init success");
                }

                @Override
                public void initFail(int errorCode, String msg) {
                    toast("sdk init fail:" + msg);
                }
            });
            return;
        }

        if (FaceSDKManager.getInstance().initStatus() != FaceSDKManager.SDK_SUCCESS) {
            toast("SDK还未激活，请先激活");
            return;
        }
        if (v == videoIdentifyBtn) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager
                    .PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
                return;
            }
            showSingleAlertDialog();
        } else if (v == userGroupManagerBtn) {
            Intent intent = new Intent(this, UserGroupManagerActivity.class);
            startActivity(intent);
        } else if (v == livenessSettingBtn) {
            Intent intent = new Intent(this, LivenessSettingActivity.class);
            startActivity(intent);
        } else if (v == btMultiThread) {
//            Intent intent = new Intent(this, MultiThreadActivity.class);
//            startActivity(intent);
//            // 测试
//            MultiThreadManager.getInstance().start(this);
            Log.e("chaixiaogang", "duoxiancheng");
        } else if (v == featureSettingBtn) {
            Intent intent = new Intent(this, FeatureSettingActivity.class);
            startActivity(intent);
        }
    }

    private AlertDialog alertDialog;
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

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("请选择分组groupID");
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int index) {
                Toast.makeText(MainsActivity.this, items[index], Toast.LENGTH_SHORT).show();
                choiceIdentityType(items[index]);
                alertDialog.dismiss();
            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void choiceIdentityType(String groupId) {
        int type = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity
                .TYPE_NO_LIVENSS);
        if (type == LivenessSettingActivity.TYPE_NO_LIVENSS) {
            Toast.makeText(this, "当前活体策略：无活体", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainsActivity.this, RgbVideoIdentityActivity.class);
            intent.putExtra("group_id", groupId);
            startActivity(intent);
        } else if (type == LivenessSettingActivity.TYPE_RGB_LIVENSS) {
            Toast.makeText(this, "当前活体策略：单目RGB活体", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainsActivity.this, RgbVideoIdentityActivity.class);
            intent.putExtra("group_id", groupId);
            startActivity(intent);
        } else if (type == LivenessSettingActivity.TYPE_RGB_DEPTH_LIVENSS) {
            Toast.makeText(this, "当前活体策略：双目RGB+Depth活体", Toast.LENGTH_LONG).show();
            int cameraType = PreferencesUtil.getInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBEC);
            Intent intent = null;
            if (cameraType == GlobalSet.ORBBECPRO) {
                intent = new Intent(MainsActivity.this, OrbbecProVideoIdentifyActivity.class);
            } else if (cameraType == GlobalSet.ORBBECPROS1) {
                intent = new Intent(MainsActivity.this, OrbbecProVideoIdentifyActivity.class);
            } else if (cameraType == GlobalSet.ORBBECPRODABAI) {
                intent = new Intent(MainsActivity.this, OrbbecProVideoIdentifyActivity.class);
            } else if (cameraType == GlobalSet.ORBBECPRODEEYEA) {
                intent = new Intent(MainsActivity.this, OrbbecProVideoIdentifyActivity.class);
            } else if (cameraType == GlobalSet.ORBBECATLAS) {
                intent = new Intent(MainsActivity.this, OrbbecProVideoIdentifyActivity.class);
            }
            if (intent != null) {
                intent.putExtra("group_id", groupId);
                startActivity(intent);
            }
        }
    }


    private void livnessTypeTip() {
        int type = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity
                .TYPE_NO_LIVENSS);

        if (type == LivenessSettingActivity.TYPE_NO_LIVENSS) {
            Toast.makeText(this, "当前活体策略：无活体, 请选用普通USB摄像头", Toast.LENGTH_LONG).show();
        } else if (type == LivenessSettingActivity.TYPE_RGB_LIVENSS) {
            Toast.makeText(this, "当前活体策略：单目RGB活体, 请选用普通USB摄像头", Toast.LENGTH_LONG).show();
        } else if (type == LivenessSettingActivity.TYPE_RGB_IR_LIVENSS) {
            Toast.makeText(this, "当前活体策略：双目RGB+IR活体, 请选用RGB+IR摄像头",
                    Toast.LENGTH_LONG).show();
        } else if (type == LivenessSettingActivity.TYPE_RGB_DEPTH_LIVENSS) {
            Toast.makeText(this, "当前活体策略：双目RGB+Depth活体，请选用RGB+Depth摄像头", Toast.LENGTH_LONG).show();
        }
    }

    private void toast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainsActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
