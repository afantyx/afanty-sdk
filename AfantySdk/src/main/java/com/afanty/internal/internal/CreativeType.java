package com.afanty.internal.internal;

import android.text.TextUtils;

import com.afanty.models.Bid;

/**
 * element type description element specification id
 * 1 native_copy_1_figure_2_text 2
 * 2 native_graph_2 figure_2 text 1, 75, 83, 89, 93, 100
 * 3 jstag 8, 9, 10, 11, 28, 29, 31, 38, 43, 44, 46, 48, 50, 51, 56, 57, 58, 59, 62, 71, 81, 94, 103
 * 4 video_1 video 2 figure 2 text 3, 33, 76, 77, 85, 91
 * 5 Single image (client to be supported in the stream) 4, 5, 6, 7, 18, 21, 22, 23, 24, 26, 27, 30, 32, 39, 42, 47, 49, 52, 53, 55, 60, 69, 72, 86, 102, 104
 * 6 native_1figure_1text 19, 64, 78, 79, 82, 88, 92
 * 7 video_1video1figure1text 25, 34, 84, 90, 95, 98, 99
 * 8 660x346 interactive_graphic 3_figure_2_text_with_btn 12
 * 9 viewport slide_1 image 1 text & 2 image 1 text 13, 20, 65
 * 10 Single image personalized_3 images (5 + personalized likes) 15
 * 11 Large image personalisation_3 image 1 text & 4 image 2 text 14
 * 12 Video personalisation_1 video 3 images 1 text & 1 video 4 images 2 text 16, 35
 * 13 Interactive image personalisation_4 image 1 text 17
 * 14 Radar background 2 images 36
 * 15 Native text 1 image 1 text with btn 41, 96
 * 16 full likes 2 images 40
 * 17 video_1 video 1 figure 2 text with btn // IOS 45
 * 18 native large image 1 image 2 text with btn 37, 63
 * 19 top icon 1 image 1 text 54
 * 20 Hot App 61
 * 21 SI star tag ad 66
 * 22 Single video ad 67, 68, 97
 * 23 Transmission rocket dazzle ad 70
 * 24 C2I 73
 * 25 VAST tsa Occupancy 74, 10007
 * 26 video_1 video 2 graphics 1 text 80
 * 27 video_1 video 1 figure 1 text 87
 * 28 video_1 video1 graphic1 text no btn 101
 * <p>
 * <p
 * P picture
 * I icon
 * T TXT
 * V video
 * t thumb
 */
public class CreativeType {

    public static final int TYPE_PT_P1T2 = 1;
    public static final int TYPE_PT_P2T2 = 2;
    public static final int TYPE_JS_TAG = 3;
    public static final int TYPE_V_V1P2T2 = 4;
    public static final int TYPE_P_P1 = 5;
    public static final int TYPE_PT_P1T1 = 6;
    public static final int TYPE_V_V1P1T1 = 7;
    public static final int TYPE_APT_P2T1 = 8;
    public static final int TYPE_SPT_P2T1 = 9;

    public static final int TYPE_P_P1_THUMB = 10;
    public static final int TYPE_PT_P1T1_THUMB = 11;
    public static final int TYPE_V_V1P1T1_THUMB = 12;
    public static final int TYPE_APT_P2T1_THUMB = 13;

    public static final int TYPE_RADAR_P2 = 14;

    public static final int TYPE_T_P1T1 = 15;
    public static final int TYPE_FEED_THUMB = 16;
    public static final int TYPE_V_V1P1T2 = 17;
    public static final int TYPE_P_P1T2 = 18;
    public static final int TYPE_I_I1T1 = 19;
    public static final int TYPE_SI_GRADE = 21;
    public static final int TYPE_V_V1 = 22;
    public static final int TYPE_ROCKET_EFFECT = 23;
    public static final int TYPE_C2I = 24;
    public static final int TYPE_VAST = 25;
    public static final int TYPE_V_P2T2 = 26;
    public static final int TYPE_OCCUPY_FLASH = 27;
    public static final int TYPE_NO_BUTTON_V1P1T1 = 28;

    public static boolean isRocketEffect(Bid data) {
        if (data == null)
            return false;

        int type = data.getCreativeType();
        return type == TYPE_ROCKET_EFFECT;
    }

    public static boolean isThumbType(Bid data) {
        if (data == null)
            return false;

        int type = data.getCreativeType();
        if (type == TYPE_P_P1_THUMB || type == TYPE_PT_P1T1_THUMB || type == TYPE_V_V1P1T1_THUMB || type == TYPE_APT_P2T1_THUMB || type == TYPE_FEED_THUMB)
            return true;

        return false;
    }

    public static boolean isVideo(Bid data) {
        if (data == null)
            return false;

        int type = data.getCreativeType();
        return type == TYPE_V_V1P2T2 || type == TYPE_V_V1P1T1_THUMB || type == TYPE_V_V1P1T1 || type == TYPE_V_V1P1T2 || type == TYPE_V_V1 || type == TYPE_V_P2T2 || type == TYPE_OCCUPY_FLASH || type == TYPE_NO_BUTTON_V1P1T1;
    }

    public static boolean isOnePoster(Bid data) {
        if (data == null)
            return false;

        int type = data.getCreativeType();
        return type == TYPE_P_P1 || type == TYPE_P_P1_THUMB;
    }

    public static boolean isJSTag(Bid data) {
        if (data == null)
            return false;

        int type = data.getCreativeType();
        return type == TYPE_JS_TAG;
    }

    public static boolean isVAST(Bid data) {
        return data != null
                && data.getCreativeType() == TYPE_V_V1P1T1
                && !TextUtils.isEmpty(data.getVast());
    }

    public static boolean isIconTxt(Bid data) {
        if (data == null)
            return false;

        int type = data.getCreativeType();
        return type == TYPE_PT_P1T2 || type == TYPE_T_P1T1;
    }


    public static boolean isGradeType(Bid data) {
        if (data == null)
            return false;

        int type = data.getCreativeType();
        return type == TYPE_SI_GRADE;
    }

    public static boolean isRadarBg(Bid data) {
        if (data == null)
            return false;

        int type = data.getCreativeType();
        return type == TYPE_RADAR_P2;
    }
}
