package com.decnorton.calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by decnorton on 11/04/2014.
 *
 * TODO: Add day headers
 *
 */
public class WeekView extends ViewGroup implements DayView.OnEventClickedListener {
    private static final String TAG = "WeekView";

    /**
     * Constants
     */
    public static final int WEEK_DAYS = 7;

    protected Context mContext;

    /**
     * Views
     */
    private DayView[] mDayViews;
    private DayView.OnEventClickedListener[] mEventListeners;

    /**
     * Data
     */
    private List<Event> mEvents = new ArrayList<>();
    private Event mSelectedEvent;
    private DateTime mNow = new DateTime();
    private DateTime mFirstDayOfWeek = TimeUtils.getFirstDayOfWeek(new DateTime());
    private int[] mDayWidths;

    /**
     * Listeners
     */
    private OnEventClickedListener mEventClickedListener;

    public WeekView(Context context) {
        super(context);

        init(context);
    }

    public WeekView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public WeekView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mDayViews = new DayView[WEEK_DAYS];
        DateTime date = mFirstDayOfWeek;

        for (int day = 0; day < WEEK_DAYS; day++) {
            DayView view = new DayView(context, date, mEvents);

            view.setOnEventClickedListener(this);
            view.setShowTimeline(day == 0);

            // Keep track of DayViews
            mDayViews[day] = view;

            // Add as a child
            addView(view);

            // Increase day
            date = date.plusDays(1);
        }

        if (isInEditMode()) {
            initDebug();
        }

    }

    private void initDebug() {
        DateTime now = new DateTime().withMinuteOfHour(0);

        DateTime[] starts = {
                now.minusDays(1),
                now.plusHours(1),
                now.plusDays(1).plusHours(2),
                now.plusDays(2).minusHours(4).plusMinutes(20),
                now.plusDays(3).minusHours(2),
                now.plusDays(4).minusHours(1),
        };

        DateTime[] ends = {
                starts[0].plusHours(1),
                starts[1].plusHours(1).plusMinutes(30),
                starts[2].plusMinutes(20),
                starts[3].plusMinutes(45),
                starts[4].plusHours(1),
                starts[5].plusDays(1).minusHours(3)
        };

        for (int i = 0; i < starts.length; i ++) {
            Event event = new Event(i, getResources().getColor(R.color.event_blue), "#" + i, "Location #" + i, false, starts[i], ends[i]);
            mEvents.add(event);
        }

        refreshDayViews();
    }

    private int getDimension(int resId) {
        return (int) getResources().getDimension(resId);
    }

    public DateTime getFirstDayOfWeek() {
        return mFirstDayOfWeek;
    }

    public int getDayWidth(int day) {
        return mDayWidths[day];
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return mDayViews[0].getSuggestedMinimumHeight();
    }

    public List<Event> getEvents() {
        return mEvents;
    }

    public void setEvents(List<Event> events) {
        Log.i(TAG, "[setEvents] " + events.size() + " events");

        mEvents.clear();
        mEvents.addAll(events);

        refreshDayViews();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        calculateDayWidth(getMeasuredWidth());
    }

    private void calculateDayWidth(int width) {
        mDayWidths = WeekUtils.calculateDayWidths(width, DayView.getTimelineWidth());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int startX = 0;
        int stopX = 0;
        int startY = 0;
        int stopY = getHeight();

        for (int day = 0; day < mDayViews.length; day++) {
            View child = mDayViews[day];

            if (child == null)
                continue;

            stopX += getDayWidth(day);

            child.layout(startX, startY, stopX, stopY);

            startX += getDayWidth(day);
        }
    }

    private void refreshDayViews() {
        for (DayView dayView : mDayViews) {
            dayView.refresh();
        }
    }

    @Override
    public void onEventSelected(DayView view, Event event) {
        setSelectedEvent(event);
        dispatchEventClicked(event);
    }

    public void setSelectedEvent(Event event) {
        mSelectedEvent = event;

        // Tell the DayViews to update
        for (DayView dayView : mDayViews) {
            dayView.setSelectedEvent(event);
        }
    }

    protected void dispatchEventClicked(Event event) {
        if (mEventClickedListener == null)
            return;

        mEventClickedListener.onEventClicked(this, event);
    }

    public void setOnEventClickedListener(OnEventClickedListener listener) {
        mEventClickedListener = listener;
    }

    public interface OnEventClickedListener {
        public void onEventClicked(WeekView view, Event event);
    }
}
