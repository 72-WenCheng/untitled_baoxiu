package main.java.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern BUILDING_NO_PATTERN = Pattern.compile("^[A-Za-z0-9-]{1,10}$");
    private static final Pattern ROOM_NO_PATTERN = Pattern.compile("^[A-Za-z0-9-]{1,10}$");
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isValidBuildingNo(String buildingNo) {
        return buildingNo != null && BUILDING_NO_PATTERN.matcher(buildingNo).matches();
    }
    
    public static boolean isValidRoomNo(String roomNo) {
        return roomNo != null && ROOM_NO_PATTERN.matcher(roomNo).matches();
    }
} 