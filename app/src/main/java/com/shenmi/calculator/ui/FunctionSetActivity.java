package com.shenmi.calculator.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shenmi.calculator.R;
import com.shenmi.calculator.util.DensityUtil;
import com.shenmi.calculator.util.UnitConvertUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面选择界面
 */
public class FunctionSetActivity extends FragmentActivity {

	private List<FunctionItem> mList = new ArrayList<FunctionItem>();
	private GridView mGridView;
	private long mExitTime;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(this).onAppStart();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_functionset_choose);
//		StatusBarUtil.setStatusBar(this);
		mGridView = (GridView) findViewById(R.id.functionset_grid);
		CustomAdapter mAdapter = new CustomAdapter(FunctionSetActivity.this,
				R.layout.girdvew_function_item, mList, mGridView);
		init();
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("null")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent mIntent = null;
				switch (position) {
				case 0:
					mIntent = new Intent(FunctionSetActivity.this,
							UpperNumActivity.class);
					break;
				case 1:
//					try {
//						ComponentName cn = new ComponentName(
//								"com.android.calculator2",
//								"com.android.calculator2.Calculator");
//						mIntent = new Intent();
//						mIntent.setComponent(cn);
//						mIntent.setAction("android.intent.action.MAIN");
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
					mIntent = new Intent(FunctionSetActivity.this,
							CalculatorActivity.class);
					break;
				case 2:
					mIntent = new Intent(FunctionSetActivity.this,
							UnitConvertActivity.class);
					mIntent.putExtra("UnitType", UnitConvertUtil.LENGTH);
					break;
				case 3:
					mIntent = new Intent(FunctionSetActivity.this,
							UnitConvertActivity.class);
					mIntent.putExtra("UnitType", UnitConvertUtil.AREA);
					break;
				case 4:
					mIntent = new Intent(FunctionSetActivity.this,
							UnitConvertActivity.class);
					mIntent.putExtra("UnitType", UnitConvertUtil.VOLUME);
					break;
				//贷款计算器
				case 5:
					mIntent = new Intent(FunctionSetActivity.this,
							LoanActivity.class);
					break;
				case 6:
					mIntent = new Intent(FunctionSetActivity.this,
							UnitConvertActivity.class);
					mIntent.putExtra("UnitType", UnitConvertUtil.SPEED);
					break;
				case 7:
					mIntent = new Intent(FunctionSetActivity.this,
							UnitConvertActivity.class);
					mIntent.putExtra("UnitType", UnitConvertUtil.TIME);
					break;
				case 8:
					mIntent = new Intent(FunctionSetActivity.this,
							UnitConvertActivity.class);
					mIntent.putExtra("UnitType", UnitConvertUtil.MASS);
					break;

				default:
					break;
				}
				if (mIntent != null) {
					startActivity(mIntent);
				}
			}
		});
		mGridView.setAdapter(mAdapter);
	}

	private void init() {
		// TODO Auto-generated method stub
		String[] convertTexts = getResources().getStringArray(
				R.array.convert_texts);
		TypedArray typedArray = getResources().obtainTypedArray(
				R.array.convert_icons);
		for (int index = 0; index < typedArray.length(); index++) {
			int resId = typedArray.getResourceId(index, 0);
			FunctionItem mFunction = new FunctionItem(convertTexts[index],
					resId);
			mList.add(mFunction);
		}
		typedArray.recycle();
	}

	class CustomAdapter extends ArrayAdapter<FunctionItem> {
		private int resourceId;
		GridView mm;

		public CustomAdapter(Context context, int resource,
				List<FunctionItem> objects, GridView gridView) {
			super(context, resource, objects);
			resourceId = resource;
			mm = gridView;
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			String functionName = getItem(position).getFunctionName();
			int pictureSrc = getItem(position).getPictureSrc();
			LayoutInflater mInflater = LayoutInflater.from(getContext());
			View view = mInflater.inflate(resourceId, null);
			TextView mTextView = (TextView) view.findViewById(R.id.function_tv);
			View mView = (View) view.findViewById(R.id.item_rel);
			ImageView mImageView = (ImageView) view
					.findViewById(R.id.function_iv);
			mTextView.setText(functionName);
			mImageView.setImageResource(pictureSrc);
			Log.e("gridviewheight", "" + mm.getHeight());
			LayoutParams mLayoutParams = mView.getLayoutParams();
			mLayoutParams.height = (int) ((mm.getHeight() - DensityUtil.dip2px(
					FunctionSetActivity.this, 4.0f)) / 3);
			return view;
		}
	}

	class FunctionItem {
		private String functionName;
		private int pictureSrc;

		public FunctionItem(String functionName, int pictureSrc) {
			this.functionName = functionName;
			this.pictureSrc = pictureSrc;
		}

		public String getFunctionName() {
			return functionName;
		}

		public int getPictureSrc() {
			return pictureSrc;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doubleClickQuitBrowser();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void doubleClickQuitBrowser() {
		long timeSpan = System.currentTimeMillis() - mExitTime;
		if (timeSpan < 1000) {
			finish();
		} else {
			Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
			mExitTime = System.currentTimeMillis();
		}
	}

}
