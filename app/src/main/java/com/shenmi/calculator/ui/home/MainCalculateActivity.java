package com.shenmi.calculator.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.haibin.calendarview.calendar.calendar.fragment.CalendarFragment;
import com.shenmi.calculator.R;
import com.shenmi.calculator.constant.ADConstant;
import com.shenmi.calculator.util.AppMarketUtil;
import com.shenmi.calculator.util.CustomApiUtils;
import com.shenmi.calculator.util.DateUtil;
import com.shenmi.calculator.util.SPUtil;
import com.snmi.baselibrary.utils.CommonUtils;
import com.snmi.sdk.Ad;
import com.snmi.sdk.AdView;
import com.snmi.sdk.SplashADInfo;
import com.snmi.sdk_3.Hs;
import com.tencent.bugly.Bugly;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MainCalculateActivity extends AppCompatActivity{

    private MainFragment mainFragment;
    private WebFragment webFragment;
    private List<Fragment> viewList;
    private final int CALL_PHONE_REQUEST_CODE = 10;

    private AdView mAdView;
    private boolean isAppFrondesk;
    private ActivityManager activityManager;
    private String packageName;
    private RadioGroup radioGroupMain;
    private LinearLayout llBook;
    private CalendarFragment calendarFragment;
    private boolean stop;
    private SplashADInfo splashAD;
    private Boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushAgent.getInstance(this).onAppStart();
        //腾讯bugly初始化
        String channelName = AnalyticsConfig.getChannel(this);
        Bugly.setAppChannel(this, channelName);
        Bugly.init(getApplicationContext(), "db5fbfbb11", false);

        setContentView(R.layout.activity_main);
        isOpen = (Boolean) SPUtil.get(this, ADConstant.ISOPENAD, false);

        initAD();
        initTime();
        initView();
        initPermission();
        CommonUtils.startPrivacyDialog(this, new CommonUtils.OnClick() {
            @Override
            public void onLeftClick() {

            }

            @Override
            public void onRight() {

            }
        });
    }

    /**
     * 第二次进来提示用户评价
     */
    private void initTime(){
        int times = (int) SPUtil.get(this, "times", 1);
        if (times %3 == 0){
            if (DateUtil.isOnOneMonth(this)){
                //到了一个月
                AppMarketUtil.goThirdApp(this);
            }
        }
        SPUtil.put(this,"times",++times);
    }

    private void initAD() {
        if (isOpen)
            Hs.config(this,ADConstant.SCREEN_LOCK,ADConstant.SCREEN_LOCK);
        Ad.configAD(getApplicationContext());
        Ad.initLockerAd(getApplicationContext(), ADConstant.APPID, "C28D978B85334C32849883788F045DC6");
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
            Log.e("config","initPermission");
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
                Toast.makeText(this,"拒绝权限会时部分功能无法使用哦~",Toast.LENGTH_SHORT).show();
                //跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            }else{
                Log.e("config","onRequestPermissionsResult");
                //全部权限通过，可以进行下一步操作
                initHttp();
            }
        }
    }

    private void initHttp() {
        CustomApiUtils.getAppSwitchConfig(this, PushAgent.getInstance(this).getMessageChannel(), "news", new CustomApiUtils.OnApiResult() {
            @Override
            public void onResponse(boolean var1, int var2) {
                SPUtil.put(MainCalculateActivity.this,ADConstant.ISOPENAD,var1);
                // 保存展示顺序
                SPUtil.put(MainCalculateActivity.this,ADConstant.ISADODDER,var2);
            }

            @Override
            public void onFailure(String msg) {
                SPUtil.put(MainCalculateActivity.this,ADConstant.ISOPENAD,true);
            }
        });
    }

    private void initView() {
        radioGroupMain = findViewById(R.id.rg_main);
        viewList = new ArrayList<>();
        mainFragment = new MainFragment();
        calendarFragment = CalendarFragment.newInstance();
        viewList.add(mainFragment);
        viewList.add(calendarFragment);
        if (isOpen){
            //开启新闻
            webFragment = new WebFragment();
            viewList.add(webFragment);
            radioGroupMain.setVisibility(View.VISIBLE);
        }else{
            radioGroupMain.setVisibility(View.GONE);
        }
        //默认选中主页
        radioGroupMain.setOnCheckedChangeListener(onChangedListener);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fl_content, viewList.get(0)).commitAllowingStateLoss();
        onChangedListener.onCheckedChanged(radioGroupMain, R.id.rb_calculate);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        packageName = this.getPackageName();
        //检测APP处于前台还是后台
       /* if (isOpen)
        new Thread(new AppStatus()).start();*/
    }

    private RadioGroup.OnCheckedChangeListener onChangedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (checkedId){
                case R.id.rb_calculate:
                    if(!calendarFragment.isAdded()){
                        ft.add(R.id.fl_content, calendarFragment);
                    }
                    if (calendarFragment!=null) {
                        ft.hide(calendarFragment);
                    }
                    if (webFragment!=null) {
                        ft.hide(webFragment);
                    }
                    ft.show(mainFragment).commitAllowingStateLoss();
                    break;
                case R.id.rb_money:
                    if (webFragment!=null)
                        ft.hide(webFragment);
                    if (mainFragment!=null)
                        ft.hide(mainFragment);
                    if(!calendarFragment.isAdded()){
                        ft.add(R.id.fl_content, calendarFragment);
                    }
                    ft.show(calendarFragment).commitAllowingStateLoss();
                    break;
                case R.id.rb_news:
                    if(!calendarFragment.isAdded()){
                        ft.add(R.id.fl_content, calendarFragment);
                    }
                    if (calendarFragment!=null) {
                        ft.hide(calendarFragment);
                    }
                    if (mainFragment!=null) {
                        ft.hide(mainFragment);
                    }
                    if(!webFragment.isAdded()){
                        ft.add(R.id.fl_content, webFragment);
                    }
                    ft.show(webFragment).commitAllowingStateLoss();
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
            //moveTaskToBack(true);
            finish();
        } else {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }
    }

    private long currentTimes = System.currentTimeMillis();
    private class AppStatus implements Runnable {
        @Override
        public void run() {
            stop = false;
            while (!stop) {
                try {
                    if (appOnForeground()) {
                        // Log.e("mrs", "-----------------前台--------------");
                        isAppFrondesk = false;
                        if (isStartSplash && ADConstant.IS_SCREEN){
                            showSplash();
                        }
                        currentTimes = System.currentTimeMillis();
                    } else {
                        isStartSplash = true;
                        //Log.e("mrs", "-----------------后台--------------");
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
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 调用开屏
     */
    boolean isStartSplash;
    public void showSplash() {
        if (!isStartSplash)
            return;
        isStartSplash = false;
        //Log.e("mrs", "-----------------前台--------------"+currentTimes);
        if (System.currentTimeMillis()-currentTimes>(25*1000)){
            Log.e("mrs", "----------------开启开屏--------------");
            handler.sendEmptyMessageDelayed(2, 10);
        }
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Hs.clear(this);
    }
}
