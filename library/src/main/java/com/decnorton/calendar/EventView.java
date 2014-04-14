package com.decnorton.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by decnorton on 11/04/2014.
 */
public class EventView extends View {
    private static final String TAG = "EventView";

    private Context mContext;

    protected Event mEvent;

    public EventView(Context context) {
        super(context);
        init();
    }

    public EventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EventView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        mContext = getContext();

        setBackgroundColor(Color.RED);
    }
}
