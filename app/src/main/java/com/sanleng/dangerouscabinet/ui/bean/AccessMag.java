package com.sanleng.dangerouscabinet.ui.bean;

public class AccessMag {
    /**
     * state : ok
     * statecode : CHEMICAL_STORE_IO_SUCCESS
     * message : 出入库记录同步成功
     */

    private String state;
    private String statecode;
    private String message;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
