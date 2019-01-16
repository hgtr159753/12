package com.shenmi.calculator.ui.home;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.shenmi.calculator.R;
import com.shenmi.calculator.constant.ConstantWeb;

/**
 * Created by SQ on 2018/12/18.
 * 新闻fragment
 */

public class WebFragment extends Fragment implements View.OnClickListener {

    private WebView webview_main;
    private ImageView iv_back;
    private ImageView iv_home;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web, container, false);
        initView(view);
        initWebView();
        return view;
    }

    private void initView(View view) {
        iv_back = view.findViewById(R.id.iv_back);
        iv_home = view.findViewById(R.id.iv_home);
        webview_main = view.findViewById(R.id.webview_main);
        iv_back.setOnClickListener(this);
        iv_home.setOnClickListener(this);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("稍等哦~");
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        //添加js监听 这样html就能调用客户端
        webview_main.setWebViewClient(webViewClient);
        webview_main.setWebChromeClient(webChromeClient);

        WebSettings webSettings= webview_main.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        //不使用缓存，只从网络获取数据.
        webSettings.setSupportZoom(true);
        // 支持屏幕缩放
        webSettings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        webview_main.loadUrl(ConstantWeb.WEBURL);
    }

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }
    };


    private WebChromeClient webChromeClient = new WebChromeClient(){

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                // 网页加载完成
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                webview_main.getSettings().setBlockNetworkImage(false);
            } else {
                // 网页加载中
                if (((MainCalculateActivity)getActivity()).getPosition() != 0){
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_home:
                webview_main.loadUrl(ConstantWeb.WEBURL);
                break;
            case R.id.iv_back:
                ((MainCalculateActivity)getActivity()).setPositionPage();
                break;
        }
    }

    /**
     * activity获取webview
     * @return
     */
    public WebView getWebview_main() {
        return webview_main;
    }
}
