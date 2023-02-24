package com.afanty.models;


import android.text.TextUtils;

import com.afanty.config.BidConfigHelper;
import com.afanty.internal.action.ActionConstants;
import com.afanty.internal.action.ActionParam;
import com.afanty.internal.helper.AdDataHelper;
import com.afanty.internal.internal.AppInfo;
import com.afanty.internal.internal.ProductData;
import com.afanty.vast.VastVideoConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bid {
    public static final int ANIMATION_NONE = 1;
    public static final int ANIMATION_REVEAL = 2;
    public static final int ANIMATION_EXPLODE = 3;
    public static final int AUTO_DOWNLOAD_OFF = 0;
    public static final int AUTO_DOWNLOAD_ON = 1;

    public String id;
    public String impid;
    public float price;
    public String nurl;
    public String burl;
    public String lurl;
    public String adm;
    public String adid;
    public List<String> adomain;
    public String bundle;
    public String iurl;
    public String cid;
    public String crid;
    public String tactic;
    public List<String> cat;
    public List<Integer> attr;
    public int api;
    public int protocol;
    public int qagmediarating;
    public String language;
    public String dealid;
    public int w;
    public int h;
    public int wratio;
    public int hratio;
    public int exp;
    public int autoDownload;
    public JSONObject ext;
    public String tagid;
    private AdmBean admBean;
    private String videoPlayUrl;
    private int width;
    private int height;
    private VastVideoConfig mVastVideoConfig;


    private int refreshInterval;
    private String landingPage;

    private int mCurShowCnt = 0;

    private String mShowCountToday = "";

    public AdmBean getAdmBean() {
        return admBean;
    }

    public void setNativeBean(AdmBean admBean) {
        this.admBean = admBean;
    }

    private boolean isLoaded = false;

    public Bid(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            parseBidJsonData(json);
        } catch (Exception e) {
        }
    }

    public Bid(JSONObject json) {
        parseBidJsonData(json);
    }

    private void parseBidJsonData(JSONObject json) {
        this.id = json.optString("id");
        this.impid = json.optString("impid");
        this.price = (float) json.optLong("price");
        this.nurl = json.optString("nurl");
        this.burl = json.optString("burl");
        this.lurl = json.optString("lurl");
        this.adm = json.optString("adm");
        if (!TextUtils.isEmpty(adm)) {
            admBean = new AdmBean(adm);
        }
        this.adid = json.optString("adid");
        this.adomain = createArray(json.optJSONArray("adomain"));
        this.bundle = json.optString("bundle");
        this.iurl = json.optString("iurl");
        this.cid = json.optString("cid");
        this.crid = json.optString("crid");
        this.tactic = json.optString("tactic");
        this.cat = createArray(json.optJSONArray("cat"));
        this.attr = createIntArray(json.optJSONArray("attr"));
        this.api = json.optInt("api");
        this.protocol = json.optInt("protocol");
        this.qagmediarating = json.optInt("qagmediarating");
        this.language = json.optString("language");
        this.dealid = json.optString("dealid");
        this.w = json.optInt("w");
        this.h = json.optInt("h");
        this.wratio = json.optInt("wratio");
        this.hratio = json.optInt("hratio");
        this.exp = json.optInt("exp");
        this.ext = json.optJSONObject("ext");
        if (ext != null)
            this.tagid = ext.optString("tagid");
        isLoaded = true;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getAdId() {
        return adid;
    }

    private List<String> createArray(JSONArray adomainArray) {
        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < adomainArray.length(); i++) {
                list.add(adomainArray.getString(i));
            }
        } catch (Exception e) {
        }
        return list;
    }

    private List<Integer> createIntArray(JSONArray adomainArray) {
        List<Integer> list = new ArrayList<>();
        try {
            for (int i = 0; i < adomainArray.length(); i++) {
                list.add(adomainArray.getInt(i));
            }
        } catch (Exception e) {
        }
        return list;
    }

    public List<String> getTrackImpUrls() {
        if (admBean == null) {
            return Collections.emptyList();
        }

        return admBean.getImptrackers();
    }

    public ActionParam getActionParam() {
        return new ActionParam(this, getDeepLinkUrl(), getLandingPageUrl(), getActionType());
    }

    public String getDeepLinkUrl() {
        if (admBean == null) {
            return "";
        }
        AdmBean.LinkBean link = admBean.getLink();
        if (link == null) {
            return "";
        }
        if (getActionType() != 103)
            return "";
        return link.getUrl();
    }

    public String getLandingPageUrl() {
        if (admBean == null) {
            return "";
        }
        if (getActionType() != 103) {
            if (admBean.getExtraBean() != null && admBean.getLink() != null) {
                return admBean.getLink().getUrl();
            }
        }
        return "";
    }


    public String getUrlByType(int type) {
        if (admBean == null) {
            return "";
        }
        AdmBean.ImgBean imgBean = admBean.getImgBeanByType(type);
        if (imgBean != null) {
            return imgBean.getUrl();
        }
        return "";
    }


    public String getTitle() {
        if (admBean == null) {
            return "";
        }
        AdmBean.TitleBean titleBean = admBean.getTitleBean();
        if (titleBean != null) {
            return titleBean.getText();
        }
        return "";
    }

    public String getDesc() {
        if (admBean == null) {
            return "";
        }
        AdmBean.DataBean titleBean = admBean.getDesc();
        if (titleBean != null) {
            return titleBean.getValue();
        }
        return "";
    }

    public AdmBean.ImgBean getImgBean(int type) {
        if (admBean == null) {
            return null;
        }
        return admBean.getImgBeanByType(type);
    }

    public String getDataByType(int type) {
        if (admBean == null) {
            return "";
        }
        AdmBean.DataBean dataBean = admBean.getDataByType(type);
        if (dataBean != null) {
            return dataBean.getValue();
        }
        return "";
    }

    public String getIconUrl() {
        if (admBean != null && admBean.getIconBean() != null)
            return admBean.getIconBean().getUrl();
        return "";
    }

    public List<AdmBean.VideoBean> getVideos() {
        if (admBean == null) {
            return Collections.emptyList();
        }
        List<AdmBean.VideoBean> videos = admBean.getVideos();
        if (videos == null || videos.size() <= 0) {
            return Collections.emptyList();
        }
        return videos;
    }

    public String getVast() {
        if (admBean == null) {
            return "";
        }
        return admBean.getVast();
    }

    public boolean isEffectiveShow() {
        return mCurShowCnt == 1;
    }

    public boolean hasNotShown() {
        return mCurShowCnt == 0;
    }

    public void addCurShowCnt() {
        this.mCurShowCnt += 1;
    }


    public String getDownloadUrl() {
        if (admBean == null || admBean.getExtraBean() == null)
            return "";
        if (!TextUtils.isEmpty(admBean.getExtraBean().url)) {
            return admBean.getExtraBean().url;
        }
        if (getProductData() != null && !TextUtils.isEmpty(getProductData().getApkUrl()))
            return getProductData().getApkUrl();
        return admBean.getExtraBean().url;
    }

    private boolean isDeepLink() {
        return getActionType() == ActionConstants.ACTION_DEEPLINK;
    }

    public int getActionType() {
        if (admBean == null)
            return 0;
        return getAdmBean().getExtraBean().actionType;
    }

    public String getPid() {
        return tagid;
    }

    public void setVastPlayUrl(String videoUrl) {
        this.videoPlayUrl = videoUrl;
    }

    public String getVastPlayUrl() {
        return videoPlayUrl;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(String landingPage) {
        this.landingPage = landingPage;
    }

    public VastVideoConfig getVastVideoConfig() {
        return mVastVideoConfig;
    }

    public void setVastVideoConfig(VastVideoConfig vastVideoConfig) {
        mVastVideoConfig = vastVideoConfig;
    }

    public int getCreativeType() {
        if (admBean == null) {
            return 0;
        }
        ExtraBean extraBean = admBean.getExtraBean();
        if (extraBean == null) {
            return 0;
        }
        return extraBean.c_type;
    }

    public void appendTrackImpressionUrls(String trackClickUrls) {
        if (admBean == null || admBean.getImptrackers() == null) {
            return;
        }
        List<String> impTrackers = admBean.getImptrackers();
        impTrackers.add(trackClickUrls);
        admBean.setImptrackers(impTrackers);
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public AppInfo getAppInfo() {
        AppInfo appInfo = null;
        try {
            String pkg = getProductData().getPkgName();
            int ver = getProductData().getAppVersionCode();
            appInfo = new AppInfo(pkg, ver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    public int getAdActionType() {
        return getActionType();
    }

    public String getRid() {
        return id;
    }


    public String getSIParam() {
        return "";
    }

    public ProductData getProductData() {
        try {
            return getAdmBean().getExtraBean().productData;
        } catch (Exception e) {
        }
        return null;
    }
    public String getPlacementId() {
        return tagid;
    }

    public String getCreativeId() {
        if (getAdmBean() != null && getAdmBean().getExtraBean() != null)
            return getAdmBean().getExtraBean().getFmt_id() + "";
        return "";
    }

    public List<String> getTrackActionAdvertiserUrls() {
        List<String> trackers = new ArrayList<>();
        if (admBean != null && admBean.getExtraBean() != null && admBean.getExtraBean().a_tracker != null) {
            JSONArray array = admBean.getExtraBean().a_tracker;
            for (int i = 0; i < array.length(); i++) {
                trackers.add(array.optString(i));
            }
        }
        if (trackers.size() > 0)
            return AdDataHelper.getReplaceMacroSiteUrls(trackers, this);
        return trackers;
    }

    public String getPackageDownloadUrl() {
        return getDownloadUrl();
    }

    public List<String> getTrackClickUrls() {
        if (getAdmBean() != null && getAdmBean().getLink() != null && getAdmBean().getLink().getClicktrackers() != null)
            return getAdmBean().getLink().getClicktrackers();
        return Collections.emptyList();
    }


    public String getTrackActionUrls() {
        List<String> urls = getTrackEffectUrls();
        if (urls == null || urls.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();

        for (String trackUrl : urls) {
            if (builder.length() == 0) {
                builder.append(trackUrl);
            } else {
                builder.append(",").append(trackUrl);
            }
        }
        return builder.toString();
    }

    public List<String> getTrackEffectUrls() {
        List<String> trackers = new ArrayList<>();
        return trackers;
    }


    public String getShowCountToday() {
        return mShowCountToday;
    }

    public void setShowCountToday(String value) {
        this.mShowCountToday = value;
    }


    public long getPriceBid() {
        return Long.MAX_VALUE;
    }

    public boolean isNeedLandPage() {
        return false;
    }

    public void setActionType(int actionType) {
        getAdmBean().getExtraBean().actionType = actionType;
    }

    public void setAutoDownLoad(int autoDownLoad) {
        autoDownload = autoDownLoad;
    }

    public List<String> getImageUrls() {
        List<String> urls = new ArrayList<>();
        if (getAdmBean() == null)
            return urls;
        if (getAdmBean().getPosterBean() != null) {
            urls.add(getAdmBean().getPosterBean().getUrl());
        }
        return urls;
    }

    public String getImageUrl() {
        List<String> imgs = getImageUrls();
        if (imgs.isEmpty())
            return "";
        return imgs.get(0);
    }

    public int getFullSkipPoint() {
        return BidConfigHelper.VideoConfig.getSkip();
    }

    public boolean isSupportSkip() {
        return false;
    }

    public int getFullClosePoint() {
        return BidConfigHelper.VideoConfig.getClose();
    }

    public int getRewardTime(){
        return BidConfigHelper.VideoConfig.getRewardTime();
    }

    public boolean isShowVideoMute() {
        return true;
    }

    public String getAMPAppId() {
        return "";
    }

    public boolean isOfflineAd() {
        return false;
    }

    public long getOfflineExpireTime() {
        return 0;
    }

    public int getAnimationType() {
        return 1;
    }

    public String getDspId() {
        return "";
    }

    public int getDspType() {
        return 0;
    }

    public String getAdCacheScene() {
        return "";
    }

    public String getViewId() {
        return "";
    }

    public boolean hasAdLogo() {
        return true;
    }

    public List<String> getLandingPageTrackImpressionUrls() {
        return Collections.emptyList();
    }

    public List<String> getLandingPageTrackClickUrls() {
        return Collections.emptyList();
    }

    public String getSource() {
        return "";
    }

    public String getSid() {
        return "";
    }

    public String getMatchAppPkgName() {
        return "";
    }
}
