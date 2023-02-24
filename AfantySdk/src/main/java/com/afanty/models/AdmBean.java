package com.afanty.models;

import android.text.TextUtils;

import com.afanty.internal.helper.AdDataHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdmBean {
    public static int TEXT_DESC = 2;
    public static int TEXT_BTN = 12;
    public static int IMG_ICON_TYPE = 1;
    public static int IMG_POSTER_TYPE = 3;
    private ExtraBean extraBean;
    private List<VideoBean> videos;
    private VastBean vastBean;
    private ImgBean posterBean;
    private ImgBean iconBean;
    private TitleBean titleBean;
    private DataBean descBean;
    private DataBean callToAction;

    private VideoBean mMaxResolutionVideo;
    private VideoBean mMinResolutionVideo;

    public AdmBean(String jsonS) {
        try {
            JSONObject jsonObject = new JSONObject(jsonS);
            if (jsonObject != null) {
                if (jsonObject.has("ext")) {
                    extraBean = new ExtraBean(jsonObject.getJSONObject("ext"));
                }
                if (jsonObject.has("native")) {
                    parseData(jsonObject.getJSONObject("native"));
                } else if (jsonObject.has("banner")) {
                    parseData(jsonObject.getJSONObject("banner"));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseData(JSONObject jsonObject) {
        try {
            ver = jsonObject.optString("ver");
            if (jsonObject.has("assets")) {
                JSONArray assetArray = jsonObject.getJSONArray("assets");
                for (int i = 0; i < assetArray.length(); i++) {
                    JSONObject asset = assetArray.getJSONObject(i);
                    int id = asset.optInt("id");
                    if (asset.has("title")) {
                        titleBean = new TitleBean(asset.getJSONObject("title"), id);
                    }
                    if (asset.has("data")) {
                        DataBean data = new DataBean(asset.getJSONObject("data"), id);
                        if (data == null) {
                            continue;
                        }
                        if (data.getType() == TEXT_BTN) {
                            setCallToAction(data);
                        } else if (data.getType() == TEXT_DESC) {
                            setDesc(data);
                        }
                    }
                    if (asset.has("img")) {
                        ImgBean imgBean = new ImgBean(asset.getJSONObject("img"), id);
                        if (imgBean != null) {
                            if (imgBean.getType() == IMG_POSTER_TYPE) {
                                setPosterBean(imgBean);
                            } else if (imgBean.getType() == IMG_ICON_TYPE) {
                                setIconBean(imgBean);
                            }
                        }
                    }
                    if (asset.has("video")) {
                        JSONObject videoJson = asset.getJSONObject("video");
                        if (videoJson.has("vasttag")) {
                            vastBean = new VastBean(videoJson, id);
                        } else if (videoJson.has("url")) {
                            List<VideoBean> videoList = new ArrayList<>();
                            String urlString = videoJson.getString("url");
                            JSONArray urlArray = new JSONArray(urlString);
                            for (int j = 0; j < urlArray.length(); j++) {
                                videoList.add(new VideoBean(urlArray.getJSONObject(j)));
                            }
                            setVideos(videoList);
                        }
                    }

                }
            }
            if (jsonObject.has("link")) {
                LinkBean linkBean = new LinkBean(jsonObject.getJSONObject("link"));
                this.link = linkBean;
            }
            if (jsonObject.has("imptrackers")) {
                JSONArray impArray = jsonObject.getJSONArray("imptrackers");
                List<String> trackers = new ArrayList<>();
                for (int i = 0; i < impArray.length(); i++) {
                    String trackerUrl = (String) impArray.get(i);
                    trackers.add(trackerUrl);
                }
                this.imptrackers = trackers;
            }
        } catch (JSONException e) {
        }
    }

    public VideoBean getMaxResolutionVideo() {
        if (mMaxResolutionVideo == null)
            mMaxResolutionVideo = AdDataHelper.getRTBMaxResolution(getVideos());

        return mMaxResolutionVideo;
    }

    public VideoBean getMinResolutionVideo() {
        if (mMinResolutionVideo == null)
            mMinResolutionVideo = AdDataHelper.getRTBMinResolution(getVideos());

        return mMinResolutionVideo;
    }

    public void setVideos(List<VideoBean> videos) {
        this.videos = videos;
    }

    public List<VideoBean> getVideos() {
        return videos;
    }

    public String getVast() {
        if (vastBean == null) {
            return "";
        }
        return vastBean.vast;
    }

    public ImgBean getPosterBean() {
        return posterBean;
    }

    public DataBean getCallToAction() {
        return callToAction;
    }

    public void setCallToAction(DataBean callToAction) {
        this.callToAction = callToAction;
    }

    public void setPosterBean(ImgBean posterBean) {
        this.posterBean = posterBean;
    }

    public ImgBean getIconBean() {
        return iconBean;
    }

    public void setIconBean(ImgBean iconBean) {
        this.iconBean = iconBean;
    }

    public ExtraBean getExtraBean() {
        return extraBean;
    }

    public void setExtraBean(ExtraBean extraBean) {
        this.extraBean = extraBean;
    }

    public VastBean getVastBean() {
        return vastBean;
    }

    public void setVastBean(VastBean vastBean) {
        this.vastBean = vastBean;
    }

    public TitleBean getTitleBean() {
        return titleBean;
    }

    public void setTitleBean(TitleBean titleBean) {
        this.titleBean = titleBean;
    }

    public DataBean getDesc() {
        return descBean;
    }

    public void setDesc(DataBean descBean) {
        this.descBean = descBean;
    }

    public ImgBean getImgBeanByType(int type) {
        if (type == IMG_ICON_TYPE) {
            return getIconBean();
        }
        if (type == IMG_POSTER_TYPE) {
            return getPosterBean();
        }
        return null;
    }

    public DataBean getDataByType(int type) {
        if (type == TEXT_BTN) {
            return getCallToAction();
        }
        if (type == TEXT_DESC) {
            return getDesc();
        }
        return null;
    }


    private String ver;
    private LinkBean link;
    private List<String> imptrackers;

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public LinkBean getLink() {
        return link;
    }

    public void setLink(LinkBean link) {
        this.link = link;
    }


    public List<String> getImptrackers() {
        return imptrackers;
    }

    public void setImptrackers(List<String> imptrackers) {
        this.imptrackers = imptrackers;
    }

    public static class LinkBean {

        private String url;
        private List<String> clicktrackers;

        public LinkBean(JSONObject linkJson) {
            List<String> trackers = new ArrayList<>();
            url = linkJson.optString("url");
            JSONArray array = null;
            try {
                array = linkJson.getJSONArray("clicktrackers");
                for (int i = 0; i < array.length(); i++) {
                    String trackerUrl = (String) array.get(i);
                    trackers.add(trackerUrl);
                }
                clicktrackers = trackers;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public List<String> getClicktrackers() {
            return clicktrackers;
        }

        public void setClicktrackers(List<String> clicktrackers) {
            this.clicktrackers = clicktrackers;
        }
    }


    public static class TitleBean {

        private String text;
        private int len;
        private int id;

        public TitleBean(JSONObject jsonString, int id) {
            text = jsonString.optString("text");
            len = jsonString.optInt("len");
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }
    }

    public static class DataBean {

        private int type;
        private int len;
        private int id;
        private String value;

        public DataBean(JSONObject data, int id) {
            type = data.optInt("type");
            value = data.optString("value");
            len = data.optInt("len");
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getLen() {
            return len;
        }

        public void setLen(int len) {
            this.len = len;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class ImgBean {

        private int type;
        private String url;
        private int w;
        private int h;
        private int id;

        public ImgBean(JSONObject img, int id) {
            type = img.optInt("type");
            url = img.optString("url");
            w = img.optInt("w");
            h = img.optInt("h");
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }

        @Override
        public String toString() {
            return "ImgBean{" +
                    "type=" + type +
                    ", url='" + url + '\'' +
                    ", w=" + w +
                    ", h=" + h +
                    '}';
        }
    }

    public static class VideoBean {
        public static final String DASH_RESOLUTION = "AUTO";
        private String resolution;
        private String size;
        private String url;
        private String downLoadUrl;

        public VideoBean(JSONObject img) {
            resolution = img.optString("resolution");
            url = img.optString("url");
            downLoadUrl = img.optString("download_url");
            size = img.optString("size");
        }

        public String getResolution() {
            return resolution;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getUrl() {
            if (!TextUtils.isEmpty(downLoadUrl)) {
                return downLoadUrl;
            }
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

    public static class VastBean {
        private int id;
        private String vast;

        public VastBean(JSONObject img, int id) {
            vast = img.optString("vasttag");
            this.id = id;
        }

        @Override
        public String toString() {
            return "VastBean{" +
                    "id=" + id +
                    ", vast='" + vast + '\'' +
                    '}';
        }
    }
}
