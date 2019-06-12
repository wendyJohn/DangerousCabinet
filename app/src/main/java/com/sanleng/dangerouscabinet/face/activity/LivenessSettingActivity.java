/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.sanleng.dangerouscabinet.face.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.baidu.aip.utils.GlobalSet;
import com.baidu.aip.utils.PreferencesUtil;
import com.sanleng.dangerouscabinet.R;

/**
 * @Time: 2018/11/12
 * @Author: v_chaixiaogang
 * @Description: 活体模式设置页面
 */
public class LivenessSettingActivity extends Activity implements View.OnClickListener {

    public static final int TYPE_NO_LIVENSS = 1;
    public static final int TYPE_RGB_LIVENSS = 2;
    public static final int TYPE_RGB_IR_LIVENSS = 3;
    public static final int TYPE_RGB_DEPTH_LIVENSS = 4;
    public static final int TYPE_RGB_IR_DEPTH_LIVENSS = 5;
    public static final String TYPE_LIVENSS = "TYPE_LIVENSS";
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioButton radioButton5;
    private RadioGroup livenessRg;
    private Button confirmBtn;
    private int livenessType = TYPE_NO_LIVENSS;

    private RadioGroup rgCamera;
    private RadioButton rbOrbbec;
    private RadioButton rbIminect;
    private RadioButton rbOrbbecPro;
    private RadioButton rbOrbbecPros1;
    private RadioButton rbOrbbecAstraDabai;
    private RadioButton rbOrbbecAstraDeeyea;
    private RadioButton rbOrbbecAtlas;

    private Button previewDisplayButton;

    private LinearLayout linearCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liveness_setting_layout);
        findView();

        int livenessType = PreferencesUtil.getInt(TYPE_LIVENSS, TYPE_NO_LIVENSS);
        int cameraType = PreferencesUtil.getInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBEC);
        defaultLiveness(livenessType);
        defaultCamera(cameraType);

        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_DEPTH_LIVENSS);
        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECATLAS);

    }

    @SuppressLint("WrongViewCast")
    private void findView() {
        linearCamera = findViewById(R.id.linear_camera);
        radioButton1 = (RadioButton) findViewById(R.id.no_liveness_rb);
        radioButton2 = (RadioButton) findViewById(R.id.rgb_liveness_rb);
        radioButton3 = (RadioButton) findViewById(R.id.rgb_depth_liveness_rb);
        radioButton4 = (RadioButton) findViewById(R.id.rgb_ir_liveness_rb);
        radioButton5 = (RadioButton) findViewById(R.id.rgb_ir_depth_liveness_rb);
        previewDisplayButton = (Button) findViewById(R.id.previewdisplay);
        previewDisplayButton.setOnClickListener(this);
        livenessRg = (RadioGroup) findViewById(R.id.liveness_rg);
        confirmBtn = (Button) findViewById(R.id.confirm_btn);
        confirmBtn.setOnClickListener(this);
        rgCamera = findViewById(R.id.rg_camera);
        rbOrbbec = findViewById(R.id.rb_orbbec);
        rbIminect = findViewById(R.id.rb_iminect);
        rbOrbbecPro = findViewById(R.id.rb_orbbec_pro);
        rbOrbbecPros1 = findViewById(R.id.rb_orbbec_pro_s1);
        rbOrbbecAstraDabai = findViewById(R.id.rb_orbbec_astra_dabai);
        rbOrbbecAstraDeeyea = findViewById(R.id.rb_orbbec_astra_deeyea);
        rbOrbbecAtlas = findViewById(R.id.rb_orbbec_atlas);

        livenessRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup rg, int checkedId) {
                switch (checkedId) {
                    case R.id.no_liveness_rb:
                        linearCamera.setVisibility(View.GONE);
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_NO_LIVENSS);
                        break;
                    case R.id.rgb_liveness_rb:
                        linearCamera.setVisibility(View.GONE);
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_LIVENSS);
                        break;
                    case R.id.rgb_ir_liveness_rb:
                        linearCamera.setVisibility(View.GONE);
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_IR_LIVENSS);
                        break;
                    case R.id.rgb_depth_liveness_rb:
                        linearCamera.setVisibility(View.VISIBLE);
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_DEPTH_LIVENSS);
                        break;
                    case R.id.rgb_ir_depth_liveness_rb:
                        linearCamera.setVisibility(View.GONE);
                        PreferencesUtil.putInt(TYPE_LIVENSS, TYPE_RGB_IR_DEPTH_LIVENSS);
                        break;
                    default:
                        break;
                }
            }
        });
        rgCamera.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                switch (i) {
                    case R.id.rb_orbbec:
                        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBEC);
                        break;
                    case R.id.rb_iminect:
                        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.IMIMECT);
                        break;
                    case R.id.rb_orbbec_pro:
                        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECPRO);
                        break;
                    case R.id.rb_orbbec_pro_s1:
                        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECPROS1);
                        break;
                    case R.id.rb_orbbec_astra_dabai:
                        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECPRODABAI);
                        break;
                    case R.id.rb_orbbec_astra_deeyea:
                        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECPRODEEYEA);
                        break;
                    case R.id.rb_orbbec_atlas:
                        PreferencesUtil.putInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBECATLAS);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void defaultLiveness(int livenessType) {
        if (livenessType == TYPE_NO_LIVENSS) {
            radioButton1.setChecked(true);
        } else if (livenessType == TYPE_RGB_LIVENSS) {
            radioButton2.setChecked(true);
        } else if (livenessType == TYPE_RGB_DEPTH_LIVENSS) {
            radioButton3.setChecked(true);
        } else if (livenessType == TYPE_RGB_IR_LIVENSS) {
            radioButton4.setChecked(true);
        } else if (livenessType == TYPE_RGB_IR_DEPTH_LIVENSS) {
            radioButton5.setChecked(true);
        }
    }

    private void defaultCamera(int cameraType) {
        if (cameraType == GlobalSet.ORBBEC) {
            rbOrbbec.setChecked(true);
        } else if (cameraType == GlobalSet.IMIMECT) {
            rbIminect.setChecked(true);
        } else if (cameraType == GlobalSet.ORBBECPRO) {
            rbOrbbecPro.setChecked(true);
        } else if (cameraType == GlobalSet.ORBBECPROS1) {
            rbOrbbecPros1.setChecked(true);
        } else if (cameraType == GlobalSet.ORBBECPRODABAI) {
            rbOrbbecAstraDabai.setChecked(true);
        } else if (cameraType == GlobalSet.ORBBECPRODEEYEA) {
            rbOrbbecAstraDeeyea.setChecked(true);
        } else if (cameraType == GlobalSet.ORBBECATLAS) {
            rbOrbbecAtlas.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == confirmBtn) {
            finish();
        } else if (v == previewDisplayButton) {
            Intent intent = new Intent(this, FacePreviewImageSetActivity.class);
            startActivity(intent);
        }
    }
}
