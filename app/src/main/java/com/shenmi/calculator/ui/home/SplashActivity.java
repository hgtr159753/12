package com.shenmi.calculator.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shenmi.calculator.R;
import com.shenmi.calculator.constant.ADConstant;
import com.shenmi.calculator.util.SPUtil;
import com.snmi.sdk.Ad;
import com.snmi.sdk.SplashADInfo;
import com.snmi.sdk.SplashFullScreenAD;
import com.snmi.sdk_3.Hs;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity {

    private RelativeLayout container;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("mrs","onCreate");
        setContentView(R.layout.activity_splash);
        initView();
        handler = new SmHandler(this);
        isOpen = (Boolean) SPUtil.get(this, ADConstant.ISOPENAD, false);
        if (isOpen) {
            initAD();
        }else{
            //没有广告的时候2秒跳过
            mTvSkip.setVisibility(View.GONE);
            //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
            handler.postDelayed(callbacks, 600);
        }
    }

    private void initView() {
        container = findViewById(R.id.rl_splash);
        mTvSkip = findViewById(R.id.tv_skip);
        imgSplash = findViewById(R.id.iv_splash);
        mTvSkip.setOnClickListener(mTvSkipOnClickListener);
    }

    private void initAD() {
        final SplashFullScreenAD splashFullScreenAD = new SplashFullScreenAD(this);
        final SplashADInfo splashAD = splashFullScreenAD.getSplashAD(ADConstant.START_SCREEN);
        if (splashAD == null) {
            // 没有开屏广告
            ADConstant.IS_SCREEN = false;
            Log.e("mrs", "---------splash--------没有开屏广告--------------");
            //没有广告的时候2秒跳过
            mTvSkip.setVisibility(View.GONE);
            //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
            handler.postDelayed(callbacks, 600);
        } else {
            Log.e("mrs", "---------splash--------有开屏广告--------------");
            //有广告的时候4秒跳过
            mTvSkip.setText("4s 跳过");
            //创建倒计时类
            mCountDownTimer = new MyCountDownTimer(4000, 1000);
            mCountDownTimer.start();
            //这是一个 Handler 里面的逻辑是从 Splash 界面跳转到 Main 界面，这里的逻辑每个公司基本上一致
            handler.postDelayed(callbacks, 4000);


            ADConstant.IS_SCREEN = true;
            imgSplash.setImageBitmap(BitmapFactory.decodeFile(splashAD.picLocalPath));
            imgSplash.setClickable(true);
            // 展示开屏广告处理
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
                                if (!splashAD.action.equals("2")) {
                                    isClickedAD = true;
                                }
                                Log.e("TAG", "实时位置ACTION_DOWN：(" + event.getRawX() + "," + event.getRawY());
                                // 点击开屏广告处理
                                splashFullScreenAD.clickSplashAD(splashAD, umb);
                            }
                            break;
                    }
                    return true;
                }
            });
        }
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
     * 开屏广告点击弹出的Activity关闭之后的回调
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

    @Override
    protected void onResume() {
        super.onResume();
        if (splashAD == null){
            return;
        }
        if(splashAD.dplink != null && i > 0){
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

    static class SmHandler extends Handler{
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
         * @param millisInFuture
         *      表示以「 毫秒 」为单位倒计时的总数
         *      例如 millisInFuture = 1000 表示1秒
         *
         * @param countDownInterval
         *      表示 间隔 多少微秒 调用一次 onTick()
         *      例如: countDownInterval = 1000 ; 表示每 1000 毫秒调用一次 onTick()
         *
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onFinish() {
            mTvSkip.setText("0s 跳过");
        }

        public void onTick(long millisUntilFinished) {
            mTvSkip.setText( millisUntilFinished / 1000 + "s 跳过");
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
}
