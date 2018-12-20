package com.shenmi.calculator.ui.home;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;
import com.google.gson.Gson;
import com.shenmi.calculator.R;
import com.shenmi.calculator.adapter.MyFragmentPagerAdapter;
import com.shenmi.calculator.constant.ConstantWeb;
import com.shenmi.calculator.net.ApiService;
import com.shenmi.calculator.net.WebRequest;
import com.shenmi.calculator.net.WebResponse;
import com.shenmi.calculator.util.AppContentUtil;
import com.shenmi.calculator.util.NetworkUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private VerticalViewPager mViewPager;
    private MainFragment mainFragment;
    private WebFragment webFragment;
    private ArrayList<Fragment> viewList;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initHttp();
    }

    private void initHttp() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConstantWeb.MAINURL)
                //可以接收自定义的Gson，当然也可以不传
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
        ApiService apiService = retrofit.create(ApiService.class);
        WebRequest.Builder webBuilder = new WebRequest.Builder();
        WebRequest webRequest = webBuilder.setAppName(AppContentUtil.getAppName(this))
                .setAppVersion(AppContentUtil.getVersionName(this)+"."+
                        AppContentUtil.getVersionCode(this))
                .setChannel(PushAgent.getInstance(this).getMessageChannel())
                .setDeviceId(AppContentUtil.getDevicedId(this))
                .setPackageName(AppContentUtil.getPackageName(this))
                .setSwitchType("news")
                .build();
        Log.e("webRequest",webRequest.toString());
        Call<WebResponse> webOpenRequest = apiService.getWebOpenRequest(webRequest);
        webOpenRequest.enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call, Response<WebResponse> response) {
                WebResponse webResponse = response.body();
                if (webResponse.getApiStatusCode() == 200){
                    if (webResponse.getAppSwitchConfigInfo().getOpen()){
                        webFragment = new WebFragment();
                        viewList.add(webFragment);
                        myFragmentPagerAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {

            }
        });
    }

    private void initView() {
        mViewPager = findViewById(R.id.viewpager_main);
        viewList = new ArrayList<>();
        mainFragment = new MainFragment();
        viewList.add(mainFragment);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), viewList);
        mViewPager.setAdapter(myFragmentPagerAdapter);
        mViewPager.setOnPageChangeListener(pageChangedlistener);
    }


    private ViewPager.OnPageChangeListener pageChangedlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //只在webview显示的时候禁止view pager上下滑动
            if (position == 1){
                mViewPager.setTouch(false);
            }else{
                mViewPager.setTouch(true);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 滑动page到第一页
     */
    public void setPositionPage(){
        mViewPager.setCurrentItem(0);
    }

    /**
     * 获取当前位置
     * @return
     */
    public int getPosition(){
        return mViewPager.isTouch()? 0:1;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //这是一个监听用的按键的方法，keyCode 监听用户的动作，如果是按了返回键，同时Webview要返回的话，WebView执行回退操作，因为mWebView.canGoBack()返回的是一个Boolean类型，所以我们把它返回为true
        if(keyCode==KeyEvent.KEYCODE_BACK
                && NetworkUtil.isNetworkAvailable(this)
                && webFragment.getWebview_main().canGoBack()){
            webFragment.getWebview_main().goBack();
            return true;
        }
        if (!mViewPager.isTouch()){
            setPositionPage();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            doubleClickQuitBrowser();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private long mExitTime;
    public void doubleClickQuitBrowser() {
        long timeSpan = System.currentTimeMillis() - mExitTime;
        if (timeSpan < 1000) {
            finish();
        } else {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
