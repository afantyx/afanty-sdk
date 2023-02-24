package com.afanty.request;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.afanty.BuildConfig;
import com.afanty.DeviceHelper;
import com.afanty.internal.internal.AdRequestListener;
import com.afanty.request.models.App;
import com.afanty.request.models.Imp;
import com.afanty.request.models.Native;
import com.afanty.request.models.Publisher;
import com.afanty.request.models.Regs;
import com.afanty.request.models.Site;
import com.afanty.request.models.User;
import com.afanty.test.TestConfig;
import com.afanty.utils.AppUtils;
import com.afanty.utils.ContextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class RTBNativeRequest extends BaseRTBRequest {


    private boolean mIsInterstitial = false;

    public RTBNativeRequest(String tagId){
        super(tagId);
    }

    public RTBNativeRequest(String tagId, boolean isInterstitial){
        super(tagId);
        mIsInterstitial = isInterstitial;
    }


    public void startLoad(AdRequestListener adRequestListener){
        String id = UUID.randomUUID().toString();
        new OpenRTBRequest.Builder()
                .appendId(id)
                .appendImp(createImp(id))
                .appendApp(createApp(null,null,""))
                .appendDevice(DeviceHelper.createDevice(ContextUtils.getContext(),1))
                .appendAt(1)
                .appendExt(createExt())
                .appendUser(createUser())
                .appendTmax(2000)
                .build()
                .loadAd(new OpenRTBReqListener() {
                    @Override
                    public void onRequestError(String errorType, String msg) {
                        if (adRequestListener != null) {
                            adRequestListener.onAdRequestError(errorType, msg);
                        }
                    }

                    @Override
                    public void onRequestSuccess(String jsonStr) {
                        adRequestListener.onAdRequestSuccess(jsonStr);
                    }
                });
    }

    private List<Imp> createImp(String impId){
        List<Imp> list = new ArrayList<>();
        Imp imp = new Imp();
        imp.id = impId;
        imp.metric = null;
        imp.banner = null;
        imp.video = null;
        imp.audio = null;
        imp.aNative = createNative();
        imp.pmp = null;
        imp.displaymanager = null;
        imp.displaymanagerver = null;
        imp.instl = mIsInterstitial?1:0;
        imp.tagid = mTagId;
        imp.bidfloor = 0;
        imp.bidfloorcur = "USD";
        imp.clickbrowser = null;
        imp.secure = null;
        imp.iframebuster = null;
        imp.exp = 3600;
        imp.ext = createImpExt();
        list.add(imp);
        return list;
    }

    private JSONObject createImpExt(){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ad_count",1);
            return jsonObject;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    private Native createNative(){
        Native aNative = new Native();
        aNative.request = "{\"native\":{\"assets\":[{\"id\":1,\"title\":{\"len\":30},\"required\":1},{\"id\":2,\"img\":{\"type\":1,\"wmin\":300,\"hmin\":300},\"required\":1},{\"id\":3,\"img\":{\"type\":3,\"wmin\":660,\"hmin\":346},\"required\":1},{\"id\":4,\"data\":{\"type\":1,\"len\":25}},{\"id\":5,\"data\":{\"type\":2,\"len\":35}},{\"id\":6,\"data\":{\"type\":12,\"len\":15}}],\"ver\":\"1.2\",\"context\":1,\"plcmttype\":1}}";
        aNative.ver = null;
        aNative.api = null;
        aNative.battr = null;
        aNative.ext = null;
        return aNative;
    }

    private Site createSite(){
        Site site = new Site();
        site.id = "";
        site.name="";
        site.domain="";
        site.cat=null;
        site.sectioncat=null;
        site.pagecat=null;
        site.page="";
        site.ref="";
        site.search=null;
        site.mobile=null;
        site.privacypolicy=0;
        site.publisher=null;
        site.content=null;
        site.keywords="";
        site.ext= null;
        return site;
    }

    private String appName;
    private String getAppName(){
        if (!TextUtils.isEmpty(appName))
            return appName;
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try{
            packageManager = ContextUtils.getContext().getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(ContextUtils.getContext().getPackageName(), 0);
            appName = (String) packageManager.getApplicationLabel(applicationInfo);
            return appName;
        }catch (Exception ignore){}
        return "";
    }

    private App createApp(String storeurl, String domin, String... cat){
        App app = new App();
        if (!TextUtils.isEmpty(TestConfig.getTestAppId()))
            app.id = TestConfig.getTestAppId();
        else
            app.id = AppUtils.getAFTAppID(ContextUtils.getContext());
        app.name=!TextUtils.isEmpty(OutParamsHelper.getAppName())?OutParamsHelper.getAppName():getAppName();
        app.bundle= TextUtils.isEmpty(OutParamsHelper.getPackageName())? ContextUtils.getContext().getPackageName():OutParamsHelper.getPackageName();
        app.domain= TextUtils.isEmpty(domin)?null:domin;
        app.storeurl= TextUtils.isEmpty(storeurl)?null:storeurl;
        if (cat!=null && cat.length>0)
            app.cat = Arrays.asList(cat);
        else
            app.cat = null;
        app.sectioncat = null;
        app.pagecat=null;
        app.ver=OutParamsHelper.getVerCode()!=null?OutParamsHelper.getVerCode().toString(): AppUtils.getSdkVerCode()+"";
        app.privacypolicy=null;
        app.paid=null;
        app.publisher=createPublisher();
        app.content=null;
        app.keywords=null;
        app.sv = 7000002L;
        app.ext=null;
        return app;
    }

    private Publisher createPublisher(){
        Publisher publisher = new Publisher();
        publisher.id = AppUtils.getAftAppKey(ContextUtils.getContext());
        publisher.name = !TextUtils.isEmpty(OutParamsHelper.getPublisherName())?OutParamsHelper.getPackageName():"";
        publisher.cat = null;
        publisher.domain = null;
        publisher.ext = null;
        return publisher;
    }

    private User createUser(){
        User user = new User();
        user.id = null;
        user.buyeruid = null;
        user.yob = null;
        user.gender = null;
        user.keywords = null;
        user.customdata = null;
        user.geo = null;
        user.data = null;
        user.ext = null;
        return user;
    }

    private List<String> createCur(String currencies){
        return Arrays.asList(currencies);
    }

    private List<String> createBcat(){
        return Arrays.asList("IAB25","IAB7-39","IAB8-18","IAB8-5","IAB8-9");
    }

    private Regs createRegs(){
        Regs regs = new Regs();
        regs.coppa = null;
        JSONObject jsob = new JSONObject();
        try {
            jsob.put("us_privacy", "1---");
        }catch (JSONException e){}
        regs.ext = jsob;
        return regs;
    }

    private JSONObject createExt(){
        JSONObject json = new JSONObject();
        try {
            JSONArray jsoa = new JSONArray();
            jsoa.put("mt");
            json.put("srclist",jsoa);
        }catch (JSONException e){}
        return json;
    }
}
