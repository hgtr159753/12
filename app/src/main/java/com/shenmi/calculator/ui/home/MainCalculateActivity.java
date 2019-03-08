package com.shenmi.calculator.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.shenmi.calculator.R;
import com.shenmi.calculator.adapter.MyFragmentPagerAdapter;
import com.shenmi.calculator.constant.ADConstant;
import com.shenmi.calculator.constant.ConstantWeb;
import com.shenmi.calculator.net.ApiService;
import com.shenmi.calculator.bean.WebRequest;
import com.shenmi.calculator.bean.WebResponse;
import com.shenmi.calculator.util.AppContentUtil;
import com.shenmi.calculator.util.AppMarketUtil;
import com.shenmi.calculator.util.NetworkUtil;
import com.shenmi.calculator.util.SPUtil;
import com.shenmi.calculator.util.SharedPUtils;
import com.sm.readbook.utils.SPUtils;
import com.snmi.sdk.Ad;
import com.snmi.sdk.AdHCallback;
import com.snmi.sdk.AdView;
import com.snmi.sdk.SplashADInfo;
import com.snmi.sdk.SplashFullScreenAD;
import com.snmi.sdk.download.GPSlistener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.zchu.reader.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainCalculateActivity extends AppCompatActivity implements  View.OnClickListener {

    private MainFragment mainFragment;
    private WebFragment webFragment;
    private List<Fragment> viewList;
    private final int CALL_PHONE_REQUEST_CODE = 10;

    private AdView mAdView;
//    private HomeWatcherReceiver mHomeKeyReceiver;
    private boolean isAppFrondesk;
    private ActivityManager activityManager;
    private String packageName;
    private RadioGroup radioGroupMain;
    private RadioButton radioButtonNews;
    private RadioButton radioButtonMoney;
    private LinearLayout llBook;
    private MoneyFragment moneyFragment;
    private boolean stop;
    private SplashADInfo splashAD;
    private Boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        setContentView(R.layout.activity_main);
        isOpen = (Boolean) SPUtil.get(this, ADConstant.ISOPENAD, false);
        initTime();
        initView();
        initPermission();
    }

    /**
     * 第二次进来提示用户评价
     */
    private void initTime() {
        int times = (int) SPUtil.get(this, "times", 1);
        if (times == 2){
            AppMarketUtil.goThirdApp(this);
        }
        SPUtil.put(this,"times",++times);
    }

    private void initAD() {
        if (isOpen)
        Ad.prepareSplashAd(this, ADConstant.APPID,ADConstant.START_SCREEN);
    }

    String[] permissions = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    List<String> mPermissionList = new ArrayList<>();
    private final int mRequestCode = 100;
    /**
     * 请求读写权限
     */
    private void initPermission(){
        mPermissionList.clear();
        // 清空没有通过的权限
        // 逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                // 添加还未授予的权限
                mPermissionList.add(permissions[i]);
            }
        }
        // 申请权限
        if (mPermissionList.size() > 0) {
            // 有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        }else{
            // 说明权限都已经通过，可以做你想做的事情去
            initHttp();
        }
    }

    //请求权限后回调的方法
    //参数： requestCode  是我们自己定义的权限请求码
    //参数： permissions  是我们请求的权限名称数组
    //参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;
        //有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                ToastUtils.showToast(this,"拒绝权限会时部分功能无法使用哦~");
                //跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
