package com.afanty.video;

public enum VideoPlayerState {
    IDLE,
    INITIALIZED,
    PREPARING,
    PREPARED,
    STARTED,
    PAUSED,
    STOPPED,
    PLAYBACKCOMPLETED,
    END,
    ERROR;
}