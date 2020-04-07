package com.shenmi.calculator.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.shenmi.calculator.R;
import com.shenmi.calculator.adapter.HistoryAdapter;
import com.shenmi.calculator.bean.HistoryBean;
import com.shenmi.calculator.bean.HistoryBean_;
import com.shenmi.calculator.db.ObjectBox;
import com.shenmi.calculator.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class HistoryActivity extends Activity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    private View mHistoryBack;
    private View mHistoryClean;
    private RecyclerView mListView;
    private Box<HistoryBean> mHistoryBeanBox;
    private int mType;

    private HistoryAdapter mAdapter = new HistoryAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mType = getIntent().getIntExtra("type", 0);
        mHistoryBeanBox = ObjectBox.get().boxFor(HistoryBean.class);
        initView();
    }

    private void initView() {
        mHistoryBack = findViewById(R.id.history_back);
        mHistoryClean = findViewById(R.id.history_clean);
        mListView = findViewById(R.id.history_list);


        initListener();
        initListView();


        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private void loadData() {
        new Thread(() -> {
            List<HistoryBean> historyBeans = mHistoryBeanBox.query().order(HistoryBean_.time).build().find();
            ArrayList<HistoryAdapter.AdapterHistoryBean> beans = new ArrayList<>();
            for (int ind = 0; ind < historyBeans.size() - 1; ind++) {
                if (format.format(historyBeans.get(ind).getTime()).equals(format.format(historyBeans.get(ind + 1).getTime()))) {
                    beans.add(new HistoryAdapter.AdapterHistoryBean(historyBeans.get(ind), false));
                } else {
                    beans.add(new HistoryAdapter.AdapterHistoryBean(historyBeans.get(ind), true));
                }
            }
            if (historyBeans.size() > 0) {
                beans.add(new HistoryAdapter.AdapterHistoryBean(historyBeans.get(historyBeans.size() - 1), true));
            }
            runOnUiThread(() -> mAdapter.setNewData(beans));
        }).start();
    }

    private void initListView() {
        mListView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(this);
    }

    private void initListener() {
        mHistoryBack.setOnClickListener(this);
        mHistoryClean.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.history_back) {
            onBackPressed();
        } else if (v.getId() == R.id.history_clean) {
            new Thread(() -> {
                mHistoryBeanBox.removeAll();
                runOnUiThread(() -> loadData());
            }).start();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        HistoryAdapter.AdapterHistoryBean bean = (HistoryAdapter.AdapterHistoryBean) adapter.getItem(position);
        Intent data = new Intent();
        if (bean.getBean().getType() == 1) {
            if (mType == 0) {
                ToastUtil.showToast(this, "科学计算数据，不可回显入基础计算");
                return;
            }
        }
        data.putExtra("input", bean.getBean().getData());
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
