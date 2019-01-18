package com.shenmi.calculator.ui.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shenmi.calculator.R;

/**
 * Created by SQ on 2018/12/18.
 * 赚点吧
 */

public class MoneyFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_money, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

    }
}
