package com.afanty.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StringUtils {

    public static String toLowerCaseIgnoreLocale(String str) {
        return str.toLowerCase(Locale.US);
    }

    public static String toUpperCaseIgnoreLocale(String str) {
        return str.toUpperCase(Locale.US);
    }

    public static String decimalFormatIgnoreLocale(String pattern, double value) {
        return new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.US)).format(value);
    }

}
