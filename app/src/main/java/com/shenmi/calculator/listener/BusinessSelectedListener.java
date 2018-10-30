package com.shenmi.calculator.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.shenmi.calculator.util.LiLvUtil;

import java.math.BigDecimal;

/**
 * Created by SQ on 2018/4/8.
 */

public class BusinessSelectedListener implements AdapterView.OnItemSelectedListener {

    EditText et;
    double lilv;

    public BusinessSelectedListener(EditText et, double lilv) {
        this.et = et;
        this.lilv = lilv;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        et.setText(new BigDecimal(LiLvUtil.getLiLv(lilv, position)).setScale(2,
                BigDecimal.ROUND_HALF_DOWN).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}