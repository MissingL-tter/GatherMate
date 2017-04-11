package com.example.android.gathermate_20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String TAG = "EVENTS";

    private final Activity context = this;

    String uid;
    DatabaseReference databaseUsers;
    DatabaseReference databaseThisUser;

    ListView listViewFriends;
    List<Friend> friendList;
    MenuItem searchEmailItem;
    MenuItem searchNameItem;
    SearchView searchView;

    //Popup Window Variables;
    LayoutInflater popupInflater;
    View popupView;
    TextView popUpTextView;
    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Friends");
        setSupportActionBar(toolbar);
        new DrawerBuilder().withActivity(this).build();

        popupInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Get the UID for this user, the user database, and initialize friendList
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseUsers = FirebaseDatabase.getInstance().getReference().child("userdb");
        databaseThisUser = FirebaseDatabase.getInstance().getReference().child("userdb").child(uid);

        listViewFriends = (ListView) findViewById(R.id.listViewFriends);
        friendList = new ArrayList<>();

        //Get friends from the databaseEvents, and update the view onDataChange
        databaseThisUser.child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendList.clear();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    friendList.add(new Friend(
                            friendSnapshot.getKey(),
                            friendSnapshot.child("name").getValue().toString()
                    ));
                }


                FriendListAdapter adapter = new FriendListAdapter(context, friendList);
                listViewFriends.setAdapter(adapter);
                registerForContextMenu(listViewFriends);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });

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
                searchView.setOnQueryTextListener(makeSearchHandler("info/email",true,searchEmailItem));
                return true;

            case R.id.appBarAddFriendsName:
                searchEmailItem.collapseActionView();
                searchView = (SearchView) MenuItemCompat.getActionView(searchNameItem);
                searchView.setQueryHint("Find Users by Name...");
                searchView.setInputType(nameInputType);
                searchView.setOnQueryTextListener(makeSearchHandler("info/name",false,searchNameItem));
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
        inflater.inflate(R.menu.friends_context_menu, contextMenu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.contextRemoveFriend:
                popupView = popupInflater.inflate(R.layout.confirm_window, (ViewGroup) findViewById(R.id.eventsAppBar));
                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                popUpTextView = (TextView) popupView.findViewById(R.id.confirmWindowText);
                popUpTextView.setText("Do you really want to remove " + friendList.get(info.position).name + "?");
                popupView.findViewById(R.id.confirmWindowAccept).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseThisUser.child("friends").child(friendList.get(info.position).uid).removeValue();
                        popupWindow.dismiss();
                    }
                });
                popupView.findViewById(R.id.confirmWindowDeny).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                return true;

            default:
                return super.onContextItemSelected(item);

        }

    }

    /**
     * Make a SearchView.OnQueryTextListener that handles searches of the database at $searchByKey
     * A value of searchByKey for email would be "info/email"
     * the returned listener will construct a pop-up with a list of the results
     * if findOne is true, only one match will be found at most
     *
     **/
    private SearchView.OnQueryTextListener makeSearchHandler (final String searchByKey, final boolean findOne, final MenuItem thisItem) {
        return new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                Query dbQuery = databaseUsers.orderByChild(searchByKey).equalTo(query);
                if (findOne) {
                    dbQuery = dbQuery.limitToFirst(1);
                }
                dbQuery.addListenerForSingleValueEvent(new ValueEventListener() {

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
                                popupView = popupInflater.inflate(R.layout.confirm_window, (ViewGroup) findViewById(R.id.eventsAppBar));
                                popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
                                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                                popUpTextView = (TextView) popupView.findViewById(R.id.confirmWindowText);
                                popUpTextView.setText("Are you sure you want to add " + userSnapshot.child("info").child("name").getValue().toString() + " as a friend?");
                                popupView.findViewById(R.id.confirmWindowAccept).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        databaseUsers.child(uid).child("friends").child(userSnapshot.getKey()).child("name").setValue(userSnapshot.child("info").child("name").getValue().toString());
                                        thisItem.collapseActionView();
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
                            popupView = popupInflater.inflate(R.layout.warning_window, (ViewGroup) findViewById(R.id.warningWindow));
                            popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
                            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                            popUpTextView = (TextView) popupView.findViewById(R.id.warningWindowText);
                            popUpTextView.setText("User not found");
                            popupView.findViewById(R.id.warningWindowDismiss).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                }
                            });
                            Log.e(TAG,"SEARCH: User Not Found for key type "+searchByKey+" "+query);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}

                });

                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) { return false; }

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
