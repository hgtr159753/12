package com.shenmi.calculator.ui.home;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
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
import com.shenmi.calculator.constant.ADConstant;
import com.shenmi.calculator.constant.ConstantWeb;
import com.shenmi.calculator.net.ApiService;
import com.shenmi.calculator.bean.WebRequest;
import com.shenmi.calculator.bean.WebResponse;
import com.shenmi.calculator.util.AppContentUtil;
import com.shenmi.calculator.util.NetworkUtil;
import com.snmi.sdk.AdView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
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


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private String[] mPermissionList = new String[]{Manifest.permission.READ_PHONE_STATE};
    private VerticalViewPager mViewPager;
    private MainFragment mainFragment;
    private WebFragment webFragment;
    private ArrayList<Fragment> viewList;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private int CALL_PHONE_REQUEST_CODE = 10;

    private AdView mAdView;
    private HomeWatcherReceiver mHomeKeyReceiver;
    private boolean isAppFrondesk;
    private ActivityManager activityManager;
    private String packageName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initPermission();
        registerHomeKeyReceiver();
        //注册监听Home按键返回广播
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (EasyPermissions.hasPermissions(this, mPermissionList)) {
                //已经同意过
                initHttp();
            } else {
                //未同意过,或者说是拒绝了，再次申请权限
                EasyPermissions.requestPermissions(this,  //上下文
                        "需要获取设备的信息", //提示文言
                        CALL_PHONE_REQUEST_CODE, //请求码
                        mPermissionList //权限列表
                );
            }
        } else {
            //6.0以下，不需要授权
            initHttp();
        }
    }

    //同意授权
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //跳转到onPermissionsGranted或者onPermissionsDenied去回调授权结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.i("授权", "onPermissionsGranted:" + requestCode);
        initHttp();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.i("授权", "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
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

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        packageName = this.getPackageName();
        //检测APP处于前台还是后台
        new Thread(new AppStatus()).start();
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
                && (webFragment != null)
                && (webFragment.getWebview_main() != null)
                && (webFragment.getWebview_main().canGoBack())){
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
            Log.e("mrs", "finish");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                mAdView = new AdView(ADConstant.DEEPLINK_ONE, MainActivity.this,
                        ADConstant.APPID, true, true,
                        new BannerMonitor(ADConstant.DEEPLINK_ONE, MainActivity.this));
                }
            }, 2000);
            moveTaskToBack(true);
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


    /*****************************************************************/
    /**
     * 注册监听Home按键返回的广播
     */
    private void registerHomeKeyReceiver() {
        Log.e("mrs", "registerHomeKeyReceiver");
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        getApplicationContext().registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    /**
     * 销毁Home监听广播
     */
    private void unregisterHomeKeyReceiver() {
        Log.e("mrs", "unregisterHomeKeyReceiver");
        if (null != mHomeKeyReceiver) {
            getApplicationContext().unregisterReceiver(mHomeKeyReceiver);
        }
    }


    /**
     * @author Home键广播监听
     *         Created by Mr on 2018/12/18.
     */

    public class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(LOG_TAG, "onReceive: action: " + action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                Log.e(LOG_TAG, "reason: " + reason);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // 短按Home键
                    Log.e("mrs", "homekey");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isAppFrondesk) {
                                mAdView = new AdView(ADConstant.DEEPLINK_ONE, MainActivity.this,
                                        ADConstant.APPID, true, true,
                                        new BannerMonitor(ADConstant.DEEPLINK_ONE, MainActivity.this));
                            }
                        }
                    }, 1000);

                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // 长按Home键 或者 activity切换键
                    Log.e(LOG_TAG, "long press home key or activity switch");

                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    // 锁屏
                    Log.e(LOG_TAG, "lock");
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung 长按Home键
                    Log.e(LOG_TAG, "assist");
                }
            }
        }
    }

    private class AppStatus implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if (appOnForeground()) {
                        isAppFrondesk = false;
                    } else {
                        isAppFrondesk = true;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterHomeKeyReceiver();
    }
}
