package com.afanty.request;

import static com.afanty.request.OutParamsHelper.getPackageName;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.afanty.DeviceHelper;
import com.afanty.internal.internal.AdRequestListener;
import com.afanty.request.models.App;
import com.afanty.request.models.Banner;
import com.afanty.request.models.Imp;
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

public class RTBBannerRequest extends BaseRTBRequest {

    public RTBBannerRequest(String tagId){
        super(tagId);
    }
    public  void startLoad(AdRequestListener adRequestListener){
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

    private List<Imp> createImp(String id){
        List<Imp> list = new ArrayList<>();
        Imp imp = new Imp();
        imp.id = id;
        imp.metric = null;
        imp.banner = createBanner();
        imp.video = null;
        imp.audio = null;
        imp.aNative = null;
        imp.pmp = null;
        imp.displaymanager = null;
        imp.displaymanagerver = null;
        imp.instl = 0;
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

    private Banner createBanner(){
        Banner banner = new Banner();
        banner.w = 320;
        banner.h = 50;
        banner.mimes = Arrays.asList("image/png","image/jpg","image/gif");
        return banner;
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
        app.id = AppUtils.getAFTAppID(ContextUtils.getContext());
        app.name=!TextUtils.isEmpty(OutParamsHelper.getAppName())?OutParamsHelper.getAppName():getAppName();
        app.bundle= TextUtils.isEmpty(getPackageName())? ContextUtils.getContext().getPackageName(): getPackageName();
        app.domain= TextUtils.isEmpty(domin)?null:domin;
        app.storeurl= TextUtils.isEmpty(storeurl)?null:storeurl;
        app.cat = Arrays.asList(cat);
        app.sectioncat = null;
        app.pagecat=null;
        app.ver=OutParamsHelper.getVerCode()!=null?OutParamsHelper.getVerCode().toString(): AppUtils.getSdkVerCode()+"";
        app.privacypolicy=null;
        app.paid=null;
        app.publisher=createPublisher();
        app.content=null;
        app.keywords=null;
        app.ext=null;
        app.sv = 7000002L;
        return app;
    }

    private Publisher createPublisher(){
        Publisher publisher = new Publisher();
        publisher.id = AppUtils.getAftAppKey(ContextUtils.getContext());
        publisher.name = !TextUtils.isEmpty(OutParamsHelper.getPublisherName())? getPackageName():"";
        publisher.cat = null;
        publisher.domain = null;
        publisher.ext = null;
        return publisher;
    }

    private static User createUser(){
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
