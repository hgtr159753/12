package com.shenmi.calculator.net;

import com.shenmi.calculator.bean.AppSwitchConfigInfoBean;
import com.snmi.baselibrary.bean.WebRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @Description: java类作用描述
 * @Author: liys
 * @CreateDate: 2020/3/24 14:39
 */

public interface CustomAPIService {

    @POST("api/wifiTask/getAppSwtichConfig")
    Call<AppSwitchConfigInfoBean> getOpenADRequest(@Body WebRequest var1);
}
