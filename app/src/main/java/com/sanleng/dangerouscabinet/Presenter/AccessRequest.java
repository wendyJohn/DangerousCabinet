package com.sanleng.dangerouscabinet.Presenter;

import android.content.Context;

import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.net.Request_Interface;
import com.sanleng.dangerouscabinet.net.URLs;
import com.sanleng.dangerouscabinet.ui.bean.AccessMag;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AccessRequest {
    public static void GetAccessRecords(final Context context, final JSONArray list) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.HOST) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                .build();
        Request_Interface request_Interface = retrofit.create(Request_Interface.class);
        Map<String, String> map = new HashMap<>();
        map.put("username", "admin");
        map.put("platformkey", "app_firecontrol_owner");
        map.put("list", list.toString());
        //对 发送请求 进行封装
        Call<AccessMag> call = request_Interface.getAccessRecordslCall(map);
        call.enqueue(new Callback<AccessMag>() {
            @Override
            public void onResponse(Call<AccessMag> call, Response<AccessMag> response) {
                System.out.println(response.body().getMessage());
            }

            @Override
            public void onFailure(Call<AccessMag> call, Throwable t) {

            }
        });

    }
}