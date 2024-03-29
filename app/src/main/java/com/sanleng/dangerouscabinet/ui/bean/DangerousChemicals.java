package com.sanleng.dangerouscabinet.ui.bean;

import android.widget.TextView;

/**
 * 危化品
 */
public class DangerousChemicals {
    private String rfid;
    private String name;
    private String ids;
    private String balancedata;
    private String sortLetters; // 显示数据拼音的首字母
    private String equation;
    private String type;
    private String acidbase;
    private String specifications;
    private String state;
    private String describe;
    private String usernamea;
    private String usernameb;
    private String manufacturer;

    public DangerousChemicals() {
        super();
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

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getUsernamea() {
        return usernamea;
    }

    public void setUsernamea(String usernamea) {
        this.usernamea = usernamea;
    }

    public String getUsernameb() {
        return usernameb;
    }

    public void setUsernameb(String usernameb) {
        this.usernameb = usernameb;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getAcidbase() {
        return acidbase;
    }

    public void setAcidbase(String acidbase) {
        this.acidbase = acidbase;
    }
}
