package com.example.android.gathermate_20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
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

    private final Activity context = this;

    ListView listViewEvents;
    String uid;
    DatabaseReference databaseEvents;
    DatabaseReference databaseUsers;
    List<Event> eventList;
    MenuItem searchEmailItem;
    MenuItem searchNameItem;
    SearchView searchView;

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

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("userdb");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_events_app_bar, menu);

        searchEmailItem = menu.findItem(R.id.appBarAddFriendsEmail);
        searchNameItem = menu.findItem(R.id.appBarAddFriendsName);

        searchEmailItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchNameItem.collapseActionView();
                searchView = (SearchView) MenuItemCompat.getActionView(searchEmailItem);
                searchView.setQueryHint("Find Users by Email...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View popupView;
                    TextView popUpTextView;
                    PopupWindow popupWindow;

                    @Override
                    public boolean onQueryTextSubmit(String emailQuery) {
                        databaseUsers.orderByChild("info/email").equalTo(emailQuery).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(final DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        //TODO: Make Better Formatting for PopupWindow, preferably make list beneath search bar
                                        //PopupWindow or something similar
                                        //For now the searchItem view is collapsing only if the user was found and added as a friend
                                        if(popupWindow != null) {
                                            popupWindow.dismiss();
                                        }
                                        popupView = layoutInflater.inflate(R.layout.confirm_window, (ViewGroup) findViewById(R.id.confirmWindow));
                                        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
                                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                        popUpTextView = (TextView) popupView.findViewById(R.id.confirmWindowText);
                                        popUpTextView.setText("Do you really want to add " + userSnapshot.child("info").child("name").getValue().toString() + " as a friend?");
                                        popupView.findViewById(R.id.confirmWindowAccept).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                databaseUsers.child(uid).child("friends").child(userSnapshot.getKey()).child("name").setValue(userSnapshot.child("info").child("name").getValue().toString());
                                                searchEmailItem.collapseActionView();
                                                popupWindow.dismiss();
                                            }
                                        });
                                        popupView.findViewById(R.id.confirmWindowDeny).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                popupWindow.dismiss();
                                            }
                                        });
                                    }
                                }else {
                                    //TODO: Make Better Formatting for PopupWindow, preferably make list beneath search bar
                                    //PopupWindow or something similar
                                    //For now searchItem view stays open if the user was not found
                                    if(popupWindow != null) {
                                        popupWindow.dismiss();
                                    }
                                    popupView = layoutInflater.inflate(R.layout.warning_window, (ViewGroup) findViewById(R.id.warningWindow));
                                    popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
                                    popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                    popUpTextView = (TextView) popupView.findViewById(R.id.warningWindowText);
                                    popUpTextView.setText("Could not find a user with the specified email");
                                    popupView.findViewById(R.id.warningWindowDismiss).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            popupWindow.dismiss();
                                        }
                                    });
                                }

                                Log.e(TAG,"SEARCH: User Email Not Found");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}

                        });

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }

                });

                return false;
            }
        });

        searchNameItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchEmailItem.collapseActionView();
                searchView = (SearchView) MenuItemCompat.getActionView(searchNameItem);
                searchView.setQueryHint("Find Users by Name...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String nameQuery) {
                        databaseUsers.orderByChild("info/name").equalTo(nameQuery).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    for(DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        //TODO: Create prompt showing name of user and verifying adding them to friends
                                        //PopupWindow or something similar
                                        //For now the searchItem view is collapsing only if the user was found and added as a friend
                                        databaseUsers.child(uid).child("friends").child(userSnapshot.getKey()).child("name").setValue(userSnapshot.child("info").child("name").getValue().toString());
                                        searchNameItem.collapseActionView();
                                    }
                                }else {
                                    //TODO: Create prompt indicating User Not Found
                                    //PopupWindow or something similar
                                    //For now searchItem view stays open if the user was not found
                                    Log.e(TAG,"SEARCH: User Not Found");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.appBarFriendsList:
                //TODO: Implement Friends List
                //This could be an entire activity, a fragment, or a ListView in a PopupWindow
                System.out.println("FRIENDS_LIST_CLICKED");
                return true;

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
}
