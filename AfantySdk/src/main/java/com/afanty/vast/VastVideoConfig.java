// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afanty.vast.utils.Constants;
import com.afanty.vast.utils.Preconditions;
import com.afanty.vast.utils.Strings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class VastVideoConfig implements Serializable {
    private static final String TAG = "Ad.VastVideoConfig";
    private static final long serialVersionUID = 2L;

    @NonNull
    private final ArrayList<VastTracker> mImpressionTrackers;
    @NonNull
    private final ArrayList<VastFractionalProgressTracker> mFractionalTrackers;
    @NonNull
    private final ArrayList<VastAbsoluteProgressTracker> mAbsoluteTrackers;
    @NonNull
    private final ArrayList<VastTracker> mPauseTrackers;
    @NonNull
    private final ArrayList<VastTracker> mResumeTrackers;
    @NonNull
    private final ArrayList<VastTracker> mCompleteTrackers;
    @NonNull
    private final ArrayList<VastTracker> mCloseTrackers;
    @NonNull
    private final ArrayList<VastTracker> mSkipTrackers;
    @NonNull
    private final ArrayList<VastTracker> mClickTrackers;
    @NonNull
    private final ArrayList<VastTracker> mErrorTrackers;
    @NonNull
    private final ArrayList<VastTracker> mMuteTrackers;
    @NonNull
    private final ArrayList<VastTracker> mUnMuteTrackers;

    @Nullable
    private String mClickThroughUrl;
    @Nullable
    private String mNetworkMediaFileUrl;
    private int mBestMediaFileWidth;
    private int mBestMediaFileHeight;
    @Nullable
    private String mDiskMediaFileUrl;
    @Nullable
    private String mSkipOffset;
    @Nullable
    private VastCompanionAdConfig mLandscapeVastCompanionAdConfig;
    @Nullable
    private VastCompanionAdConfig mPortraitVastCompanionAdConfig;
    @NonNull
    private Map<String, VastCompanionAdConfig> mSocialActionsCompanionAds;
    @Nullable
    private VastIconConfig mVastIconConfig;
    private boolean mIsRewardedVideo;

    // Custom extensions
    @Nullable
    private String mCustomCtaText;
    @Nullable
    private String mCustomSkipText;
    @Nullable
    private String mCustomCloseIconUrl;
    @Nullable
    private VideoViewAbilityTracker mVideoViewAbilityTracker;
    // ViewAbility
    @NonNull
    private final Map<String, String> mExternalViewAbilityTrackers;
    @NonNull
    private final Set<String> mAvidJavascriptResources;
    @NonNull
    private final Set<String> mMoatImpressionPixels;

    // MoPub-specific metadata
    private String mDspCreativeId;
    private String mPrivacyInformationIconImageUrl;
    private String mPrivacyInformationIconClickthroughUrl;

    private ArrayList<VastMediaXmlManager> mMediaFileManagers;
    private String mDuration;
    private String mAdTitle;

    public VastVideoConfig() {
        mImpressionTrackers = new ArrayList<VastTracker>();
        mFractionalTrackers = new ArrayList<VastFractionalProgressTracker>();
        mAbsoluteTrackers = new ArrayList<VastAbsoluteProgressTracker>();
        mPauseTrackers = new ArrayList<VastTracker>();
        mResumeTrackers = new ArrayList<VastTracker>();
        mCompleteTrackers = new ArrayList<VastTracker>();
        mCloseTrackers = new ArrayList<VastTracker>();
        mSkipTrackers = new ArrayList<VastTracker>();
        mClickTrackers = new ArrayList<VastTracker>();
        mErrorTrackers = new ArrayList<VastTracker>();
        mSocialActionsCompanionAds = new HashMap<String, VastCompanionAdConfig>();
        mMuteTrackers = new ArrayList<>();
        mUnMuteTrackers = new ArrayList<>();

        mIsRewardedVideo = false;

        mExternalViewAbilityTrackers = new HashMap<String, String>();
        mAvidJavascriptResources = new HashSet<String>();
        mMoatImpressionPixels = new HashSet<String>();

        mMediaFileManagers = new ArrayList<>();
    }

    /**
     * Setters
     */

    public String getAdTitle() {
        return mAdTitle;
    }

    public void setAdTitle(String adTitle) {
        mAdTitle = adTitle;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }

    public ArrayList<VastMediaXmlManager> getMediaFiles() {
        return mMediaFileManagers;
    }

    public void setMediaFiles(ArrayList<VastMediaXmlManager> vastMediaXmlManagers) {
        mMediaFileManagers = vastMediaXmlManagers;
    }


    public void setDspCreativeId(@NonNull final String dspCreativeId) {
        mDspCreativeId = dspCreativeId;
    }

    public String getDspCreativeId() {
        return mDspCreativeId;
    }

    public void addImpressionTrackers(@NonNull final List<VastTracker> impressionTrackers) {
        Preconditions.checkNotNull(impressionTrackers, "impressionTrackers cannot be null");
        mImpressionTrackers.addAll(impressionTrackers);
    }

    /**
     * Add trackers for percentage-based tracking. This includes all quartile trackers and any
     * "progress" events with other percentages.
     */
    public void addFractionalTrackers(@NonNull final List<VastFractionalProgressTracker> fractionalTrackers) {
        Preconditions.checkNotNull(fractionalTrackers, "fractionalTrackers cannot be null");
        mFractionalTrackers.addAll(fractionalTrackers);
        Collections.sort(mFractionalTrackers);
    }

    /**
     * Add trackers for absolute tracking.
     */
    public void addAbsoluteTrackers(@NonNull final List<VastAbsoluteProgressTracker> absoluteTrackers) {
        Preconditions.checkNotNull(absoluteTrackers, "absoluteTrackers cannot be null");
        mAbsoluteTrackers.addAll(absoluteTrackers);
        Collections.sort(mAbsoluteTrackers);
    }

    public void addCompleteTrackers(@NonNull final List<VastTracker> completeTrackers) {
        Preconditions.checkNotNull(completeTrackers, "completeTrackers cannot be null");
        mCompleteTrackers.addAll(completeTrackers);
    }

    /**
     * Add trackers for when the video is paused.
     *
     * @param pauseTrackers List of String URLs to hit
     */
    public void addPauseTrackers(@NonNull List<VastTracker> pauseTrackers) {
        Preconditions.checkNotNull(pauseTrackers, "pauseTrackers cannot be null");
        mPauseTrackers.addAll(pauseTrackers);
    }

    /**
     * Add trackers for when the video is resumed.
     *
     * @param resumeTrackers List of String URLs to hit
     */
    public void addResumeTrackers(@NonNull List<VastTracker> resumeTrackers) {
        Preconditions.checkNotNull(resumeTrackers, "resumeTrackers cannot be null");
        mResumeTrackers.addAll(resumeTrackers);
    }

    public void addCloseTrackers(@NonNull final List<VastTracker> closeTrackers) {
        Preconditions.checkNotNull(closeTrackers, "closeTrackers cannot be null");
        mCloseTrackers.addAll(closeTrackers);
    }

    public void addSkipTrackers(@NonNull final List<VastTracker> skipTrackers) {
        Preconditions.checkNotNull(skipTrackers, "skipTrackers cannot be null");
        mSkipTrackers.addAll(skipTrackers);
    }

    public void addClickTrackers(@NonNull final List<VastTracker> clickTrackers) {
        Preconditions.checkNotNull(clickTrackers, "clickTrackers cannot be null");
        mClickTrackers.addAll(clickTrackers);
    }

    public void addMuteTrackers(@NonNull final List<VastTracker> muteTrackers) {
        Preconditions.checkNotNull(muteTrackers, "muteTrackers cannot be null");
        mMuteTrackers.addAll(muteTrackers);
    }

    public void addUnMuteTrackers(@NonNull final List<VastTracker> unmuteTrackers) {
        Preconditions.checkNotNull(unmuteTrackers, "unmuteTrackers cannot be null");
        mUnMuteTrackers.addAll(unmuteTrackers);
    }

    /**
     * Add trackers for errors.
     *
     * @param errorTrackers A URL to hit when an error happens.
     */
    public void addErrorTrackers(@NonNull final List<VastTracker> errorTrackers) {
        Preconditions.checkNotNull(errorTrackers, "errorTrackers cannot be null");
        mErrorTrackers.addAll(errorTrackers);
    }

    /**
     * Adds internal video trackers from a JSONObject in the form:
     * {
     * urls: [ "...%%VIDEO_EVENT%%...", ... ],
     * events: [ "companionAdView", ... ]
     * }
     * <p>
     * Each event adds a corresponding tracker type with all the listed urls, with %%VIDEO_EVENT%%
     * replaced with the event name. The currently supported trackers and their mappings are:
     * > start: addAbsoluteTrackers(url, 0)
     * > firstQuartile: addFractionalTrackers(url, 0.25f)
     * > midpoint: addFractionalTrackers(url, 0.5f)
     * > thirdQuartile: addFractionalTrackers(url, 0.75f)
     * > complete: addCompleteTrackers(url)
     * > companionAdView: VastCompanionAdConfig.addCreativeViewTrackers
     * > companionAdClick: VastCompanionAdConfig.addClickTrackers
     *
     * @param videoTrackers A JSONObject with the urls and events to track
     */
    public void addVideoTrackers(@Nullable final JSONObject videoTrackers) {
        if (videoTrackers == null) {
            return;
        }

        final JSONArray urls = videoTrackers.optJSONArray(Constants.VIDEO_TRACKING_URLS_KEY);
        final JSONArray events = videoTrackers.optJSONArray(Constants.VIDEO_TRACKING_EVENTS_KEY);
        if (urls == null || events == null) {
            return;
        }

        for (int i = 0; i < events.length(); i++) { // JSONArray isn't Iterable -_-)
            final String eventName = events.optString(i);
            final List<String> urlsForEvent = hydrateUrls(eventName, urls);
            final VideoTrackingEvent event = VideoTrackingEvent.fromString(eventName);
            if (eventName == null || urlsForEvent == null) {
                continue;
            }

            switch (event) {
                case START:
                    addStartTrackersForUrls(urlsForEvent);
                    break;
                case FIRST_QUARTILE:
                    addFractionalTrackersForUrls(urlsForEvent, 0.25f, VideoTrackingEvent.FIRST_QUARTILE.getName());
                    break;
                case MIDPOINT:
                    addFractionalTrackersForUrls(urlsForEvent, 0.5f, VideoTrackingEvent.MIDPOINT.getName());
                    break;
                case THIRD_QUARTILE:
                    addFractionalTrackersForUrls(urlsForEvent, 0.75f, VideoTrackingEvent.THIRD_QUARTILE.getName());
                    break;
                case COMPLETE:
                    addCompleteTrackersForUrls(urlsForEvent);
                    break;
                case COMPANION_AD_VIEW:
                    addCompanionAdViewTrackersForUrls(urlsForEvent);
                    break;
                case COMPANION_AD_CLICK:
                    addCompanionAdClickTrackersForUrls(urlsForEvent);
                    break;
                case UNKNOWN:
                default:
            }
        }
    }

    public void addExternalViewAbilityTrackers(
            @Nullable final Map<String, String> externalViewAbilityTrackers) {
        if (externalViewAbilityTrackers != null) {
            mExternalViewAbilityTrackers.putAll(externalViewAbilityTrackers);
        }
    }

    public void addAvidJavascriptResources(@Nullable final Set<String> javascriptResources) {
        if (javascriptResources != null) {
            mAvidJavascriptResources.addAll(javascriptResources);
        }
    }

    public void addMoatImpressionPixels(@Nullable final Set<String> impressionPixels) {
        if (impressionPixels != null) {
            mMoatImpressionPixels.addAll(impressionPixels);
        }
    }

    public void setClickThroughUrl(@Nullable final String clickThroughUrl) {
        mClickThroughUrl = clickThroughUrl;
    }

    public void setNetworkMediaFileUrl(@Nullable final String networkMediaFileUrl) {
        mNetworkMediaFileUrl = networkMediaFileUrl;
    }

    public void setMediaWidth(@Nullable final int width) {
        mBestMediaFileWidth = width;
    }

    public int getMediaWidth() {
        return mBestMediaFileWidth;
    }

    public void setMediaHeight(@Nullable final int height) {
        mBestMediaFileHeight = height;
    }

    public int getMediaHeight() {
        return mBestMediaFileHeight;
    }

    public void setDiskMediaFileUrl(@Nullable final String diskMediaFileUrl) {
        mDiskMediaFileUrl = diskMediaFileUrl;
    }

    public void setVastCompanionAd(@Nullable final VastCompanionAdConfig landscapeVastCompanionAdConfig,
                                   @Nullable final VastCompanionAdConfig portraitVastCompanionAdConfig) {
        mLandscapeVastCompanionAdConfig = landscapeVastCompanionAdConfig;
        mPortraitVastCompanionAdConfig = portraitVastCompanionAdConfig;
    }

    public void setSocialActionsCompanionAds(
            @NonNull final Map<String, VastCompanionAdConfig> socialActionsCompanionAds) {
        this.mSocialActionsCompanionAds = socialActionsCompanionAds;
    }

    public void setVastIconConfig(@Nullable final VastIconConfig vastIconConfig) {
        mVastIconConfig = vastIconConfig;
    }

    public void setCustomCtaText(@Nullable final String customCtaText) {
        if (customCtaText != null) {
            mCustomCtaText = customCtaText;
        }
    }

    public void setCustomSkipText(@Nullable final String customSkipText) {
        if (customSkipText != null) {
            mCustomSkipText = customSkipText;
        }
    }

    public void setCustomCloseIconUrl(@Nullable final String customCloseIconUrl) {
        if (customCloseIconUrl != null) {
            mCustomCloseIconUrl = customCloseIconUrl;
        }
    }

    public void setSkipOffset(@Nullable final String skipOffset) {
        if (skipOffset != null) {
            mSkipOffset = skipOffset;
        }
    }

    public void setVideoViewAbilityTracker(@Nullable final VideoViewAbilityTracker videoViewAbilityTracker) {
        if (videoViewAbilityTracker != null) {
            mVideoViewAbilityTracker = videoViewAbilityTracker;
        }
    }

    public void setIsRewardedVideo(final boolean isRewardedVideo) {
        mIsRewardedVideo = isRewardedVideo;
    }

    public void setPrivacyInformationIconImageUrl(
            @Nullable final String privacyInformationIconImageUrl) {
        mPrivacyInformationIconImageUrl = privacyInformationIconImageUrl;
    }

    public void setPrivacyInformationIconClickthroughUrl(
            @Nullable final String privacyInformationIconClickthroughUrl) {
        mPrivacyInformationIconClickthroughUrl = privacyInformationIconClickthroughUrl;
    }

    /**
     * Getters
     */

    @NonNull
    public List<VastTracker> getImpressionTrackers() {
        return mImpressionTrackers;
    }

    @NonNull
    public ArrayList<VastAbsoluteProgressTracker> getAbsoluteTrackers() {
        return mAbsoluteTrackers;
    }

    @NonNull
    public ArrayList<VastFractionalProgressTracker> getFractionalTrackers() {
        return mFractionalTrackers;
    }

    @NonNull
    public List<VastTracker> getPauseTrackers() {
        return mPauseTrackers;
    }

    @NonNull
    public List<VastTracker> getResumeTrackers() {
        return mResumeTrackers;
    }

    @NonNull
    public List<VastTracker> getCompleteTrackers() {
        return mCompleteTrackers;
    }

    @NonNull
    public List<VastTracker> getCloseTrackers() {
        return mCloseTrackers;
    }

    @NonNull
    public List<VastTracker> getSkipTrackers() {
        return mSkipTrackers;
    }

    @NonNull
    public List<VastTracker> getClickTrackers() {
        return mClickTrackers;
    }


    @NonNull
    public ArrayList<VastTracker> getUnMuteTrackers() {
        return mUnMuteTrackers;
    }

    @NonNull
    public ArrayList<VastTracker> getMuteTrackers() {
        return mMuteTrackers;
    }

    /**
     * Gets a list of error trackers.
     *
     * @return List of String URLs.
     */
    @NonNull
    public List<VastTracker> getErrorTrackers() {
        return mErrorTrackers;
    }

    @Nullable
    public String getClickThroughUrl() {
        return mClickThroughUrl;
    }

    @Nullable
    public String getNetworkMediaFileUrl() {
        return mNetworkMediaFileUrl;
    }

    @Nullable
    public String getDiskMediaFileUrl() {
        return mDiskMediaFileUrl;
    }

    @Nullable
    public VastCompanionAdConfig getVastCompanionAd(final int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                return mPortraitVastCompanionAdConfig;
            case Configuration.ORIENTATION_LANDSCAPE:
                return mLandscapeVastCompanionAdConfig;
            default:
                return mLandscapeVastCompanionAdConfig;
        }
    }

    @NonNull
    public Map<String, VastCompanionAdConfig> getSocialActionsCompanionAds() {
        return mSocialActionsCompanionAds;
    }

    @Nullable
    public VastIconConfig getVastIconConfig() {
        return mVastIconConfig;
    }

    @Nullable
    public String getCustomCtaText() {
        return mCustomCtaText;
    }

    @Nullable
    public String getCustomSkipText() {
        return mCustomSkipText;
    }

    @Nullable
    public String getCustomCloseIconUrl() {
        return mCustomCloseIconUrl;
    }

    @Nullable
    public VideoViewAbilityTracker getVideoViewAbilityTracker() {
        return mVideoViewAbilityTracker;
    }

    @NonNull
    public Map<String, String> getExternalViewAbilityTrackers() {
        return mExternalViewAbilityTrackers;
    }

    @NonNull
    public Set<String> getAvidJavascriptResources() {
        return mAvidJavascriptResources;
    }

    @NonNull
    public Set<String> getMoatImpressionPixels() {
        return mMoatImpressionPixels;
    }

    /**
     * Returns whether or not there is a companion ad set. There must be both a landscape and a
     * portrait companion ad set for this to be true.
     *
     * @return true if both the landscape and portrait companion ads are set, false otherwise.
     */
    public boolean hasCompanionAd() {
        return mLandscapeVastCompanionAdConfig != null && mPortraitVastCompanionAdConfig != null;
    }

    /**
     * Gets the String specified in the VAST document regarding the skip offset. This should be in
     * the form HH:MM:SS[.mmm] or n%. (e.g. 00:00:12, 00:00:12.345, 42%).
     *
     * @return String representation of the skip offset or {@code null} if not set.
     */
    @Nullable
    public String getSkipOffsetString() {
        return mSkipOffset;
    }

    /**
     * Returns whether or not this is an unskippable rewarded video.
     *
     * @return True if this is a rewarded video, false otherwise.
     */
    public boolean isRewardedVideo() {
        return mIsRewardedVideo;
    }

    @Nullable
    public String getPrivacyInformationIconImageUrl() {
        return mPrivacyInformationIconImageUrl;
    }

    @Nullable
    public String getPrivacyInformationIconClickthroughUrl() {
        return mPrivacyInformationIconClickthroughUrl;
    }

    /**
     * Called when the video starts playing.
     *
     * @param context         The context. Can be application or activity context.
     * @param contentPlayHead Current video playback time.
     */
    public void handleImpression(@NonNull final Context context, int contentPlayHead) {
        Preconditions.checkNotNull(context, "context cannot be null");
//        makeVastTrackingHttpRequest(
//                mImpressionTrackers,
//                null,
//                contentPlayHead,
//                mNetworkMediaFileUrl,
//                context
//        );
    }

    /**
     * Called when the video is clicked. Handles forwarding the user to the specified click through
     * url, calling  when done.
     *
     * @param activity        Used to call startActivityForResult with provided requestCode.
     * @param contentPlayHead Current video playback time when clicked.
     * @param requestCode     Code that identifies what kind of activity request is going to be
     *                        made.
     */
    public void handleClickForResult(@NonNull final Activity activity, final int contentPlayHead,
                                     final int requestCode) {
        handleClick(activity, contentPlayHead, requestCode);
    }

    /**
     * Called when the video is clicked. Handles forwarding the user to the specified click through
     * url. Does not provide any feedback when opened activity is finished.
     *
     * @param context         Used to call startActivity.
     * @param contentPlayHead Current video playback time when clicked.
     */
    public void handleClickWithoutResult(@NonNull final Context context,
                                         final int contentPlayHead) {
        handleClick(context.getApplicationContext(), contentPlayHead, null);
    }

    /**
     * Called when the video is clicked. Handles forwarding the user to the specified click through
     * url.
     *
     * @param context         If an Activity context, used to call startActivityForResult with
     *                        provided requestCode; otherwise, used to call startActivity.
     * @param contentPlayHead Current video playback time when clicked.
     * @param requestCode     Required when the context is an Activity; code that identifies what
     *                        kind of activity request is going to be made.
     */
    private void handleClick(@NonNull final Context context, final int contentPlayHead,
                             @Nullable final Integer requestCode) {
        Preconditions.checkNotNull(context, "context cannot be null");

    }

    /**
     * Called when the video is not finished and is resumed from the middle of the video.
     *
     * @param context         The context. Can be application or activity context.
     * @param contentPlayHead Current video playback time.
     */
    public void handleResume(@NonNull final Context context, int contentPlayHead) {
        Preconditions.checkNotNull(context, "context cannot be null");
//        makeVastTrackingHttpRequest(
//                mResumeTrackers,
//                null,
//                contentPlayHead,
//                mNetworkMediaFileUrl,
//                context
//        );
    }

    /**
     * Called when the video is not finished and is paused.
     *
     * @param context         The context. Can be application or activity context.
     * @param contentPlayHead Current video playback time.
     */
    public void handlePause(@NonNull final Context context, int contentPlayHead) {
        Preconditions.checkNotNull(context, "context cannot be null");
//        makeVastTrackingHttpRequest(
//                mPauseTrackers,
//                null,
//                contentPlayHead,
//                mNetworkMediaFileUrl,
//                context
//        );
    }

    /**
     * Called when the video is closed.
     *
     * @param context         The context. Can be application or activity context.
     * @param contentPlayHead Current video playback time.
     */
    public void handleClose(@NonNull Context context, int contentPlayHead) {
        Preconditions.checkNotNull(context, "context cannot be null");
//        makeVastTrackingHttpRequest(
//                mCloseTrackers,
//                null,
//                contentPlayHead,
//                mNetworkMediaFileUrl,
//                context
//        );
    }

    /**
     * Called when the video is skipped.
     *
     * @param context         The context. Can be application or activity context.
     * @param contentPlayHead Current video playback time.
     */
    public void handleSkip(@NonNull Context context, int contentPlayHead) {
        Preconditions.checkNotNull(context, "context cannot be null");
//        makeVastTrackingHttpRequest(
//                mSkipTrackers,
//                null,
//                contentPlayHead,
//                mNetworkMediaFileUrl,
//                context
//        );
    }

    /**
     * Called when the video is played completely without skipping.
     *
     * @param context         The context. Can be application or activity context.
     * @param contentPlayHead Current video playback time (should be duration of video).
     */
    public void handleComplete(@NonNull Context context, int contentPlayHead) {
        Preconditions.checkNotNull(context, "context cannot be null");
//        makeVastTrackingHttpRequest(
//                mCompleteTrackers,
//                null,
//                contentPlayHead,
//                mNetworkMediaFileUrl,
//                context
//        );
    }

    /**
     * Returns untriggered VAST progress trackers with a progress before the provided position.
     *
     * @param currentPositionMillis the current video position in milliseconds.
     * @param videoLengthMillis     the total video length.
     */
    @NonNull
    public List<VastTracker> getUntriggeredTrackersBefore(final int currentPositionMillis, final int videoLengthMillis) {
        if (Preconditions.NoThrow.checkArgument(videoLengthMillis > 0) && currentPositionMillis >= 0) {
            float progressFraction = currentPositionMillis / (float) (videoLengthMillis);
            List<VastTracker> untriggeredTrackers = new ArrayList<VastTracker>();

            VastAbsoluteProgressTracker absoluteTest = new VastAbsoluteProgressTracker("",
                    currentPositionMillis, "");
            int absoluteTrackerCount = mAbsoluteTrackers.size();
            for (int i = 0; i < absoluteTrackerCount; i++) {
                VastAbsoluteProgressTracker tracker = mAbsoluteTrackers.get(i);
                if (tracker.compareTo(absoluteTest) > 0) {
                    break;
                }
                if (!tracker.isTracked()) {
                    untriggeredTrackers.add(tracker);
                }
            }

            final VastFractionalProgressTracker fractionalTest = new VastFractionalProgressTracker("", progressFraction, "");
            int fractionalTrackerCount = mFractionalTrackers.size();
            for (int i = 0; i < fractionalTrackerCount; i++) {
                VastFractionalProgressTracker tracker = mFractionalTrackers.get(i);
                if (tracker.compareTo(fractionalTest) > 0) {
                    break;
                }
                if (!tracker.isTracked()) {
                    untriggeredTrackers.add(tracker);
                }
            }

            return untriggeredTrackers;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns the number of untriggered progress trackers.
     *
     * @return Integer count >= 0 of the remaining progress trackers.
     */
    public int getRemainingProgressTrackerCount() {
        return getUntriggeredTrackersBefore(Integer.MAX_VALUE, Integer.MAX_VALUE).size();
    }

    /**
     * Gets the skip offset in milliseconds. If the skip offset would be past the video duration,
     * this returns the video duration. Returns null when the skip offset is not set or cannot be parsed.
     *
     * @param videoDuration Used to calculate percentage based offsets.
     * @return The skip offset in milliseconds. Can return null.
     */
    @Nullable
    public Integer getSkipOffsetMillis(final int videoDuration) {
        if (mSkipOffset != null) {
            try {
                final Integer skipOffsetMilliseconds;
                if (Strings.isAbsoluteTracker(mSkipOffset)) {
                    skipOffsetMilliseconds = Strings.parseAbsoluteOffset(mSkipOffset);
                } else if (Strings.isPercentageTracker(mSkipOffset)) {
                    float percentage = Float.parseFloat(mSkipOffset.replace("%", "")) / 100f;
                    skipOffsetMilliseconds = Math.round(videoDuration * percentage);
                } else {
                    return null;
                }

                if (skipOffsetMilliseconds != null) {
                    if (skipOffsetMilliseconds < videoDuration) {
                        return skipOffsetMilliseconds;
                    } else {
                        return videoDuration;
                    }
                }
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }

    @Nullable
    private List<String> hydrateUrls(@Nullable final String event, @NonNull final JSONArray urls) {
        Preconditions.checkNotNull(urls);

        if (event == null) {
            return null;
        }

        final List<String> hydratedUrls = new ArrayList<String>();
        for (int i = 0; i < urls.length(); i++) {
            final String url = urls.optString(i);
            if (url == null) {
                continue;
            }
            hydratedUrls.add(url.replace(Constants.VIDEO_TRACKING_URL_MACRO, event));
        }
        return hydratedUrls;
    }

    private List<VastTracker> createVastTrackersForUrls(@NonNull final List<String> urls, String event) {
        Preconditions.checkNotNull(urls);

        final List<VastTracker> trackers = new ArrayList<VastTracker>();
        for (String url : urls) {
            trackers.add(new VastTracker(url, event));
        }
        return trackers;
    }

    private void addCompleteTrackersForUrls(@NonNull final List<String> urls) {
        Preconditions.checkNotNull(urls);

        addCompleteTrackers(createVastTrackersForUrls(urls, VideoTrackingEvent.COMPLETE.getName()));
    }

    private void addStartTrackersForUrls(@NonNull final List<String> urls) {
        Preconditions.checkNotNull(urls);

        final List<VastAbsoluteProgressTracker> startTrackers = new ArrayList<VastAbsoluteProgressTracker>();
        for (String url : urls) {
            startTrackers.add(new VastAbsoluteProgressTracker(url, 0, VideoTrackingEvent.START.getName()));
        }
        addAbsoluteTrackers(startTrackers);
    }

    private void addFractionalTrackersForUrls(@NonNull final List<String> urls,
                                              final float fraction, String event) {
        Preconditions.checkNotNull(urls);

        final List<VastFractionalProgressTracker> fractionalTrackers = new ArrayList<VastFractionalProgressTracker>();
        for (String url : urls) {
            fractionalTrackers.add(new VastFractionalProgressTracker(url, fraction, event));
        }
        addFractionalTrackers(fractionalTrackers);
    }

    private void addCompanionAdViewTrackersForUrls(@NonNull final List<String> urls) {
        Preconditions.checkNotNull(urls);

        if (hasCompanionAd()) {
            final List<VastTracker> companionAdViewTrackers = createVastTrackersForUrls(urls, VideoTrackingEvent.COMPANION_AD_VIEW.getName());
            mLandscapeVastCompanionAdConfig.addCreativeViewTrackers(companionAdViewTrackers);
            mPortraitVastCompanionAdConfig.addCreativeViewTrackers(companionAdViewTrackers);
        }
    }

    private void addCompanionAdClickTrackersForUrls(@NonNull final List<String> urls) {
        Preconditions.checkNotNull(urls);

        if (hasCompanionAd()) {
            final List<VastTracker> companionAdClickTrackers = createVastTrackersForUrls(urls, VideoTrackingEvent.COMPANION_AD_CLICK.getName());
            mLandscapeVastCompanionAdConfig.addClickTrackers(companionAdClickTrackers);
            mPortraitVastCompanionAdConfig.addClickTrackers(companionAdClickTrackers);
        }
    }

}
