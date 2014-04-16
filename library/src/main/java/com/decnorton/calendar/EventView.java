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
import android.view.View;

/**
 * Created by decnorton on 11/04/2014.
 */
public class EventView extends View {
    private static final String TAG = "EventView";

    private Context mContext;
    private Paint mPaint;
    private Rect mRect;

    // Text
    private StaticLayout mTextLayout;
    private TextPaint mTextPaint = new TextPaint();
    private Spannable mText;


    /**
     * Dimensions
     */
    private static int PADDING = 0;
    private static int TEXT_SIZE = 0;

    /**
     * Data
     */
    protected Event mEvent;

    public EventView(Context context, Event event) {
        super(context);

        mEvent = event;
        init(context);
    }

    protected void init(Context context) {
        mContext = context;
        mPaint = new Paint();
        mRect = new Rect();

        // Get dimensions

        if (TEXT_SIZE == 0)
            TEXT_SIZE = getDimension(R.dimen.event_text_size);

        if (PADDING == 0)
            PADDING = getDimension(R.dimen.event_padding);

        setPadding(PADDING, PADDING, PADDING, PADDING);

        setupTextStyle();
        setupTextLayout();

        setBackgroundColor(mEvent.getColor());

    }

    private void setupTextStyle() {
        // Set up TextPaint
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setColor(Color.WHITE);

        String title = mEvent.getTitle();
        String location = mEvent.getLocation();
        mText = new SpannableString(title + " " + location);

        // Make title bold
        mText.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setupTextLayout() {
        mTextLayout = new StaticLayout(mText, mTextPaint, getInsideWidth(), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
    }

    public Event getEvent() {
        return mEvent;
    }

    private int getDimension(int resId) {
        return (int) getResources().getDimension(resId);
    }

    private int getInsideWidth() {
        return Math.max(getWidth() - getPaddingLeft() - getPaddingRight(), 0);
    }

    private int getInsideHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setupTextLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        // Translate
        canvas.translate(getPaddingTop(), getPaddingLeft());

        // Draw text
        mTextLayout.draw(canvas);

        canvas.restore();
    }

}
