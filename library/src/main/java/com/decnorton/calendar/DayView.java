package com.decnorton.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by decnorton on 11/04/2014.
 *
 * TODO: Scaling
 *
 */
public class DayView extends View {
    private static final String TAG = "DayView";

    private Context mContext;

    protected static final int DAY_HOURS = 24;
    protected static final int DAY_MINUTES = 1440;

    // Distance between each cell.
    protected static final int CELL_GAP = 1;

    private static int PADDING_TOP = 4;

    // Timeline
    private static int TIMELINE_WIDTH = 80;
    private static int TIMELINE_HOUR_PADDING_TOP = 2;
    private static int TIMELINE_HOUR_PADDING_BOTTOM = 2;
    private static int TIMELINE_HOUR_PADDING_LEFT = 4;
    private static int TIMELINE_HOUR_PADDING_RIGHT = 4;
    private static int TIMELINE_HOUR_PADDING_H = TIMELINE_HOUR_PADDING_LEFT + TIMELINE_HOUR_PADDING_RIGHT;
    private static int TIMELINE_HOUR_PADDING_V = TIMELINE_HOUR_PADDING_TOP + TIMELINE_HOUR_PADDING_BOTTOM;
    private static float TIMELINE_TEXT_SIZE = 12;

    // Event
    private static int EVENT_PADDING_TOP = 2;
    private static int EVENT_PADDING_BOTTOM = 2;
    private static int EVENT_PADDING_LEFT = 4;
    private static int EVENT_PADDING_RIGHT = 4;

    // Current time
    private static int CURRENT_TIME_WIDTH = 2;

    /**
     * Data
     */
    private List<Event> mEvents = new ArrayList<>();
    private List<Event> mAllDayEvents = new ArrayList<>();
    private List<EventView> mEventViews = new ArrayList<>();

    private Event mSelectedEvent;

    // Dates
    private DateTime mDay;
    private DateTime mNow = new DateTime();
    private Interval mToday;


    /**
     * Drawing stuff
     */

    // Used for supporting different screen densities
    private static float mScale = 0;
    private boolean mShowTimeline = false;

    private Paint mPaint = new Paint();
    private Rect mRect = new Rect();

    // Position
    protected int mViewStartX;
    protected int mViewStartY;

    // Dimensions
    protected static final int MIN_HEIGHT = 200;
    protected static final int MIN_WIDTH = 200;
    protected int mViewWidth;
    protected int mViewHeight;
    private int mTimelineTextHeight;

    // Shared among all DayView instances
    protected static int sCellHeight = 0;

    // Canvas.drawLines() requires takes each line as x0 y0 x1 y1
    private float[] mLines = new float[DAY_HOURS * 4];

    // Typefaces
    protected Typeface mRobotoLight;

    /**
     * Listeners
     */
    private OnEventClickedListener mEventClickedListener;

