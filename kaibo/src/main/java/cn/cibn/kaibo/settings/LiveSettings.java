package cn.cibn.kaibo.settings;

import com.tv.lib.core.Settings;
import com.tv.lib.core.lang.ObjectStore;

public class LiveSettings {
    private static final String SP_NAME = "ggshop_live";

    private static final String KEY_GUIDE_ENTER = "key_guide_enter_i";
    private static final String KEY_GUIDE_EXIT = "key_guide_exit_i";
    private static final String KEY_HAS_REPORT_DEVICE_INFO = "key_has_report_device_info";
    private static final String KEY_GP_TIME_START = "key_gp_time_start";
    private static final String KEY_GP_TIME_END = "key_gp_time_end";

    private static Settings settings;

    private static Settings getSettings() {
        if (settings == null) {
            settings = new Settings(ObjectStore.getContext(), SP_NAME);
        }
        return settings;
    }

    public static boolean hasShownGuideEnter() {
        return getSettings().getInt(KEY_GUIDE_ENTER) > 0;
    }

    public static void increaseShowGuideEnterCount() {
        int count = getSettings().getInt(KEY_GUIDE_ENTER);
        getSettings().setInt(KEY_GUIDE_ENTER, count + 1);
    }

    public static void setShownGuideEnterCount(int count) {
        getSettings().setInt(KEY_GUIDE_ENTER, count);
    }

    public static boolean hasShownGuideExit() {
        return getSettings().getInt(KEY_GUIDE_EXIT) > 0;
    }

    public static void increaseShowGuideExitCount() {
        int count = getSettings().getInt(KEY_GUIDE_EXIT);
        getSettings().setInt(KEY_GUIDE_EXIT, count + 1);
    }

    public static void setShownGuideExitCount(int count) {
        getSettings().setInt(KEY_GUIDE_EXIT, count);
    }

    public static boolean hasReportDeviceInfo() {
        return getSettings().getBoolean(KEY_HAS_REPORT_DEVICE_INFO);
    }

    public static void setHasReportDeviceInfo(boolean hasReportDeviceInfo) {
        getSettings().setBoolean(KEY_HAS_REPORT_DEVICE_INFO, hasReportDeviceInfo);
    }

    public static void setGpTimeStart(String gpTimeStart) {
        getSettings().set(KEY_GP_TIME_START, gpTimeStart);
    }

    public static String getGpTimeStart() {
        return getSettings().get(KEY_GP_TIME_START);
    }

    public static void setGpTimeEnd(String gpTimeEnd) {
        getSettings().set(KEY_GP_TIME_END, gpTimeEnd);
    }

    public static String getGpTimeEnd() {
        return getSettings().get(KEY_GP_TIME_END);
    }
}
