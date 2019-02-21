package com.shenmi.calculator.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shenmi.calculator.R;
import com.shenmi.calculator.constant.ADConstant;
import com.snmi.sdk.Ad;
import com.snmi.sdk.SplashADInfo;
import com.snmi.sdk.SplashFullScreenAD;
import com.zchu.reader.utils.ToastUtils;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity {

    private RelativeLayout container;
    private int i = 0;
    ImageView imgSplash;
    boolean isClickedAD = false;
    private float ClickX;
    private float ClickY;
    private String[] umb = new String[4];
    SplashADInfo splashAD;
    private SmHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("mrs","onCreate");
        setContentView(R.layout.activity_splash);
        container = findViewById(R.id.rl_splash);
        imgSplash = findViewById(R.id.iv_splash);
        handler = new SmHandler(this);
        initAD();
    }

    private void initAD() {
        final SplashFullScreenAD splashFullScreenAD = new SplashFullScreenAD(this);
        final SplashADInfo splashAD = splashFullScreenAD.getSplashAD(ADConstant.START_SCREEN);
        if (splashAD == null) {
            // 没有开屏广告
            ADConstant.IS_SCREEN = false;
            Log.e("mrs", "---------splash--------没有开屏广告--------------");
        } else {
            Log.e("mrs", "---------splash--------有开屏广告--------------");
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
        handler.postDelayed(new Runnable() {
            public void run() {
               gotoMain();
            }
        }, 3000);
    }

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
        Log.d("splash", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("mrs","onDestroy");
        com.snmi.sdk.Log.d("splash", "onDestroy");
        super.onDestroy();
    }

    static class SmHandler extends Handler{
        private WeakReference<Activity> weakReference;

        public SmHandler(Activity activity) {
            this.weakReference = new WeakReference<>(activity);
        }
    }
}
