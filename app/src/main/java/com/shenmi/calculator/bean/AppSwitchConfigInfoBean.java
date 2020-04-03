package com.shenmi.calculator.bean;

import java.io.Serializable;

/**
 * @Description: 自定义接收GG开关类后期加入对应jar
 * @Author: Mr
 * @CreateDate: 2020/3/24 14:29
 */

public class AppSwitchConfigInfoBean implements Serializable {

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public ConofigBean getData() {
        return data;
    }

    public void setData(ConofigBean data) {
        this.data = data;
    }

    private int code;
    private String Msg;
    private ConofigBean data;

    public class ConofigBean{

        public boolean isOpenAD() {
            return isOpenAD;
        }

        public void setOpenAD(boolean openAD) {
            isOpenAD = openAD;
        }

        public int getIsShowOrder() {
            return isShowOrder;
        }

        public void setIsShowOrder(int isShowOrder) {
            this.isShowOrder = isShowOrder;
        }

        private boolean isOpenAD;
        private int isShowOrder;   //1 自家，2.CSJ，3.GDT

    }
}
