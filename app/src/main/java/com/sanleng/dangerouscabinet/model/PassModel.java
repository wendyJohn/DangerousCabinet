package com.sanleng.dangerouscabinet.model;

public interface PassModel {
    void PassSuccess(String msg,String user_code,String user_name,String type);
    void PassFailed();
}