    public DayView(Context context) {
        super(context);

        init(context, null);
    }

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, null);
        initAttributes(attrs);
    }

    public DayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context, null);
        initAttributes(attrs);
    }

    public DayView(Context context, DateTime day) {
        super(context);
        init(context, day);
    }

    private void init(Context context, DateTime date) {
        mContext = context;
        mDay = date == null ? new DateTime().withTimeAtStartOfDay() : date.withTimeAtStartOfDay();

        calculateTodayInterval();

        // getDimension() handles scaling for us
        PADDING_TOP = getDimension(R.dimen.day_padding_top);

        EVENT_PADDING_TOP = getDimension(R.dimen.day_event_padding_top);
        EVENT_PADDING_BOTTOM = getDimension(R.dimen.day_event_padding_bottom);
        EVENT_PADDING_LEFT = getDimension(R.dimen.day_event_padding_left);
        EVENT_PADDING_RIGHT = getDimension(R.dimen.day_event_padding_right);

        TIMELINE_TEXT_SIZE = getDimension(R.dimen.timeline_text_size);
        TIMELINE_HOUR_PADDING_TOP = getDimension(R.dimen.timeline_hour_padding_top);
        TIMELINE_HOUR_PADDING_BOTTOM = getDimension(R.dimen.timeline_hour_padding_bottom);
        TIMELINE_HOUR_PADDING_LEFT = getDimension(R.dimen.timeline_hour_padding_left);
        TIMELINE_HOUR_PADDING_RIGHT = getDimension(R.dimen.timeline_hour_padding_right);
        TIMELINE_WIDTH = getDimension(R.dimen.timeline_width);
        TIMELINE_HOUR_PADDING_H = TIMELINE_HOUR_PADDING_LEFT + TIMELINE_HOUR_PADDING_RIGHT;
        TIMELINE_HOUR_PADDING_V = TIMELINE_HOUR_PADDING_TOP + TIMELINE_HOUR_PADDING_BOTTOM;

        CURRENT_TIME_WIDTH = getDimension(R.dimen.current_time_width);

        if (sCellHeight == 0) {
            sCellHeight = getDimension(R.dimen.cell_height);
        }

        if (mScale == 0) {
            mScale = getResources().getDisplayMetrics().density;

            if (mScale != 1) {
                // Calculate scaled values
            }
        }
    }

    private void initAttributes(AttributeSet attributes) {
        Resources.Theme theme = mContext.getTheme();

        if (theme == null)
            return;

        TypedArray a = theme.obtainStyledAttributes(
                attributes,
                R.styleable.DayView,
                0, 0);

        try {
            mShowTimeline = a.getBoolean(R.styleable.DayView_showTimeline, false);
        } finally {
            a.recycle();
        }
    }

    public List<Event> getEvents() {
        return mEvents;
    }

    public void setEvents(List<Event> events) {
        mEvents = events;
    }

    private int getDimension(int resId) {
        return (int) getResources().getDimension(resId);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = w;
        mViewHeight = h;

        // Determine the text height
        Paint p = new Paint();
        setupTimelineTextPaint(p);
        mTimelineTextHeight = (int) Math.abs(p.ascent());
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return ((sCellHeight + CELL_GAP) * DAY_HOURS) + PADDING_TOP + getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return MIN_WIDTH + getPaddingLeft() + getPaddingRight();
    }

    protected int getStageTop() {
        return PADDING_TOP + getPaddingTop();
    }

    protected int getStageHeight() {
        return sCellHeight * DAY_HOURS;
    }

    protected int getStageLeft() {
        return mShowTimeline ? TIMELINE_WIDTH : 0;
    }

    protected int getStageRight() {
        return mViewWidth;
    }

    protected int getStageBottom() { return getStageTop() + getStageHeight(); }

    protected Rect getStageBounds() {
        return new Rect(getStageLeft(), getStageTop(), getStageRight(), getStageBottom());
    }

    public static int getTimelineWidth() {
        return TIMELINE_WIDTH;
    }

    public boolean getShowTimeline() {
        return mShowTimeline;
    }

    public void setShowTimeline(boolean showTimeline) {
        mShowTimeline = showTimeline;
    }

    public float getTopForHour(int hour) {
        return getStageTop() + (getStageHeight() * ((float) hour / (float) DAY_HOURS));
    }

    private float getTopForMinute(int minute) {
        return getStageTop() + (getStageHeight() * ((float) minute / (float) DAY_MINUTES));
    }


    public DateTime getDate() {
        return mDay;
    }

    public void setDate(DateTime date) {
        this.mDay = date;

        calculateTodayInterval();
        invalidate();
    }

    private void calculateTodayInterval() {
        mToday = mDay != null
                ? new Interval(mDay, mDay.plusHours(DAY_HOURS))
                : null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        doDraw(canvas);
    }

    private void doDraw(Canvas canvas) {
        drawGridBackground(mRect, canvas, mPaint);
        drawTimeline(mRect, canvas, mPaint);

        // Draw the greyed-out background
        drawCurrentTimeBackground(mRect, canvas, mPaint);

        // Draw events
        drawEvents(mRect, canvas, mPaint);

        // Draw a line for the current time
        drawCurrentTimeLine(mRect, canvas, mPaint);
    }

    private void drawGridBackground(Rect rect, Canvas canvas, Paint paint) {
        Paint.Style savedStyle = paint.getStyle();

        float x = getStageLeft();
        final float stopX = mViewWidth;

        int linesIndex = 0;
        for (int hour = 0; hour < DAY_HOURS; hour++) {
            float y = getTopForHour(hour);

            mLines[linesIndex++] = x; // x0
            mLines[linesIndex++] = y; // y0
            mLines[linesIndex++] = stopX; // x1
            mLines[linesIndex++] = y; // y1
        }

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(CELL_GAP);

        // Draw our lines
        canvas.drawLines(mLines, 0, linesIndex, paint);

        // Reset the saved style
        paint.setStyle(savedStyle);
        paint.setAntiAlias(true);
    }

    private void drawTimeline(Rect rect, Canvas canvas, Paint paint) {
        if (!mShowTimeline)
            return;

        drawTimelineHours(rect, canvas, paint);
    }

    private void drawTimelineHours(Rect rect, Canvas canvas, Paint paint) {
        setupTimelineTextPaint(paint);

        // We're right aligning the text so subtract the right padding
        int x = TIMELINE_WIDTH - TIMELINE_HOUR_PADDING_RIGHT;

        for (int hour = 0; hour < DAY_HOURS; hour++) {
            String time = TimeUtils.getHour(hour);

            // Centered vertically
            canvas.drawText(time, x, getTopForHour(hour) + (mTimelineTextHeight / 2), paint);
        }
    }

    private void drawEvents(Rect rect, Canvas canvas, Paint paint) {
        for (Event event : mEvents) {
            drawEvent(event, rect, canvas, paint);
        }
    }

    private void drawEvent(Event event, Rect rect, Canvas canvas, Paint paint) {
        // Get the event bounds
        Rect bounds = computeEventBounds(event);

        if (bounds == null) {
            // Event shouldn't be here
            return;
        }

        // Save the previous style
        Paint.Style savedStyle = paint.getStyle();

        Paint bgPaint = new Paint();

        // Darken the background colour if selected
        int backgroundColour = event.equals(mSelectedEvent)
                ? ColorUtils.darken(event.getColor(), 0.8f)
                : event.getColor();

        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(backgroundColour);

        // Draw the background onto our canvas
        canvas.drawRect(bounds, bgPaint);

        // Set up title paint
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(TIMELINE_TEXT_SIZE);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setAntiAlias(true);

        // Get the text bounds
        Rect textBounds = new Rect();
        paint.getTextBounds(event.getTitle(), 0, event.getTitle().length(), textBounds);

        int x = bounds.left + EVENT_PADDING_LEFT;
        int y = bounds.top + EVENT_PADDING_TOP + textBounds.height();

        // Check we have enough room in the container
        if (bounds.height() > textBounds.height() + EVENT_PADDING_TOP + EVENT_PADDING_BOTTOM) {
            // Draw the text
            canvas.drawText(event.getTitle(), x, y, paint);
        }

        // Restore the style
        paint.setStyle(savedStyle);
    }

    private Rect computeEventBounds(Event event) {
        DateTime start = event.getStart();
        DateTime end = event.getEnd();

        Interval eventInterval = new Interval(start, end);

        if (!eventInterval.overlaps(mToday)) {
            return null;
        }

        int startMinute;
        int endMinute;

        if (TimeUtils.isSameDay(mDay, start)) {
            startMinute = start.getMinuteOfDay();
        } else if (start.isBefore(mDay)) {
            // Not same day and before now = draw from top
            startMinute = 0;
        } else {
            // Not same day and after now = shouldn't be drawn
            return null;
        }

        if (TimeUtils.isSameDay(mDay, end)) {
            endMinute = end.getMinuteOfDay();
        } else if (end.isAfter(mDay)) {
            endMinute = DAY_MINUTES;
        } else {
            // Not same day and before now - don't draw
            return null;
        }

        int startY = (int) getTopForMinute(startMinute);
        int stopY = (int) getTopForMinute(endMinute);

        return new Rect(getStageLeft(), startY, getStageRight(), stopY);
    }

    private void drawCurrentTimeBackground(Rect rect, Canvas canvas, Paint paint) {
        boolean isToday = TimeUtils.isSameDay(mNow, mDay);

        if (mDay.isBeforeNow() || isToday) {
            Paint.Style savedStyle = paint.getStyle();

            // Set the background style
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0x22000000);

            Rect bounds = getStageBounds();

            if (isToday) {
                // Bottom should go down to the line
                bounds.bottom = Math.round(getTopForMinute(mNow.getMinuteOfDay()));
            }

            // Draw the background
            canvas.drawRect(bounds, paint);

            // Restore the Paint style
            paint.setStyle(savedStyle);
        }
    }

    private void drawCurrentTimeLine(Rect rect, Canvas canvas, Paint paint) {
        boolean isToday = TimeUtils.isSameDay(mNow, mDay);

        int x = getStageLeft();
        int y = Math.round(getTopForMinute(mNow.getMinuteOfDay()));

        if (!isToday) {
            return;
        }

        Paint.Style savedStyle = paint.getStyle();

        // Set the line style
        paint.setColor(Color.RED);
        paint.setStrokeWidth(CURRENT_TIME_WIDTH);

        // Draw the line
        canvas.drawLine(x, y, mViewWidth, y, paint);

        paint.setStyle(savedStyle);
    }

    private void setupTimelineTextPaint(Paint paint) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(TIMELINE_TEXT_SIZE);
        paint.setTypeface(getTimelineTypeface());
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setAntiAlias(true);
    }

    private Typeface getTimelineTypeface() {
        if (isInEditMode())
            return Typeface.DEFAULT;

        if (mRobotoLight == null) {
            mRobotoLight = Typeface.createFromAsset(mContext.getAssets(), Font.ROBOTO_LIGHT);
        }

        return mRobotoLight;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                return true;

            case MotionEvent.ACTION_UP:
                mSelectedEvent = findEventFromPosition(Math.round(event.getX()), Math.round(event.getY()));

                dispatchEventClicked(mSelectedEvent);

                invalidate();

                return true;

        }

        return super.onTouchEvent(event);
    }

    private Event findEventFromPosition(int x, int y) {
        for (Event event : mEvents) {
            Rect bounds = computeEventBounds(event);

            if (bounds != null && bounds.contains(x, y)) {
                return event;
            }
        }

        return null;
    }

    public void setSelectedEvent(Event event) {
        if (event == mSelectedEvent)
            return;

        // Should allow for multi-day events
        if (event != null && mEvents.indexOf(event) > -1) {
            mSelectedEvent = event;
        } else {
            mSelectedEvent = null;
        }

        invalidate();
    }

    protected void dispatchEventClicked(Event event) {
        if (mEventClickedListener == null)
            return;

        mEventClickedListener.onEventSelected(this, event);
    }

    public void setOnEventClickedListener(OnEventClickedListener listener) {
        mEventClickedListener = listener;
    }

    public interface OnEventClickedListener {
        public void onEventSelected(DayView view, Event event);
    }

}
