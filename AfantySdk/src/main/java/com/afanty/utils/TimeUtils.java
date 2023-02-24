package com.afanty.utils;

import android.os.SystemClock;

import com.afanty.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtils {

    static long initTime;
    static long initTick;

    public static void init() {
        initTime = System.currentTimeMillis();
        initTick = SystemClock.elapsedRealtime();
    }

    public static long currentTimeMillis() {
        return initTime + (SystemClock.elapsedRealtime() - initTick);
    }

    /**
     * @param first  UTC by milliseconds
     * @param second UTC by milliseconds
     * @return 0 means same day, -n means first before than second, +n means first is last than second
     */
    public static long compareByDay(long first, long second) {
        long firstDay = first / (24 * 60 * 60 * 1000);
        long secondDay = second / (24 * 60 * 60 * 1000);
        return firstDay - secondDay;
    }

    public static String getTimeScope(float duration, float[] sections) {
        for (int i = 0; i < sections.length; i++) {
            if (Float.compare(duration, sections[i]) == 0 && (i == 0 || (sections[i] - sections[i - 1] == 1)))
                return formatTime(sections[i]);

            if (duration >= sections[i])
                continue;
            if (i == 0)
                return "<" + formatTime(sections[i]);
            return ">=" + formatTime(sections[i - 1]) + ", <" + formatTime(sections[i]);
        }
        return ">=" + formatTime(sections[sections.length - 1]);
    }

    private static String formatTime(float time) {
        long division = 1;
        String unit = "s";
        if (time >= 60) {
            division = 60;
            unit = "m";
        }
        if (time >= 60 * 60) {
            division = 60 * 60;
            unit = "h";
        }
        if (time >= 60 * 60 * 24) {
            division = 60 * 60 * 24;
            unit = "d";
        }
        float result = time / division;
        return StringUtils.decimalFormatIgnoreLocale("#.#", result) + unit;
    }

    public static boolean isUnreachedServerTime(long startTime, long serverTime) {
        return (startTime != -1 && serverTime < startTime);
    }

    public static boolean isExpiredServerTime(long endTime, long serverTime) {
        return (endTime != -1 && serverTime > endTime);
    }

    public static boolean isSameDate(long currentTime, long lastTime) {
        try {
            Calendar nowCal = Calendar.getInstance();
            Calendar dataCal = Calendar.getInstance();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.US);
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.US);

            String data1 = df1.format(currentTime);
            String data2 = df2.format(lastTime);

            java.util.Date now = df1.parse(data1);
            java.util.Date date = df2.parse(data2);

            if (now == null || date == null) {
                return false;
            }
            nowCal.setTime(now);
            dataCal.setTime(date);
            return isSameDay(nowCal, dataCal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            return false;
        }
    }

    /**
     * Convert timestamp to time interval "how long after"
     *
     * @param timeStr: Timestamp
     */
    public static String getStandardDate(long timeStr) {
        String temp = "";
        try {
            long diff = timeStr;
            long months = diff / (60 * 60 * 24 * 30);
            long days = diff / (60 * 60 * 24);
            long hours = (diff - days * (60 * 60 * 24)) / (60 * 60);
            long minutes = (diff - days * (60 * 60 * 24) - hours * (60 * 60)) / 60;
            if (months > 0) {
                temp = months + " " + ContextUtils.getContext().getResources().getString(R.string.aft_timer_later_months);
            } else if (days > 0) {
                temp = days + " " + ContextUtils.getContext().getResources().getString(R.string.aft_timer_later_days);
            } else if (hours > 0) {
                temp = hours + " " + ContextUtils.getContext().getResources().getString(R.string.aft_timer_later_hours);
            } else {
                temp = minutes + " " + ContextUtils.getContext().getResources().getString(R.string.aft_timer_later_minutes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * Convert timestamp to min
     *
     * @param timeStr
     * @return
     */
    public static long getStandarMinData(long timeStr) {
        return timeStr / 60;
    }

    public static String getTimeFormat(long time, String format) {
        if (time <= 0) {
            time = System.currentTimeMillis();
        }
        Date nowTime = new Date(time);
        SimpleDateFormat sdFormatter = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdFormatter.format(nowTime);
    }

    public static String getHHMMFormatIgnoreZone(long time) {
        time = time % 86400000;
        time -= TimeZone.getDefault().getRawOffset();
        time = (time + 86400000) % 86400000;
        return getTimeFormat(time, "HH:mm");
    }
}
