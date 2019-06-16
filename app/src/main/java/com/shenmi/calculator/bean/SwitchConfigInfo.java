package com.shenmi.calculator.bean;

/**
 * Created by SQ on 2019/3/28.
 */

public class SwitchConfigInfo {

    // "vivo_new"
     private String ChannelId;
     private SwitchConfigInfoDetailBean SwitchConfigInfoDetail;

    public String getChannelId() {
        return ChannelId;
    }

    public void setChannelId(String channelId) {
        ChannelId = channelId;
    }

    public SwitchConfigInfoDetailBean getSwitchConfigInfoDetail() {
        return SwitchConfigInfoDetail;
    }

    public void setSwitchConfigInfoDetail(SwitchConfigInfoDetailBean switchConfigInfoDetail) {
        SwitchConfigInfoDetail = switchConfigInfoDetail;
    }

    @Override
    public String toString() {
        return "SwitchConfigInfo{" +
                "ChannelId='" + ChannelId + '\'' +
                ", SwitchConfigInfoDetail=" + SwitchConfigInfoDetail +
                '}';
    }
}
