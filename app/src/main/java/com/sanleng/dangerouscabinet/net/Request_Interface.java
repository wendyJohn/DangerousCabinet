package com.sanleng.dangerouscabinet.net;

import com.sanleng.dangerouscabinet.ui.bean.AccessMag;
import com.sanleng.dangerouscabinet.ui.bean.Chemical;
import com.sanleng.dangerouscabinet.ui.bean.Pass;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Request_Interface {

    //获取最新的危化品信息
    @POST("/kspf/app/chemicalStore/getOneDetail?")
    Call<Chemical> getChemicalCall(@Query("macAddress") String macAddress, @Query("username") String username, @Query("platformkey") String platformkey);

    //危化品出入库信息提交
    @FormUrlEncoded
    @POST("/kspf/app/chemicalStoreIo/startIOList")
    Call<AccessMag> getAccessRecordslCall(@FieldMap Map<String, String> paramas);

    //密码验证
    @POST("/kspf/app/chemicalStoreDynamicPass/verifyPass?")
    Call<Pass> getPasswordVerification(@Query("pass") String pass, @Query("username") String username, @Query("platformkey") String platformkey);

}
