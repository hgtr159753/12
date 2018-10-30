package com.shenmi.calculator.listener;

import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

/**
 * Created by SQ on 2018/4/8.
 */

public class ProvidentFundSelectedListener implements AdapterView.OnItemSelectedListener {

    EditText et;

    public ProvidentFundSelectedListener(EditText et) {
        this.et = et;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        if (position <= 4)
            et.setText(String.valueOf(4.0));
        else
            et.setText(String.valueOf(4.5));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}
