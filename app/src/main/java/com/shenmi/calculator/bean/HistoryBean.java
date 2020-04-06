package com.shenmi.calculator.bean;

import android.text.TextUtils;

import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class HistoryBean {
    @Id
    private long id;

    /**
     * type
     * 0 基础计算器
     * 1 科学计算器
     */
    private int type;
    private String datas;
    private Date time = new Date();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDatas() {
        return datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void addData(String str) {
        StringBuffer buffer = new StringBuffer();
        if (!TextUtils.isEmpty(datas)) {
            buffer.append(datas);
            if (!TextUtils.isEmpty(str)) {
                buffer.append(":");
            }
        }
        buffer.append(str);
        datas = buffer.toString();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String[] getData() {
        if (TextUtils.isEmpty(datas)) {
            return new String[0];
        }
        return datas.split(":");
    }
}
