package com.shenmi.calculator.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.iflytek.cloud.SpeechUtility;
import com.sm.readbook.AppManager;
import com.sm.readbook.ReadSDK;
import com.sm.readbook.data.db.DBRepository;
import com.snmi.sdk.Ad;
import com.tencent.bugly.Bugly;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

/**
 * Created by SQ on 2018/4/9.
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication mAppContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        Ad.configAD(this);
        DBRepository.initDatabase(this);
        AppManager.init(this);
        ReadSDK.initSDK(mAppContext);
        //公共区域
        UMConfigure.init(this,UMConfigure.DEVICE_TYPE_PHONE, "afd7439042e4225d6dcbda9a80a5ef4c");
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

        UMConfigure.setLogEnabled(true);
        MobclickAgent.setCatchUncaughtExceptions(true);
        UMConfigure.setEncryptEnabled(true);
        //推送相关
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
                Log.e("Token",deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.e("Token",s + s1);
            }
        });
        //腾讯bugly初始化
        String channelName = AnalyticsConfig.getChannel(this);
        Bugly.setAppChannel(this, channelName);
        Bugly.init(getApplicationContext(), "e968353f92", false);
        //初始化讯飞
        // 设置你申请的应用appid
        SpeechUtility.createUtility(this, "appid=5bd15cce");
        mPushAgent.addAlias("calendar", "aplsh", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static MyApplication getAppContext() {
        return mAppContext;
    }

}
