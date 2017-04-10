package com.example.android.gathermate_20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EVENT_DETAIL";

    TextView locationView;
    TextView dateView;
    TextView timeView;
    TextView descriptionView;
    TextView nameView;
    Button deleteButton;

    private DatabaseReference databaseEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        final Event event = intent.getParcelableExtra("event");

        //Location
        locationView = (TextView) findViewById(R.id.detailVenueNameContent);
        locationView.setText(event.venueName);

        //Date and Time
        dateView = (TextView) findViewById(R.id.detailDateContent);
        timeView = (TextView) findViewById(R.id.detailTimeContent);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.time);
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONTH);
        Integer day = calendar.get(Calendar.DAY_OF_MONTH);
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer minute = calendar.get(Calendar.MINUTE);

        String timeText = String.format("%d:%02d", hour, minute) + " " + "AM";
        if(hour >= 12) {
            if(hour > 12){
                hour -= 12;
            }
            timeText = String.format("%d:%02d", hour, minute) + " " + "PM";
        }else if(hour == 0) {
            hour = 12;
            timeText = String.format("%d:%02d", hour, minute) + " " + "AM";
        }
        dateView.setText(month + "/" + day + "/" + year);
        timeView.setText(timeText);

        //Description
        descriptionView = (TextView) findViewById(R.id.detailDetailsContent);
        descriptionView.setText(event.description);

        //Name
        nameView = (TextView) findViewById(R.id.detailOwnerNameContent);

        //Delete
        deleteButton = (Button) findViewById(R.id.eventDeleteButton);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(event.uid)) {
            nameView.setText("You");
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { deleteEvent(v, event.uid, event.eventId); }
            });
        } else {
            nameView.setText(event.ownerName);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        databaseEvents = FirebaseDatabase.getInstance().getReference();
    }

    public void deleteEvent (View v, String uid, String eventId) {
        databaseEvents.child("eventdb").child(uid).child(eventId).removeValue();
        Intent intent = new Intent(EventDetailActivity.this, EventsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_event_detail_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.appBarSettings:
                System.out.println("SETTINGS_CLICKED");
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
