package com.afanty.internal.action.type;

import android.content.Context;
import android.text.TextUtils;

import com.afanty.ads.DelayRunnableWork;
import com.afanty.ads.ThreadManager;
import com.afanty.config.BasicAftConfig;
import com.afanty.internal.action.ActionConstants;
import com.afanty.internal.action.ActionParam;
import com.afanty.internal.action.ActionResult;
import com.afanty.internal.action.ActionTypeInterface;
import com.afanty.internal.action.ActionUtils;
import com.afanty.internal.webview.WebViewCache;
import com.afanty.models.Bid;
import com.afanty.utils.ContextUtils;
import com.afanty.utils.HttpUtils;

public class ActionTypeDeeplink implements ActionTypeInterface {

    @Override
    public void resolveUrl(String deepLink, String landingPage, final ResolveResultListener resolveResultListener) {
        if (HttpUtils.isHttpUrl(deepLink)) {
            uploadAdClick(deepLink, resolveResultListener);
        } else {
            resolveResultListener.onSuccess(true, deepLink);
        }
    }

    private void uploadAdClick(String deepLink, ResolveResultListener resolveResultListener) {
//        if (!BasicAftConfig.isUseHttpRedirect()) {
//            AttributionManager.getInstance().uploadAdClick(WebViewCache.getInstance().getSingleWebView(ContextUtils.getContext()), deepLink, new AttributionManager.OnResultCallback() {
//                @Override
//                public void onResultClick(boolean success, String resultUrl) {
//                    resolveResultListener.onSuccess(success, resultUrl);
//                }
//            });
//        } else {
//            ThreadManager.getInstance().run(new DelayRunnableWork() {
//                @Override
//                public void execute() throws Exception {
//                    AttributionManager.getInstance().uploadAdClickByHttp(deepLink, new AttributionManager.OnResultCallback() {
//                        @Override
//                        public void onResultClick(boolean success, String resultUrl) {
//                            resolveResultListener.onSuccess(success, resultUrl);
//                        }
//                    }, AttributionManager.getInstance().getKeyUserAgent());
//                }
//            });
//        }
    }

    @Override
    public boolean shouldTryHandlingAction(Bid bid, int actionType) {
        return !TextUtils.isEmpty(bid.getDeepLinkUrl());
    }

    @Override
    public ActionResult performAction(Context context, Bid bid, String url, ActionParam actionParam) {
        boolean result = ActionUtils.startAppByDeeplink(url);
        return new ActionResult.Builder(result).build();
    }

    @Override
    public ActionResult performActionWhenOffline(Context context, Bid bid, String url, ActionParam actionParam) {
        boolean result = ActionUtils.startAppByDeeplink(url);
        return new ActionResult.Builder(result).offlineAction(true).build();
    }

    @Override
    public int getActionType() {
        return ActionConstants.ACTION_DEEPLINK;
    }
}
