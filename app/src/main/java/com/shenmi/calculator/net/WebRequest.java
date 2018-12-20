package com.shenmi.calculator.net;

import android.support.annotation.NonNull;

/**
 * Created by SQ on 2018/12/19.
 */

public class WebRequest {

//    "SwitchType":"开关的事件类型,news：新闻，ad：广告，read：文章",
//    "DeviceId":"设备号",
//    "Channel":"app渠道：来源于华为，小米，等等",
//    "AppVersion":"版本",
//    "AppName":"APP名称",
//    "PackageName":"app包名"

    private String SwitchType;
    private String DeviceId;
    private String Channel;
    private String AppVersion;
    private String AppName;
    private String PackageName;

    public String getSwitchType() {
        return SwitchType;
    }

    public void setSwitchType(String switchType) {
        SwitchType = switchType;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getChannel() {
        return Channel;
    }

    public void setChannel(String channel) {
        Channel = channel;
    }

    public String getAppVersion() {
        return AppVersion;
    }

    public void setAppVersion(String appVersion) {
        AppVersion = appVersion;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    @Override
    public String toString() {
        return "WebRequest{" +
                "SwitchType='" + SwitchType + '\'' +
                ", DeviceId='" + DeviceId + '\'' +
                ", Channel='" + Channel + '\'' +
                ", AppVersion='" + AppVersion + '\'' +
                ", AppName='" + AppName + '\'' +
                ", PackageName='" + PackageName + '\'' +
                '}';
    }

    public static class Builder{

        private WebRequest webRequest = new WebRequest();

        public Builder setSwitchType(@NonNull String switchType){
            webRequest.SwitchType = switchType;
            return this;
        }

        public Builder setDeviceId(@NonNull String deviceId){
            webRequest.DeviceId = deviceId;
            return this;
        }

        public Builder setChannel(@NonNull String channel){
            webRequest.Channel = channel;
            return this;
        }

        public Builder setAppVersion(@NonNull String appVersion){
            webRequest.AppVersion = appVersion;
            return this;
        }

        public Builder setAppName(@NonNull String appName){
            webRequest.AppName = appName;
            return this;
        }

        public Builder setPackageName(@NonNull String packageName){
            webRequest.PackageName = packageName;
            return this;
        }

        public WebRequest build(){
            return webRequest;
        }
    }
}
