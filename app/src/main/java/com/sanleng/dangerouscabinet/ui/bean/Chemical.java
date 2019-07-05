package com.sanleng.dangerouscabinet.ui.bean;

import java.util.List;

public class Chemical {

    /**
     * msg : 查询成功
     * code : 0
     * data : {"chemicalStoreInfo":{"ch_monitor2":"88","ch_monitor1":"88","adm2":"renyp","ch_type":"88","ch_del":1,"ch_ids":"bcaad187de7c49599995b101a4005bfb","ch_name":"888柜子","adm1":"longqi","build_name":"内蒙古肿瘤医院","unit_name":"内蒙古复大肿瘤医院有限公司","floor_name":"20层","ch_latitude":"55","room_name":"总统房","pt_user_code2":"0c92929e352146299ae163811809238c","ch_mac_address":"88-88-88-88-88-88","pt_user_code1":"0aeee9175f6a459199ebc2e3d34f1017","owner_unit_code":"7aa7b0a491e44d3183d1b293518ea315","ch_longitude":"121","owner_building_room_code":"2bc44f58a3474f13a3e5101538befcf9","owner_building_code":"69def22d6cda4260a6b324d9ab4b2bed","owner_building_floor_code":"02ae6f3f78cd4e69a7ead74b7fcf7280"},"chemicalStoreSubstanceList":[{"CurrentWeight":"900","Description":"描述哈哈哈00","Ant":null,"Equation":"C2H5OH","StationId":"bcaad187de7c49599995b101a4005bfb","Name":"酒精","Balancedata":"0","StationName":"888柜子","Type":"易燃","Manufacturer":"南京哈哈哈化工制品有限公司","Epc":"ASDHJSKAD22","Staus":null,"Ids":"00e7e6d2c0c04118886ae5fe09b7c368","StorageLocation":null,"Acidbase":"中性"}]}
     */

    private String msg;
    private String code;
    private DataBean data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * chemicalStoreInfo : {"ch_monitor2":"88","ch_monitor1":"88","adm2":"renyp","ch_type":"88","ch_del":1,"ch_ids":"bcaad187de7c49599995b101a4005bfb","ch_name":"888柜子","adm1":"longqi","build_name":"内蒙古肿瘤医院","unit_name":"内蒙古复大肿瘤医院有限公司","floor_name":"20层","ch_latitude":"55","room_name":"总统房","pt_user_code2":"0c92929e352146299ae163811809238c","ch_mac_address":"88-88-88-88-88-88","pt_user_code1":"0aeee9175f6a459199ebc2e3d34f1017","owner_unit_code":"7aa7b0a491e44d3183d1b293518ea315","ch_longitude":"121","owner_building_room_code":"2bc44f58a3474f13a3e5101538befcf9","owner_building_code":"69def22d6cda4260a6b324d9ab4b2bed","owner_building_floor_code":"02ae6f3f78cd4e69a7ead74b7fcf7280"}
         * chemicalStoreSubstanceList : [{"CurrentWeight":"900","Description":"描述哈哈哈00","Ant":null,"Equation":"C2H5OH","StationId":"bcaad187de7c49599995b101a4005bfb","Name":"酒精","Balancedata":"0","StationName":"888柜子","Type":"易燃","Manufacturer":"南京哈哈哈化工制品有限公司","Epc":"ASDHJSKAD22","Staus":null,"Ids":"00e7e6d2c0c04118886ae5fe09b7c368","StorageLocation":null,"Acidbase":"中性"}]
         */

        private ChemicalStoreInfoBean chemicalStoreInfo;
        private List<ChemicalStoreSubstanceListBean> chemicalStoreSubstanceList;

        public ChemicalStoreInfoBean getChemicalStoreInfo() {
            return chemicalStoreInfo;
        }

        public void setChemicalStoreInfo(ChemicalStoreInfoBean chemicalStoreInfo) {
            this.chemicalStoreInfo = chemicalStoreInfo;
        }

        public List<ChemicalStoreSubstanceListBean> getChemicalStoreSubstanceList() {
            return chemicalStoreSubstanceList;
        }

