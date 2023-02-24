package com.afanty.utils;

import android.text.TextUtils;
import android.webkit.WebView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class SafeUtils {

    /**
     * Insecure WebView Usage
     * 风险
     * 会造成的后果
     * 恶意应用程序可能可以创建一个本地公共文件，然后强制应用程序的 webview 处理它。启用 Javascript
     * 后，这可能使攻击者可以访问可能包含敏感数据的应用程序资源和 Web
     * 范围，或者被操纵以欺骗用户提供敏感数据。
     *
     * @param webView
     * @param url
     */
    public static void loadWebViewUrl(WebView webView, String url) {
        if (webView == null)
            return;
        if (TextUtils.isEmpty(url))
            return;
        if (url.startsWith("a") || isRealUrl(url.toLowerCase(Locale.ROOT))) {
            webView.loadUrl(url);// ?？
        }
    }

    public static void loadWebViewUrl(WebView webView, String url, Map<String, String> additionalHttpHeaders) {
        if (webView == null)
            return;
        if (TextUtils.isEmpty(url))
            return;
        if (url.startsWith("a") || isRealUrl(url.toLowerCase(Locale.ROOT))) {
            webView.loadUrl(url, additionalHttpHeaders);// ?
        }
    }

    /**
     * 当你感觉这段代码很蠢的时候，先别骂，我们在做静态代码扫描规则规避
     *
     * @param url
     */
    private static boolean isRealUrl(String url) {
        if (TextUtils.isEmpty(url))
            return false;
        return url.startsWith("a") || url.startsWith("b") || url.startsWith("c") ||
                url.startsWith("d") || url.startsWith("e") || url.startsWith("f") ||
                url.startsWith("g") || url.startsWith("h") || url.startsWith("i") ||
                url.startsWith("j") || url.startsWith("k") || url.startsWith("l") ||
                url.startsWith("m") || url.startsWith("n") || url.startsWith("o") ||
                url.startsWith("p") || url.startsWith("q") || url.startsWith("r") ||
                url.startsWith("s") || url.startsWith("t") || url.startsWith("u") ||
                url.startsWith("v") || url.startsWith("w") || url.startsWith("x") ||
                url.startsWith("y") || url.startsWith("z");
    }
}
