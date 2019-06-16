package com.shenmi.calculator.bean;

import java.util.List;

/**
 * Created by SQ on 2019/3/28.
 *  广告分渠道、版本号开关
 */

public class ADSwitchConfigInfo {

    //app名字
     private String ADType;

     private List<SwitchConfigInfo> SwitchConfigInfoList;

    public String getADType() {
        return ADType;
    }

    public void setADType(String ADType) {
        this.ADType = ADType;
    }

    public List<SwitchConfigInfo> getSwitchConfigInfoList() {
        return SwitchConfigInfoList;
    }

    public void setSwitchConfigInfoList(List<SwitchConfigInfo> switchConfigInfoList) {
        SwitchConfigInfoList = switchConfigInfoList;
    }

    @Override
    public String toString() {
        return "ADSwitchConfigInfo{" +
                "ADType='" + ADType + '\'' +
                ", SwitchConfigInfoList=" + SwitchConfigInfoList +
                '}';
    }
}
