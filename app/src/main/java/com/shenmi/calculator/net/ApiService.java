package com.shenmi.calculator.net;

import com.shenmi.calculator.bean.WebRequest;
import com.shenmi.calculator.bean.WebResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by SQ on 2018/12/19.
 */

public interface ApiService {

    @POST("api/APPConfig/GetAppSwitchConfig")
    Call<WebResponse> getWebOpenRequest(@Body WebRequest webRequest);

}
