package com.decnorton.calendar.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    /**
     * Views
     */
    private Button mDayButton;
    private Button mWeekButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDayButton = (Button) findViewById(R.id.dayButton);
        mWeekButton = (Button) findViewById(R.id.weekButton);

        mDayButton.setOnClickListener(this);
        mWeekButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.dayButton:
                Intent dayIntent = new Intent(this, DayActivity.class);
                startActivity(dayIntent);
                break;

            case R.id.weekButton:
                Intent weekIntent = new Intent(this, WeekActivity.class);
                startActivity(weekIntent);
                break;
        }
    }
}
