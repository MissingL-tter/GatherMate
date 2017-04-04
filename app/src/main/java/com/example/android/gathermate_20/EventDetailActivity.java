package com.example.android.gathermate_20;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EVENT_DETAIL";

    TextView locationView;
    TextView timeView;
    TextView descView;
    TextView nameView;
    Button deleteButton;
    boolean isOwner;

    private DatabaseReference databaseEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Intent intent = getIntent();
        final Event event = intent.getParcelableExtra("event");

        locationView = (TextView) findViewById(R.id.detailLocation);
        locationView.setText(event.location);
        //Time Parser
        timeView = (TextView) findViewById(R.id.detailTime);
        Integer hour = Integer.parseInt(event.hour);
        Integer minute = Integer.parseInt(event.minute);
        String time = String.format("%d:%02d", hour, minute) + " " + "AM";
        if(hour >= 12) {
            if(hour > 12){
                hour -= 12;
            }
            time = String.format("%d:%02d", hour, minute) + " " + "PM";
        }else if(hour == 0) {
            hour = 12;
            time = String.format("%d:%02d", hour, minute) + " " + "AM";
        }
        timeView.setText(time);
        descView = (TextView) findViewById(R.id.detailDesc);
        descView.setText(event.description);
        nameView = (TextView) findViewById(R.id.detailName);
        deleteButton = (Button) findViewById(R.id.eventDeleteButton);
        isOwner = intent.getBooleanExtra("isOwner",false);
        // for testing only. can remove this block when done - Daniel
        if (isOwner) {
            nameView.setText(event.name + " (me)");
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { deleteEvent(v, event.uid, event.eventId); }
            });
        } else {
            nameView.setText(event.name);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        databaseEvents = FirebaseDatabase.getInstance().getReference();
    }

    public void deleteEvent (View v, String uid, String eventId) {
        databaseEvents.child(uid).child(eventId).removeValue();
        Intent intent = new Intent(EventDetailActivity.this, EventsActivity.class);
        startActivity(intent);
    }
}
