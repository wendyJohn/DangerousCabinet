/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.sanleng.dangerouscabinet.face.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.baidu.aip.api.FaceApi;
import com.baidu.aip.callback.CameraDataCallback;
import com.baidu.aip.callback.FaceDetectCallBack;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.Feature;
import com.baidu.aip.entity.IdentifyRet;
import com.baidu.aip.entity.LivenessModel;
import com.baidu.aip.entity.User;
import com.baidu.aip.face.AutoTexturePreviewView;
import com.baidu.aip.face.FaceTrackManager;
import com.baidu.aip.face.camera.Camera1PreviewManager;
import com.baidu.aip.manager.FaceEnvironment;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.PreferencesUtil;
import com.baidu.idl.facesdk.model.FaceInfo;
import com.sanleng.dangerouscabinet.R;
import com.sanleng.dangerouscabinet.face.utils.GlobalFaceTypeModel;
import com.sanleng.dangerouscabinet.utils.TTSUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE;
import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE_CLOSE;
import static com.baidu.aip.utils.GlobalSet.TYPE_PREVIEWIMAGE_OPEN;

/**
 * @Time: 2018/11/12
 * @Author: v_chaixiaogang
 * @Description: RGB可见光1:N检测比对识别
 */
public class RgbVideoIdentityActivity extends Activity implements View.OnClickListener {

    private static final int FEATURE_DATAS_UNREADY = 1;
    private static final int IDENTITY_IDLE = 2;
    private static final int IDENTITYING = 3;

    private AutoTexturePreviewView mPreviewView;

    // textureView用于绘制人脸框等。
    private TextureView textureView;
    // 为了方便调式。
    private ImageView testView;
    private TextView userOfMaxSocre;
    private ImageView matchAvatorIv;
    private TextView matchUserTv;
    private TextView tvShowTip;
    private TextView scoreTv;
    private TextView facesetsCountTv;
    private TextView detectDurationTv;
    private TextView rgbLivenssDurationTv;
    private TextView rgbLivenessScoreTv;
    private TextView featureDurationTv;
    private Handler handler = new Handler();
    private String groupId = "";

    private volatile int identityStatus = FEATURE_DATAS_UNREADY;
    private String userIdOfMaxScore = "";
    private float maxScore = 0;
    private ExecutorService es = Executors.newSingleThreadExecutor();
    private int liveType;

