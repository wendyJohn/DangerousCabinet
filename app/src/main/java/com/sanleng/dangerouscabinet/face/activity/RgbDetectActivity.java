/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.sanleng.dangerouscabinet.face.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.ImageFrame;
import com.baidu.aip.callback.CameraDataCallback;
import com.baidu.aip.callback.FaceDetectCallBack;
import com.baidu.aip.entity.LivenessModel;
import com.baidu.aip.face.AutoTexturePreviewView;
import com.baidu.aip.face.FaceCropper;
import com.baidu.aip.face.FaceTrackManager;
import com.baidu.aip.face.camera.Camera1PreviewManager;
import com.baidu.aip.manager.FaceEnvironment;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.ImageUtils;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.model.FaceInfo;
import com.sanleng.dangerouscabinet.R;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE;
import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE_CLOSE;
import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE_OPEN;

/**
 * 单目rgb可见光检测人脸。
 *
 * @Time: 2018/11/12
 * @Author: v_chaixiaogang
 */
public class RgbDetectActivity extends Activity {
    // textureView用于绘制人脸框等。
    private TextureView textureView;
    private TextView detectDurationTv;
    private TextView rgbLivenssDurationTv;
    private TextView rgbLivenessScoreTv;
    private int liveType;
    private TextView tipTv;
    // 为了方便调式。
    private ImageView previewView;
    private Handler handler = new Handler();
    private int source;
    private AutoTexturePreviewView mPreviewView;

    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
    private static final int mWidth = 640;
    private static final int mHeight = 480;
    private int selectType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgb_detect);
        findView();
        Intent intent = getIntent();
        if (intent != null) {
            source = intent.getIntExtra("source", -1);
        }
        liveType = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity
                .TYPE_NO_LIVENSS);
    }

    private void findView() {
        selectType = PreferencesUtil.getInt(TYPE_PREVIEWIMAGE, TYPE_PREVIEWIMAGE_CLOSE);
        previewView = (ImageView) findViewById(R.id.test_view);

        mPreviewView = findViewById(R.id.preview_view);
        textureView = (TextureView) findViewById(R.id.texture_view);
        textureView.setOpaque(false);
        // 不需要屏幕自动变黑。
        textureView.setKeepScreenOn(true);
        tipTv = findViewById(R.id.tip_tv);
        detectDurationTv = findViewById(R.id.detect_duration_tv);
        rgbLivenssDurationTv = findViewById(R.id.rgb_liveness_duration_tv);
        rgbLivenessScoreTv = findViewById(R.id.rgb_liveness_score_tv);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 摄像头图像预览
        startCameraPreview();
    }

    /**
     * 摄像头图像预览
     */
    private void startCameraPreview() {
        // 设置前置摄像头
        // Camera1PreviewManager.getInstance().setCameraFacing(Camera1PreviewManager.CAMERA_FACING_FRONT);
        // 设置后置摄像头
        // Camera1PreviewManager.getInstance().setCameraFacing(Camera1PreviewManager.CAMERA_FACING_BACK);
        // 设置USB摄像头
        Camera1PreviewManager.getInstance().setCameraFacing(Camera1PreviewManager.CAMERA_USB);

        Camera1PreviewManager.getInstance().startPreview(this, mPreviewView, mWidth, mHeight, new CameraDataCallback() {
            @Override
            public void onGetCameraData(int[] data, Camera camera, int width, int height) {
                dealCameraData(data, width, height);
            }
        });
    }

    /**
     * 摄像头数据处理
     *
     * @param data
     * @param width
     * @param height
     */
    private void dealCameraData(int[] data, int width, int height) {
        if (selectType == TYPE_PREVIEWIMAGE_OPEN) {
            showDetectImage(width, height, data); // 显示检测的图片。用于调试，如果人脸sdk检测的人脸需要朝上，可以通过该图片判断。实际应用中可注释掉
        }
        // 摄像头预览数据进行人脸检测
        faceDetect(data, width, height);
    }

    /**
     * 显示检测的图片。用于调试，如果人脸sdk检测的人脸需要朝上，可以通过该图片判断。实际应用中可注释掉
     *
     * @param width
     * @param height
     * @param argb
     */
    private void showDetectImage(int width, int height, int[] argb) {
        final Bitmap bitmap = Bitmap.createBitmap(argb, width, height, Bitmap.Config.ARGB_8888);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewView.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * 人脸检测
     *
     * @param argb
     * @param width
     * @param height
     */
    private void faceDetect(int[] argb, int width, int height) {
        if (liveType == LivenessSettingActivity.TYPE_NO_LIVENSS) {
            FaceTrackManager.getInstance().setAliving(false); // 无活体检测
        } else if (liveType == LivenessSettingActivity.TYPE_RGB_LIVENSS) {
            FaceTrackManager.getInstance().setAliving(true); // 活体检测
        }
        FaceTrackManager.getInstance().faceTrack(argb, width, height, new FaceDetectCallBack() {
            @Override
            public void onFaceDetectCallback(LivenessModel livenessModel) {
                showFrame(livenessModel);
                checkResult(livenessModel);
            }

            @Override
            public void onTip(int code, final String msg) {
                displayTip(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Camera1PreviewManager.getInstance().stopPreview();
    }

    // 检测结果输出
    private void checkResult(LivenessModel model) {
        if (model == null) {
            clearTip();
            return;
        }
        if (liveType == LivenessSettingActivity.TYPE_NO_LIVENSS) { // 无活体检测
            displayResult(model, null);
            saveFace(model.getFaceInfo(), model.getImageFrame());
        } else if (liveType == LivenessSettingActivity.TYPE_RGB_LIVENSS) { // 活体检测
            displayResult(model, "livess");
            boolean livenessSuccess = false;
            livenessSuccess = (model.getRgbLivenessScore() > FaceEnvironment.LIVENESS_RGB_THRESHOLD) ? true : false;
            if (livenessSuccess) {
                saveFace(model.getFaceInfo(), model.getImageFrame());
            }
        }
    }

    private void displayResult(final LivenessModel livenessModel, final String livess) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (livess != null && livess.equals("livess")) {
                    rgbLivenessScoreTv.setVisibility(View.VISIBLE);
                    rgbLivenssDurationTv.setVisibility(View.VISIBLE);
                    rgbLivenessScoreTv.setText("RGB活体得分：" + livenessModel.getRgbLivenessScore());
                    rgbLivenssDurationTv.setText("RGB活体耗时：" + livenessModel.getRgbLivenessDuration());
                } else {
                    rgbLivenessScoreTv.setVisibility(View.GONE);
                    rgbLivenssDurationTv.setVisibility(View.GONE);
                }
                detectDurationTv.setText("人脸检测耗时：" + livenessModel.getRgbDetectDuration());
            }
        });
    }


    private void displayTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tipTv.setText(tip);
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
            }
        });
    }

    /**
     * 保存图片
     *
     * @param faceInfo
     * @param imageFrame
     */
    private void saveFace(FaceInfo faceInfo, ImageFrame imageFrame) {
        final Bitmap bitmap = FaceCropper.getFace(imageFrame.getArgb(), faceInfo, imageFrame.getWidth());
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
                finish();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void toast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RgbDetectActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
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
        mapFromOriginalRect(rectF, faceInfo, imageFrame);
        float yaw2 = Math.abs(faceInfo.headPose[0]);
        float patch2 = Math.abs(faceInfo.headPose[1]);
        float roll2 = Math.abs(faceInfo.headPose[2]);
        if (yaw2 > 20 || patch2 > 20 || roll2 > 20) {
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

    /**
     * 获取人脸框区域。
     *
     * @return 人脸框区域
     */
    // TODO padding?
    public Rect getFaceRectTwo(FaceInfo faceInfo, ImageFrame frame) {
        Rect rect = new Rect();
        int[] points = new int[8];
        faceInfo.getRectPoints(points);
        int left = points[2];
        int top = points[3];
        int right = points[6];
        int bottom = points[7];

        int width = (right - left);
        int height = (bottom - top);

        left = (int) ((faceInfo.mCenter_x - width / 2));
        top = (int) ((faceInfo.mCenter_y - height / 2));

        rect.top = top < 0 ? 0 : top;
        rect.left = left < 0 ? 0 : left;
        rect.right = (int) ((faceInfo.mCenter_x + width / 2));
        rect.bottom = (int) ((faceInfo.mCenter_y + height / 2));
        return rect;
    }

    public void mapFromOriginalRect(RectF rectF, FaceInfo faceInfo, ImageFrame imageFrame) {
        int selfWidth = mPreviewView.getPreviewWidth();
        int selfHeight = mPreviewView.getPreviewHeight();
        Matrix matrix = new Matrix();
        if (selfWidth * imageFrame.getHeight() > selfHeight * imageFrame.getWidth()) {
            int targetHeight = imageFrame.getHeight() * selfWidth / imageFrame.getWidth();
            int delta = (targetHeight - selfHeight) / 2;
            float ratio = 1.0f * selfWidth / imageFrame.getWidth();
            matrix.postScale(ratio, ratio);
            matrix.postTranslate(0, -delta);
        } else {
            int targetWith = imageFrame.getWidth() * selfHeight / imageFrame.getHeight();
            int delta = (targetWith - selfWidth) / 2;
            float ratio = 1.0f * selfHeight / imageFrame.getHeight();
            matrix.postScale(ratio, ratio);
            matrix.postTranslate(-delta, 0);
        }
        matrix.mapRect(rectF);
        if (false) { // 根据镜像调整
            float left = selfWidth - rectF.right;
            float right = left + rectF.width();
            rectF.left = left;
            rectF.right = right;
        }
    }
}
