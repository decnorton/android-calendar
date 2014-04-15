package com.decnorton.calendar.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.decnorton.calendar.Event;
import com.decnorton.calendar.WeekView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class WeekActivity extends Activity {
    private static final String TAG = "WeekActivity";

    /**
     * Views
     */
    private WeekView mWeekView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week);

        DateTime now = new DateTime().withMinuteOfHour(0).withSecondOfMinute(0);

        DateTime[] starts = {
                now.minusDays(1),
                now.plusHours(1),
                now.plusDays(1).plusHours(2),
                now.plusDays(2).minusHours(4).plusMinutes(20),
                now.plusDays(3).minusHours(2),
                now.plusDays(4).minusHours(2),
        };

        DateTime[] ends = {
                starts[0].plusHours(1),
                starts[1].plusHours(1).plusMinutes(30),
                starts[2].plusMinutes(20),
                starts[3].plusMinutes(45),
                starts[4].plusHours(1),
                starts[5].plusDays(1).minusHours(3)
        };

        List<Event> events = new ArrayList<>();

        for (int i = 0; i < starts.length; i ++) {
            Event event = new Event(i, getResources().getColor(com.decnorton.calendar.R.color.event_blue), "#" + i, "Location #" + i, false, starts[i], ends[i]);
            events.add(event);
        }

        mWeekView = (WeekView) findViewById(R.id.week);

        mWeekView.setEvents(events);
        mWeekView.setOnEventClickedListener(new WeekView.OnEventClickedListener() {
            @Override
            public void onEventClicked(WeekView view, Event event) {
                if (event == null)
                    return;

                Toast.makeText(WeekActivity.this, "Event clicked: " + event.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.week, menu);
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
