package com.example.android.gathermate_20;

import android.app.Activity;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "EVENTS";

    private final Activity context = this;

    ListView listViewEvents;
    String uid;
    DatabaseReference databaseEvents;
    ValueEventListener eventsEventListener;
    DatabaseReference databaseThisUser;
    MenuItem searchEmailItem;
    MenuItem searchNameItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listViewEvents = (ListView) findViewById(R.id.listViewEvents);
        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = (Event) parent.getItemAtPosition(position);
                Intent intent = new Intent(context, EventDetailActivity.class);
                intent.putExtra("event", event);
                startActivityForResult(intent, 0);
            }
        });

        //Get the UID for this user, the user database, the event database, and initialize friendList
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseThisUser = FirebaseDatabase.getInstance().getReference().child("userdb").child(uid);
        databaseEvents = FirebaseDatabase.getInstance().getReference().child("eventdb");

        //Get Events only for friends and yourself
        getFriendEvents();

        //Find AddEventButton and create listener to start AddEventActivity
        FloatingActionButton addEventButton = (FloatingActionButton) findViewById(R.id.eventAddEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddEventActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStop() {
        databaseEvents.removeEventListener(eventsEventListener);
        super.onStop();
    }

    public void onRestart() {
        super.onRestart();
        //Get Events only for friends and yourself
        getFriendEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the App Bar
        getMenuInflater().inflate(R.menu.activity_events_app_bar, menu);

        //Create MenuItem variables to be used in onOptionSelected
        searchEmailItem = menu.findItem(R.id.appBarAddFriendsEmail);
        searchNameItem = menu.findItem(R.id.appBarAddFriendsName);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //When the user selects the social icon, open the start the friends activity
            case R.id.appBarSocial:
                Intent intent = new Intent(context, FriendsActivity.class);
                startActivity(intent);
                return true;

            //Do something when the user selects settings
            case R.id.appBarSettings:
                //TODO: Implement Settings Activity
                //Manual XML coding to make the cog icon generate a list
                //Talk to Chris before proceeding
                System.out.println("SETTINGS_CLICKED");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //When we receive a confirm or deny of location service permission,
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
                    //For now we are letting Android hide the prompts if the user selects "Never Show Again"
                }
            }
        }
    }

    /**
     * Populate friendList with users that you have registered as friends
     * For each member in friendList, populate the events list with that friend's events.
     **/
    private void getFriendEvents() {
        final List<String> friendList = new ArrayList<>();
        final List<Event> eventList = new ArrayList<>();

        //Get friends from the this users database
        databaseThisUser.child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendList.clear();
                //Add this user to list and populate the list with friends
                friendList.add(uid);
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    friendList.add(friendSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

        //Create listener for events
        eventsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                //Populated only from friend events
                for(String friend : friendList) {
                    DataSnapshot friendSnapshot = dataSnapshot.child(friend);
                    for(DataSnapshot eventSnapshot : friendSnapshot.getChildren()){
                        Event event = eventSnapshot.getValue(Event.class);
                        event.uid = friendSnapshot.getKey();
                        event.eventId = eventSnapshot.getKey();

                        eventList.add(event);
                    }
                }

                EventsListAdapter adapter = new EventsListAdapter(EventsActivity.this, eventList);
                listViewEvents.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        };

        //Start the listener
        databaseEvents.addValueEventListener(eventsEventListener);
    }

}
