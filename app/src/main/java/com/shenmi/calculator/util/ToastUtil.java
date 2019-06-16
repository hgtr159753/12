package com.shenmi.calculator.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by SQ on 2019/4/8.
 */

public class ToastUtil {

    private static Toast mToast;

    public static void showToast(Context mContext, String text) {
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showToastShort(Context mContext, String resId) {
        showToast(mContext,resId);
    }

}
