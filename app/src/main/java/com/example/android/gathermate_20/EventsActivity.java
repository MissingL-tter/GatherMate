package com.example.android.gathermate_20;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    FloatingActionButton addEventButton;
    ListView listViewEvents;
    List<Event> eventList;

    private DatabaseReference databaseEvents;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        new DrawerBuilder().withActivity(this).build();
        databaseEvents = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        eventList = new ArrayList<>();

        addEventButton = (FloatingActionButton) findViewById(R.id.eventAddEventButton);

        listViewEvents = (ListView) findViewById(R.id.listViewEvents);
        listViewEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event item = (Event) parent.getItemAtPosition(position);
                //TextView nameView = (TextView) parent.findViewById(R.id.detailName);
                //String name = listViewEvents.getItemAtPosition(position).toString().trim();
                Intent intent = new Intent(EventsActivity.this, EventDetailActivity.class);
                System.out.println(item.name);
                intent.putExtra("event", item);
                if (firebaseAuth.getCurrentUser().getUid().equals(item.uid)) {
                    intent.putExtra("isOwner",true);
                }
                startActivity(intent);
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAddEvent();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.clear();
                for (DataSnapshot eventListSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot eventSnapshot : eventListSnapshot.getChildren()) {
                        Event event = (Event) eventSnapshot.getValue(Event.class);
                        event.uid = (eventListSnapshot.getKey());
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
    }

    private void startAddEvent() {
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }
}