package com.sanleng.dangerouscabinet.Presenter;

import android.content.Context;

import com.sanleng.dangerouscabinet.model.PassModel;
import com.sanleng.dangerouscabinet.net.Request_Interface;
import com.sanleng.dangerouscabinet.net.URLs;
import com.sanleng.dangerouscabinet.ui.bean.Pass;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 密码认证
 */
public class PassVerificationRequest {
    public static void GetPassVerification(final PassModel passModel, final Context context, final String pass, final String type) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.HOST) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                .build();
        Request_Interface request_Interface = retrofit.create(Request_Interface.class);
        //对 发送请求 进行封装
        Call<Pass> call = request_Interface.getPasswordVerification(pass, "admin", "app_firecontrol_owner");
        call.enqueue(new Callback<Pass>() {
            @Override
            public void onResponse(Call<Pass> call, Response<Pass> response) {
                try {
                    if(response.body().getMsg().equals("验证成功")){
                        passModel.PassSuccess(type + response.body().getMsg(), response.body().getData().getUser_code(), response.body().getData().getUser_name(), type);
                    }else{
                        passModel.PassSuccess(type + response.body().getMsg(),null, null, type);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Pass> call, Throwable t) {
                passModel.PassFailed();
            }
        });

    }
}