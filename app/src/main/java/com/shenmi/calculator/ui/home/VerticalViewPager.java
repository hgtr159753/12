package com.shenmi.calculator.ui.home;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 纵向viewpager
 * Created by SQ on 2018/12/18.
 */

public class VerticalViewPager extends ViewPager {

    private boolean isTouch = true;

    public boolean isTouch() {
        return isTouch;
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }

    public VerticalViewPager(Context context) {
        this(context,null);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPageTransformer(true,new DefaultTransformer());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isTouch){
            boolean touchEvent = super.onInterceptTouchEvent(swapEvent(ev));
            swapEvent(ev);
            return touchEvent;
        }else{
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(swapEvent(ev));
    }

    private MotionEvent swapEvent(MotionEvent ev) {
        //获取宽高
        float width = getWidth();
        float height = getHeight();
        float swappedX = (ev.getY() / height) * width;
        float swappedY = (ev.getX() / width) * height;
        ev.setLocation(swappedX,swappedY);
        return ev;
    }

    /**
     * 设置动画
     */
    public class DefaultTransformer implements PageTransformer {

        public static final String TAG = "simple";

        @Override
        public void transformPage(View view, float position) {
            float alpha = 0;
            if (0 <= position && position <=1){
                alpha = 1-position;
            }else if(-1 < position && position< 0){
                alpha = position +1;
            }
            view.setAlpha(alpha);
            float trasX = view.getWidth() * -position;
            view.setTranslationX(trasX);
            float trasY = view.getHeight() * position;
            view.setTranslationY(trasY);
        }
    }
}