    // 图片越大，性能消耗越大，也可以选择640*480， 1280*720
    private static final int mWidth = 640;
    private static final int mHeight = 480;
    private int selectType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_identify);
        findView();
        DBManager.getInstance().init(this);
        loadFeature2Memery();
        Intent intent = getIntent();
        if (intent != null) {
            groupId = intent.getStringExtra("group_id");
        }
    }

    private void findView() {
        selectType = PreferencesUtil.getInt(TYPE_PREVIEWIMAGE, TYPE_PREVIEWIMAGE_CLOSE);
        testView = findViewById(R.id.test_view);
        matchAvatorIv = findViewById(R.id.match_avator_iv);
        userOfMaxSocre = findViewById(R.id.user_of_max_score_tv);
        mPreviewView = findViewById(R.id.preview_view);
        textureView = findViewById(R.id.texture_view);
        textureView.setOpaque(false);
        // 不需要屏幕自动变黑。
        textureView.setKeepScreenOn(true);
        matchUserTv = findViewById(R.id.match_user_tv);
        scoreTv = findViewById(R.id.score_tv);
        facesetsCountTv = findViewById(R.id.facesets_count_tv);
        detectDurationTv = findViewById(R.id.detect_duration_tv);
        rgbLivenssDurationTv = findViewById(R.id.rgb_liveness_duration_tv);
        rgbLivenessScoreTv = findViewById(R.id.rgb_liveness_score_tv);
        featureDurationTv = findViewById(R.id.feature_duration_tv);
        tvShowTip = findViewById(R.id.text_tip);

        liveType = PreferencesUtil.getInt(LivenessSettingActivity.TYPE_LIVENSS, LivenessSettingActivity
                .TYPE_NO_LIVENSS);
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
                testView.setImageBitmap(bitmap);
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
                displayTip(msg, tvShowTip);
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
            identity(model.getImageFrame(), model.getFaceInfo());
        } else if (liveType == LivenessSettingActivity.TYPE_RGB_LIVENSS) { // 活体检测
            displayResult(model, "livess");
            boolean livenessSuccess = false;
            livenessSuccess = (model.getRgbLivenessScore() > FaceEnvironment.LIVENESS_RGB_THRESHOLD) ? true : false;
            if (livenessSuccess) {
                identity(model.getImageFrame(), model.getFaceInfo());
            } else {
                cleanRecognizeInfo();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

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
                toast("人脸数据加载完成，即将开始1：N");
                int count = FaceApi.getInstance().getGroup2Facesets().get(groupId).size();
                displayTip("底库人脸个数：" + count, facesetsCountTv);
            }
        });
    }

    private void identity(ImageFrame imageFrame, FaceInfo faceInfo) {

        float raw = Math.abs(faceInfo.headPose[0]);
        float patch = Math.abs(faceInfo.headPose[1]);
        float roll = Math.abs(faceInfo.headPose[2]);
        // 人脸的三个角度大于20不进行识别
        if (raw > 20 || patch > 20 || roll > 20) {
            return;
        }

        long starttime = System.currentTimeMillis();
        int[] argb = imageFrame.getArgb();
        int rows = imageFrame.getHeight();
        int cols = imageFrame.getWidth();
        int[] landmarks = faceInfo.landmarks;

        int type = PreferencesUtil.getInt(GlobalFaceTypeModel.TYPE_MODEL, GlobalFaceTypeModel.RECOGNIZE_LIVE);
        IdentifyRet identifyRet = null;
        if (type == GlobalFaceTypeModel.RECOGNIZE_LIVE) {
            identifyRet = FaceApi.getInstance().identity(argb, rows, cols, landmarks, groupId);
        } else if (type == GlobalFaceTypeModel.RECOGNIZE_ID_PHOTO) {
            identifyRet = FaceApi.getInstance().identityForIDPhoto(argb, rows, cols, landmarks, groupId);
        }
        if (identifyRet != null) {
            displayUserOfMaxScore(identifyRet.getUserId(), identifyRet.getScore());
        }
        identityStatus = IDENTITY_IDLE;
        displayTip("特征抽取对比耗时:" + (System.currentTimeMillis() - starttime), featureDurationTv);
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

    private void displayUserOfMaxScore(final String userId, final float score) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (score < 80) {
                    scoreTv.setText("");
                    matchUserTv.setText("");
                    matchAvatorIv.setImageBitmap(null);
                    return;
                }
                if (userIdOfMaxScore.equals(userId)) {
                    if (score < maxScore) {
                        scoreTv.setText("" + score);
                    } else {
                        maxScore = score;
                        userOfMaxSocre.setText("userId：" + userId + "\nscore：" + score);
                        scoreTv.setText(String.valueOf(maxScore));
                    }
                    if (matchUserTv.getText().toString().length() > 0) {
                        return;
                    }
                } else {
                    userIdOfMaxScore = userId;
                    maxScore = score;
                }
                scoreTv.setText(String.valueOf(maxScore));
                User user = FaceApi.getInstance().getUserInfo(groupId, userId);
                if (user == null) {
                    return;
                }
                matchUserTv.setText(user.getUserInfo());
                List<Feature> featureList = user.getFeatureList();
                if (featureList != null && featureList.size() > 0) {
                    // featureTv.setText(new String(featureList.get(0).getFeature()));
                    File faceDir = FileUitls.getFaceDirectory();
                    if (faceDir != null && faceDir.exists()) {
                        File file = new File(faceDir, featureList.get(0).getImageName());
                        if (file != null && file.exists()) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            matchAvatorIv.setImageBitmap(bitmap);
                        }
                    }
                }
//                List<Feature>  featureList = DBManager.getInstance().queryFeatureByUeserId(userId);
//                if (featureList != null && featureList.size() > 0) {
//                    File faceDir = FileUitls.getFaceDirectory();
//                    if (faceDir != null && faceDir.exists()) {
//                        File file = new File(faceDir, featureList.get(0).getImageName());
//                        if (file != null && file.exists()) {
//                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                            testView.setImageBitmap(bitmap);
//                        }
//                    }
//                }
            }
        });
    }

    private void cleanRecognizeInfo() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTv.setText("");
                matchUserTv.setText("");
                matchAvatorIv.setImageBitmap(null);
                featureDurationTv.setText("");
            }
        });
    }

    private void toast(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RgbVideoIdentityActivity.this, text, Toast.LENGTH_LONG).show();
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
                featureDurationTv.setText("");
                scoreTv.setText("");
                matchUserTv.setText("");
                matchAvatorIv.setImageBitmap(null);
            }
        });
    }

    private void displayTip(final String text, final TextView textView) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
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
