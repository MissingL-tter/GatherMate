package com.example.android.gathermate_20;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "EVENTS";

    ListView listViewEvents;
    DatabaseReference databaseEvents;
    List<Event> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        new DrawerBuilder().withActivity(this).build();

        listViewEvents = (ListView) findViewById(R.id.listViewEvents);
        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(EventsActivity.this, EventDetailActivity.class);
                intent.putExtra("event", event);
                startActivity(intent);
            }
        });

        databaseEvents = FirebaseDatabase.getInstance().getReference().child("eventdb");
        eventList = new ArrayList<>();

        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot eventSnapshot : userSnapshot.getChildren()) {
                        Event event = eventSnapshot.getValue(Event.class);
                        event.uid = (userSnapshot.getKey());
                        event.eventId = (eventSnapshot.getKey());

                        eventList.add(event);
                    }
                }

                EventsListAdapter adapter = new EventsListAdapter(EventsActivity.this, eventList);
                listViewEvents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FloatingActionButton addEventButton = (FloatingActionButton) findViewById(R.id.eventAddEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddEvent();
            }
        });

    }

    private void startAddEvent() {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.recreate();
                } else {
                    //TODO: Handle Location Permission Denied
                    //Presumably never request an update again
                    //For now we are letting Androids "Don't show again" feature handle it
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_events_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.socialFindFriends:
                System.out.println("ADD_FRIENDS_CLICKED");
                return true;

            case R.id.socialFriendsList:
                System.out.println("FRIENDS_LIST_CLICKED");
                return true;

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
