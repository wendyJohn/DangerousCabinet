/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.sanleng.dangerouscabinet.face.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.api.FaceApi;
import com.baidu.aip.callback.ILivenessCallBack;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Feature;
import com.baidu.aip.entity.IdentifyRet;
import com.baidu.aip.entity.LivenessModel;
import com.baidu.aip.entity.User;
import com.baidu.aip.manager.FaceEnvironment;
import com.baidu.aip.manager.FaceLiveness;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.GlobalSet;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.model.FaceInfo;
//import com.githang.stepview.StepView;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.hb.dialog.myDialog.MyImageMsgDialog;
import com.orbbec.view.OpenGLView;
import com.sanleng.dangerouscabinet.MainActivity;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.face.utils.GlobalFaceTypeModel;
import com.sanleng.dangerouscabinet.fid.entity.Lock;
import com.sanleng.dangerouscabinet.ui.activity.OperationActivity;
import com.sanleng.dangerouscabinet.ui.view.StepView;
import com.sanleng.dangerouscabinet.utils.PreferenceUtils;
import com.sanleng.dangerouscabinet.utils.TTSUtils;
import com.wang.avi.AVLoadingIndicatorView;

import org.openni.Device;
import org.openni.DeviceInfo;
import org.openni.ImageRegistrationMode;
import org.openni.OpenNI;
import org.openni.PixelFormat;
import org.openni.SensorType;
import org.openni.VideoMode;
import org.openni.VideoStream;
import org.openni.android.OpenNIHelper;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * @Time: 2019/1/11
 * @Author: v_chaixiaogang
 * @Description: 奥比中光Pro镜头RGB+DEPTH 视频VS人脸库
 */
