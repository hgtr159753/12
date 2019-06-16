package com.shenmi.calculator.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.shenmi.calculator.ui.home.SplashActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SQ on 2019/4/8.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Context mContext;

    public CrashHandler(Context context){
        this.mContext=context;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        showToast(t);
    }

    /**
     * 操作
     * @param thread
     */
    private void showToast(Thread thread) {
        Log.e("uncaughtException","uncaughtException");
        restartApp();
    }

    /**
     * 重启应用
     */
    private void restartApp(){
        Intent intent = new Intent(mContext,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());//再此之前可以做些退出等操作
    }
}