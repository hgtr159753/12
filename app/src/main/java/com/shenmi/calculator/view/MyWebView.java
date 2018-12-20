package com.shenmi.calculator.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.GridView;

/**
 * Created by SQ on 2018/12/11.
 */

public class MyWebView extends WebView {

    private GridView mWebView;
    private int currentY;

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context) {
        super(context);
    }

    /**
     * 覆写onInterceptTouchEvent方法，点击操作发生在ListView的区域的时候,
     * 返回false让ScrollView的onTouchEvent接收不到MotionEvent，而是把Event传到下一级的控件中
     */

    public GridView getWebView() {
        return mWebView;
    }

    public void setWebView(GridView listView) {
        this.mWebView = listView;
    }

//    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, mExpandSpec);
//    }

}
