package com.sanleng.dangerouscabinet.net;

import com.sanleng.dangerouscabinet.ui.bean.AccessMag;
import com.sanleng.dangerouscabinet.ui.bean.Chemical;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface Request_Interface {

    //获取最新的危化品信息
    @POST("/kspf/app/chemicalStore/getOneDetail?")
    Call<Chemical> getChemicalCall(@Query("macAddress") String macAddress, @Query("username") String username, @Query("platformkey") String platformkey);


    //危化品出入库信息提交
    @POST("/kspf/app/chemicalStoreIo/startIOList?")
    Call<AccessMag> getAccessRecordslCall(@QueryMap Map<String, String> paramas);
}
