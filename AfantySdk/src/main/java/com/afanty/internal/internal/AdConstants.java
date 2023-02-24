package com.afanty.internal.internal;

public interface AdConstants {

    interface MetaDataKey {
        String AFT_TOKEN = "com.afanty.token";
        String AFT_CHANNEL = "com.afanty.channel";
        String AFT_APP_KEY = "com.afanty.APP_KEY";
        String AFT_APP_ID = "com.afanty.APP_ID";
    }


    interface PortalKey {
        String GET_AD = "get_ad";
        String SI_REPORT = "si_report";
        String TRACK_HELPER = "TrackHelper";
        String MULTI_DOWNLOAD = "multi_download";
    }

    interface Portal {
        String FROM_AD = "ad";
        String A_START = "a_s";
        String C_DOWNLOAD = "c_d";
        String RWD_C_DOWNLOAD = "rwd_c_d";
    }

    interface Config {
        String KEY_CFG_AFT_CONFIG = "aft_config";

        String CONFIG_KEY_JS_TAG_LOAD_WITH_BASE_URL_ENABLE = "jstag_loadwithbaseurl";
        String CONFIG_KEY_INTERSTITIAL_CLICK_AREA = "interstitial_click_area";

        String CONFIG_KEY_VAST_DOWNLOAD_TIMEOUT = "vast_download_timeout";
        String CONFIG_KEY_VIDEO_DOWNLOAD_TIMEOUT = "video_download_timeout";
        String CONFIG_KEY_SOURCE_DOWNLOAD_IMAGE_TIMEOUT = "glide_timeout_download";

        String CONFIG_KEY_NATIVE_VIDEO_PAUSE_PLAY_PERCENTAGE = "native_video_pause_play_percentage";
        String CONFIG_KEY_NATIVE_VIDEO_RESUME_PLAY_PERCENTAGE = "native_video_resume_play_percentage";

        String CONFIG_KEY_IMPRESSION_MIN_PX = "impression_min_px";
        String CONFIG_KEY_BANNER_IMPRESSION_MIN_PX = "banner_impression_min_px";
        String CONFIG_KEY_IMPRESSION_MIN_PERCENTAGE = "impression_min_percentage";
        String CONFIG_KEY_IMPRESSION_MIN_TIME = "impression_min_time";

        String KEY_CONFIG_APP_KEY = "app_keys";

        String AD_DNS_LIST = "ad_dns_list";
        String AD_DNS_SWITCH = "ad_dns_switch";

        String KEY_CFG_MIUI_BUNDLE_CONFIG = "miui_bundle_config";

    }

    interface Advance {
        String KEY_CFG_AD_ADVANCE_INFO = "ad_advance_info";
        String AD_ABANDON_PRIORITY = "priority_adids";
    }

    interface ContextUtil {
        String FULL_SCREEN_AD = "full_screen_ad";
    }

    interface Vast {
        String NO_VAST_CONTENT = "No Vast Content";

        String SOURCE_TYPE_CARDBUTTON = "cardbutton";
        String SOURCE_TYPE_VIDEO = "video";
        String SOURCE_TYPE_COMPANIONVIEW = "companionView";
        String SOURCE_TYPE_TAILNONEBUTTON = "tailnonbutton";
    }

    interface AppInstall {
        int APP_STATUS_UNINSTALL = 0;
        int APP_STATUS_INSTALLED = 1;
        int APP_STATUS_NEED_UPGRADE = 2;
        int APP_STATUS_INSTALLING = 3;
        int APP_STATUS_INSTALL_FAILED = 4;
    }

    interface AutoStart {
        String KEY_AUTO_START = "auto_start";
        String KEY_AUTO_START_TYPE = "type";
        String KEY_AUTO_START_INTERVAL = "interval";
    }

    interface Extra {
        String KEY_INSTALL_TIME = "install_time";
        String KEY_COMMON_EXTRA = "common_extra";
        String AD_PORTAL = "ad";
    }

}
