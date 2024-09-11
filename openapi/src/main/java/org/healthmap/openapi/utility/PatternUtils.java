package org.healthmap.openapi.utility;

public class PatternUtils {
    public static String getStartHourWithAmPm(String startHour, String startTimeAmPm) {
        StringBuilder result = new StringBuilder();
        if (startTimeAmPm.equals("오전") || startTimeAmPm.equals("아침")) {         // 오전 09시, 11시, 9시
            result.append(String.format("%02d", Integer.parseInt(startHour)));
        } else {
            if (startHour.length() == 1) {                                         // 오후 1시
                result.append(convert12To24Format(startHour));
            } else {
                if (startHour.charAt(0) == '0') {                                  // 오후 01시
                    result.append(convert12To24Format(startHour));
                } else if (startHour.charAt(0) == '1') {                           // 오후 13시
                    result.append(convert24Format(startHour));
                }
            }
        }

        return result.toString();
    }

    public static String getStartHourWithoutAmPm(String startHour) {
        StringBuilder result = new StringBuilder();
        if (startHour.length() == 1) {
            if (Integer.parseInt(startHour) > 5) {      //ex) 9시
                result.append(convert24Format(startHour));
            } else {                                    //ex) 1시
                result.append(convert12To24Format(startHour));
            }
        } else {
            if (startHour.charAt(0) == '0') {
                if (Integer.parseInt(startHour) > 5) {  //ex) 09시
                    result.append(convert24Format(startHour));
                } else {                                //ex) 01시
                    result.append(convert12To24Format(startHour));
                }
            } else if (startHour.charAt(0) == '1') {     //ex) 13시
                result.append(convert24Format(startHour));
            }
        }

        return result.toString();
    }

    public static String getStartMinute(String startMinute) {
        StringBuilder result = new StringBuilder();
        if (startMinute != null) {
            result.append(":").append(startMinute.replace("분", "")).append(" ~ ");
        } else {
            result.append(":00").append(" ~ ");
        }

        return result.toString();
    }

    public static String getEndHour(String endHour) {
        StringBuilder result = new StringBuilder();
        if (endHour.length() == 1) {
            result.append(convert12To24Format(endHour));
        } else {
            if (endHour.charAt(0) == '0') {     // 두번째 hour
                result.append(convert12To24Format(endHour));
            } else if (endHour.charAt(0) == '1' || endHour.charAt(0) == '2') {
                result.append(convert24Format(endHour));
            }
        }
        return result.toString();
    }

    public static String getEndMinute(String endMinute) {
        StringBuilder result = new StringBuilder();
        if (endMinute != null) {
            result.append(":").append(endMinute.replace("분", ""));
        } else {
            result.append(":00");
        }
        return result.toString();
    }

    private static String convert24Format(String input) {
        return String.format("%02d", Integer.parseInt(input));
    }
    private static String convert12To24Format(String input) {
        return String.format("%02d", (Integer.parseInt(input) + 12));
    }
}
