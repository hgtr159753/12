package com.shenmi.calculator.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;
import com.shenmi.calculator.R;
import com.shenmi.calculator.constant.ADConstant;
import com.shenmi.calculator.util.CustomApiUtils;
import com.shenmi.calculator.util.DensityUtil;
import com.shenmi.calculator.util.SPUtil;
import com.shenmi.calculator.util.TTAdManagerHolder;
import com.snmi.baselibrary.activity.CommonWebViewActivity;
import com.snmi.baselibrary.utils.AppUtils;
import com.snmi.sdk.Ad;
import com.snmi.sdk.SplashADInfo;
import com.snmi.sdk.SplashFullScreenAD;
import com.snmi.sdk_3.Hs;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity implements SplashADListener {

    private RelativeLayout container;
    private RelativeLayout adsRl;
    private int i = 0;
    ImageView imgSplash;
    TextView mTvSkip;
    boolean isClickedAD = false;
    private float ClickX;
    private float ClickY;
    private String[] umb = new String[4];
    SplashADInfo splashAD;
    private SmHandler handler;
    private boolean isOpen;
    //计时器
    private MyCountDownTimer mCountDownTimer;
    private TTAdNative mTTAdNative;
    private TextView js_text;

    private String mCodeId = "887295142";
    private boolean mIsExpress = true; //是否请求模板广告
    private static final int AD_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("mrs", "onCreate");
        setContentView(R.layout.activity_splash);
        initView();
        handler = new SmHandler(this);
        initHttp();

    }

    private void initView() {
        container = findViewById(R.id.rl_splash);
        adsRl = findViewById(R.id.adsRl);
        mTvSkip = findViewById(R.id.tv_skip);
        imgSplash = findViewById(R.id.start_img);
        js_text = findViewById(R.id.js_text);
        js_text.setText(AppUtils.getAppName(this));
        mTvSkip.setOnClickListener(mTvSkipOnClickListener);
    }

    private void initHttp() {
        CustomApiUtils.getAppSwitchConfig(this, PushAgent.getInstance(this).getMessageChannel(), "news", new CustomApiUtils.OnApiResult() {
            @Override
            public void onResponse(boolean var1, int var2) {
                SPUtil.put(SplashActivity.this,ADConstant.ISOPENAD,var1);
                // 保存展示顺序
                SPUtil.put(SplashActivity.this,ADConstant.ISADODDER,var2);
                initSplash();
            }

            @Override
            public void onFailure(String msg) {
                SPUtil.put(SplashActivity.this,ADConstant.ISOPENAD,true);
                initSplash();
            }
        });
    }


    private void initSplash() {
        isOpen = (Boolean) SPUtil.get(this, ADConstant.ISOPENAD, false);
        if (isOpen) {
            initAD();
        } else {
            Log.e("isOpen", isOpen + "");
            //没有广告的时候2秒跳过
            mTvSkip.setText("4s 跳过");
            //创建倒计时类
            mCountDownTimer = new MyCountDownTimer(2000, 1000);
            mCountDownTimer.start();
            //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
            handler.postDelayed(callbacks, 2000);
        }
    }

    private void initAD() {
        if (!isOpen) {
            return;
        }
        // 判断优先展示那个GG
        int isOrder = (int) SPUtil.get(this, ADConstant.ISADODDER, 1);
        //isOrder = 2;
        if (isOrder == 1) {
            // SM
            initSMad();
            MobclickAgent.onEvent(this, "SMADHTTP");
        } else if (isOrder == 2) {
            // CSJ
            csjAD();
            MobclickAgent.onEvent(this, "CSJADHTTP");
        } else {
            // GDT
            fetchSplashAD(this, adsRl, mTvSkip, ADConstant.APPID_TENCENT,
                    ADConstant.SplashPosID_TENCENT, this, 1);
            MobclickAgent.onEvent(this, "GDTADHTTP");
        }
    }

    //初始化SM SDK
    @SuppressLint("ClickableViewAccessibility")
    public void initSMad() {
        final SplashFullScreenAD splashFullScreenAD = new SplashFullScreenAD(this);
        final SplashADInfo splashAD = splashFullScreenAD.getSplashAD(ADConstant.START_SCREEN);
        if (splashAD == null) {
            // 没有开屏GG
            MobclickAgent.onEvent(this, "SMNOAD");
            fetchSplashAD(this, adsRl, mTvSkip, ADConstant.APPID_TENCENT,
                    ADConstant.SplashPosID_TENCENT, this, 1);
        } else {
            Log.e("mrs", "---------splash--------有开屏--------------");
            //有GG的时候4秒跳过
            MobclickAgent.onEvent(this, "SMADISSHOW");
            mTvSkip.setText("4s 跳过");
            mTvSkip.setVisibility(View.VISIBLE);
            //创建倒计时类
            mCountDownTimer = new MyCountDownTimer(4000, 1000);
            mCountDownTimer.start();
            //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
            handler.postDelayed(callbacks, 4000);

            ADConstant.IS_SCREEN = true;
            imgSplash.setImageBitmap(BitmapFactory.decodeFile(splashAD.picLocalPath));
            imgSplash.setClickable(true);
            // 展示开屏GG处理
            splashFullScreenAD.sendSplashADShowLog(splashAD, container);
            imgSplash.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ClickX = event.getRawX();
                            ClickY = event.getRawY();
                            umb[0] = "" + ClickX;
                            umb[1] = "" + ClickY;
                            Log.e("TAG", "实时位置ACTION_DOWN：(" + event.getRawX() + "," + event.getRawY());
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            if (event.getRawX() - ClickX < 20 && event.getRawY() - ClickY < 20) {
                                umb[2] = "" + event.getRawX();
                                umb[3] = "" + event.getRawY();
                                if (!isClickedAD) {
                                    isClickedAD = true;
                                    // 点击开屏广告处理
                                    splashFullScreenAD.clickSplashAD(splashAD, umb);
                                    MobclickAgent.onEvent(SplashActivity.this, "SMADCLICK");
                                }
                                Log.e("TAG", "实时位置ACTION_DOWN：(" + event.getRawX() + "," + event.getRawY());
                            }
                            break;
                    }
                    return true;
                }
            });
        }

    }

    /**
     * *********************************广点通开屏***********************************************
     */
    private SplashAD splashADScreen;

    /**
     * 拉取开屏广告，开屏广告的构造方法有3种，详细说明请参考开发者文档。
     *
     * @param activity      展示广告的 activity
     * @param adContainer   展示广告的大容器
     * @param skipContainer 自定义的跳过按钮：传入该 view 给 SDK 后，SDK 会自动给它绑定点击跳过事件。SkipView 的样式可以由开发者自由定制，其尺寸限制请参考 activity_splash.xml 或下面的注意事项。
     * @param appId         应用 ID
     * @param posId         广告位 ID
     * @param adListener    广告状态监听器
     * @param fetchDelay    拉取广告的超时时长：即开屏广告从请求到展示所花的最大时长（并不是指广告曝光时长）取值范围[3000, 5000]，设为0表示使用广点通 SDK 默认的超时时长。
     */
    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        MobclickAgent.onEvent(this, ADConstant.SPLASH_REQUEST);
        Log.e("AD_DEMO--", "SPLASH_REQUEST");
        splashADScreen = new SplashAD(activity, appId, posId, adListener, fetchDelay);
        splashADScreen.fetchAndShowIn(adContainer);

    }


    private void getExtraInfo() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String codeId = intent.getStringExtra("splash_rit");
        if (!TextUtils.isEmpty(codeId)) {
            mCodeId = codeId;
        }
        mIsExpress = intent.getBooleanExtra("is_express", false);
    }


    private Runnable callbacks = new Runnable() {
        public void run() {
            gotoMain();
        }
    };

    void gotoMain() {
        Log.d("splash", "goMain");
        Intent mainIntent = new Intent(this, MainCalculateActivity.class);
        startActivity(mainIntent);
        finish();
    }

    /*
     * 开屏GG点击弹出的Activity关闭之后的回调
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                gotoMain();
                break;
            default:
                break;
        }
    }

    /**
     * 穿山甲
     */
    private void csjAD() {
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        getExtraInfo();
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = null;
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int heigth = dm.heightPixels;
        int width = dm.widthPixels;

        if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
            //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
            float expressViewWidth = DensityUtil.getScreenWidthDp(this);
            float expressViewHeight = DensityUtil.getHeight(this);
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(width, (int) (heigth - DensityUtil.dip2px(SplashActivity.this, 80)))
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                    .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                    .build();
        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920)
                    .build();
        }
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
                Log.d("mrs", String.valueOf(message));
                //showToast(message);
                //gotoMain();
                MobclickAgent.onEvent(SplashActivity.this, "CSJADERROR");
                initSMad();
            }

            @Override
            @MainThread
            public void onTimeout() {
                //showToast("开屏加载超时");
                initSMad();
                MobclickAgent.onEvent(SplashActivity.this, "CSJADTIMEOUT");
                //gotoMain();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
                Log.d("mrs", "开屏请求成功");
                if (ad == null) {
                    return;
                }
                //获取SplashView
                View view = ad.getSplashView();
                if (view != null && adsRl != null && !SplashActivity.this.isFinishing()) {
                    adsRl.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    adsRl.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                } else {
                    gotoMain();
                }

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
                        Log.d("mrs", "onAdClicked");
                        //showToast("开屏点击");
                        MobclickAgent.onEvent(SplashActivity.this, "CSJADLOADCLICK");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
                        Log.d("mrs", "onAdShow");
                        //showToast("开屏展示");
                        MobclickAgent.onEvent(SplashActivity.this, "CSJADLOADSUCCESS");
                    }

                    @Override
                    public void onAdSkip() {
                        Log.d("mrs", "onAdSkip");
                        //showToast("开屏跳过");
                        MobclickAgent.onEvent(SplashActivity.this, "CSJADLOADSKIP");
                        gotoMain();

                    }

                    @Override
                    public void onAdTimeOver() {
                        Log.d("mrs", "onAdTimeOver");
                        //showToast("开屏倒计时结束");
                        MobclickAgent.onEvent(SplashActivity.this, "CSJADLOADTIMEOVER");
                        gotoMain();
                    }
                });
                if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {

                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
                                showToast("下载中...");
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                            showToast("下载暂停...");

                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                            showToast("下载失败...");

                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {

                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {

                        }
                    });
                }
            }
        }, AD_TIME_OUT);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (splashAD == null) {
            return;
        }
        if (splashAD.dplink != null && i > 0) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    gotoMain();
                }
            }, 0);
        }
        i++;
    }

    @Override
    protected void onStop() {
        handler.removeCallbacks(callbacks);
        Log.d("splash", "onStop");
        super.onStop();
    }

    @Override
    public void onADDismissed() {
        Log.i("AD_DEMO", "SplashADDismissed");
        gotoMain();
    }

    @Override
    public void onNoAD(AdError adError) {
        Log.i("AD_DEMO", String.format("LoadSplashADFail, eCode=%d, errorMsg=%s", adError.getErrorCode(), adError.getErrorMsg()));
        MobclickAgent.onEvent(this, "GDTNOAD");
        /** 如果加载广告失败，则直接跳转 */
        // 没有开屏广告
        ADConstant.IS_SCREEN = false;
        Log.e("mrs", "---------splash--------没有开屏广告--------------");
        mTvSkip.setText("4s 跳过");
        mTvSkip.setVisibility(View.VISIBLE);
        //创建倒计时类
        mCountDownTimer = new MyCountDownTimer(2000, 1000);
        mCountDownTimer.start();
        //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
        handler.postDelayed(callbacks, 2000);
    }

    @Override
    public void onADPresent() {
        Log.e("AD_DEMO", "SplashADPresent");
        MobclickAgent.onEvent(SplashActivity.this, "GDTADSHOWSUCCESS");
        ADConstant.IS_SCREEN = true;
        imgSplash.setVisibility(View.INVISIBLE);
        // 广告展示后一定要把预设的开屏图片隐藏起来
        MobclickAgent.onEvent(this, ADConstant.SPLASH_OPEN);
    }

    @Override
    public void onADClicked() {
        MobclickAgent.onEvent(this, "GDTADCLICK");
        Log.e("AD_DEMO--", "SplashADClicked");
        String clickUrl = "";
        if (splashADScreen.getExt() != null) {
            if (splashADScreen.getExt().containsKey("clickUrl")) {
                clickUrl = (String) splashADScreen.getExt().get("clickUrl");
            }
        }
        Log.i("AD_DEMO", "SplashADClicked clickUrl: " + clickUrl);

        if (!TextUtils.isEmpty(clickUrl)) {
            Intent intent = new Intent();
            intent.setClass(this, CommonWebViewActivity.class);
            intent.putExtra("url", clickUrl);
            startActivity(intent);
        }
    }

    private static final String SKIP_TEXT = "click jump %d";

    @Override
    public void onADTick(long millisUntilFinished) {
        Log.i("AD_DEMO", "SplashADTick " + millisUntilFinished + "ms");
        mTvSkip.setText(String.format(SKIP_TEXT,
                Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADExposure() {
        Log.i("AD_DEMO", "SplashADExposure");
    }

    static class SmHandler extends Handler {
        private WeakReference<Activity> weakReference;

        public SmHandler(Activity activity) {
            this.weakReference = new WeakReference<>(activity);
        }
    }

    /**
     * 倒计时timer
     */
    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以「 毫秒 」为单位倒计时的总数
         *                          例如 millisInFuture = 1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick()
         *                          例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            mTvSkip.setText("0s 跳过");
        }

        public void onTick(long millisUntilFinished) {
            mTvSkip.setText(millisUntilFinished / 1000 + "s 跳过");
        }
    }

    private View.OnClickListener mTvSkipOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gotoMain();
        }
    };

    @Override
    protected void onDestroy() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
        super.onDestroy();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
