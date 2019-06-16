package com.shenmi.calculator.bean;

/**
 * Created by SQ on 2019/3/28.
 */

public class SwitchConfigInfoDetailBean {

    //渠道名
    private String ChannelName;
    //版本号
    private String VersionCode;
    //是否开广告
    private boolean IsOpenAD;

    public String getChannelName() {
        return ChannelName;
    }

    public void setChannelName(String channelName) {
        ChannelName = channelName;
    }

    public String getVersionCode() {
        return VersionCode;
    }

    public void setVersionCode(String versionCode) {
        VersionCode = versionCode;
    }

    public boolean isOpenAD() {
        return IsOpenAD;
    }

    public void setOpenAD(boolean openAD) {
        IsOpenAD = openAD;
    }

    @Override
    public String toString() {
        return "SwitchConfigInfoDetailBean{" +
                "ChannelName='" + ChannelName + '\'' +
                ", VersionCode='" + VersionCode + '\'' +
                ", IsOpenAD=" + IsOpenAD +
                '}';
    }
}