//                btn_sure.setClickable(false);
            }else{
                //全部权限通过，可以进行下一步操作
                initHttp();
            }
        }
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
                if (response.body() == null){
                    return;
                }
                WebResponse webResponse = response.body();
                Log.e("webRequest","webResponse=="+webResponse.toString());
                if (webResponse.getApiStatusCode() == 200){
                    //判断是否显示广告
                    if (webResponse.getAppSwitchConfigInfo().getOpenCalculatorAD()){
                        SPUtil.put(MainCalculateActivity.this,ADConstant.ISOPENAD,true);
                    }else{
                        SPUtil.put(MainCalculateActivity.this,ADConstant.ISOPENAD,false);
                    }
                    //是否有一个栏目显示了
                    boolean isNone = false;
//                    if (webResponse.getAppSwitchConfigInfo().getOpenZhuanDianBa()){
//                        moneyFragment = new MoneyFragment();
//                        viewList.add(moneyFragment);
//                        isNone = true;
//                    }else{
//                        radioButtonMoney.setVisibility(View.GONE);
//                    }
                    if (webResponse.getAppSwitchConfigInfo().getOpenNews()){
                        webFragment = new WebFragment();
                        viewList.add(webFragment);
                    }else{
                        radioButtonNews.setVisibility(View.GONE);
                        isNone = true;
                    }
                    if (!webResponse.getAppSwitchConfigInfo().getOpenReadBook()){
                        llBook.setVisibility(View.GONE);
                    }
                    //显示
                    if (isNone){
                        //如果赚钱、新闻、小说都不显示，那么也不显示导航栏
                        radioGroupMain.setVisibility(View.GONE);
                    }
                    radioGroupMain.setOnCheckedChangeListener(onChangedListener);
                }
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {

            }
        });
    }

    private void initView() {
        radioGroupMain = findViewById(R.id.rg_main);
        radioButtonNews = findViewById(R.id.rb_news);
        radioButtonMoney = findViewById(R.id.rb_money);
        llBook = findViewById(R.id.ll_book);
        llBook.setOnClickListener(this);


        viewList = new ArrayList<>();
        mainFragment = new MainFragment();
        viewList.add(mainFragment);
        //默认选中主页
        radioGroupMain.setOnCheckedChangeListener(onChangedListener);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_content, viewList.get(0)).commit();
        onChangedListener.onCheckedChanged(radioGroupMain, R.id.rb_calculate);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        packageName = this.getPackageName();
        //检测APP处于前台还是后台
        if (isOpen)
        new Thread(new AppStatus()).start();
    }

    private RadioGroup.OnCheckedChangeListener onChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (checkedId){
                case R.id.rb_calculate:
                    if (moneyFragment!=null)
                        ft.hide(moneyFragment);
                    if (webFragment!=null)
                        ft.hide(webFragment);
                    ft.show(mainFragment).commit();
                    break;
                case R.id.rb_money:
                    if (webFragment!=null)
                        ft.hide(webFragment);
                    ft.hide(mainFragment);
                    if(!moneyFragment.isAdded()){
                        ft.add(R.id.fl_content, moneyFragment);
                    }
                    ft.show(moneyFragment).commit();
                    break;
                case R.id.rb_news:
                    if (moneyFragment!=null)
                        ft.hide(moneyFragment);
                    ft.hide(mainFragment);
                    if(!webFragment.isAdded()){
                        ft.add(R.id.fl_content, webFragment);
                    }
                    ft.show(webFragment).commit();
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
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
            Log.e("mrs", "finish");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isOpen)
                mAdView = new AdView(ADConstant.DEEPLINK_ONE, MainCalculateActivity.this,
                        ADConstant.APPID, true, true,
                        new BannerMonitor(ADConstant.DEEPLINK_ONE, MainCalculateActivity.this));
                }
            }, 2000);
            moveTaskToBack(true);
        } else {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_book:
                com.sm.readbook.ui.activity.MainActivity.startAction(MainCalculateActivity.this);
                break;
        }
    }

    private class AppStatus implements Runnable {
        @Override
        public void run() {
            stop = false;
            while (!stop) {
                try {
                    if (appOnForeground()) {
                         Log.e("mrs", "-----------------前台--------------");
                        isAppFrondesk = false;
                        if (isStartSplash && ADConstant.IS_SCREEN){
                            showSplash();
                        }
                    } else {
                        isStartSplash = true;
                        Log.e("mrs", "-----------------后台--------------");
                        showSnMiSDK();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void onResume() {
        super.onResume();
        initAD();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 调用广告
     */
    public void showSnMiSDK() {
        if (isAppFrondesk)
            return;
        handler.sendEmptyMessageDelayed(1, 50);
//        Log.e("mrs", "==========showSnMiSDK===========");
        isAppFrondesk = true;
    }

    /**
     * 调用开屏
     */
    boolean isStartSplash;
    public void showSplash() {
        if (!isStartSplash)
            return;
        isStartSplash = false;
        handler.sendEmptyMessageDelayed(2, 10);
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Ad.prepareSplashAd(MainCalculateActivity.this, ADConstant.APPID, ADConstant.DEEPLINK_ONE, new AdHCallback() {
                        @Override
                        public void adSuccess() {
                            Log.d("mrs", "==========成功===========");
                        }
                    });
                    break;
                case 2:
                    synchronized(this) {
                        isStartSplash = false;
                        Log.e("mrs", "==========showSplash===========");
                        if (!ADConstant.IS_SCREEN) {
                            //没有开屏，就不打开
                            Log.e("mrs", "==========没有开屏===========");
                        } else {
                            Log.e("mrs", "==========有开屏===========");
                            Intent intent = new Intent(MainCalculateActivity.this, SplashActivity.class);
                            startActivity(intent);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 检测APP处于前后台
     */
    private boolean appOnForeground() {
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }


    public class BannerMonitor implements com.snmi.sdk.BannerListener {
        private String loactionID;
        private Context mContext;

        public BannerMonitor(String locationID, Context context) {
            this.loactionID = locationID;
            this.mContext = context;
        }

        @Override
        public void bannerClicked() {
            //Toast.makeText(getApplicationContext(), "banner点击了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void adpageClosed() {
            //Toast.makeText(getApplicationContext(), "来自banner的广告详情页关闭了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void bannerClosed() {
            //Toast.makeText(getApplicationContext(), "banner关闭了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void bannerShown(String json) {
            //Toast.makeText(getApplicationContext(), "banner展示了", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void noAdFound() {
            //Toast.makeText(getApplicationContext(), "banner无广告", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void qtClicked(String s) {

        }
    }

}
