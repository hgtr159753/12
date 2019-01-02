package com.shenmi.calculator.bean;

import java.io.Serializable;

/**
 * Created by SQ on 2018/12/19.
 */

public class WebResponse implements Serializable {

//    "AppSwitchConfigInfo":{"IsOpen":true表示打开，false表示关闭},
//    "ApiStatusCode":200表示请求成功，其他都为失败,
//    "ApiStatusMsg":"请求成功"

    private ConfigInfo AppSwitchConfigInfo;
    private int ApiStatusCode;
    private String ApiStatusMsg;

    public ConfigInfo getAppSwitchConfigInfo() {
        return AppSwitchConfigInfo;
    }

    public void setAppSwitchConfigInfo(ConfigInfo appSwitchConfigInfo) {
        AppSwitchConfigInfo = appSwitchConfigInfo;
    }

    public int getApiStatusCode() {
        return ApiStatusCode;
    }

    public void setApiStatusCode(int apiStatusCode) {
        ApiStatusCode = apiStatusCode;
    }

    public String getApiStatusMsg() {
        return ApiStatusMsg;
    }

    public void setApiStatusMsg(String apiStatusMsg) {
        ApiStatusMsg = apiStatusMsg;
    }

    @Override
    public String toString() {
        return "WebResponse{" +
                "AppSwitchConfigInfo=" + AppSwitchConfigInfo +
                ", ApiStatusCode=" + ApiStatusCode +
                ", ApiStatusMsg='" + ApiStatusMsg + '\'' +
                '}';
    }
}
