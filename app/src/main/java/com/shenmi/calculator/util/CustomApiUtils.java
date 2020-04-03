package com.shenmi.calculator.util;

import android.content.Context;
import android.util.Log;

import com.eric.commonlibrary.bean.WebRequest;
import com.eric.commonlibrary.utils.AppUtils;
import com.google.gson.Gson;
import com.shenmi.calculator.bean.AppSwitchConfigInfoBean;
import com.shenmi.calculator.net.CustomAPIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Description: 获取开关
 * @Author: Mr
 * @CreateDate: 2020/3/24 14:34
 */

public class CustomApiUtils {

    public CustomApiUtils() {
    }

    public static void getAppSwitchConfig(Context context, String channel, String switchType, final OnApiResult onApiResult) {
        Retrofit retrofit = (new Retrofit.Builder()).baseUrl("http://118.190.166.164:95/").addConverterFactory(GsonConverterFactory.create(new Gson())).build();
        CustomAPIService apiService = retrofit.create(CustomAPIService.class);
        com.eric.commonlibrary.bean.WebRequest.Builder webBuilder = new com.eric.commonlibrary.bean.WebRequest.Builder();
        WebRequest webRequest = webBuilder.setAppName(AppUtils.getAppName(context)).setAppVersion(AppUtils.getVersionName(context) + "." + AppUtils.getVersionCode(context)).setChannel(channel).setDeviceId(AppUtils.getDevicedId(context)).setPackageName(AppUtils.getPackageName(context)).setSwitchType(switchType).build();
        Log.e("getAppSwitchConfig", "webRequest:" + webRequest.toString());
        Call<AppSwitchConfigInfoBean> webOpenRequest = apiService.getOpenADRequest(webRequest);
        webOpenRequest.enqueue(new Callback<AppSwitchConfigInfoBean>() {
            public void onResponse(Call<AppSwitchConfigInfoBean> call, Response<AppSwitchConfigInfoBean> response) {
                if (response != null && response.body() != null) {
                    AppSwitchConfigInfoBean appSwitchConfigInfo = response.body();
                    Log.e("getAppSwitchConfig", "webResponse:" + appSwitchConfigInfo.toString());
                    if (response != null && response.body() != null && (response.body()).getData() != null) {
                        onApiResult.onResponse(appSwitchConfigInfo.getData().isOpenAD(),appSwitchConfigInfo.getData().getIsShowOrder());
                    } else {
                        onApiResult.onResponse(false,1);
                    }

                }
            }

            public void onFailure(Call<AppSwitchConfigInfoBean> call, Throwable t) {
                Log.e("getAppSwitchConfig", "onFailure:" + t.getMessage());
                onApiResult.onFailure(t.getMessage());
            }
        });
    }

    public interface OnApiResult {
        void onResponse(boolean var1, int var2);

        void onFailure(String var1);
    }
}
