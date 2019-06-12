/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.sanleng.dangerouscabinet.face.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.ARGBImg;
import com.baidu.aip.entity.Feature;
import com.baidu.aip.entity.Group;
import com.baidu.aip.entity.User;
import com.baidu.aip.manager.FaceEnvironment;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.FeatureUtils;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.PreferencesUtil;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.face.utils.GlobalFaceTypeModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

/**
 * @Time: 2018/11/12
 * @Author: v_chaixiaogang
 * @Description: 批量导入人脸到人脸库中
 */
public class BatchImportActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "chaixiaogang";

    private EditText batchPathEt;
    // private EditText groupIdEt;
    private Spinner groupIdSpinner;
    private TextView progressTv;
    private Button batchImportBtn;
    private ImageView importingIv;
    private List<String> groupIds = new ArrayList<>();
    private String groupId = "";
    private volatile boolean importing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_import);

        findView();
    }

    private void findView() {
        batchPathEt = (EditText) findViewById(R.id.path_et);
        // groupIdEt = (EditText) findViewById(R.id.group_id_et);
        groupIdSpinner = (Spinner) findViewById(R.id.spinner);
        progressTv = (TextView) findViewById(R.id.progress_tv);
        batchImportBtn = (Button) findViewById(R.id.batch_import_iv);
        importingIv = (ImageView) findViewById(R.id.importing_iv);
        batchImportBtn.setOnClickListener(this);

        groupIdSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (arg2 < groupIds.size()) {
                    groupId = groupIds.get(arg2);
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        //  batchPathEt.setText("v5_105");
        // groupIdEt.setText("face");
        init();
    }

    private void init() {
        List<Group> groupList = DBManager.getInstance().queryGroups(0, 1000);

        for (Group group : groupList) {
            groupIds.add(group.getGroupId());
        }
        if (groupIds.size() > 0) {
            groupId = groupIds.get(0);
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupIds);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupIdSpinner.setAdapter(arrayAdapter);
    }

    PowerManager.WakeLock wl;

    @Override
    protected void onResume() {
        super.onResume();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PowerManager");
//        wl.acquire();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (wl != null) {
//            wl.acquire();
//        }
    }

    @Override
    public void onClick(View v) {
        if (v == batchImportBtn) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                    .PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
            if (!importing) {
                batchImportBtn.setText("停止导入");
                batchImoprt();
            } else {
                importing = false;
                batchImportBtn.setText("开始导入");
            }
        }
    }

    private void batchImoprt() {
        String batchPath = batchPathEt.getText().toString();
        // String groupId = groupIdEt.getText().toString();

        if (TextUtils.isEmpty(batchPath)) {
            Toast.makeText(this, "批量导入的文件名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(groupId)) {
            Toast.makeText(this, "导入到分组group_id不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(batchPath)) {
            File batchfaceDir = FileUitls.getBatchFaceDirectory(batchPath);
            String[] files = batchfaceDir.list();
            if (files == null || files.length == 0) {
                Toast.makeText(this, "导入数据的文件夹没有数据", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDisplay("总人脸数:" + files.length + ", 完成：0" + " 成功：0" + " 失败：0");
            asyncImport(files, batchfaceDir, groupId);
            //syncImport(files, batchfaceDir, groupId);
        }
    }

    int totalCount = 0;
    int finishCount = 0;
    int successCount = 0;
    int failCount = 0;

    private void asyncImport(final String[] files, final File batchfaceDir, final String groupId) {
        final int type = PreferencesUtil.getInt(GlobalFaceTypeModel.TYPE_MODEL, GlobalFaceTypeModel.RECOGNIZE_LIVE);
        totalCount = files.length;
        finishCount = 0;
        successCount = 0;
        failCount = 0;
        importing = true;
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                FaceEnvironment faceEnvironment = new FaceEnvironment();
                faceEnvironment.setDetectInterval(0);
                faceEnvironment.setTrackInterval(0);
                FaceSDKManager.getInstance().getFaceDetector().loadConfig(faceEnvironment);
                long startTime = System.currentTimeMillis();
                for (int i = 0; i < files.length; i++) {
                    if (!importing) {
                        break;
                    }
                    String file = files[i];
                    boolean success = false;
                    File facePath = new File(batchfaceDir, file);
                    if (facePath.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(facePath.getAbsolutePath());
                        ARGBImg argbImg = FeatureUtils.getImageInfo(bitmap);
                        byte[] bytes = new byte[512];
                        float ret = -1;
                        if ((argbImg.width * argbImg.height) >= GlobalFaceTypeModel.pictureSize) {
                            int dstHeight = 480;
                            //  int dstWidth = 640;
                            float scalH = ((float) dstHeight) / bitmap.getHeight();
                            int dstWidth = (int) (bitmap.getWidth() * scalH);
                            int[] dstData = new int[dstHeight * dstWidth];
                            FaceSDKManager.getInstance().getFaceDetector().imgResize(argbImg.data, argbImg.height,
                                    argbImg.width, dstData, dstHeight, dstWidth, 0);
                            if (type == GlobalFaceTypeModel.RECOGNIZE_LIVE) {
                                ret = FaceApi.getInstance().extractVisFeature(dstData, dstHeight, dstWidth, bytes);
                                Log.e("chaixiaogang", ret + "live");
                            } else if (type == GlobalFaceTypeModel.RECOGNIZE_ID_PHOTO) {
                                ret = FaceApi.getInstance().extractIdPhotoFeature(dstData, dstHeight, dstWidth, bytes);
                                Log.e("chaixiaogang", ret + "idcard");
                            }
                        } else {
                            if (type == GlobalFaceTypeModel.RECOGNIZE_LIVE) {
                                ret = FaceApi.getInstance().extractVisFeature(argbImg, bytes);
                                Log.e(TAG, ret + "live");
                            } else if (type == GlobalFaceTypeModel.RECOGNIZE_ID_PHOTO) {
                                ret = FaceApi.getInstance().extractIdPhotoFeature(argbImg, bytes);
                                Log.e(TAG, ret + "idcard");
                            }
                        }
                        if (ret == -1) {
                            progressDisplay("未检测到人脸，可能原因：人脸太小（必须大于最小检测人脸minFaceSize），"
                                    + "或者人脸角度太大，人脸不是朝上");
                        } else if (ret == 128) {
                            Feature feature = new Feature();
                            feature.setGroupId(groupId);
                            final String uid = UUID.randomUUID().toString();
                            feature.setUserId(uid);
                            feature.setFeature(bytes);
                            feature.setImageName(file);
                            User user = new User();
                            user.setUserId(uid);
                            user.setUserInfo(uid);
                            user.setGroupId(groupId);
                            user.getFeatureList().add(feature);
                            if (FaceApi.getInstance().userAdd(user)) {
                                success = true;
                                File faceDir = FileUitls.getFaceDirectory();
                                if (faceDir != null) {
                                    File saveFacePath = new File(faceDir, file);
                                    if (FileUitls.saveFile(saveFacePath, bitmap)) {
                                        // facePath.delete();
                                        Log.e(TAG, "facePath");
                                    }
                                }
                            }
                        } else if (ret == -1) {
                            progressDisplay("抽取特征失败");
                        } else {
                            progressDisplay("未检测到人脸");
                        }
                    }
                    if (success) {
                        successCount++;
                    } else {
                        failCount++;
                        Log.i("chaixiaogang", "失败图片:" + file);
                    }
                    finishCount++;
                    progressDisplay("总人脸数:" + totalCount + ", 完成：" +
                            finishCount + " 成功:" + successCount + " 失败:" + failCount);
                }
                Log.i("chaixiaogang", "import total time--" + (System.currentTimeMillis() - startTime));
                // 还原检测参数配置
                faceEnvironment = FaceSDKManager.getInstance().getFaceEnvironmentConfig();
                faceEnvironment.setDetectInterval(1000);
                faceEnvironment.setTrackInterval(500);
                FaceSDKManager.getInstance().getFaceDetector().loadConfig(faceEnvironment);
                importFinish();
            }
        });
    }


    private void pictureResize(Bitmap bitmap, int index) {
        int dstHeight = 100;
        int dstWidth = 100;
        int[] dstData = new int[dstHeight * dstWidth];
        ARGBImg argbImg = FeatureUtils.getImageInfo(bitmap);
        FaceSDKManager.getInstance().getFaceDetector().imgResize(argbImg.data, argbImg.height,
                argbImg.width, dstData, dstHeight, dstWidth, 0);
    }

    private void progressDisplay(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressTv.setText(text);
            }
        });
    }

//    private void progressDisplay(final Bitmap bitmap) {
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                importingIv.setImageBitmap(bitmap);
//            }
//        });
//    }

    private void importFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                batchImportBtn.setText("开始导入");
                importing = false;
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper());
}
