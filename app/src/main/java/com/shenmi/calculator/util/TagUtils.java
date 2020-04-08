package com.shenmi.calculator.util;

import android.app.Activity;

import com.eric.commonlibrary.utils.AppUtils;
import com.shenmi.calculator.app.MyApplication;
import com.zchu.rxcache.utils.LogUtils;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class TagUtils {


    private static String URL_API = "http://47.104.12.170/";

    public static ExecutorService singleThread = Executors.newSingleThreadExecutor();

    public static void tryUp(final String msg) {
        singleThread.execute(() -> {
            Call<String> stringCall = getHttp().clickName(msg, AppUtils.getDevicedId(MyApplication.getAppContext()),
                    AppUtils.getVersionName(MyApplication.getAppContext()),
                    AppUtils.getPackageName(MyApplication.getAppContext()));
            try {
                Response<String> execute = stringCall.execute();
                LogUtils.debug(execute.code() + ":" + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    static SoftReference<API> apiSoft = new SoftReference(initHttp());

    private static API initHttp() {
        API api = new Retrofit.Builder().baseUrl(URL_API).addConverterFactory(GsonConverterFactory.create()).build().create(API.class);
        return api;
    }

    private static API getHttp() {
        if (apiSoft.get() == null) {
            apiSoft = new SoftReference(initHttp());
        }
        return apiSoft.get();
    }

    interface API {
        //点击事件名称
        @GET("AppHttpReported")
        public Call<String> clickName(@Query("clickName") String clickName, @Query("deviceId") String deviceId, @Query("appVersion") String appVersion, @Query("pkgName") String pkgName);

        //停留时长
        public Call<String> StayTime(@Query("StayTime") String stayTime, @Query("deviceId") String deviceId, @Query("appVersion") String appVersion, @Query("pkgName") String pkgName);

        //当前时间
        public Call<String> currentTime(@Query("clickName") String clickName, @Query("deviceId") String deviceId, @Query("appVersion") String appVersion, @Query("pkgName") String pkgName);

        //App进入App时间
        public Call<String> intoTime(@Query("intoTime") String intoTime, @Query("currentTime") String currentTime, @Query("deviceId") String deviceId, @Query("appVersion") String appVersion, @Query("pkgName") String pkgName);

        //离开APP时间
        public Call<String> fromTime(@Query("fromTime") String fromTime, @Query("deviceId") String deviceId, @Query("appVersion") String appVersion, @Query("pkgName") String pkgName);

        //完成事件名称
        public Call<String> eventSuccess(@Query("eventSuccess") String eventSuccess, @Query("deviceId") String deviceId, @Query("appVersion") String appVersion, @Query("pkgName") String pkgName);

        //页面请求是否显示
        public Call<String> pageIsShow(@Query("pageIsShow") String pageIsShow, @Query("deviceId") String deviceId, @Query("appVersion") String appVersion, @Query("pkgName") String pkgName);

    }
}