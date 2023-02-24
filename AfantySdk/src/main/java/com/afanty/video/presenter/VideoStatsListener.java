package com.afanty.video.presenter;

import com.afanty.video.MediaStatsData;

public interface VideoStatsListener {
    void onProgressUpdateStats(int progress);

    void onProgressUpdateQuarter(MediaStatsData mediaStatsData, int progress, int startPlayTimes);

    void onProgressUpdateHalf(MediaStatsData mediaStatsData, int progress, int startPlayTimes);

    void onProgressUpdateThreeQuarter(MediaStatsData mediaStatsData, int progress, int startPlayTimes);

    void setUnMuteStats(MediaStatsData mediaStatsData);

    void setMuteStats(MediaStatsData mediaStatsData);

    void onPlayStatsPrepared(MediaStatsData mediaStatsData, String playUrl, long loadTime);

    void onPlayStatsStarted(MediaStatsData mediaStatsData, int startPlayTimes);

    void onPlayStatsPlayed(MediaStatsData mediaStatsData, int startPlayTimes);

    void onPlayStatsCompleted(MediaStatsData mediaStatsData, int duration, int startPlayTimes);

    void onPlayStatsError(MediaStatsData mediaStatsData, String playUrl, long loadTime, String reason);

    void onMediaEventResult(MediaStatsData mediaStatsData, int duration, int startedDuration, int playPosition, int height, int width);

    void onMediaEventBuffer();

    void onMediaEventBufferFinish();

    void onPause(MediaStatsData mediaStatsData);

    void onResumePlay(MediaStatsData mediaStatsData);

    void onStopPlay();

    void onSkip(MediaStatsData mediaStatsData);

    void onClose(MediaStatsData mediaStatsData);

    void onCreateView(MediaStatsData mediaStatsData);
}
