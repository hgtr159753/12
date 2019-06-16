package com.shenmi.calculator.ui;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shenmi.calculator.R;
import com.shenmi.calculator.bean.tax.TaxBean;

public class TaxResultActivity extends AppCompatActivity {

    private TaxBean taxbean;
    private ImageView icon_back;
    private TextView tv_result;
    private TextView tv_res_money;
    private TextView tv_res_tax;
    private TextView tv_res_wuxian;
    private TextView tv_res_gongjijin;
    private TextView tv_res_yiliao;
    private TextView tv_res_yanglao;
    private TextView tv_res_shiye;
    private TextView tv_res_gongshang;
    private TextView tv_res_shengyu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tax_result);
        taxbean = getIntent().getParcelableExtra("taxbean");
        initView();
        initData();
    }

    private void initView() {
        tv_result = findViewById(R.id.tv_result);
        tv_res_money = findViewById(R.id.tv_res_money);
        tv_res_tax = findViewById(R.id.tv_res_tax);
        tv_res_wuxian = findViewById(R.id.tv_res_wuxian);
        tv_res_gongjijin = findViewById(R.id.tv_res_gongjijin);
        tv_res_yiliao = findViewById(R.id.tv_res_yiliao);
        tv_res_yanglao = findViewById(R.id.tv_res_yanglao);
        tv_res_shiye = findViewById(R.id.tv_res_shiye);
        tv_res_gongshang = findViewById(R.id.tv_res_gongshang);
        tv_res_shengyu = findViewById(R.id.tv_res_shengyu);

        icon_back = findViewById(R.id.icon_back);
        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        float gongjijin = taxbean.getMoney()*(Float.parseFloat(taxbean.getGongjijin()))/100;
        float yiliao = taxbean.getMoney()*(Float.parseFloat(taxbean.getYiliao()))/100;
        float yanglao = taxbean.getMoney()*(Float.parseFloat(taxbean.getYanglao()))/100;
        float shiye = taxbean.getMoney()*(Float.parseFloat(taxbean.getShiye()))/100;
        float gongshang = taxbean.getMoney()*(Float.parseFloat(taxbean.getGongshang()))/100;
        float shengyu = taxbean.getMoney()*(Float.parseFloat(taxbean.getShengyu()))/100;
        tv_res_money.setText(taxbean.getMoney()+"元");
        tv_res_gongjijin.setText(gongjijin+"元");
        tv_res_yiliao.setText(yiliao+"元");
        tv_res_yanglao.setText(yanglao+"元");
        tv_res_shiye.setText(shiye+"元");
        tv_res_gongshang.setText(gongshang+"元");
        tv_res_shengyu.setText(shengyu+"元");
        float wuxianyijin = gongjijin + yiliao + yanglao + shiye + gongshang + shengyu;
        tv_res_wuxian.setText(wuxianyijin+"元");
        float tax = 0;
        float currentWuxian = taxbean.getMoney()-wuxianyijin;
        if (currentWuxian>0 && currentWuxian <= 2520){
            tax = (float) (currentWuxian*0.03);
        }else if (currentWuxian> 2520 && currentWuxian <= 16920){
            tax = (float) (currentWuxian*0.1);
        }else if (currentWuxian> 16920 && currentWuxian <= 31920){
            tax = (float) (currentWuxian*0.2);
        }else if (currentWuxian> 31920 && currentWuxian <= 52920){
            tax = (float) (currentWuxian*0.25);
        }else if (currentWuxian> 52920 && currentWuxian <= 85920){
            tax = (float) (currentWuxian*0.3);
        }else if (currentWuxian> 85920 && currentWuxian <= 181920){
            tax = (float) (currentWuxian*0.35);
        }else if (currentWuxian> 181920){
            tax = (float) (currentWuxian*0.45);
        }
        tv_res_tax.setText(tax+"元");
        tv_result.setText(currentWuxian-tax+"元");
    }
}
