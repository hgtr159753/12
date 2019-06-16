package com.shenmi.calculator.net;

import com.shenmi.calculator.bean.WebRequest;
import com.shenmi.calculator.bean.WebResponse;
import com.shenmi.calculator.bean.rate.RateResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by SQ on 2018/12/19.
 */

public interface ApiService {

    @POST("api/APPConfig/GetAppSwitchConfigNew")
    Call<WebResponse> getWebOpenRequest(@Body WebRequest webRequest);

    @Headers({
            "Cache-Control:private",
            "Connection:Keep-Alive",
            "Content-Type:application/json;charset=gbk"
            })
    @GET("8aQDcjqpAAV3otqbppnN2DJv/api.php")
    Call<RateResponse> getRateRequest(@Query("query") String query
            , @Query("resource_id") int resource_id
            , @Query("t") String t
    );

// query=2英镑等于多少人民币
// &co=
// &resource_id=6017
// &t=1555379362160
// &cardId=6017
// &ie=utf8
// &oe=gbk
// &cb=op_aladdin_callback
// &format=json
// &tn=baidu
// &cb=jQuery110205459430690946229_1555377195865
// &_=1555377195897
}
