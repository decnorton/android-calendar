package com.decnorton.calendar;

import org.joda.time.DateTime;

/**
 * Created by decnorton on 11/04/2014.
 */
public class Event {
    private static final String TAG = "Event";

    protected Object id;
    protected int color;
    protected String title;
    protected String location;
    protected boolean isAllDay = false;

    protected DateTime start;
    protected DateTime end;

    public Event(Object id, int color, String title, String location, boolean isAllDay, DateTime start, DateTime end) {
        this.id = id;
        this.color = color;
        this.title = title;
        this.location = location;
        this.isAllDay = isAllDay;
        this.start = start;
        this.end = end;
    }

    public Object getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public boolean isAllDay() {
        return isAllDay;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Event) {
            return ((Event) o).getId() == id;
        }

        return super.equals(o);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        builder.append(" id : " + id);
        builder.append(" color : " + color);
        builder.append(" title : " + title);
        builder.append(" location : " + location);
        builder.append(" isAllDay : " + isAllDay);
        builder.append(" start : " + start);
        builder.append( "end : " + end);

        builder.append(" }");
        return builder.toString();
    }
}
