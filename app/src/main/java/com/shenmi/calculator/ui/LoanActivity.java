package com.shenmi.calculator.ui;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenmi.calculator.R;
import com.shenmi.calculator.adapter.MyFragmentPagerAdapter;
import com.shenmi.calculator.fragment.BusinessFragment;
import com.shenmi.calculator.fragment.CombinationFragment;
import com.shenmi.calculator.fragment.ProvidentFundFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 贷款计算器界面
 */
public class LoanActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private ImageView ivBottomLine;
    private TextView tvTab1, tvTab2, tvTab3;
    ListFragment mFrag;
    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_loan);
        // 设置沉浸式状态栏
//        StatusBarUtil.setStatusBar(this);
        InitWidth();
        InitTextView();
        InitViewPager();
        MobclickAgent.onEvent(this,"Calculator_loan");
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

    private void InitTextView() {
        tvTab1 = (TextView) findViewById(R.id.tv_tab1);
        tvTab2 = (TextView) findViewById(R.id.tv_tab2);
        tvTab3 = (TextView) findViewById(R.id.tv_tab3);
        tvTab1.setOnClickListener(new MyOnClickListener(0));
        tvTab2.setOnClickListener(new MyOnClickListener(1));
        tvTab3.setOnClickListener(new MyOnClickListener(2));
        ImageView switch_btn = (ImageView) findViewById(R.id.switch_btn);
        switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        fragmentsList = new ArrayList<>();
        LayoutInflater mInflater = getLayoutInflater();
        mInflater.inflate(R.layout.viewpager_lay, null);

        fragmentsList.add(new BusinessFragment());
        fragmentsList.add(new ProvidentFundFragment());
        fragmentsList.add(new CombinationFragment());

        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setCurrentItem(0);
        mPager.setOffscreenPageLimit(5);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    private void InitWidth() {
        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
        bottomLineWidth = BitmapFactory.decodeResource(getResources(),
                R.drawable.tab_tag_selected).getWidth();// 获取图片宽度;
        Log.d(TAG, "cursor imageview width=" + bottomLineWidth);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        int screenH = dm.heightPixels;
        Log.e(TAG, "screenW"+screenW+":"+"screenH"+screenH+"bottomLineWidth:"+bottomLineWidth);
        offset = (int) ((screenW / 3.0 - bottomLineWidth) / 2);
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        ivBottomLine.setImageMatrix(matrix);//設置初始位置

        Log.i("MainActivity", "offset=" + offset);

        position_one = (int) (screenW / 3.0);
//        position_two = position_one * 2;
//        position_three = position_one * 3;
    }

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = new TranslateAnimation(position_one * currIndex, position_one
                    * arg0, 0, 0);// 显然这个比较简洁，只有一行代码。
            currIndex = arg0;
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }


}
