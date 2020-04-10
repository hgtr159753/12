package com.shenmi.calculator.util;

import com.zhy.http.okhttp.OkHttpUtils;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

public class NetWorkInfoObtain {
    private static Double usdCny = null;
    private static Double defUsdCny = 7.0301;

    public static double usdToCny() {
        if (usdCny == null) {
            synchronized (defUsdCny) {
                new Thread(() -> {
                    if (usdCny == null) {
                        synchronized (defUsdCny) {
                            usdCny = obUsdToCny();
                        }
                    }
                }).start();
            }
            return defUsdCny;
        } else {
            return usdCny;
        }
    }

    private static Double obUsdToCny() {
        String url = "https://www.usd-cny.com/";
        try {
            Response execute = OkHttpUtils.get().url(url).build().execute();
            byte[] bytes = execute.body().bytes();
            Pattern compile = Pattern.compile("美元</a></td><td>[0-9.]*?</td>");
            Matcher codeFind = compile.matcher(new String(bytes, Charset.forName("GB2312")));
            if (codeFind.find()) {
                String number = codeFind.group();
                String group = number.substring(15, number.length() - 5);
                return Double.valueOf(group) / 100;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obUsdToCnyTry1();
    }

    private static Double obUsdToCnyTry1() {
        String url = "http://hl.anseo.cn/cal_USD_To_CNY.aspx";
        try {
            Response execute = OkHttpUtils.get().url(url).build().execute();
            String text = execute.body().string();
            Pattern compile = Pattern.compile("当前汇率：</strong>[0-9.]*?</p>");
            Matcher codeFind = compile.matcher(text);
            if (codeFind.find()) {
                String number = codeFind.group();
                String group = number.substring(14, number.length() - 4);
                return Double.valueOf(group);
            }
        } catch (Exception e) {
        }
        return defUsdCny;
    }

    /**
     * 加载网络汇率参数
     */
    public static void loadNumber() {

        usdToCny();
    }
}
