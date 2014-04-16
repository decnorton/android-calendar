package com.decnorton.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.joda.time.DateTime;

/**
 * Created by decnorton on 16/04/2014.
 */
public class WeekHeaderView extends View {
    private static final String TAG = "WeekHeaderView";

    /**
     * Constants
     */

    protected Context mContext;
    private Paint mPaint;
    private Rect mRect;

    // Text
    private TextPaint mTextPaint = new TextPaint();
    private Spannable mText;

    /**
     * Views
     */
    private WeekView mWeekView;

    /**
     * Dimensions
     */
    private static int HEIGHT = 12;
    private static int PADDING_LEFT = 0;
    private static int TEXT_SIZE = 0;
    private static int DAY_PADDING = 0;
    private int mDayWidth;

    /**
     * Data
     */
    private DateTime mFirstDayOfWeek = TimeUtils.getFirstDayOfWeek(new DateTime());
    private String[] mDayNames = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };
    private int[] mDayDates = new int[mDayNames.length];
    private Spannable[] mDaySpannables = new Spannable[mDayNames.length];
    private StaticLayout[] mDayLayouts = new StaticLayout[mDayNames.length];

    // Typefaces
    protected Typeface mRobotoLight;

    public WeekHeaderView(Context context) {
        super(context);

        init(context);
    }

    public WeekHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public WeekHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    public WeekHeaderView(Context context, WeekView weekView) {
        super(context);

        init(context);
        setWeekView(weekView);
    }

    private void init(Context context) {
        mContext = context;

        HEIGHT = getDimension(R.dimen.week_header_height);
        PADDING_LEFT = DayView.getTimelineWidth();

        // Get dimensions

        if (TEXT_SIZE == 0)
            TEXT_SIZE = getDimension(R.dimen.week_header_day_text_size);

        if (DAY_PADDING == 0)
            DAY_PADDING = getDimension(R.dimen.week_header_day_padding);

        setBackgroundColor(ColorUtils.getColor(getResources(), R.color.week_header_background));

        calculateDayDates();
        setupDayTextStyle();
        setupDayLayouts();
    }

    private int getDimension(int resId) {
        return (int) getResources().getDimension(resId);
    }

    private void setWeekView(WeekView weekView) {
        mWeekView = weekView;
        mFirstDayOfWeek = weekView.getFirstDayOfWeek();

        // First day of the week has changed so recalculate dates
        calculateDayDates();
    }

    private void calculateDayDates() {
        // Set up spannables
        for (int day = 0; day < mDayNames.length; day++) {
            mDayDates[day] = mFirstDayOfWeek.plusDays(day).getDayOfMonth();
        }
    }

    private void setupDayTextStyle() {
        // Set up TextPaint
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setTypeface(getDayTypeface());
        mTextPaint.setColor(Color.BLACK);

        // Set up spannables
        for (int day = 0; day < mDayNames.length; day++) {
            mDaySpannables[day] = new SpannableString(mDayNames[day] + " " + mDayDates[day]);
            mDaySpannables[day].setSpan(new StyleSpan(Typeface.BOLD), mDayNames[day].length() + 1, mDaySpannables[day].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void setupDayLayouts() {
        int dayWidth = Math.max(mDayWidth - (DAY_PADDING * 2), 0);

        for (int day = 0; day < mDayNames.length; day++) {
            mDayLayouts[day] = new StaticLayout(mDaySpannables[day], mTextPaint, dayWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
        }
    }

    public String getNameForDay(int day) {
        return mDayNames[day];
    }

    public int getDateForDay(int day) {
        return mDayDates[day];
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return HEIGHT;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        int paddingTop = DAY_PADDING * 2;


        Rect bounds = new Rect();

        for (int day = 0; day < mDayLayouts.length; day++) {
            mDayLayouts[0].getLineBounds(0, bounds);

            height = Math.max(bounds.height() + paddingTop, height);
        }

        setMeasuredDimension(width, height);

        calculateDayWidth(getMeasuredWidth());
    }

    private void calculateDayWidth(int width) {
        mDayWidth = WeekUtils.calculateDayWidth(width, DayView.getTimelineWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setupDayLayouts();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i(TAG, "[onDraw] Day width: " + mDayWidth);

        canvas.save();

        canvas.translate(PADDING_LEFT + DAY_PADDING, DAY_PADDING);

        for (int day = 0; day < mDayLayouts.length; day++) {
            mDayLayouts[day].draw(canvas);
            canvas.translate(mDayWidth, 0);
        }

        canvas.restore();
    }

    private Typeface getDayTypeface() {
        if (isInEditMode())
            return Typeface.DEFAULT;

        if (mRobotoLight == null) {
            mRobotoLight = Typeface.createFromAsset(mContext.getAssets(), Font.ROBOTO_LIGHT);
        }

        return mRobotoLight;
    }


}
