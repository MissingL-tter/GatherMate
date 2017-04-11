package com.example.android.gathermate_20;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddEventActivity extends FragmentActivity {

    private static final String TAG = "ADD_EVENT";

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private Place pickedPlace;
    TimeDialogHandler timeDialogHandler;
    DateDialogHandler dateDialogHandler;
    Button createEventButton;
    EditText descriptionET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        descriptionET = (EditText) findViewById(R.id.addEventDescriptionEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        createEventButton = (Button) findViewById(R.id.addEventCreateEventButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                pickedPlace = place;
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "Error: " + status.toString());
            }
        });
    }

    public void setTimeOnClick(View view) {
        timeDialogHandler = new TimeDialogHandler();
        timeDialogHandler.show(getFragmentManager(), "timePicker");
    }

    public void setDateOnClick(View view) {
        dateDialogHandler = new DateDialogHandler();
        dateDialogHandler.show(getFragmentManager(), "datePicker");
    }

    private void createEvent() {
        FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        DatabaseReference eventReference = databaseReference.child("eventdb").child(fbUser.getUid()).push();

        Calendar calendar = Calendar.getInstance();
        calendar.set(dateDialogHandler.year, dateDialogHandler.month, dateDialogHandler.day, timeDialogHandler.hour, timeDialogHandler.minute);
        Long time = calendar.getTimeInMillis();

        // TODO: handle null values appropriately (fail or continue safely)

        Event event = new Event(
                descriptionET.getText().toString(),
                pickedPlace.getName().toString(),
                pickedPlace.getLatLng().latitude,
                pickedPlace.getLatLng().longitude,
                time,
                fbUser.getDisplayName(),
                fbUser.getUid(),
                eventReference.getKey());
        eventReference.setValue(event.toHashmap());

        Toast.makeText(this, "Event Created!", Toast.LENGTH_LONG).show();
        finish();
    }
}
