package com.decnorton.calendar;

/**
 * Created by decnorton on 16/04/2014.
 */
public class WeekUtils {

    public static final int WEEK_DAYS = 7;

    public static int[] calculateDayWidths(int width) {
        return calculateDayWidths(width, 0);
    }

    public static int[] calculateDayWidths(int width, int timelineWidth) {
        int[] widths = new int[WEEK_DAYS];

        for (int day = 0; day < WEEK_DAYS; day++) {
            widths[day] = calculateDayWidth(width, timelineWidth);

            if (day == 0)
                widths[day] += timelineWidth;

        }

        return widths;
    }

    public static int calculateDayWidth(int width, int timelineWidth) {
        return (width - timelineWidth) / WEEK_DAYS;
    }

}
