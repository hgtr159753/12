package com.shenmi.calculator.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenmi.calculator.R;
import com.shenmi.calculator.bean.tax.TaxBean;
import com.shenmi.calculator.ui.home.TaxSelectActivity;
import com.shenmi.calculator.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class TaxActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_start_calculate;
    private TextView tv_select;
    private TaxBean taxBean;
    private EditText et_money;
    private EditText et_gongjijin;
    private EditText et_yiliao;
    private EditText et_yanglao;
    private EditText et_shiye;
    private EditText et_gongshang;
    private EditText et_shengyu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax);
        MobclickAgent.onEvent(this,"Calculator_tax");
        initView();
        initData();
        initLinstener();
    }

    private void initData() {
        taxBean = new TaxBean();
    }

    private void initLinstener() {
        tv_start_calculate.setOnClickListener(this);
    }

    private void initView() {
        tv_start_calculate = findViewById(R.id.tv_start_calculate);
        tv_select = findViewById(R.id.tv_select);
        et_money = findViewById(R.id.et_money);
        et_gongjijin = findViewById(R.id.et_gongjijin);
        et_yiliao = findViewById(R.id.et_yiliao);
        et_yanglao = findViewById(R.id.et_yanglao);
        et_shiye = findViewById(R.id.et_shiye);
        et_gongshang = findViewById(R.id.et_gongshang);
        et_shengyu = findViewById(R.id.et_shengyu);
        ImageView icon_back = findViewById(R.id.icon_back);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaxActivity.this,TaxSelectActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_start_calculate:
                if (isOpenActivity()){
                    Intent intent = new Intent(this,TaxResultActivity.class);
                    intent.putExtra("taxbean",taxBean);
                    startActivity(intent);
                }
                break;
        }
    }

    private boolean isOpenActivity() {
        String money = et_money.getText().toString().trim();
        String gongjijin = et_gongjijin.getText().toString().trim();
        String yiliao = et_yiliao.getText().toString().trim();
        String yanglao = et_yanglao.getText().toString().trim();
        String shiye = et_shiye.getText().toString().trim();
        String gongshang = et_gongshang.getText().toString().trim();
        String shengyu = et_shengyu.getText().toString().trim();
        if (TextUtils.isEmpty(money)
                ||TextUtils.isEmpty(gongjijin)
                ||TextUtils.isEmpty(yiliao)
                ||TextUtils.isEmpty(yanglao)
                ||TextUtils.isEmpty(shiye)
                ||TextUtils.isEmpty(gongshang)
                ||TextUtils.isEmpty(shengyu)){
            ToastUtil.showToast(this,"请填写完整的信息");
            return false;
        }
        taxBean.setMoney(Integer.parseInt(money));
        taxBean.setGongjijin(gongjijin);
        taxBean.setYiliao(yiliao);
        taxBean.setYanglao(yanglao);
        taxBean.setShiye(shiye);
        taxBean.setGongshang(gongshang);
        taxBean.setShengyu(shengyu);
        return true;
    }
}
