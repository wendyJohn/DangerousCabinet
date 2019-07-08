package com.sanleng.dangerouscabinet.ui.bean;

public class Pass {

    /**
     * msg : result
     * code : 0
     * data : {"msg":"验证成功","user_code":"41bfde8e383745a0bcebfa758b75db89","user_name":"sanleng"}
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
         * msg : 验证成功
         * user_code : 41bfde8e383745a0bcebfa758b75db89
         * user_name : sanleng
         */

        private String msg;
        private String user_code;
        private String user_name;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getUser_code() {
            return user_code;
        }

        public void setUser_code(String user_code) {
            this.user_code = user_code;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }
    }
}
