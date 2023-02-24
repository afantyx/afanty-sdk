package com.afanty.internal.action;

import com.afanty.models.Bid;

public class ActionParam {

    public Bid mBid;
    public String mDeepLink;
    public String mLandingPage;
    public int mActionType;
    public int mViewCenterX = -1;
    public int mViewCenterY = -1;
    public String mSourceType = "none";
    public boolean mForceGpAction = false;
    public int mEffectType = -1;

    public ActionParam(Bid adData, String deepLink, String landingPage, int actionType) {
        this.mBid = adData;
        this.mDeepLink = deepLink;
        this.mLandingPage = landingPage;
        this.mActionType = actionType;
    }

    @Override
    public String toString() {
        return "ActionParam{" +
                "mAdData=" + mBid +
                ", mDeepLink='" + mDeepLink + '\'' +
                ", mLandingPage='" + mLandingPage + '\'' +
                ", mActionType=" + mActionType +
                ", mViewCenterX=" + mViewCenterX +
                ", mViewCenterY=" + mViewCenterY +
                ", mSoureceType='" + mSourceType + '\'' +
                ", mForceGpAction=" + mForceGpAction +
                ", mEffectType=" + mEffectType +
                '}';
    }

}
