package com.shenmi.calculator.util;

import android.content.Context;
import android.util.Log;

import com.shenmi.calculator.ui.home.MainCalculateActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SQ on 2019/5/9.
 */

public class DateUtil {

    /**
     * 判断是否到了一个月
     * @return
     * @param mContext
     */
    public static boolean isOnOneMonth(Context mContext){
        String month = (String) SPUtil.get(mContext, "month", "2019-01-01");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (month.equals("2019-01-01")){
            //表示是第一次，存入第一次提示评论的日期
            long currentTimeMillis = System.currentTimeMillis();
            String currentMonth = dateFormat.format(new Date(currentTimeMillis));
            Log.e("month","第一次"+currentMonth);
            SPUtil.put(mContext,"month",currentMonth);
            return true;
        }else{
            //获取当前时间，和上一次评论的时间做比较，大于一个月则显示
            long currentTimeMillis = System.currentTimeMillis();
            try {
                Date date = dateFormat.parse(month);
                long time = date.getTime();
                long differ = currentTimeMillis - time;
                //时间差转化为具体天数
                long day = differ / (1000 * 60 * 60 * 24);
                if (day>=30){
                    String currentMonth = dateFormat.format(new Date(currentTimeMillis));
                    Log.e("month","超过一个月"+currentMonth);
                    SPUtil.put(mContext,"month",currentMonth);
                    return true;
                }else{
                    Log.e("month","不超过一个月");
                    return false;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
