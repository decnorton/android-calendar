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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by decnorton on 11/04/2014.
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
    private DateTime mDay = new DateTime("2014-04-12T00:00:00.000+01:00");
    private DateTime mNow = new DateTime();

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
    private OnEventSelectedListener mEventSelectedListener;

    public DayView(Context context) {
        super(context);

        init(context);
    }

    public DayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
        initAttributes(attrs);
    }

    public DayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(context);
        initAttributes(attrs);
    }

    private void init(Context context) {
        mContext = context;

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

        initDebug();
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

    private void initDebug() {
        DateTime[] starts = {
                new DateTime("2014-04-12T11:00:00.000+01:00"),
                new DateTime("2014-04-12T13:00:00.000+01:00"),
                new DateTime("2014-04-12T18:00:00.000+01:00"),
                new DateTime("2014-04-12T20:00:00.000+01:00")
        };

        DateTime[] ends = {
                new DateTime("2014-04-12T11:20:00.000+01:00"),
                new DateTime("2014-04-12T13:40:00.000+01:00"),
                new DateTime("2014-04-12T19:00:00.000+01:00"),
                new DateTime("2014-04-12T20:10:00.000+01:00")
        };

        for (int i = 0; i < 4; i ++) {
            Event event = new Event(i, getResources().getColor(R.color.event_blue), "Event #" + i, "Location #" + i, false, starts[i], ends[i]);
            mEvents.add(event);
        }
    }

    private void setDay(DateTime day) {
        mDay = day;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        doDraw(canvas);
    }

    private void doDraw(Canvas canvas) {
        drawGridBackground(mRect, canvas, mPaint);
        drawTimeline(mRect, canvas, mPaint);

        drawEvents(mRect, canvas, mPaint);

        drawCurrentTime(mRect, canvas, mPaint);
    }

    private void drawGridBackground(Rect rect, Canvas canvas, Paint paint) {
        Paint.Style savedStyle = paint.getStyle();

        float x = getStageLeft();
        float y = getStageTop();
        final float stopX = mViewWidth;

        final float deltaY = sCellHeight + CELL_GAP;

        int linesIndex = 0;
        for (int hour = 0; hour < DAY_HOURS; hour++) {
            mLines[linesIndex++] = x; // x0
            mLines[linesIndex++] = y; // y0
            mLines[linesIndex++] = stopX; // x1
            mLines[linesIndex++] = y; // y1

            // Next grid line
            y += deltaY;
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
        int y = getStageTop();
        int deltaY = sCellHeight + CELL_GAP;

        for (int hour = 0; hour < DAY_HOURS; hour++) {
            String time = TimeUtils.getHour(hour);

            // Centered vertically
            canvas.drawText(time, x, y + (mTimelineTextHeight / 2), paint);

            y += deltaY;
        }
    }

    private void drawEvents(Rect rect, Canvas canvas, Paint paint) {
        for (Event event : mEvents) {
            drawEvent(event, rect, canvas, paint);
        }
    }

    private void drawEvent(Event event, Rect rect, Canvas canvas, Paint paint) {
        // Save the previous style
        Paint.Style savedStyle = paint.getStyle();

        Paint bgPaint = new Paint();

        // Darken the background colour if selected
        int backgroundColour = event.equals(mSelectedEvent)
                ? ColorUtils.darken(event.getColor(), 0.8f)
                : event.getColor();

        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(backgroundColour);

        // Get the event bounds
        Rect bounds = computeEventBounds(event);

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

        int startMinute = start.getMinuteOfDay();
        int endMinute = end.getMinuteOfDay();

        int startY = (int) getYForMinute(startMinute);
        int stopY = (int) getYForMinute(endMinute);

        return new Rect(getStageLeft(), startY, getStageRight(), stopY);
    }

    private float getYForMinute(int minute) {
        return getStageTop() + (getStageHeight() * ((float) minute / (float) DAY_MINUTES));
    }

    private boolean isDayToday() {
        return mNow.withTimeAtStartOfDay().equals(mDay.withTimeAtStartOfDay());
    }

    private void drawCurrentTime(Rect rect, Canvas canvas, Paint paint) {
        if (!isDayToday())
            return;

        int x = getStageLeft();
        float y = getYForMinute(mNow.getMinuteOfDay());

        Paint.Style savedStyle = paint.getStyle();

        paint.setColor(Color.RED);
        paint.setStrokeWidth(CURRENT_TIME_WIDTH);

        canvas.drawLine(x, y, mViewWidth, y, paint);

        paint.setStyle(savedStyle);
    }

    private void setupTimelineTextPaint(Paint paint) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(TIMELINE_TEXT_SIZE);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setAntiAlias(true);
    }

    private Typeface getTimelineTypeface() {
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

                if (mSelectedEvent != null) {
                    dispatchEventSelected(mSelectedEvent);
                }

                invalidate();

                return true;

        }

        return super.onTouchEvent(event);
    }

    private Event findEventFromPosition(int x, int y) {
        for (Event event : mEvents) {
            Rect bounds = computeEventBounds(event);

            if (bounds.contains(x, y)) {
                return event;
            }
        }

        return null;
    }

    protected void dispatchEventSelected(Event event) {
        if (mEventSelectedListener == null)
            return;

        mEventSelectedListener.onEventSelected(this, event);
    }

    public void setOnEventSelectedListener(OnEventSelectedListener listener) {
        mEventSelectedListener = listener;
    }

    public interface OnEventSelectedListener {
        public void onEventSelected(DayView view, Event event);
    }

}