        public void setChemicalStoreSubstanceList(List<ChemicalStoreSubstanceListBean> chemicalStoreSubstanceList) {
            this.chemicalStoreSubstanceList = chemicalStoreSubstanceList;
        }

        public static class ChemicalStoreInfoBean {
            /**
             * ch_monitor2 : 88
             * ch_monitor1 : 88
             * adm2 : renyp
             * ch_type : 88
             * ch_del : 1
             * ch_ids : bcaad187de7c49599995b101a4005bfb
             * ch_name : 888柜子
             * adm1 : longqi
             * build_name : 内蒙古肿瘤医院
             * unit_name : 内蒙古复大肿瘤医院有限公司
             * floor_name : 20层
             * ch_latitude : 55
             * room_name : 总统房
             * pt_user_code2 : 0c92929e352146299ae163811809238c
             * ch_mac_address : 88-88-88-88-88-88
             * pt_user_code1 : 0aeee9175f6a459199ebc2e3d34f1017
             * owner_unit_code : 7aa7b0a491e44d3183d1b293518ea315
             * ch_longitude : 121
             * owner_building_room_code : 2bc44f58a3474f13a3e5101538befcf9
             * owner_building_code : 69def22d6cda4260a6b324d9ab4b2bed
             * owner_building_floor_code : 02ae6f3f78cd4e69a7ead74b7fcf7280
             */

            private String ch_monitor2;
            private String ch_monitor1;
            private String adm2;
            private String ch_type;
            private int ch_del;
            private String ch_ids;
            private String ch_name;
            private String adm1;
            private String build_name;
            private String unit_name;
            private String floor_name;
            private String ch_latitude;
            private String room_name;
            private String pt_user_code2;
            private String ch_mac_address;
            private String pt_user_code1;
            private String owner_unit_code;
            private String ch_longitude;
            private String owner_building_room_code;
            private String owner_building_code;
            private String owner_building_floor_code;

            public String getCh_monitor2() {
                return ch_monitor2;
            }

            public void setCh_monitor2(String ch_monitor2) {
                this.ch_monitor2 = ch_monitor2;
            }

            public String getCh_monitor1() {
                return ch_monitor1;
            }

            public void setCh_monitor1(String ch_monitor1) {
                this.ch_monitor1 = ch_monitor1;
            }

            public String getAdm2() {
                return adm2;
            }

            public void setAdm2(String adm2) {
                this.adm2 = adm2;
            }

            public String getCh_type() {
                return ch_type;
            }

            public void setCh_type(String ch_type) {
                this.ch_type = ch_type;
            }

            public int getCh_del() {
                return ch_del;
            }

            public void setCh_del(int ch_del) {
                this.ch_del = ch_del;
            }

            public String getCh_ids() {
                return ch_ids;
            }

            public void setCh_ids(String ch_ids) {
                this.ch_ids = ch_ids;
            }

            public String getCh_name() {
                return ch_name;
            }

            public void setCh_name(String ch_name) {
                this.ch_name = ch_name;
            }

            public String getAdm1() {
                return adm1;
            }

            public void setAdm1(String adm1) {
                this.adm1 = adm1;
            }

            public String getBuild_name() {
                return build_name;
            }

            public void setBuild_name(String build_name) {
                this.build_name = build_name;
            }

            public String getUnit_name() {
                return unit_name;
            }

            public void setUnit_name(String unit_name) {
                this.unit_name = unit_name;
            }

            public String getFloor_name() {
                return floor_name;
            }

            public void setFloor_name(String floor_name) {
                this.floor_name = floor_name;
            }

            public String getCh_latitude() {
                return ch_latitude;
            }

            public void setCh_latitude(String ch_latitude) {
                this.ch_latitude = ch_latitude;
            }

            public String getRoom_name() {
                return room_name;
            }

            public void setRoom_name(String room_name) {
                this.room_name = room_name;
            }

            public String getPt_user_code2() {
                return pt_user_code2;
            }

            public void setPt_user_code2(String pt_user_code2) {
                this.pt_user_code2 = pt_user_code2;
            }

            public String getCh_mac_address() {
                return ch_mac_address;
            }

