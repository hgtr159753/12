package com.shenmi.calculator.bean;

import java.util.List;

/**
 * Created by SQ on 2018/12/19.
 */

public class ConfigInfo {

    private Boolean IsOpenReadBook;
    private Boolean IsOpenZhuanDianBa;
    private Boolean IsOpenNews;
    //是否开启计算器的广告
    private Boolean IsOpenCalculatorAD;
    //是否开启倒计时的广告
    private Boolean IsOpenDJSAD;

    private List<ADSwitchConfigInfo> ADSwitchConfigInfo;

    public Boolean getOpenReadBook() {
        return IsOpenReadBook;
    }

    public void setOpenReadBook(Boolean openReadBook) {
        IsOpenReadBook = openReadBook;
    }

    public Boolean getOpenZhuanDianBa() {
        return IsOpenZhuanDianBa;
    }

    public void setOpenZhuanDianBa(Boolean openZhuanDianBa) {
        IsOpenZhuanDianBa = openZhuanDianBa;
    }

    public Boolean getOpenNews() {
        return IsOpenNews;
    }

    public void setOpenNews(Boolean openNews) {
        IsOpenNews = openNews;
    }

    public Boolean getOpenCalculatorAD() {
        return IsOpenCalculatorAD;
    }

    public void setOpenCalculatorAD(Boolean openCalculatorAD) {
        IsOpenCalculatorAD = openCalculatorAD;
    }

    public List<ADSwitchConfigInfo> getAdSwitchConfigInfo() {
        return ADSwitchConfigInfo;
    }

    public void setAdSwitchConfigInfo(List<ADSwitchConfigInfo> adSwitchConfigInfo) {
        this.ADSwitchConfigInfo = adSwitchConfigInfo;
    }

    public Boolean getOpenDJSAD() {
        return IsOpenDJSAD;
    }

    public void setOpenDJSAD(Boolean openDJSAD) {
        IsOpenDJSAD = openDJSAD;
    }

    @Override
    public String toString() {
        return "ConfigInfo{" +
                "IsOpenReadBook=" + IsOpenReadBook +
                ", IsOpenZhuanDianBa=" + IsOpenZhuanDianBa +
                ", IsOpenNews=" + IsOpenNews +
                ", IsOpenCalculatorAD=" + IsOpenCalculatorAD +
                ", IsOpenDJSAD=" + IsOpenDJSAD +
                ", adSwitchConfigInfo=" + ADSwitchConfigInfo +
                '}';
    }
}
