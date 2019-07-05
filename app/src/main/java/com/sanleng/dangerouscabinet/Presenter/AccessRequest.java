package com.sanleng.dangerouscabinet.Presenter;

import android.content.Context;

import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.net.Request_Interface;
import com.sanleng.dangerouscabinet.net.URLs;
import com.sanleng.dangerouscabinet.ui.bean.AccessMag;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccessRequest {
    public static void GetAccess(final Context context, final String list) {
        System.out.println("=====出入库记录======" + list);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.HOST) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                .build();
        Request_Interface request_Interface = retrofit.create(Request_Interface.class);
        //对 发送请求 进行封装
        Map paramas = new HashMap<>();
        paramas.put("list", list);
        paramas.put("username", "admin");
        paramas.put("platformkey", "app_firecontrol_owner");
        Call<AccessMag> call = request_Interface.getAccessRecordslCall(paramas);
        call.enqueue(new Callback<AccessMag>() {
            @Override
            public void onResponse(Call<AccessMag> call, Response<AccessMag> response) {
                String str = response.body().getState();
                System.out.println("=====出入库记录======" + str);
            }

            @Override
            public void onFailure(Call<AccessMag> call, Throwable t) {

            }
        });
    }
}