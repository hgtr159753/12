package com.shenmi.calculator.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.shenmi.calculator.app.MyApplication;

/**
 * Created by SQ on 2019/2/25.
 */

public class AppMarketUtil {

    private static String HUAWEI = "com.huawei.appmarket";
    private static String YINGYONGBAO = "com.tencent.android.qqdownloader";
    private static String SAMSUNG = "com.sec.android.app.samsungapps";
    private static String PACAKAGE = "com.shenmi.calculator";

    /** 跳转到腾讯应用宝下载软件 */
    public static void goThirdApp(Context context) {
        if (isAvilible(MyApplication.getAppContext(), HUAWEI)) {
            // 华为市场存在
            openDialog(context,HUAWEI);
        }
        else if (isAvilible(MyApplication.getAppContext(), YINGYONGBAO)) {
            // 应用宝市场存在
            openDialog(context,YINGYONGBAO);
        }
        else if (isAvilible(MyApplication.getAppContext(), SAMSUNG)) {
            // 三星市场存在
            openDialog(context,SAMSUNG);
        }
    }

    private static void openDialog(Context appContext, final String market) {
        AlertDialog dialog = new AlertDialog.Builder(appContext)
                .setCancelable(false)
                .setMessage("小主，能用应用市场给个好评吗?")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppStore(MyApplication.getAppContext(),PACAKAGE, market);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    /** 启动到app详情界面 */
    public static void startAppStore(Context context, String appPkg, String marketPkg) {
        try {
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("市场","startAppStore="+e.getMessage());
            e.printStackTrace();
        }
    }

    /** 判断软件是否存在 */
    public static boolean isAvilible(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("市场","PackageManager="+e.getMessage());
            return false;
        }
    }
}
