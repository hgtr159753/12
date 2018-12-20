package com.shenmi.calculator.util;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by Mr on 2017/12/25.
 */
public class SharedPUtils {

    public final static String USER_SETTING = "userInfo_setting";

    public static void setShake(Context context, boolean isOpen) {
        SharedPreferences sp = context.getSharedPreferences(USER_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("shake", isOpen);
        editor.commit();
    }

    public static boolean getShake(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USER_SETTING, Context.MODE_PRIVATE);
        if (sp != null) {
            boolean isShake = sp.getBoolean("shake", false);
            return isShake;
        }
        return true;
    }


    /**
     * 保存是否显示
     */
    public static void setMusic(Context context, boolean isOpen) {
        SharedPreferences sp = context.getSharedPreferences(USER_SETTING, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("bottomIsShow", isOpen);
        editor.commit();
    }


    /**
     * 获取是否显示
     */
    public static boolean getMusic(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USER_SETTING, Context.MODE_PRIVATE);
        if (sp != null) {
            boolean isMusic= sp.getBoolean("bottomIsShow", false);
            return isMusic;
        }
        return true;
    }


    /**
     * 判断是否第一次进入APP
     *
     * @param context
     * @return
     */
    public static boolean isFirstStart(Context context) {
        SharedPreferences sp = context.getSharedPreferences(USER_SETTING, Context.MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("first", true);
        //第一次则修改记录
        if (isFirst)
            sp.edit().putBoolean("first", false).commit();
        return isFirst;
    }
}
