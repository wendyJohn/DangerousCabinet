package com.sanleng.dangerouscabinet.ui.bean;

/**
 * 危化品
 */
public class DangerousChemicals {
    private String rfid;
    private String name;
    private String ids;
    private String balancedata;
    private String sortLetters; // 显示数据拼音的首字母

    public DangerousChemicals() {
        super();
    }

    public DangerousChemicals(String name, String sortLetters, String ids, String balancedata,String rfid) {
        super();
        this.name = name;
        this.sortLetters = sortLetters;
        this.ids = ids;
        this.balancedata = balancedata;
        this.rfid = rfid;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getBalancedata() {
        return balancedata;
    }

    public void setBalancedata(String balancedata) {
        this.balancedata = balancedata;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }
}
