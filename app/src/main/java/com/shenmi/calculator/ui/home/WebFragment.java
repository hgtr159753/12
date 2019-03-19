package com.shenmi.calculator.ui.home;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.shenmi.calculator.R;
import com.shenmi.calculator.constant.ConstantWeb;
import com.sm.readbook.base.BaseFragment;

/**
 * Created by SQ on 2018/12/18.
 * 新闻fragment
 */

public class WebFragment extends Fragment implements View.OnClickListener {

    private WebView webview_main;
    private ImageView iv_home;
    private ImageView iv_back;
    private ProgressBar progressBar;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_web, container,false);
            initView(mRootView);
            setListener();
        }
        return mRootView;
    }

    protected void initView(View view) {
        iv_home = view.findViewById(R.id.iv_home);
        iv_back = view.findViewById(R.id.iv_back);
        progressBar = view.findViewById(R.id.progressBar);
        webview_main = view.findViewById(R.id.webview_main);
        initWebView();
    }

    protected void setListener() {
        iv_home.setOnClickListener(this);
        iv_back.setOnClickListener(this);
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
            Log.e("webviewurl=",url);
            if (url.contains(ConstantWeb.WEBURL_MAIN_TWO)
                    || url.contains(ConstantWeb.WEBURL_MAIN_ONE)){
                iv_back.setVisibility(View.INVISIBLE);
            }else{
                iv_back.setVisibility(View.VISIBLE);
            }
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
                progressBar.setVisibility(View.GONE);
                webview_main.getSettings().setBlockNetworkImage(false);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
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
                if (webview_main.canGoBack())
                    webview_main.goBack();
                break;
        }
    }
}