public class OrbbecProVideoIdentifyActivity extends Activity implements OpenNIHelper.DeviceOpenListener,
        ActivityCompat.OnRequestPermissionsResultCallback, ILivenessCallBack, View.OnClickListener {

    private static final int MSG_WHAT = 5;
    private static final String MSG_KEY = "YUV";
    private static final int FEATURE_DATAS_UNREADY = 1;
    private static final int IDENTITY_IDLE = 2;
    private static final int IDENTITYING = 3;

    ImageView mImageView;
    private TextView mPrompt;
    private Activity mContext;
    private final int CREATE_OPENNI = 0x001;
    private boolean m_InitOk = false;
    private boolean m_pause = false;

    private TextView mMessageTv;

    private ImageView testView;
    private TextView userOfMaxSocre;

    private ImageView matchAvatorIv;
    private TextView matchUserTv;
    private TextView scoreTv;
    private TextView facesetsCountTv;
    private TextView detectDurationTv;
    private TextView rgbLivenssDurationTv;
    private TextView rgbLivenessScoreTv;
    private TextView depthLivenssDurationTv;
    private TextView depthLivenessScoreTv;
    private TextView featureDurationTv;

    private OpenGLView mDepthGLView;
//    private OpenGLView mRgbGLView;

    private boolean initOk = false;
    private Device device;
    private Thread thread;
    private OpenNIHelper mOpenNIHelper;
    private VideoStream depthStream;
//    private VideoStream rgbStream;

    private int mWidth = com.orbbec.utils.GlobalDef.RESOLUTION_X;
    private int mHeight = com.orbbec.utils.GlobalDef.RESOLUTION_Y;

    private int mDepthWidth = 640;
    private int mDepthHeight = 400;

    private final int DEPTH_NEED_PERMISSION = 33;
    private Object sync = new Object();
    private boolean exit = false;

    private String groupId = "";

    private volatile int identityStatus = FEATURE_DATAS_UNREADY;
    private String userIdOfMaxScore = "";
    private float maxScore = 0;

    //uvcTest
//    private final Object mSync = new Object();
    private TextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private Matrix matrix = new Matrix();

    // textureView用于绘制人脸框等。
    private TextureView textureView;

    private MyHandler mHandler;
    private static int cameraType;

    private Camera mCamera;
    private int mCameraNum;
    private int[] rgbData = null;
    private byte[] depthData;
    private boolean mSurfaceCreated = false;

    private AVLoadingIndicatorView avi;
    private LinearLayout youte;
    public CountDownTimer countdowntimer;
    private long advertisingTime = 60 * 1000;//60S退出人脸识别
    private TextView countdown;
    private TextView back;
    private TextView register;
    private Button nextstep;
    private StepView mSV1;
    private DBHelpers mOpenHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orbbec_pro_video_identity);
        FaceSDKManager.getInstance().getFaceLiveness().setLivenessCallBack(this);
        cameraType = PreferencesUtil.getInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBEC);
        findView();
        initPreview();
        mContext = this;
        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "FaceOne", "暂无人脸名称");
        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "FaceTwo", "暂无人脸名称");
        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "chioUserName1", "1号暂无名称");
        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "chioUserCode1", "1号暂无ID");
        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "chioUserName2", "2号暂无名称");
        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "chioUserCode2", "2号暂无ID");
        //registerHomeListener();
        Intent intent = getIntent();
        if (intent != null) {
            groupId = intent.getStringExtra("group_id");
        }
        TTSUtils.getInstance().speak("请正视摄像头位置");
        DBManager.getInstance().init(this);
        loadFeature2Memery();
        hideBottomUIMenu();
    }

    private void findView() {
        mSV1 = (StepView) findViewById(R.id.view1);
        mSV1.setBottomText(new String[]{"请识别第一张人脸","请识别第二张人脸","识别成功"});
        mSV1.setCurrentStep(1);  //设置当前进度
        mOpenHelper = new DBHelpers(OrbbecProVideoIdentifyActivity.this);

        nextstep = findViewById(R.id.nextstep);
        textureView = findViewById(R.id.texture_view);
        textureView.setOpaque(false);
        mDepthGLView = (OpenGLView) findViewById(R.id.depthGlView);
//       mRgbGLView = (OpenGLView) findViewById(R.id.rgbGlView);
        mTextureView = (TextureView) findViewById(R.id.camera_surface_view);
        mTextureView.setOpaque(false);
        mTextureView.setKeepScreenOn(true);
        mMessageTv = (TextView) findViewById(R.id.message);
        matchAvatorIv = (ImageView) findViewById(R.id.match_avator_iv);
        matchUserTv = (TextView) findViewById(R.id.match_user_tv);
        scoreTv = (TextView) findViewById(R.id.score_tv);
        facesetsCountTv = (TextView) findViewById(R.id.facesets_count_tv);
        detectDurationTv = (TextView) findViewById(R.id.detect_duration_tv);
        rgbLivenssDurationTv = (TextView) findViewById(R.id.rgb_liveness_duration_tv);
        rgbLivenessScoreTv = (TextView) findViewById(R.id.rgb_liveness_score_tv);
        depthLivenssDurationTv = (TextView) findViewById(R.id.depth_liveness_duration_tv);
        depthLivenessScoreTv = (TextView) findViewById(R.id.depth_liveness_score_tv);
        featureDurationTv = (TextView) findViewById(R.id.feature_duration_tv);

        mOpenNIHelper = new OpenNIHelper(this);
        mOpenNIHelper.requestDeviceOpen(this);

        register = findViewById(R.id.register);
        back = findViewById(R.id.back);
        avi = findViewById(R.id.avi);
        countdown = findViewById(R.id.countdown);
        back.setOnClickListener(this);
        nextstep.setOnClickListener(this);

    }



    private void init(UsbDevice device) {
        OpenNI.setLogAndroidOutput(false);
        OpenNI.setLogMinSeverity(0);
        OpenNI.initialize();

        List<DeviceInfo> opennilist = OpenNI.enumerateDevices();
        if (opennilist.size() <= 0) {
            Toast.makeText(this, " openni enumerateDevices 0 devices", Toast.LENGTH_LONG).show();
            return;
        }

        this.device = null;
        //Find device ID
        for (int i = 0; i < opennilist.size(); i++) {
            if (opennilist.get(i).getUsbProductId() == device.getProductId()) {
                if (cameraType == GlobalSet.ORBBECPRO || cameraType == GlobalSet.ORBBECPROS1) {
                    if (device.getProductId() == 1555 || device.getProductId() == 1547
                            || device.getProductId() == 1550) {
                        Toast.makeText(this, "当前模式跟镜头不匹配", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if (cameraType == GlobalSet.ORBBECPRODEEYEA) {
                    if (device.getProductId() == 1550) {
                        Toast.makeText(this, "当前模式跟镜头不匹配", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                this.device = Device.open();
                break;
            }
        }

        if (this.device == null) {
            Toast.makeText(this, " openni open devices failed: " + device.getDeviceName(),
                    Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("chaixiaogang", "onStart:");
        mHandler = new MyHandler(this);
        startTime();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_WHAT);
            mHandler = null;
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            Log.e("chaixiaogang", "onPause----destroy camera");
        }
        exit = true;
        if (initOk) {

            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            long time1 = System.currentTimeMillis();
            if (depthStream != null) {
                depthStream.stop();
            }
            if (device != null) {
                device.close();
            }
        }
        if (mOpenNIHelper != null) {
            mOpenNIHelper.shutdown();
        }
        finish();

        //当activity不在前台是停止定时
        if (countdowntimer != null) {
            countdowntimer.cancel();
            System.out.println("==================" + "取消定时");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_WHAT);
            mHandler = null;
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    @Override
    public void onDestroy() {
        Log.v("chaixiaogang", "onDestroy:");
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_WHAT);
            mHandler = null;
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        //销毁时停止定时
        if (countdowntimer != null) {
            countdowntimer.cancel();
        }
    }

    private void initPreview() {
        mCameraNum = Camera.getNumberOfCameras();
        if (cameraType == GlobalSet.ORBBECPRO || cameraType == GlobalSet.ORBBECPRODABAI) {
            mTextureView.setRotationY(180); // 旋转90度
        }
        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture texture, int i, int i1) {
                Log.e("chaixiaogang", "onSurfaceTextureAvailable");
                mSurfaceTexture = texture;
                mSurfaceCreated = true;
                if (mSurfaceCreated) {
                    Log.e("chaixiaogang", "have create surfacetexture");
                    initCamera();
                    Log.e("chaixiaogang", "camera start");
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int i, int i1) {
                Log.e("chaixiaogang", "onSurfaceTextureSizeChanged");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                Log.e("chaixiaogang", "onSurfaceTextureDestroyed");
                //关闭、释放Camera资源
                try {
                    if (null != mCamera) {
                        Log.e("chaixiaogang", "camera destroy");
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                    }
                    mSurfaceCreated = false;
                    return true;
                } catch (Exception e) {
                    Log.e("chaixiaogang", "ERR_MSG", e);
                }
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                Log.e("chaixiaogang", "onSurfaceTextureUpdated");
            }
        });
    }

    private void initCamera() {
        try {
            if (mCamera == null) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                mCamera = Camera.open(0);
                Log.e("chaixiaogang", "initCamera---open camera");
            }
            Camera.Parameters params = mCamera.getParameters();
            params.setPreviewSize(mWidth, mHeight);
            mCamera.setParameters(params);
            try {
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        if (mHandler != null) {
                            mHandler.removeMessages(MSG_WHAT);
                            Message message = mHandler.obtainMessage();
                            message.getData().putByteArray(MSG_KEY, bytes);
                            message.what = MSG_WHAT;
                            mHandler.sendMessage(message);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("chaixiaogang", e.getMessage());
            }
        } catch (RuntimeException e) {
            Log.e("chaixiaogang", e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                Intent intent = new Intent(OrbbecProVideoIdentifyActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
                break;
            case R.id.nextstep:
                String stra = PreferenceUtils.getString(OrbbecProVideoIdentifyActivity.this, "FaceOne");
                String strb = PreferenceUtils.getString(OrbbecProVideoIdentifyActivity.this, "FaceTwo");
                if (stra.equals("暂无人脸名称") || strb.equals("暂无人脸名称")) {
                    TTSUtils.getInstance().speak("请先完成双人人脸认证");
                } else {
                    TTSUtils.getInstance().speak("正在开锁，请稍后");
                    MyImageMsgDialog myImageMsgDialog = new MyImageMsgDialog(OrbbecProVideoIdentifyActivity.this).builder()
                            .setImageLogo(getResources().getDrawable(R.mipmap.ic_launcher))
                            .setMsg("双人认证成功，开锁中...");
                    ImageView logoImg = myImageMsgDialog.getLogoImg();
                    logoImg.setImageResource(R.drawable.connect_animation);
                    AnimationDrawable connectAnimation = (AnimationDrawable) logoImg.getDrawable();
                    connectAnimation.start();
                    myImageMsgDialog.show();
                    //此处进行开锁
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            // 等待2000毫秒后销毁此页面，并开门
                            Lock.getInstance().sendA();
                            mOpenHelper.deleterecords();
                            Intent intent = new Intent(OrbbecProVideoIdentifyActivity.this, OperationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 2000);
                }
                break;
        }
    }

    private class MyHandler extends Handler {
        public MyHandler(OrbbecProVideoIdentifyActivity pActivity) {

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT) {
                byte[] data = msg.getData().getByteArray(MSG_KEY);
                dealRgb(data);
            }
        }
    }

    private void dealRgb(byte[] data) {
        if (rgbData == null) {
            int[] argb = new int[mWidth * mHeight];
            if (cameraType == GlobalSet.ORBBECPRO || cameraType == GlobalSet.ORBBECPRODABAI) {
                FaceSDKManager.getInstance().getFaceDetector().yuvToARGB(data, mWidth,
                        mHeight, argb, 0, 1);
            } else {
                FaceSDKManager.getInstance().getFaceDetector().yuvToARGB(data, mWidth,
                        mHeight, argb, 0, 0);
            }
            rgbData = argb;
        }
        checkLiving();
    }

    private void checkLiving() {
        if (rgbData != null && depthData != null) {
            FaceSDKManager.getInstance().getFaceLiveness().setRgbInt(rgbData);
            FaceSDKManager.getInstance().getFaceLiveness().setDepthData(depthData);
            FaceSDKManager.getInstance().getFaceLiveness().livenessCheck(mWidth,
                    mHeight, 0X0101);
            rgbData = null;
            depthData = null;
        }
    }

    @Override
    public void onCallback(LivenessModel livenessModel) {
        checkResult(livenessModel);
    }

    @Override
    public void onTip(int code, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMessageTv.setText(msg);
            }
        });
    }

    @Override
    public void onCanvasRectCallback(LivenessModel livenessModel) {
        showFrame(livenessModel);
    }

    public Bitmap cameraByte2Bitmap(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }

    @Override
    public void onDeviceOpened(UsbDevice device) {
        init(device);

        depthStream = VideoStream.create(this.device, SensorType.DEPTH);
        if (depthStream != null) {
            List<VideoMode> mVideoModes = depthStream.getSensorInfo().getSupportedVideoModes();

            for (VideoMode mode : mVideoModes) {
                int x = mode.getResolutionX();
                int y = mode.getResolutionY();
                int fps = mode.getFps();
                if (cameraType == GlobalSet.ORBBECPRODABAI || cameraType == GlobalSet.ORBBECPRODEEYEA) {
                    if (x == mDepthWidth && y == mDepthHeight && mode.getPixelFormat() == PixelFormat.DEPTH_1_MM) {
                        depthStream.setVideoMode(mode);
                        this.device.setImageRegistrationMode(ImageRegistrationMode.DEPTH_TO_COLOR);
                        break;
                    }
                } else if (cameraType == GlobalSet.ORBBECATLAS) {
                    if (x == mDepthHeight && y == mDepthWidth && mode.getPixelFormat() == PixelFormat.DEPTH_1_MM) {
                        depthStream.setVideoMode(mode);
                        this.device.setImageRegistrationMode(ImageRegistrationMode.DEPTH_TO_COLOR);
                        break;
                    }
                } else {
                    if (x == mWidth && y == mHeight && mode.getPixelFormat() == PixelFormat.DEPTH_1_MM) {
                        depthStream.setVideoMode(mode);
                        break;
                    }
                }

            }
            startThread();
        }
    }

    @Override
    public void onDeviceOpenFailed(String msg) {
        showAlertAndExit("Open Device failed: " + msg);
    }

    void startThread() {
        initOk = true;
        thread = new Thread() {
            @Override
            public void run() {

                List<VideoStream> streams = new ArrayList<VideoStream>();
                streams.add(depthStream);
                depthStream.start();

                while (!exit) {
                    try {
                        OpenNI.waitForAnyStream(streams, 2000);
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        continue;
                    }

                    synchronized (sync) {
                        mDepthGLView.update(depthStream, com.orbbec.utils.GlobalDef.TYPE_DEPTH);
                        ByteBuffer depthByteBuf = depthStream.readFrame().getData();
                        if (depthByteBuf != null) {
                            int depthLen = depthByteBuf.remaining();
                            byte[] depthByte = new byte[depthLen];
                            depthByteBuf.get(depthByte);
                            depthData = depthByte;
                            checkLiving();
                        }
                    }
                }
            }
        };
        thread.start();
    }

    private void showAlertAndExit(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == DEPTH_NEED_PERMISSION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Permission Grant", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkResult(LivenessModel model) {

        if (model == null) {
            clearTip();
            return;
        }
        displayResult(model);
        int type = model.getLiveType();
        boolean livenessSuccess = false;
        // 同一时刻都通过才认为活体通过，开发者也可以根据自己的需求修改策略
        if ((type & FaceLiveness.MASK_RGB) == FaceLiveness.MASK_RGB) {
            livenessSuccess = (model.getRgbLivenessScore() > FaceEnvironment.LIVENESS_RGB_THRESHOLD) ? true : false;
        }
        if ((type & FaceLiveness.MASK_DEPTH) == FaceLiveness.MASK_DEPTH) {
            boolean depthScore = (model.getDepthLivenessScore() > FaceEnvironment.LIVENESS_DEPTH_THRESHOLD) ? true :
                    false;
            if (!depthScore) {
                livenessSuccess = false;
            } else {
                livenessSuccess &= depthScore;
            }
        }

        if (livenessSuccess) {
            asyncIdentity(model.getImageFrame(), model.getFaceInfo());
//            identity(model.getImageFrame(), model.getFaceInfo());
        } else {
            cleanRecognizeInfo();
        }
    }

    private void displayResult(final LivenessModel livenessModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int type = livenessModel.getLiveType();
                detectDurationTv.setText("人脸检测耗时：" + livenessModel.getRgbDetectDuration());
                if ((type & FaceLiveness.MASK_RGB) == FaceLiveness.MASK_RGB) {
                    rgbLivenessScoreTv.setText("RGB活体得分：" + livenessModel.getRgbLivenessScore());
                    rgbLivenssDurationTv.setText("RGB活体耗时：" + livenessModel.getRgbLivenessDuration());
                }
                if ((type & FaceLiveness.MASK_DEPTH) == FaceLiveness.MASK_DEPTH) {
                    depthLivenessScoreTv.setText("Depth活体得分：" + livenessModel.getDepthLivenessScore());
                    depthLivenssDurationTv.setText("Depth活体耗时：" + livenessModel.getDetphtLivenessDuration());
                }
            }

        });
    }


    private ExecutorService es = Executors.newSingleThreadExecutor();

    private void loadFeature2Memery() {
        if (identityStatus != FEATURE_DATAS_UNREADY) {
            return;
        }
        es.submit(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                // android.os.Process.setThreadPriority (-4);
                FaceApi.getInstance().loadFacesFromDB(groupId);
//                toast("人脸数据加载完成，即将开始1：N");
                int count = FaceApi.getInstance().getGroup2Facesets().get(groupId).size();
                displayTip("底库人脸个数：" + count, facesetsCountTv);
                identityStatus = IDENTITY_IDLE;
            }
        });
    }

    private void cleanRecognizeInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                register.setText("人脸识别中...");
                scoreTv.setText("");
                matchUserTv.setText("");
                matchAvatorIv.setImageBitmap(null);
            }
        });
    }

    private void clearTip() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                detectDurationTv.setText("");
                rgbLivenessScoreTv.setText("");
                rgbLivenssDurationTv.setText("");
                depthLivenessScoreTv.setText("");
                depthLivenssDurationTv.setText("");
                featureDurationTv.setText("");
            }
        });
    }

    private void asyncIdentity(final ImageFrame imageFrame, final FaceInfo faceInfo) {
        if (identityStatus != IDENTITY_IDLE) {
            return;
        }

        es.submit(new Runnable() {
            @Override
            public void run() {
                identity(imageFrame, faceInfo);
            }
        });
    }


    private void identity(ImageFrame imageFrame, FaceInfo faceInfo) {


        if (imageFrame == null || faceInfo == null) {
            return;
        }

        float raw = Math.abs(faceInfo.headPose[0]);
        float patch = Math.abs(faceInfo.headPose[1]);
        float roll = Math.abs(faceInfo.headPose[2]);
        // 人脸的三个角度大于20不进行识别
        if (raw > 20 || patch > 20 || roll > 20) {
            return;
        }

        identityStatus = IDENTITYING;

        long starttime = System.currentTimeMillis();
        int[] argb = imageFrame.getArgb();
        int rows = imageFrame.getHeight();
        int cols = imageFrame.getWidth();
        int[] landmarks = faceInfo.landmarks;
        IdentifyRet identifyRet = null;
        int type = PreferencesUtil.getInt(GlobalFaceTypeModel.TYPE_MODEL, GlobalFaceTypeModel.RECOGNIZE_LIVE);
        if (type == GlobalFaceTypeModel.RECOGNIZE_LIVE) {
            identifyRet = FaceApi.getInstance().identity(argb, rows, cols, landmarks, groupId);
        } else if (type == GlobalFaceTypeModel.RECOGNIZE_ID_PHOTO) {
            identifyRet = FaceApi.getInstance().identityForIDPhoto(argb, rows, cols, landmarks, groupId);
        }
        if (identifyRet != null) {
            displayUserOfMaxScore(identifyRet.getUserId(), identifyRet.getScore());
            Log.e("gangzi", identifyRet.getScore() + "");
        }
        identityStatus = IDENTITY_IDLE;
        displayTip("特征抽取对比耗时：" + (System.currentTimeMillis() - starttime), featureDurationTv);
    }

    private void displayUserOfMaxScore(final String userId, final float score) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (score < 80) {
                    register.setText("识别失败" + "\n" + "请确认是否已注册人脸识别");
                    scoreTv.setText("");
                    matchUserTv.setText("");
                    matchAvatorIv.setImageBitmap(null);
                    return;
                }

                if (userIdOfMaxScore.equals(userId)) {
                    if (score < maxScore) {
                        scoreTv.setText("" + score);
                        register.setText("" + score);
                    } else {
                        maxScore = score;
                        register.setText("" + score);
                        scoreTv.setText("" + score);
                    }
                    if (matchUserTv.getText().toString().length() > 0) {
                        return;
                    }
                } else {
                    userIdOfMaxScore = userId;
                    maxScore = score;
                }

                scoreTv.setText("" + score);
                register.setText("" + score);
                User user = FaceApi.getInstance().getUserInfo(groupId, userId);
                if (user == null) {
                    return;
                }
                matchUserTv.setText(user.getUserInfo());
                List<Feature> featureList = user.getFeatureList();
                if (featureList != null && featureList.size() > 0) {
                    File faceDir = FileUitls.getFaceDirectory();
                    if (faceDir != null && faceDir.exists()) {
                        File file = new File(faceDir, featureList.get(0).getImageName());
                        if (file != null && file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            matchAvatorIv.setImageBitmap(bitmap);
                            System.out.println("==========用户ID==========" + user.getUserId());
                            //第一个人脸名称
                            String faceone = PreferenceUtils.getString(OrbbecProVideoIdentifyActivity.this, "FaceOne");
                            if (faceone.equals("暂无人脸名称")) {
                                PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "FaceOne", user.getUserInfo());
                                PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "chioUserName1", user.getUserInfo());
                                PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "chioUserCode1", user.getUserId());
                                TTSUtils.getInstance().speak("识别成功，请识别第二张人脸");
                                new SVProgressHUD(OrbbecProVideoIdentifyActivity.this).showSuccessWithStatus("识别成功");
                                mSV1.setBottomText(new String[]{PreferenceUtils.getString(OrbbecProVideoIdentifyActivity.this, "FaceOne"),"请识别第二张人脸","识别成功"});
                                mSV1.setCurrentStep(2);  //设置当前进度
                            } else {
                                String facetwo = PreferenceUtils.getString(OrbbecProVideoIdentifyActivity.this, "FaceTwo");
                                //第二个人脸名称
                                if (!user.getUserInfo().equals(PreferenceUtils.getString(OrbbecProVideoIdentifyActivity.this, "FaceOne"))) {
                                    if (facetwo.equals("暂无人脸名称")) {
                                        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "FaceTwo", user.getUserInfo());
                                        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "chioUserName2", user.getUserInfo());
                                        PreferenceUtils.setString(OrbbecProVideoIdentifyActivity.this, "chioUserCode2", user.getUserId());
                                        TTSUtils.getInstance().speak("识别成功，识别认证通过可进行下一步操作");
                                        new SVProgressHUD(OrbbecProVideoIdentifyActivity.this).showSuccessWithStatus("识别成功");
                                        mSV1.setBottomText(new String[]{PreferenceUtils.getString(OrbbecProVideoIdentifyActivity.this, "FaceOne"),PreferenceUtils.getString(OrbbecProVideoIdentifyActivity.this, "FaceTwo"),"识别成功"});
                                        mSV1.setCurrentStep(3);  //设置当前进度

                                    }
                                }
                            }


                        }
                    }
                }
            }
        });
    }

