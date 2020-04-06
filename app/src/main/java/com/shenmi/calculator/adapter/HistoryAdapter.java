package com.shenmi.calculator.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shenmi.calculator.R;
import com.shenmi.calculator.app.MyApplication;
import com.shenmi.calculator.bean.HistoryBean;
import com.shenmi.calculator.util.ToastUtil;
import com.zchu.rxcache.utils.LogUtils;

import java.text.SimpleDateFormat;

public class HistoryAdapter extends BaseQuickAdapter<HistoryBean, BaseViewHolder> implements View.OnClickListener {
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public HistoryAdapter() {
        super(R.layout.item_history);
    }

    @Override
    protected void convert(BaseViewHolder helper, HistoryBean item) {
        helper.setText(R.id.history_time, format.format(item.getTime()));
        LinearLayout linear = helper.getView(R.id.history_space);
        linear.removeAllViews();

        String[] items = item.getData();
        for (int ind = 0; ind < items.length; ind += 2) {
            View itemView = View.inflate(helper.itemView.getContext(), R.layout.item_history_data, null);
            ((TextView) itemView.findViewById(R.id.data_item1)).setText(items[ind]);
            ((TextView) itemView.findViewById(R.id.data_item2)).setText(items[ind + 1]);
            linear.addView(itemView);
            itemView.setTag(items[ind + 1]);
            itemView.setOnClickListener(this);
        }

        helper.addOnClickListener(R.id.history_time);
    }

    @Override
    public void onClick(View v) {
        String tag = (String) v.getTag();
        ClipboardManager clipboardManager = (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", tag));
        ToastUtil.showToast(v.getContext(), "结果复制成功");

    }
}
