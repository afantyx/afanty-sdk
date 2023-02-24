package com.afanty.video.presenter;

import com.afanty.test.TrackType;
import com.afanty.test.TrackUrlsHelper;
import com.afanty.video.MediaStatsData;

public class VideoStatsWrapper implements VideoStatsListener {


    @Override
    public void onProgressUpdateStats(int progress) {
    }

    @Override
    public void onProgressUpdateQuarter(MediaStatsData mediaStatsData, int progress, int startPlayTimes) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportVideoTrack(mediaStatsData.getQuarterTrackUrls(), progress, mediaStatsData.getAdId());
    }

    @Override
    public void onProgressUpdateHalf(MediaStatsData mediaStatsData, int progress, int startPlayTimes) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportVideoTrack(mediaStatsData.getHalfTrackUrls(), progress, mediaStatsData.getAdId());
    }

    @Override
    public void onProgressUpdateThreeQuarter(MediaStatsData mediaStatsData, int progress, int startPlayTimes) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportVideoTrack(mediaStatsData.getThreeQuarterUrls(), progress, mediaStatsData.getAdId());
    }

    @Override
    public void setMuteStats(MediaStatsData mediaStatsData) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportTrackUrls(mediaStatsData.getMuteTrackUrls(), TrackType.VIDEO, mediaStatsData.getAdId());
    }

    @Override
    public void setUnMuteStats(MediaStatsData mediaStatsData) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportTrackUrls(mediaStatsData.getUnMuteTrackUrls(), TrackType.VIDEO, mediaStatsData.getAdId());
    }

    @Override
    public void onPlayStatsPrepared(MediaStatsData mediaStatsData, String playUrl, long loadTime) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportVideoTrack(mediaStatsData.getImpressionTrackers(), 0, mediaStatsData.getAdId());
    }

    @Override
    public void onPlayStatsStarted(MediaStatsData mediaStatsData, int startPlayTimes) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportVideoTrack(mediaStatsData.getStartTrackUrls(), 0, mediaStatsData.getAdId());
    }

    @Override
    public void onPlayStatsPlayed(MediaStatsData mediaStatsData, int startPlayTimes) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportVideoTrack(mediaStatsData.getPlayTrackUrls(), 0, mediaStatsData.getAdId());
    }

    @Override
    public void onPlayStatsCompleted(MediaStatsData mediaStatsData, int duration, int startPlayTimes) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportVideoTrack(mediaStatsData.getCompleteTrackUrls(), duration, mediaStatsData.getAdId());
    }

    @Override
    public void onPlayStatsError(MediaStatsData mediaStatsData, String playUrl, long loadTime, String reason) {
        TrackUrlsHelper.reportTrackUrlsWithMacro(mediaStatsData.getErrorTrackUrls(), TrackType.VIDEO, mediaStatsData.getAdId(), "ERRORCODE", reason);
    }

    @Override
    public void onMediaEventResult(MediaStatsData mediaStatsData, int duration, int startedDuration, int playPosition, int height, int width) {
    }

    @Override
    public void onMediaEventBuffer() {
    }

    @Override
    public void onMediaEventBufferFinish() {
    }

    @Override
    public void onPause(MediaStatsData mediaStatsData) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportTrackUrls(mediaStatsData.getPauseTrackers(), TrackType.VIDEO, mediaStatsData.getAdId());
    }

    @Override
    public void onResumePlay(MediaStatsData mediaStatsData) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportTrackUrls(mediaStatsData.getResumeTrackers(), TrackType.VIDEO, mediaStatsData.getAdId());
    }

    @Override
    public void onStopPlay() {
    }

    @Override
    public void onSkip(MediaStatsData mediaStatsData) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportTrackUrls(mediaStatsData.getSkipTrackUrls(), TrackType.VIDEO, mediaStatsData.getAdId());
    }

    @Override
    public void onClose(MediaStatsData mediaStatsData) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportTrackUrls(mediaStatsData.getCloseTrackUrls(), TrackType.VIDEO, mediaStatsData.getAdId());
    }

    @Override
    public void onCreateView(MediaStatsData mediaStatsData) {
        if (mediaStatsData == null)
            return;
        TrackUrlsHelper.reportTrackUrls(mediaStatsData.getCreativeViewTrackUrls(), TrackType.VIDEO, mediaStatsData.getAdId());
    }


}
