package com.sanleng.dangerouscabinet.ui.bean;

/**
 * 危化品物资信息
 */
public class Dangerous {
    String Epc;//RFID卡号
    String Ant;//天线号
    String Staus;//出入库状态
    String Ids;//危化品ID
    String StationName;//站点名称
    String StorageLocation;//存放位置
    String StationId;//站点ID
    String Name;//危化品名称
    String Balancedata;//秤重重量
    String Equation;//方程式
    String Acidbase;//酸碱性
    String Type;//固液气分类
    String CurrentWeight;//规格重量
    String Manufacturer;//厂商
    String Threshold;//阈值

    public String getEpc() {
        return Epc;
    }

    public void setEpc(String epc) {
        Epc = epc;
    }

    public String getAnt() {
        return Ant;
    }

    public void setAnt(String ant) {
        Ant = ant;
    }

    public String getStaus() {
        return Staus;
    }

    public void setStaus(String staus) {
        Staus = staus;
    }

    public String getIds() {
        return Ids;
    }

    public void setIds(String ids) {
        Ids = ids;
    }

    public String getStationName() {
        return StationName;
    }

    public void setStationName(String stationName) {
        StationName = stationName;
    }

    public String getStorageLocation() {
        return StorageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        StorageLocation = storageLocation;
    }

    public String getStationId() {
        return StationId;
    }

    public void setStationId(String stationId) {
        StationId = stationId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getBalancedata() {
        return Balancedata;
    }

    public void setBalancedata(String balancedata) {
        Balancedata = balancedata;
    }

    public String getEquation() {
        return Equation;
    }

    public void setEquation(String equation) {
        Equation = equation;
    }

    public String getAcidbase() {
        return Acidbase;
    }

    public void setAcidbase(String acidbase) {
        Acidbase = acidbase;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getCurrentWeight() {
        return CurrentWeight;
    }

    public void setCurrentWeight(String currentWeight) {
        CurrentWeight = currentWeight;
    }

    public String getManufacturer() {
        return Manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        Manufacturer = manufacturer;
    }

    public String getThreshold() {
        return Threshold;
    }

    public void setThreshold(String threshold) {
        Threshold = threshold;
    }
}
