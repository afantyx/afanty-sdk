// Copyright 2018-2019 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.afanty.vast.utils;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Pattern;

public class Strings {
    // Regex patterns
    private static Pattern percentagePattern = Pattern.compile("((\\d{1,2})|(100))%");
    private static Pattern absolutePattern = Pattern.compile("\\d{2}:\\d{2}:\\d{2}(.\\d{3})?");

    public static String fromStream(InputStream inputStream) throws IOException {
        int numberBytesRead = 0;
        StringBuilder out = new StringBuilder();
        byte[] bytes = new byte[4096];

        while (numberBytesRead != -1) {
            out.append(new String(bytes, 0, numberBytesRead));
            numberBytesRead = inputStream.read(bytes);
        }

        inputStream.close();

        return out.toString();
    }

    public static void closeStream(Closeable stream) {
        if (stream == null) {
            return;
        }

        try {
            stream.close();
        } catch (Exception e) {
        }
    }

    public static void copyContent(final InputStream inputStream, final OutputStream outputStream) throws IOException {
        if (inputStream == null || outputStream == null) {
            throw new IOException("Unable to copy from or to a null stream.");
        }

        byte[] buffer = new byte[16384];
        int length;

        while ((length = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, length);
        }
    }

    public static boolean isPercentageTracker(String progressValue) {
        return !TextUtils.isEmpty(progressValue)
                && percentagePattern.matcher(progressValue).matches();
    }

    public static boolean isAbsoluteTracker(String progressValue) {
        return !TextUtils.isEmpty(progressValue)
                && absolutePattern.matcher(progressValue).matches();
    }

    /**
     * This helper method creates a delimited String from values in a List
     *
     * @param object    The List of objects. If this is null, then an empty String is returned.
     * @param delimiter The String to be used as a delimiter. If this is null, then ", " will be used.
     * @return A delimited String of all values in the list.
     */
    @SuppressWarnings("unchecked")
    public static String getDelimitedString(@Nullable final Object object, @Nullable final String delimiter) {
        if (!(object instanceof List<?>)) {
            return "";
        }
        final List<Object> list = (List<Object>) object;

        if (list.isEmpty()) {
            return "";
        }

        return TextUtils.join((delimiter != null ? delimiter : ", "), list);
    }

    @Nullable
    public static Integer parseAbsoluteOffset(@Nullable String progressValue) {
        if (progressValue == null) {
            return null;
        }

        final String[] split = progressValue.split(":");
        if (split.length != 3) {
            return null;
        }

        return Integer.parseInt(split[0]) * 60 * 60 * 1000 // Hours
                + Integer.parseInt(split[1]) * 60 * 1000 // Minutes
                + (int) (Float.parseFloat(split[2]) * 1000);
    }
}
