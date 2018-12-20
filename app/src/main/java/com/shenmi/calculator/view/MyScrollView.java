package com.shenmi.calculator.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.GridView;
import android.widget.ScrollView;

/**
 * Created by SQ on 2018/12/11.
 */

public class MyScrollView extends ScrollView {

    private GridView mWebView;
    private int currentY;

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context) {
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 将父scrollview的滚动事件拦截
            currentY = (int) ev.getY();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            // 把滚动事件恢复给父Scrollview
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
//        if (getScrollY()>=1780){
//            isCurrent =false;
//        }
        Log.e("滚动距离","====="+getScrollY());
    }

    boolean isCurrent = true;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        View child = getChildAt(0);
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            int height = child.getMeasuredHeight();
            height = height - getMeasuredHeight();
            // System.out.println("height=" + height);
            int scrollY = getScrollY();
            // System.out.println("scrollY" + scrollY);
            int y = (int) ev.getY();
            // 手指向下滑动
            if (currentY < y) {
                if (scrollY <= 0) {
                    // 如果向下滑动到头，就把滚动交给父Scrollview
                    Log.e("手势","向下滑动到头");
                } else {
                    Log.e("手势","向下滑动到");
                }
            } else if (currentY > y) {
                if (scrollY >= height) {
                    // 如果向上滑动到头，就把滚动交给父Scrollview
                    Log.e("手势","向上滑动到头");
//                    isCurrent = false;
                } else {
                    Log.e("手势","向上滑动到");
                }
            }
            currentY = y;
        }
        return super.onTouchEvent(ev);
    }

}