//    private void registerHomeListener() {
//        mHomeListener = new HomeKeyListener(this);
//        mHomeListener.setOnHomePressedListener(new HomeKeyListener.OnHomePressedListener() {
//
//                    @Override
//                    public void onHomePressed() {
//                        // TODO
//                        finish();
//                    }
//
//                    @Override
//                    public void onHomeLongPressed() {
//                        // TODO
//                    }
//                });
//        mHomeListener.startWatch();
//    }

//    private void unRegisterHomeListener() {
//        if (mHomeListener != null) {
//            mHomeListener.stopWatch();
//        }
//    }


    private void displayTip(final String text, final TextView textView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
    }

    private void toast(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OrbbecProVideoIdentifyActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private boolean shouldUpload = true;
//
//    // 上传一帧至服务器进行，人脸识别。
//    private void upload(final Bitmap face) {
//
//        shouldUpload = false;
//        try {
//            final File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
//            // 人脸识别不需要整张图片。可以对人脸区别进行裁剪。减少流量消耗和，网络传输占用的时间消耗。
//            ImageUtils.resize(face, file, 200, 200);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

    private Paint paint = new Paint();

    {
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(30);
    }

    RectF rectF = new RectF();

    /**
     * 绘制人脸框。
     */
    private void showFrame(LivenessModel model) {
        canvasRgbRect(model);
    }

    private void canvasRgbRect(LivenessModel model) {
        Canvas canvas = textureView.lockCanvas();
        if (canvas == null) {
            textureView.unlockCanvasAndPost(canvas);
            return;
        }
        if (model == null) {
            // 清空canvas
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            textureView.unlockCanvasAndPost(canvas);
            return;
        }
        FaceInfo[] faceInfos = model.getTrackFaceInfo();
        ImageFrame imageFrame = model.getImageFrame();
        if (faceInfos == null || faceInfos.length == 0) {
            // 清空canvas
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            textureView.unlockCanvasAndPost(canvas);
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        FaceInfo faceInfo = faceInfos[0];

        rectF.set(getFaceRectTwo(faceInfo, imageFrame));

        // 检测图片的坐标和显示的坐标不一样，需要转换。
        // mPreview[typeIndex].mapFromOriginalRect(rectF);

        float yaw = Math.abs(faceInfo.headPose[0]);
        float patch = Math.abs(faceInfo.headPose[1]);
        float roll = Math.abs(faceInfo.headPose[2]);
        if (yaw > 20 || patch > 20 || roll > 20) {
            // 不符合要求，绘制黄框
            paint.setColor(Color.YELLOW);

            String text = "请正视屏幕";
            float width = paint.measureText(text) + 50;
            float x = rectF.centerX() - width / 2;
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(text, x + 25, rectF.top - 20, paint);
            paint.setColor(Color.YELLOW);

        } else {
            // 符合检测要求，绘制绿框
            paint.setColor(Color.GREEN);
        }
        paint.setStyle(Paint.Style.STROKE);
        // 绘制框
        canvas.drawRect(rectF, paint);
        textureView.unlockCanvasAndPost(canvas);
    }

    public Rect getFaceRectTwo(FaceInfo faceInfo, ImageFrame frame) {
        Rect rect = new Rect();
        int[] points = new int[8];
        faceInfo.getRectPoints(points);
        int left = points[2];
        int top = points[3];
        int right = points[6];
        int bottom = points[7];
//        int previewWidth=surfaViews[typeIndex].getWidth();
//        int previewHeight=surfaViews[typeIndex].getHeight();
        int previewWidth = mTextureView.getWidth();
        int previewHeight = mTextureView.getHeight();
        float scaleW = 1.0f * previewWidth / frame.getWidth();
        float scaleH = 1.0f * previewHeight / frame.getHeight();
        int width = (right - left);
        int height = (bottom - top);
        left = (int) ((faceInfo.mCenter_x - width / 2) * scaleW);
        top = (int) ((faceInfo.mCenter_y - height / 2) * scaleH);

//        left = (int) ((faceInfo.mCenter_x)* scaleW);
//        top =  (int) ((faceInfo.mCenter_y) * scaleW);
        rect.top = top < 0 ? 0 : top;
        rect.left = left < 0 ? 0 : left;
//        rect.right = (left + width) > frame.getWidth() ? frame.getWidth() : (left + width);
        rect.right = (int) ((faceInfo.mCenter_x + width / 2) * scaleW);
        rect.bottom = (int) ((faceInfo.mCenter_y + height / 2) * scaleH);
//        rect.bottom = (top + height) > frame.getHeight() ? frame.getHeight() : (top + height);
        return rect;
    }


    /**
     * 定时关闭人脸识别功能
     */
    public void startTime() {
        if (countdowntimer == null) {
            countdowntimer = new CountDownTimer(advertisingTime, 1000l) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (!OrbbecProVideoIdentifyActivity.this.isFinishing()) {
                        int remainTime = (int) (millisUntilFinished / 1000L);
                        System.out.println("=========倒计时=========" + remainTime + "秒");
                        countdown.setText(remainTime + "秒");
                    }
                }

                @Override
                public void onFinish() { //定时完成后的操作
                    Intent intent = new Intent(OrbbecProVideoIdentifyActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            };
            countdowntimer.start();
        } else {
            countdowntimer.start();
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
}