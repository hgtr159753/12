package com.shenmi.calculator.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shenmi.calculator.R;
import com.shenmi.calculator.listener.ProvidentFundSelectedListener;

import java.math.BigDecimal;

/**
 * Created by SQ on 2018/4/8.
 * 公积金贷款
 */

public class ProvidentFundFragment extends Fragment {
    private static final String[] nx = { "1", "2", "3", "4", "5", "10", "15",
            "20", "25", "30" };
    private EditText gongjijinlilv;
    private EditText zevj1;
    private Spinner nxvj1, lvv1;
    private Button subj1;
    private TextView am10;
    private TextView am11;
    private TextView am12;
    private TextView am13;
    private TextView am14;
    private LinearLayout shangyelilv_layout;
    private RelativeLayout nianlilv_layout;
    private RadioGroup radioGroup;
    private int fangshi = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_view, container, false);
        initMyView(view);
        return view;
    }


    private void initMyView(View view) {
        init(view);
        clear();
        zevj1 = (EditText) view.findViewById(R.id.zev1);
        nxvj1 = (Spinner) view.findViewById(R.id.nxv1);
        lvv1 = (Spinner) view.findViewById(R.id.lvv1);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
        radioGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.radioBenjin) {
                            fangshi = 1;
                        } else {
                            fangshi = 0;
                        }
                    }
                });
        gongjijinlilv = (EditText) view.findViewById(R.id.gongjijinlilv);
        gongjijinlilv.setText(String.valueOf("3.25"));
        shangyelilv_layout = (LinearLayout) view
                .findViewById(R.id.shangyelilv_layout);
        nianlilv_layout = (RelativeLayout) view
                .findViewById(R.id.nianlilv_layout);
        nianlilv_layout.setVisibility(View.GONE);
        shangyelilv_layout.setVisibility(View.GONE);
        subj1 = (Button) view.findViewById(R.id.sub1);
        // 将可选内容与ArrayAdapter连接起来
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this.getActivity(), android.R.layout.simple_spinner_item, nx);
        // 设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 将adapter 添加到spinner中
        nxvj1.setAdapter(adapter);
        nxvj1.setSelection(7, true);
        // 设置默认值
        nxvj1.setVisibility(View.VISIBLE);
        // 添加事件Spinner事件监听
        nxvj1.setOnItemSelectedListener(new ProvidentFundSelectedListener(
                gongjijinlilv));
        // 设置默认值
        lvv1.setVisibility(View.GONE);
        this.subj1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                JiSuan();
            }
        });
        JiSuan();
    }

    private void JiSuan() {
        Editable value = zevj1.getText();
        if (value.toString() == null || value.toString().length() <= 0) {
            Toast.makeText(getActivity(), "贷款总额不能为空", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        Editable value1 = gongjijinlilv.getText();
        if (value1.toString() == null || value1.toString().length() <= 0) {
            Toast.makeText(getActivity(), "利率不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        double ze = Double.parseDouble(value.toString()) * 10000;
        double nx = Double.parseDouble(nxvj1.getSelectedItem().toString()) * 12;
        double rate = Double.parseDouble(gongjijinlilv.getText().toString()) / 100;
        cal(ze, nx, rate);
    }

    public void cal(double ze, double nx, double rate) {
        double zem = (ze * rate / 12 * Math.pow((1 + rate / 12), nx))
                / (Math.pow((1 + rate / 12), nx) - 1);
        double amount = zem * nx;
        double rateAmount = amount - ze;

        BigDecimal zemvalue = new BigDecimal(zem);
        double zemval = zemvalue.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        BigDecimal amountvalue = new BigDecimal(amount);
        double amountval = amountvalue.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        BigDecimal rateAmountvalue = new BigDecimal(rateAmount);
        double rateAmountval = rateAmountvalue.setScale(2,
                BigDecimal.ROUND_HALF_UP).doubleValue();

        double benjinm = ze / nx;
        double lixim = ze * (rate / 12);
        double diff = benjinm * (rate / 12);
        double huankuanm = benjinm + lixim;
        double zuihoukuan = diff + benjinm;
        double av = (huankuanm + zuihoukuan) / 2;
        double zong = av * nx;
        double zongli = zong - ze;

        BigDecimal huankuanmvalue = new BigDecimal(huankuanm);
        double huankuanmval = huankuanmvalue.setScale(2,
                BigDecimal.ROUND_HALF_UP).doubleValue();

        BigDecimal diffvalue = new BigDecimal(diff);
        double diffmval = diffvalue.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        BigDecimal zongvalue = new BigDecimal(zong);
        double zongval = zongvalue.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        BigDecimal zonglivalue = new BigDecimal(zongli);
        double zonglival = zonglivalue.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();

        if (fangshi == 0) {
            am10.setText((new BigDecimal(ze / 10000)).setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue()
                    + " 万");
            am11.setText(nx + " 月");
            am12.setText(zemval + " 元");
            am13.setText((new BigDecimal(rateAmountval / 10000)).setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue()
                    + " 万");
            am14.setText((new BigDecimal(amountval / 10000)).setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue()
                    + " 万");
        } else {
            am10.setText((new BigDecimal(ze / 10000)).setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue()
                    + " 万");
            am11.setText(nx + "月");
            am12.setText("首月" + huankuanmval + ",月减" + diffmval);
            am13.setText((new BigDecimal(zonglival / 10000)).setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue()
                    + " 万");
            am14.setText((new BigDecimal(zongval / 10000)).setScale(2,
                    BigDecimal.ROUND_HALF_UP).doubleValue()
                    + " 万");
        }
    }

    public void init(View view) {
        am10 = (TextView) view.findViewById(R.id.am10);
        am11 = (TextView) view.findViewById(R.id.am11);
        am12 = (TextView) view.findViewById(R.id.am12);
        am13 = (TextView) view.findViewById(R.id.am13);
        am14 = (TextView) view.findViewById(R.id.am14);
    }

    public void clear() {
        am10.setText(0 + "万元");

        am11.setText(0 + "月");

        am12.setText(0 + "元");

        am13.setText(0 + "万元");

        am14.setText(0 + "万元");
    }


}
