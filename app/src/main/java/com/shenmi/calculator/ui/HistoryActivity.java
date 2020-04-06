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

import java.util.List;

import io.objectbox.Box;

public class HistoryActivity extends Activity implements View.OnClickListener, BaseQuickAdapter.OnItemChildClickListener {
    private View mHistoryBack;
    private View mHistoryClean;
    private RecyclerView mListView;
    private Box<HistoryBean> mHistoryBeanBox;

    private HistoryAdapter mAdapter = new HistoryAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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

    private void loadData() {
        new Thread(() -> {
            List<HistoryBean> historyBeans = mHistoryBeanBox.query().order(HistoryBean_.time).build().find();
            runOnUiThread(() -> mAdapter.setNewData(historyBeans));
        }).start();
    }

    private void initListView() {
        mListView.setAdapter(mAdapter);


        mAdapter.setOnItemChildClickListener(this);
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
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        HistoryBean bean = (HistoryBean) adapter.getItem(position);
        if (view.getId() == R.id.history_time) {
            Intent data = new Intent();
            data.putExtra("input", bean.getData());
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
