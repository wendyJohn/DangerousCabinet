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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.callback.ILivenessCallBack;
import com.baidu.aip.entity.LivenessModel;
import com.baidu.aip.face.FaceCropper;
import com.baidu.aip.manager.FaceEnvironment;
import com.baidu.aip.manager.FaceLiveness;
import com.baidu.aip.manager.FaceSDKManager;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.GlobalSet;
import com.baidu.aip.utils.ImageUtils;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.model.FaceInfo;
import com.orbbec.obDepth2.HomeKeyListener;
import com.orbbec.view.OpenGLView;
import com.sanleng.dangerouscabinet.R;

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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * @Time: 2019/1/11
 * @Author: v_chaixiaogang
 * @Description: 奥比中光Pro镜头RGB+DEPTH 活体检测页面
 */
public class OrbbecProLivenessDetectActivity extends Activity implements OpenNIHelper.DeviceOpenListener,
        ActivityCompat.OnRequestPermissionsResultCallback, ILivenessCallBack {

    private static String TAG = "OpenniLivenessDetect";
    ImageView mImageView;
    private TextView tipTv;
    private HomeKeyListener mHomeListener;
    private HandlerThread mHThread;
    private Activity mContext;
    private ExecutorService es;
    private Future future;
    private boolean success = false;
    private int source;

    private boolean initOk = false;
    private Device device;
    private Thread thread;
    private OpenNIHelper mOpenNIHelper;
    private VideoStream depthStream;
//    private VideoStream rgbStream;

    private MyHandler mHandler;
    private static final int MSG_WHAT = 5;
    private static final String MSG_KEY = "YUV";

    private TextView detectDurationTv;
    private TextView rgbLivenssDurationTv;
    private TextView rgbLivenessScoreTv;
    private TextView depthLivenssDurationTv;
    private TextView depthLivenessScoreTv;

    private OpenGLView mDepthGLView;
//    private OpenGLView mRgbGLView;

    private int mWidth = com.orbbec.utils.GlobalDef.RESOLUTION_X;
    private int mHeight = com.orbbec.utils.GlobalDef.RESOLUTION_Y;

    private int mDepthWidth = 640;
    private int mDepthHeight = 400;

    private final int depthNeedPermission = 33;
    private Object sync = new Object();
    private boolean exit = false;

    // uvcTest
    private final Object mSync = new Object();
    private TextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private Matrix matrix = new Matrix();
    Handler handler = new Handler();

    // textureView用于绘制人脸框等。
    private TextureView textureView;

    private static int cameraType;

    private Camera mCamera;
    private int mCameraNum;
    private int[] rgbData = null;
    private byte[] depthData;
    private boolean mSurfaceCreated = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orbbec_pro_liveness_detect);
        FaceSDKManager.getInstance().getFaceLiveness().setLivenessCallBack(this);
        cameraType = PreferencesUtil.getInt(GlobalSet.TYPE_CAMERA, GlobalSet.ORBBEC);
        findView();
        initPreview();
        mContext = this;
        registerHomeListener();
        es = Executors.newSingleThreadExecutor();

        Intent intent = getIntent();
        if (intent != null) {
            source = intent.getIntExtra("source", -1);
        }
    }


    private void findView() {
        textureView = findViewById(R.id.texture_view);
        textureView.setOpaque(false);
        tipTv = (TextView) findViewById(R.id.message);

        mDepthGLView = (OpenGLView) findViewById(R.id.depthGlView);
        // mRgbGLView = (OpenGLView) findViewById(R.id.rgbGlView);
        mTextureView = (TextureView) findViewById(R.id.camera_surface_view);
        mTextureView.setOpaque(false);
        mTextureView.setKeepScreenOn(true);
        detectDurationTv = (TextView) findViewById(R.id.detect_duration_tv);
        rgbLivenssDurationTv = (TextView) findViewById(R.id.rgb_liveness_duration_tv);
        rgbLivenessScoreTv = (TextView) findViewById(R.id.rgb_liveness_score_tv);
        depthLivenssDurationTv = (TextView) findViewById(R.id.depth_liveness_duration_tv);
        depthLivenessScoreTv = (TextView) findViewById(R.id.depth_liveness_score_tv);

        mOpenNIHelper = new OpenNIHelper(this);
        mOpenNIHelper.requestDeviceOpen(this);
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
        unRegisterHomeListener();
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
                // 关闭、释放Camera资源
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

    private class MyHandler extends Handler {

        public MyHandler(OrbbecProLivenessDetectActivity pActivity) {

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
                tipTv.setText(msg);
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

        if (requestCode == depthNeedPermission) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permission Grant");
                Toast.makeText(mContext, "Permission Grant", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Permission Denied");
                Toast.makeText(mContext, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void checkResult(LivenessModel model) {

        if (model == null) {

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
            Bitmap bitmap = FaceCropper.getFace(model.getImageFrame().getArgb(),
                    model.getFaceInfo(), model.getImageFrame().getWidth());
            if (source == RegActivity.SOURCE_REG) {
                // 注册来源保存到注册人脸目录
                File faceDir = FileUitls.getFaceDirectory();
                if (faceDir != null) {
                    String imageName = UUID.randomUUID().toString();
                    File file = new File(faceDir, imageName);
                    // 压缩人脸图片至300 * 300，减少网络传输时间
                    ImageUtils.resize(bitmap, file, 300, 300);
                    Intent intent = new Intent();
                    intent.putExtra("file_path", file.getAbsolutePath());
                    setResult(Activity.RESULT_OK, intent);
                    success = true;
                    finish();
                } else {
                    toast("注册人脸目录未找到");
                }
            } else {
                try {
                    // 其他来源保存到临时目录
                    final File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
                    // 人脸识别不需要整张图片。可以对人脸区别进行裁剪。减少流量消耗和，网络传输占用的时间消耗。
                    ImageUtils.resize(bitmap, file, 300, 300);
                    Intent intent = new Intent();
                    intent.putExtra("file_path", file.getAbsolutePath());
                    setResult(Activity.RESULT_OK, intent);
                    success = true;
                    finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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


    private void clearTip() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                detectDurationTv.setText("");
                rgbLivenessScoreTv.setText("");
                rgbLivenssDurationTv.setText("");
                depthLivenessScoreTv.setText("");
                depthLivenssDurationTv.setText("");
            }
        });
    }

    private void registerHomeListener() {
        mHomeListener = new HomeKeyListener(this);
        mHomeListener
                .setOnHomePressedListener(new HomeKeyListener.OnHomePressedListener() {

                    @Override
                    public void onHomePressed() {
                        finish();
                    }

                    @Override
                    public void onHomeLongPressed() {
                    }
                });
        mHomeListener.startWatch();
    }

    private void unRegisterHomeListener() {
        if (mHomeListener != null) {
            mHomeListener.stopWatch();
        }
    }

    private void toast(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(OrbbecProLivenessDetectActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void aysncDetect(final int[] argb, final int height, final int width) {

        if (success || (future != null && !future.isDone())) {
            return;
        }

        future = es.submit(new Runnable() {
            @Override
            public void run() {
                detect(argb, height, width);
            }
        });
    }

    private void detect(int[] argb, int height, int width) {
        Log.i("wtf", "detect--" + argb.length);
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetector().trackMaxFace(argb, width, height);
        if (faceInfos != null && faceInfos.length > 0) {
            FaceInfo faceInfo = faceInfos[0];
            if (filter(faceInfo, width, height)) {
                Bitmap bitmap = FaceCropper.getFace(argb, faceInfo, width);
                try {
                    final File file = File.createTempFile(UUID.randomUUID().toString() + "", ".jpg");
                    ImageUtils.resize(bitmap, file, 300, 300);

                    Intent intent = new Intent();
                    intent.putExtra("file_path", file.getAbsolutePath());
                    setResult(Activity.RESULT_OK, intent);
                    success = true;
                    finish();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private boolean filter(FaceInfo faceInfo, int bitMapWidth, int bitMapHeight) {

        if (faceInfo.mConf < 0.6) {
            tipTv.setText("人脸置信度太低");
            return false;
        }

        float[] headPose = faceInfo.headPose;
        // Log.i("wtf", "headpose->" + headPose[0] + " " + headPose[1] + " " + headPose[2]);
        if (Math.abs(headPose[0]) > 15 || Math.abs(headPose[1]) > 15 || Math.abs(headPose[2]) > 15) {
            tipTv.setText("人脸置角度太大，请正对屏幕");
            return false;
        }

        // 判断人脸大小，若人脸超过屏幕二分一，则提示文案“人脸离手机太近，请调整与手机的距离”；
        // 若人脸小于屏幕三分一，则提示“人脸离手机太远，请调整与手机的距离”
        float ratio = (float) faceInfo.mWidth / (float) bitMapHeight;
        // Log.i("liveness_ratio", "ratio=" + ratio);
        if (ratio > 0.6) {
            tipTv.setText("人脸离屏幕太近，请调整与屏幕的距离");
            // clearInfo();
            return false;
        } else if (ratio < 0.2) {
            tipTv.setText("人脸离屏幕太远，请调整与屏幕的距离");
            // clearInfo();
            return false;
        } else if (faceInfo.mCenter_x > bitMapWidth * 3 / 4) {
            tipTv.setText("人脸在屏幕中太靠右");
            return false;
        } else if (faceInfo.mCenter_x < bitMapWidth / 4) {
            tipTv.setText("人脸在屏幕中太靠左");
            // clearInfo();
            return false;
        } else if (faceInfo.mCenter_y > bitMapHeight * 3 / 4) {
            tipTv.setText("人脸在屏幕中太靠下");
            // clearInfo();
            return false;
        } else if (faceInfo.mCenter_x < bitMapHeight / 4) {
            tipTv.setText("人脸在屏幕中太靠上");
            // clearInfo();
            return false;
        }

        return true;
    }

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

}
