package com.shenmi.calculator.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shenmi.calculator.R;
import com.shenmi.calculator.bean.HistoryBean;

import java.text.SimpleDateFormat;

public class HistoryAdapter extends BaseQuickAdapter<HistoryAdapter.AdapterHistoryBean, BaseViewHolder> {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public HistoryAdapter() {
        super(R.layout.item_history);
    }

    @Override
    protected void convert(BaseViewHolder helper, AdapterHistoryBean item) {
        helper.setGone(R.id.history_time, item.showTime);
        helper.setGone(R.id.history_line, !item.showTime);
        helper.setText(R.id.history_time, format.format(item.bean.getTime()));
        String[] data = item.bean.getData();
        if (data.length == 2) {
            helper.setText(R.id.history_data_item1, data[0]);
            helper.setText(R.id.history_data_item2, data[1]);
        }
    }

    public static class AdapterHistoryBean {
        HistoryBean bean;
        boolean showTime;

        public AdapterHistoryBean(HistoryBean bean, boolean showTime) {
            this.bean = bean;
            this.showTime = showTime;
        }

        public HistoryBean getBean() {
            return bean;
        }

        @Override
        public String toString() {
            return "AdapterHistoryBean{" +
                    "bean=" + bean +
                    ", showTime=" + showTime +
                    '}';
        }
    }
}
