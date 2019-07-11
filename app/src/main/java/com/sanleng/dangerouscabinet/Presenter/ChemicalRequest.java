package com.sanleng.dangerouscabinet.Presenter;

import android.content.Context;

import com.sanleng.dangerouscabinet.data.DBHelpers;
import com.sanleng.dangerouscabinet.net.Request_Interface;
import com.sanleng.dangerouscabinet.net.URLs;
import com.sanleng.dangerouscabinet.ui.bean.Chemical;
import com.sanleng.dangerouscabinet.ui.bean.Dangerous;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChemicalRequest {
    public static void GetChemical(final Context context, final String mac) {
        DBHelpers mOpenHelper = new DBHelpers(context);
        mOpenHelper.delete();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLs.HOST) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                .build();
        Request_Interface request_Interface = retrofit.create(Request_Interface.class);
        //对 发送请求 进行封装
        Call<Chemical> call = request_Interface.getChemicalCall(mac, "admin", "app_firecontrol_owner");
        call.enqueue(new Callback<Chemical>() {
            @Override
            public void onResponse(Call<Chemical> call, Response<Chemical> response) {
                try {
                    String chioUnitCode = response.body().getData().getChemicalStoreInfo().getOwner_unit_code();
                    String chioBuildCode = response.body().getData().getChemicalStoreInfo().getOwner_building_code();
                    String chioFloorCode = response.body().getData().getChemicalStoreInfo().getOwner_building_floor_code();
                    String chioRoomCode = response.body().getData().getChemicalStoreInfo().getOwner_building_room_code();
                    String chioUnitName = response.body().getData().getChemicalStoreInfo().getUnit_name();
                    String chioBuildName = response.body().getData().getChemicalStoreInfo().getBuild_name();
                    String chioFloorName = response.body().getData().getChemicalStoreInfo().getFloor_name();
                    String chioRoomName = response.body().getData().getChemicalStoreInfo().getRoom_name();
                    for (int i = 0; i < response.body().getData().getChemicalStoreSubstanceList().size(); i++) {
                        Dangerous bean = new Dangerous();
                        String Epc = response.body().getData().getChemicalStoreSubstanceList().get(i).getEpc();
                        String Ant = response.body().getData().getChemicalStoreSubstanceList().get(i).getAnt();
                        String Staus = response.body().getData().getChemicalStoreSubstanceList().get(i).getStaus();
                        String Ids = response.body().getData().getChemicalStoreSubstanceList().get(i).getIds();
                        String StationName = response.body().getData().getChemicalStoreSubstanceList().get(i).getStationName();
                        String StorageLocation = response.body().getData().getChemicalStoreSubstanceList().get(i).getStorageLocation();
                        String StationId = response.body().getData().getChemicalStoreSubstanceList().get(i).getStationId();
                        String Name = response.body().getData().getChemicalStoreSubstanceList().get(i).getName();
                        String Balancedata = response.body().getData().getChemicalStoreSubstanceList().get(i).getBalancedata();
                        String Equation = response.body().getData().getChemicalStoreSubstanceList().get(i).getEquation();
                        String Acidbase = response.body().getData().getChemicalStoreSubstanceList().get(i).getAcidbase();
                        String Type = response.body().getData().getChemicalStoreSubstanceList().get(i).getType();
                        String CurrentWeight = response.body().getData().getChemicalStoreSubstanceList().get(i).getCurrentWeight();
                        String Manufacturer = response.body().getData().getChemicalStoreSubstanceList().get(i).getManufacturer();
                        String Describe = response.body().getData().getChemicalStoreSubstanceList().get(i).getDescription();

                        bean.setEpc(Epc);
                        bean.setAnt(Ant);
                        bean.setStaus(Staus);
                        bean.setIds(Ids);
                        bean.setStationName(StationName);
                        bean.setStorageLocation(StorageLocation);
                        bean.setStationId(StationId);
                        bean.setName(Name);
                        bean.setBalancedata(Balancedata);
                        bean.setEquation(Equation);
                        bean.setAcidbase(Acidbase);
                        bean.setType(Type);
                        bean.setCurrentWeight(CurrentWeight);
                        bean.setManufacturer(Manufacturer);
                        bean.setDescribe(Describe);
                        bean.setChioUnitCode(chioUnitCode);
                        bean.setChioBuildCode(chioBuildCode);
                        bean.setChioFloorCode(chioFloorCode);
                        bean.setChioRoomCode(chioRoomCode);
                        bean.setChioUnitName(chioUnitName);
                        bean.setChioBuildName(chioBuildName);
                        bean.setChioFloorName(chioFloorName);
                        bean.setChioRoomName(chioRoomName);
                        mOpenHelper.insert(bean);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Chemical> call, Throwable t) {

            }
        });

    }
}