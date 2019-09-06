package com.shenmi.calculator.bean.tax;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by SQ on 2019/4/17.
 */

public class TaxBean implements Parcelable {

    private Long money;
    private String gongjijin;
    private String yiliao;
    private String yanglao;
    private String shiye;
    private String gongshang;
    private String shengyu;
    private String type;//年终0，工资1

    public Long getMoney() {
        return money;
    }

    public void setMoney(Long money) {
        this.money = money;
    }

    public String getGongjijin() {
        return gongjijin;
    }

    public void setGongjijin(String gongjijin) {
        this.gongjijin = gongjijin;
    }

    public String getYiliao() {
        return yiliao;
    }

    public void setYiliao(String yiliao) {
        this.yiliao = yiliao;
    }

    public String getYanglao() {
        return yanglao;
    }

    public void setYanglao(String yanglao) {
        this.yanglao = yanglao;
    }

    public String getShiye() {
        return shiye;
    }

    public void setShiye(String shiye) {
        this.shiye = shiye;
    }

    public String getGongshang() {
        return gongshang;
    }

    public void setGongshang(String gongshang) {
        this.gongshang = gongshang;
    }

    public String getShengyu() {
        return shengyu;
    }

    public void setShengyu(String shengyu) {
        this.shengyu = shengyu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.money);
        dest.writeString(this.gongjijin);
        dest.writeString(this.yiliao);
        dest.writeString(this.yanglao);
        dest.writeString(this.shiye);
        dest.writeString(this.gongshang);
        dest.writeString(this.shengyu);
        dest.writeString(this.type);
    }

    public TaxBean() {
    }

    protected TaxBean(Parcel in) {
        this.money = in.readLong();
        this.gongjijin = in.readString();
        this.yiliao = in.readString();
        this.yanglao = in.readString();
        this.shiye = in.readString();
        this.gongshang = in.readString();
        this.shengyu = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<TaxBean> CREATOR = new Parcelable.Creator<TaxBean>() {
        @Override
        public TaxBean createFromParcel(Parcel source) {
            return new TaxBean(source);
        }

        @Override
        public TaxBean[] newArray(int size) {
            return new TaxBean[size];
        }
    };
}
