package com.sanleng.dangerouscabinet.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.aip.api.FaceApi;
import com.baidu.aip.db.DBManager;
import com.baidu.aip.entity.ARGBImg;
import com.baidu.aip.entity.Feature;
import com.baidu.aip.entity.Group;
import com.baidu.aip.entity.User;
import com.baidu.aip.utils.FeatureUtils;
import com.baidu.aip.utils.FileUitls;
import com.baidu.aip.utils.ImageUtils;
import com.baidu.aip.utils.PreferencesUtil;
import com.sanleng.dangerouscabinet.MyApplication;
import com.sanleng.dangerouscabinet.face.utils.GlobalFaceTypeModel;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

public class Receiver extends BroadcastReceiver {
    Bitmap bitmap;
    private List<String> groupIds = new ArrayList<>();
    private String groupId = "";
    private Context Context;
    private String username;

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(MyApplication.BROADCAST_ACTION_DISC)) {
            String str = intent.getStringExtra("str_test");
            String[] strarray = str.split("&");
            System.out.println(strarray[0]);
            System.out.println(strarray[1]);
            String path = "https://slyj.slicity.com" + strarray[0];
            username = strarray[1];
            new Task().execute(path);
        }
//        if (action.equals(MyApplication.BROADCAST_ACTION_PASS)) {
//            EventBus.getDefault().post(new MessageEvent(MyApplication.MESSAGE_OUT));
//        }
//        if (action.equals(MyApplication.BROADCAST_ACTION_DISMISS)) {
//            EventBus.getDefault().post(new MessageEvent(MyApplication.MESSAGE_DISMISS));
//        }
//        if (action.equals(MyApplication.BROADCAST_ACTION_VIDEO)) {
//            EventBus.getDefault().post(new MessageEvent(MyApplication.MESSAGE));
//        }
    }


    /**
     * 异步线程下载图片
     */
    class Task extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... params) {
            bitmap = GetImageInputStream((String) params[0]);
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            SavaImage(bitmap);
        }
    }

    /**
     * 获取网络图片
     *
     * @param imageurl 图片网络地址
     * @return Bitmap 返回位图
     */
    public Bitmap GetImageInputStream(String imageurl) {
        URL url;
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            url = new URL(imageurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(6000); //超时设置
            connection.setDoInput(true);
            connection.setUseCaches(false); //设置不使用缓存
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 保存位图到本地
     *
     * @param bitmap
     * @return void
     */
    public void SavaImage(Bitmap bitmap) {
        // 注册来源保存到注册人脸目录
        File faceDir = FileUitls.getFaceDirectory();
        if (faceDir != null) {
            String imageName = UUID.randomUUID().toString();
            File file = new File(faceDir, imageName);
            // 压缩人脸图片至300 * 300，减少网络传输时间
            ImageUtils.resize(bitmap, file, 300, 300);
            register(file.getAbsolutePath(), username);
        }
    }

    private void register(final String filePath, String myusername) {
        System.out.println("==========图片路径==========" + filePath);
        System.out.println("==========名称==========" + myusername);
        List<Group> groupList = DBManager.getInstance().queryGroups(0, 1000);
        for (Group group : groupList) {
            groupIds.add(group.getGroupId());
        }
        if (groupIds.size() > 0) {
            groupId = groupIds.get(0);
        }

        final String username = myusername;
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(Context, "userid不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(groupId)) {
            Toast.makeText(Context, "分组groupId为空", Toast.LENGTH_SHORT).show();
            return;
        }

        final String uid = UUID.randomUUID().toString();
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(Context, "人脸文件不存在", Toast.LENGTH_LONG).show();
            return;
        }
        final File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(Context, "人脸文件不存在", Toast.LENGTH_LONG).show();
            return;
        }


        final User user = new User();
        user.setUserId(uid);
        user.setUserInfo(username);
        user.setGroupId(groupId);

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                ARGBImg argbImg = FeatureUtils.getARGBImgFromPath(filePath);
                byte[] bytes = new byte[512];
                float ret = -1;
                int type = PreferencesUtil.getInt(GlobalFaceTypeModel.TYPE_MODEL, GlobalFaceTypeModel.RECOGNIZE_LIVE);
                if (type == GlobalFaceTypeModel.RECOGNIZE_LIVE) {
                    ret = FaceApi.getInstance().extractVisFeature(argbImg, bytes, 50);
                } else if (type == GlobalFaceTypeModel.RECOGNIZE_ID_PHOTO) {
                    ret = FaceApi.getInstance().extractIdPhotoFeature(argbImg, bytes, 50);
                }
                if (ret == -1) {
//                    toast("人脸太小（必须打于最小检测人脸minFaceSize），或者人脸角度太大，人脸不是朝上");
                } else if (ret != -1) {
                    Feature feature = new Feature();
                    feature.setGroupId(groupId);
                    feature.setUserId(uid);
                    feature.setFeature(bytes);
                    feature.setImageName(file.getName());
                    user.getFeatureList().add(feature);
                    if (FaceApi.getInstance().userAdd(user)) {
                    } else {
                    }
                } else {

                }
            }
        });
    }

    private Handler handler = new Handler(Looper.getMainLooper());
}
