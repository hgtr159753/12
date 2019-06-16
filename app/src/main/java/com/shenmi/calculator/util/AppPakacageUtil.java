package com.shenmi.calculator.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by SQ on 2019/3/28.
 */

public class AppPakacageUtil {

    /**
     *
     * @param context
     * @return
     */
    public static String getPackageCode(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        int code = 1;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } return (name+"."+code);
    }
}
