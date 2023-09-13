package cn.cibn.kaibo.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateUtils {
    private static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static boolean isInRange(String timeStart, String timeEnd) {
        SimpleDateFormat sf = getDateTimeFormat();
        long start = 0;
        long end = 0;
        try {
            start = sf.parse(timeStart).getTime();
            end = sf.parse(timeEnd).getTime();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        long now = System.currentTimeMillis();
        return now >= start && now <= end;
    }

    public static String formatDate(long timestamp) {
        return getDateFormat().format(timestamp);
    }

    public static String formatDateTime(long timestamp) {
        return getDateTimeFormat().format(timestamp);
    }

    private static SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    private static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }
}
