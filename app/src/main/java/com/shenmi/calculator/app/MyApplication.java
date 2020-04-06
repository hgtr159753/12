package com.shenmi.calculator.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.iflytek.cloud.SpeechUtility;
import com.shenmi.calculator.constant.ADConstant;
import com.shenmi.calculator.db.ObjectBox;
import com.shenmi.calculator.util.TTAdManagerHolder;
import com.snmi.sdk.Ad;
import com.snmi.sdk_3.Hs;
import com.snmi.sdk_3.util.HsHelper;
import com.snmi.sdk_3.util.SDKHelper;
import com.tencent.bugly.Bugly;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * Created by SQ on 2018/4/9.
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication mAppContext;
    private static String channelName;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppContext = this;
        //公共区域
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "afd7439042e4225d6dcbda9a80a5ef4c");
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
                Log.e("Token", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("Token", s + s1);
            }
        });
//        //腾讯bugly初始化
        channelName = AnalyticsConfig.getChannel(this);
        //初始化讯飞
        // 设置你申请的应用appid
        SpeechUtility.createUtility(this, "appid=5bd15cce");
        mPushAgent.addAlias("calendar", "aplsh", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {

            }
        });
        SDKHelper.newInstance().register(this, ADConstant.DEEPLINK_ONE, ADConstant.DEEPLINK_ONE, new SDKHelper.SDKHelperListener() {
            @Override
            public void success() {
                Log.e("SDKHelper", "SDKHelper");
            }
        });
        HsHelper.newInstance().register(this);
        //初始化okhttp
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
//        CookieJarImpl cookieJar1 = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        TTAdManagerHolder.init(this);
        ObjectBox.init(this);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        //onTerminate中注销
        HsHelper.newInstance().unRegister();
//        SDKHelper.newInstance().unRegister();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public static MyApplication getAppContext() {
        return mAppContext;
    }

    public static String getAppChannelName() {
        return channelName;
    }

}
