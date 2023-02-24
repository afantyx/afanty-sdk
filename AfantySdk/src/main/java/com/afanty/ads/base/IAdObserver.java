package com.afanty.ads.base;

import com.afanty.ads.AdError;
import com.afanty.ads.core.RTBAd;

import java.util.Map;

public class IAdObserver {

    public enum AdEvent {
        AD_ACTION_IMPRESSION_ERROR("impression_error"),
        AD_ACTION_IMPRESSION("impression"),
        AD_ACTION_CLICKED("clicked"),
        AD_ACTION_COMPLETE("complete"),
        AD_ACTION_CLOSED("closed"),
        ;

        private final String mActionName;
        private Map<String, Object> mExtras;

        AdEvent(String actionName) {
            this.mActionName = actionName;
        }

        AdEvent(String actionName, Map<String, Object> extras) {
            this.mActionName = actionName;
            this.mExtras = extras;
        }

        public String getActionName() {
            return mActionName;
        }

        public Map<String, Object> getExtraParams() {
            return mExtras;
        }

    }

    public interface AdLoadCallback {

        void onAdLoaded(RTBAd rtbAd);

        void onAdLoadError(AdError adError);
    }

    public interface AdEventListener {

        void onAdImpressionError(AdError error);

        void onAdImpression();

        void onAdClicked();

        void onAdCompleted();

        void onAdClosed(boolean hasRewarded);

    }

    public interface VideoLifecycleCallbacks {
        /**
         * Called when video playback first begins.
         */
        void onVideoStart();

        /**
         * Called when video playback is playing.
         */
        void onVideoPlay();

        /**
         * Called when video playback is paused.
         */
        void onVideoPause();

        /**
         * Called when video playback finishes playing.
         */
        void onVideoEnd();

        /**
         * Called when the video changes mute state.
         */
        void onVideoMute(boolean isMuted);
    }

    public static abstract class AdLoadInnerListener {

        public abstract void onAdLoaded(RTBWrapper rtbBaseAd);

        public abstract void onAdLoadError(AdError adError);

        public void onAdLoaded(String tagId, RTBWrapper rtbBaseAd) {
            onAdLoaded(rtbBaseAd);
        }

        public void onAdLoadError(String tagId, AdError adError) {
            onAdLoadError(adError);
        }

    }

}