            public void setCh_mac_address(String ch_mac_address) {
                this.ch_mac_address = ch_mac_address;
            }

            public String getPt_user_code1() {
                return pt_user_code1;
            }

            public void setPt_user_code1(String pt_user_code1) {
                this.pt_user_code1 = pt_user_code1;
            }

            public String getOwner_unit_code() {
                return owner_unit_code;
            }

            public void setOwner_unit_code(String owner_unit_code) {
                this.owner_unit_code = owner_unit_code;
            }

            public String getCh_longitude() {
                return ch_longitude;
            }

            public void setCh_longitude(String ch_longitude) {
                this.ch_longitude = ch_longitude;
            }

            public String getOwner_building_room_code() {
                return owner_building_room_code;
            }

            public void setOwner_building_room_code(String owner_building_room_code) {
                this.owner_building_room_code = owner_building_room_code;
            }

            public String getOwner_building_code() {
                return owner_building_code;
            }

            public void setOwner_building_code(String owner_building_code) {
                this.owner_building_code = owner_building_code;
            }

            public String getOwner_building_floor_code() {
                return owner_building_floor_code;
            }

            public void setOwner_building_floor_code(String owner_building_floor_code) {
                this.owner_building_floor_code = owner_building_floor_code;
            }
        }

        public static class ChemicalStoreSubstanceListBean {
            /**
             * CurrentWeight : 900
             * Description : 描述哈哈哈00
             * Ant : null
             * Equation : C2H5OH
             * StationId : bcaad187de7c49599995b101a4005bfb
             * Name : 酒精
             * Balancedata : 0
             * StationName : 888柜子
             * Type : 易燃
             * Manufacturer : 南京哈哈哈化工制品有限公司
             * Epc : ASDHJSKAD22
             * Staus : null
             * Ids : 00e7e6d2c0c04118886ae5fe09b7c368
             * StorageLocation : null
             * Acidbase : 中性
             */

            private String CurrentWeight;
            private String Description;
            private String Ant;
            private String Equation;
            private String StationId;
            private String Name;
            private String Balancedata;
            private String StationName;
            private String Type;
            private String Manufacturer;
            private String Epc;
            private String Staus;
            private String Ids;
            private String StorageLocation;
            private String Acidbase;

            public String getCurrentWeight() {
                return CurrentWeight;
            }

            public void setCurrentWeight(String CurrentWeight) {
                this.CurrentWeight = CurrentWeight;
            }

            public String getDescription() {
                return Description;
            }

            public void setDescription(String Description) {
                this.Description = Description;
            }

            public String getAnt() {
                return Ant;
            }

            public void setAnt(String Ant) {
                this.Ant = Ant;
            }

            public String getEquation() {
                return Equation;
            }

            public void setEquation(String Equation) {
                this.Equation = Equation;
            }

            public String getStationId() {
                return StationId;
            }

            public void setStationId(String StationId) {
                this.StationId = StationId;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getBalancedata() {
                return Balancedata;
            }

            public void setBalancedata(String Balancedata) {
                this.Balancedata = Balancedata;
            }

            public String getStationName() {
                return StationName;
            }

            public void setStationName(String StationName) {
                this.StationName = StationName;
            }

            public String getType() {
                return Type;
            }

            public void setType(String Type) {
                this.Type = Type;
            }

            public String getManufacturer() {
                return Manufacturer;
            }

            public void setManufacturer(String Manufacturer) {
                this.Manufacturer = Manufacturer;
            }

            public String getEpc() {
                return Epc;
            }

            public void setEpc(String Epc) {
                this.Epc = Epc;
            }

            public String getStaus() {
                return Staus;
            }

            public void setStaus(String Staus) {
                this.Staus = Staus;
            }

            public String getIds() {
                return Ids;
            }

            public void setIds(String Ids) {
                this.Ids = Ids;
            }

            public String getStorageLocation() {
                return StorageLocation;
            }

            public void setStorageLocation(String StorageLocation) {
                this.StorageLocation = StorageLocation;
            }

            public String getAcidbase() {
                return Acidbase;
            }

            public void setAcidbase(String Acidbase) {
                this.Acidbase = Acidbase;
            }
        }
    }
}
