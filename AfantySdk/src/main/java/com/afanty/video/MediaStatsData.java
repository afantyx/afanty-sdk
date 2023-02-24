package com.afanty.video;

import java.util.ArrayList;
import java.util.List;

public class MediaStatsData {
    private String mRid;
    private String mAdId;
    private String mPlacementId;
    private String mCreativeId;
    private List<String> mPlayTrackUrls = new ArrayList<>();
    private List<String> mStartTrackUrls = new ArrayList<>();
    private List<String> mQuarterTrackUrls = new ArrayList<>();
    private List<String> mHalfTrackUrls = new ArrayList<>();
    private List<String> mThreeQuarterUrls = new ArrayList<>();
    private List<String> mCompleteTrackUrls = new ArrayList<>();

    private List<String> mCreativeViewTrackUrls = new ArrayList<>();
    private List<String> mSkipTrackUrls = new ArrayList<>();
    private List<String> mCloseTrackUrls = new ArrayList<>();
    private List<String> mImpressionTrackers = new ArrayList<>();

    private List<String> mUnMuteTrackUrls = new ArrayList<>();
    private List<String> mMuteTrackUrls = new ArrayList<>();

    private List<String> mErrorTrackUrls = new ArrayList<>();
    private List<String> mPauseTrackers = new ArrayList<>();
    private List<String> mResumeTrackers = new ArrayList<>();

    public String getRid() {
        return mRid;
    }

    public void setRid(String rid) {
        this.mRid = rid;
    }

    public String getAdId() {
        return mAdId;
    }

    public void setAdId(String adId) {
        this.mAdId = adId;
    }

    public String getPlacementId() {
        return mPlacementId;
    }

    public void setPlacementId(String placementId) {
        this.mPlacementId = placementId;
    }

    public String getCreativeId() {
        return mCreativeId;
    }

    public void setCreativeId(String creativeId) {
        this.mCreativeId = creativeId;
    }

    public List<String> getPlayTrackUrls() {
        return mPlayTrackUrls;
    }

    public void setPlayTrackUrls(List<String> playTrackUrls) {
        this.mPlayTrackUrls = playTrackUrls;
    }

    public List<String> getStartTrackUrls() {
        return mStartTrackUrls;
    }

    public void setStartTrackUrls(List<String> startTrackUrls) {
        this.mStartTrackUrls = startTrackUrls;
    }

    public List<String> getQuarterTrackUrls() {
        return mQuarterTrackUrls;
    }

    public void setQuarterTrackUrls(List<String> quarterTrackUrls) {
        this.mQuarterTrackUrls = quarterTrackUrls;
    }

    public List<String> getHalfTrackUrls() {
        return mHalfTrackUrls;
    }

    public void setHalfTrackUrls(List<String> halfTrackUrls) {
        this.mHalfTrackUrls = halfTrackUrls;
    }

    public List<String> getThreeQuarterUrls() {
        return mThreeQuarterUrls;
    }

    public void setThreeQuarterUrls(List<String> threeQuarterUrls) {
        this.mThreeQuarterUrls = threeQuarterUrls;
    }

    public List<String> getCompleteTrackUrls() {
        return mCompleteTrackUrls;
    }

    public void setCompleteTrackUrls(List<String> completeTrackUrls) {
        this.mCompleteTrackUrls = completeTrackUrls;
    }

    public List<String> getCreativeViewTrackUrls() {
        return mCreativeViewTrackUrls;
    }

    public void setCreativeViewTrackUrls(List<String> creativeViewTrackUrls) {
        this.mCreativeViewTrackUrls = creativeViewTrackUrls;
    }

    public List<String> getSkipTrackUrls() {
        return mSkipTrackUrls;
    }

    public void setSkipTrackUrls(List<String> skipTrackUrls) {
        this.mSkipTrackUrls = skipTrackUrls;
    }

    public List<String> getCloseTrackUrls() {
        return mCloseTrackUrls;
    }

    public void setCloseTrackUrls(List<String> closeTrackUrls) {
        this.mCloseTrackUrls = closeTrackUrls;
    }

    public List<String> getUnMuteTrackUrls() {
        return mUnMuteTrackUrls;
    }

    public void setUnMuteTrackUrls(List<String> unMuteTrackUrls) {
        this.mUnMuteTrackUrls = unMuteTrackUrls;
    }

    public List<String> getMuteTrackUrls() {
        return mMuteTrackUrls;
    }

    public void setMuteTrackUrls(List<String> muteTrackUrls) {
        this.mMuteTrackUrls = muteTrackUrls;
    }

    public List<String> getErrorTrackUrls() {
        return mErrorTrackUrls;
    }

    public void setErrorTrackUrls(List<String> errorTrackUrls) {
        this.mErrorTrackUrls = errorTrackUrls;
    }

    public List<String> getPauseTrackers() {
        return mPauseTrackers;
    }

    public void setPauseTrackers(List<String> pauseTrackers) {
        this.mPauseTrackers = pauseTrackers;
    }

    public List<String> getResumeTrackers() {
        return mResumeTrackers;
    }

    public void setResumeTrackers(List<String> resumeTrackers) {
        this.mResumeTrackers = resumeTrackers;
    }

    public List<String> getImpressionTrackers() {
        return mImpressionTrackers;
    }

    public void setImpressionTrackers(List<String> impressionTrackers) {
        this.mImpressionTrackers = impressionTrackers;
    }
}
