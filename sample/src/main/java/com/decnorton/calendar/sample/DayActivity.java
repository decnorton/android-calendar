package com.decnorton.calendar.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.decnorton.calendar.DayView;
import com.decnorton.calendar.Event;


public class DayActivity extends Activity {
    private static final String TAG = "DayActivity";

    /**
     * Views
     */
    private DayView mDayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        mDayView = (DayView) findViewById(R.id.day);
        mDayView.setOnEventClickedListener(new DayView.OnEventClickedListener() {
            @Override
            public void onEventSelected(DayView view, Event event) {
                if (event == null)
                    return;

                Toast.makeText(DayActivity.this, "Event selected: " + event.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.day, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
