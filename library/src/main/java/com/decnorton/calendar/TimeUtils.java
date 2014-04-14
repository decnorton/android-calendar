package com.decnorton.calendar;

/**
 * Created by decnorton on 12/04/2014.
 */
public class TimeUtils {

    protected static String[] sHourStrs = {
            "00:00", "1 am", "2 am", "3 am", "4 am", "5 am", "6 am", "7 am", "8 am", "9 am", "10 am", "11 am",
            "Noon", "1 pm", "2 pm", "3 pm", "4 pm", "5 pm", "6 pm", "7 pm", "8 pm", "9 pm", "10 pm", "11 pm"
    };


    protected static String[] sHours = {
            "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00",
            "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
            "18:00", "19:00", "20:00", "21:00", "22:00", "23:00"
    };

    public static String getHour(int hour) {
        return sHours[hour];
    }

    public static String getHourString(int hour) {
        return sHourStrs[hour];
    }


}
