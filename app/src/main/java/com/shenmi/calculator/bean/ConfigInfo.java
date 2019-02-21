package com.shenmi.calculator.bean;

/**
 * Created by SQ on 2018/12/19.
 */

public class ConfigInfo {

    private Boolean IsOpenReadBook;
    private Boolean IsOpenZhuanDianBa;
    private Boolean IsOpenNews;

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

    @Override
    public String toString() {
        return "ConfigInfo{" +
                "IsOpenReadBook=" + IsOpenReadBook +
                ", IsOpenZhuanDianBa=" + IsOpenZhuanDianBa +
                ", IsOpenNews=" + IsOpenNews +
                '}';
    }
}
