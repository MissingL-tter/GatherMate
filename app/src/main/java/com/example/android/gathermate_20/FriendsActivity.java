package com.example.android.gathermate_20;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "EVENTS";

    private final Activity context = this;

    //Reference Variables
    String uid;
    DatabaseReference databaseEvents;
    DatabaseReference databaseUsers;
    DatabaseReference databaseThisUser;
    ValueEventListener thisUserEventListener;

    //Menu Item View Variables
    ListView listViewFriends;
    List<Friend> friendList;
    MenuItem searchEmailItem;
    MenuItem searchNameItem;
    SearchView searchView;

    //Popup Window Variables;
    ListPopupWindow listPopupWindow;
    LayoutInflater popupInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Friends");
        setSupportActionBar(toolbar);
        new DrawerBuilder().withActivity(this).build();

        popupInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Get the UID for this user, the user database, and initialize
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseEvents = FirebaseDatabase.getInstance().getReference().child("eventdb");
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("userdb");
        databaseThisUser = databaseUsers.child(uid);

        listViewFriends = (ListView) findViewById(R.id.listViewFriends);

        getFriendList();

        listViewFriends.setOnItemClickListener((parent, view, position, id) -> {
            Friend friend = (Friend) parent.getItemAtPosition(position);
            databaseEvents.child(friend.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<Event> eventList = new ArrayList<>();
                        for (DataSnapshot eventsSnapshot : dataSnapshot.getChildren()) {
                            Event event = eventsSnapshot.getValue(Event.class);
                            event.uid = dataSnapshot.getKey();
                            event.eventId = eventsSnapshot.getKey();
                            eventList.add(event);
                        }
                        initPopup(view);
                        showEventPopup(eventList);
                    } else {
                        Log.e(TAG, "NO SNAPSHOT");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        });
    }

    @Override
    public void onStop() {
        databaseThisUser.removeEventListener(thisUserEventListener);
        super.onStop();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getFriendList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the App Bar
        getMenuInflater().inflate(R.menu.activity_friends_app_bar, menu);

        //Create MenuItem variables to be used in onOptionSelected
        searchEmailItem = menu.findItem(R.id.appBarAddFriendsEmail);
        searchNameItem = menu.findItem(R.id.appBarAddFriendsName);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int emailInputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
        final int nameInputType = InputType.TYPE_TEXT_FLAG_CAP_WORDS;
        switch (item.getItemId()) {
            //When the user clicks an Add Friend search option, open the search bar
            //Wait for the user to submit text
            //Search the database and create a popup detailing the results
            case R.id.appBarAddFriendsEmail:
                searchNameItem.collapseActionView();
                searchView = (SearchView) MenuItemCompat.getActionView(searchEmailItem);
                searchView.setQueryHint("Find Users by Email...");
                searchView.setInputType(emailInputType);
                searchView.setOnQueryTextListener(makeSearchHandler("info/email", 1, searchEmailItem));
                return true;

            case R.id.appBarAddFriendsName:
                searchEmailItem.collapseActionView();
                searchView = (SearchView) MenuItemCompat.getActionView(searchNameItem);
                searchView.setQueryHint("Find Users by Name...");
                searchView.setInputType(nameInputType);
                searchView.setOnQueryTextListener(makeSearchHandler("info/name", 10, searchNameItem));
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

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(contextMenu, view, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_friends_context_menu, contextMenu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.contextRemoveFriend:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Remove " + friendList.get(info.position).name + " from friends?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", (dialog, id) -> {
                            databaseThisUser.child("friends").child(friendList.get(info.position).uid).removeValue();
                            dialog.cancel();
                        });

                builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
                AlertDialog alert = builder.create();
                alert.show();
                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }

    //Get friends from the databaseEvents, and update the view onDataChange
    private void getFriendList() {
        friendList = new ArrayList<>();

        thisUserEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendList.clear();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    friendList.add(new Friend(
                            friendSnapshot.getKey(),
                            friendSnapshot.child("name").getValue().toString()
                    ));
                }

                Collections.sort(friendList, (f1, f2) -> f1.name.compareTo(f2.name));
                friendList.add(0, new Friend(uid, FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                FriendListAdapter adapter = new FriendListAdapter(context, friendList);
                listViewFriends.setAdapter(adapter);
                registerForContextMenu(listViewFriends);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        };

        databaseThisUser.child("friends").addValueEventListener(thisUserEventListener);
    }

    /**
     * ListPopupWindow Handling Methods
     */
    private void initPopup(View view) {
        if (listPopupWindow != null) {
            listPopupWindow.dismiss();
        }
        listPopupWindow = new ListPopupWindow(context);
        listPopupWindow.setAnchorView(view);
        listPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        listPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        listPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.friend_search_list_background));
        listPopupWindow.setModal(true);
    }

    private void showEventPopup(List<Event> eventList) {
        EventsListAdapter adapter = new EventsListAdapter(context, eventList);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            Event event = (Event) parent.getItemAtPosition(position);
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event", event);
            listPopupWindow.dismiss();
            startActivityForResult(intent, 0);
        });
        listPopupWindow.show();
    }

    private void showFriendSearchPopup(List<Friend> potentialFriendList) {
        final FriendListAdapter adapter = new FriendListAdapter(context, potentialFriendList);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            final Friend newFriend = (Friend) parent.getItemAtPosition(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Add " + newFriend.name + " as a friend?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (dialog, id1) -> {
                databaseThisUser.child("friends").child(newFriend.uid).child("name").setValue(newFriend.name);
                searchNameItem.collapseActionView();
                listPopupWindow.dismiss();
                dialog.cancel();
            });

            builder.setNegativeButton("No", (dialog, id12) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        });
        listPopupWindow.setModal(false);
        listPopupWindow.show();
    }

    /**
     * Make a SearchView.OnQueryTextListener that handles searches of the database at $searchByKey
     * A value of searchByKey for email would be "info/email"
     * the returned listener will construct a pop-up with a list of the results
     * if findOne is true, only one match will be found at most
     **/
    private SearchView.OnQueryTextListener makeSearchHandler(final String searchByKey, final int numberOfResults, final MenuItem thisItem) {
        initPopup(searchView);

        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                Query dbQuery = databaseUsers.orderByChild(searchByKey).equalTo(query).limitToFirst(numberOfResults);
                dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && searchByKey.equals("info/email")) {
                            for (final DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                //TODO: Make Better Formatting for PopupWindow, preferably make list beneath search bar
                                //PopupWindow or something similar
                                //For now the searchItem view is collapsing only if the user was found and added as a friend
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Add " + userSnapshot.child("info/name").getValue().toString() + " as a friend?");
                                builder.setCancelable(true);
                                builder.setPositiveButton("Yes", (dialog, id) -> {
                                            databaseThisUser.child("friends").child(userSnapshot.getKey()).child("name").setValue(userSnapshot.child("info/name").getValue().toString());
                                            searchEmailItem.collapseActionView();
                                            dialog.cancel();
                                        });
                                builder.setNegativeButton("No", (dialog, id) -> dialog.cancel());
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } else {
                            //TODO: Make Better Formatting for PopupWindow, preferably make list beneath search bar
                            //PopupWindow or something similar
                            //For now searchItem view stays open if the user was not found
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage("User Not Found");
                            builder.setCancelable(true);
                            builder.setPositiveButton(
                                    "Dismiss",
                                    (dialog, id) -> dialog.cancel());
                            AlertDialog alert = builder.create();
                            alert.show();
                            Log.e(TAG, "SEARCH: User Not Found for key type " + searchByKey + " " + query);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });

                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newQuery) {
                if (newQuery.length() > 0 && searchByKey.equals("info/name")) {
                    Query dbQuery = databaseUsers.orderByChild(searchByKey).startAt(newQuery).endAt(newQuery + "~").limitToFirst(numberOfResults);
                    dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            List<Friend> potentialFriendList = new ArrayList<>();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                    Friend friendResult = new Friend(userSnapshot.getKey(), userSnapshot.child(searchByKey).getValue().toString());
                                    potentialFriendList.add(friendResult);
                                }

                                showFriendSearchPopup(potentialFriendList);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                return true;
            }

        };
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
}
