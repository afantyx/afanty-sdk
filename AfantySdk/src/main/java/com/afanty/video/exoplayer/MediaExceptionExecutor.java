package com.afanty.video.exoplayer;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import java.io.IOException;

public class MediaExceptionExecutor {
    public MediaExceptionExecutor() {
    }

    public static boolean isBehindLiveWindow(ExoPlaybackException e) {
        if (e.type != 0) {
            return false;
        } else {
            for (Object cause = e.getSourceException(); cause != null; cause = ((Throwable) cause).getCause()) {
                if (cause instanceof BehindLiveWindowException) {
                    return true;
                }
            }

            return false;
        }
    }

    public static String getRendererErrorMessage(ExoPlaybackException e) {
        String errorString = "";
        if (e.type == 1) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException = (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = "Unable to query device decoders";
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = "This device does not provide a secure decoder for " + decoderInitializationException.mimeType;
                    } else {
                        errorString = "This device does not provide a decoder for " + decoderInitializationException.mimeType;
                    }
                } else {
                    errorString = "Unable to instantiate decoder " + decoderInitializationException;
                }
            }
        }

        return errorString;
    }

    public static String getNetworkErrorMessage(ExoPlaybackException error) {
        String errorString = "";
        if (error.type == 0) {
            IOException cause = error.getSourceException();
            if (cause instanceof HttpDataSource.HttpDataSourceException) {
                errorString = "error_network";
            }
        }

        return errorString;
    }
}
