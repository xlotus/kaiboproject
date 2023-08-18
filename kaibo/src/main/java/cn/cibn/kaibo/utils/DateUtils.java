package cn.cibn.kaibo.utils;

import java.text.SimpleDateFormat;

public class DateUtils {
    public static boolean isInRange(String timeStart, String timeEnd) {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
}
