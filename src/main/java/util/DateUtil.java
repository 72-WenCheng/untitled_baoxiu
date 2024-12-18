package main.java.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT;
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        // 设置为中国时区
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    }

    public static String formatDateTime(Date date) {
        return date != null ? DATE_FORMAT.format(date) : "";
    }
    
    public static Date parseDateTime(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String getCurrentDateTime() {
        return formatDateTime(new Date());
    }
} 