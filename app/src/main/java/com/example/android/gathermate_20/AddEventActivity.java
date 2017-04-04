package com.example.android.gathermate_20;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEventActivity extends FragmentActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    TimeDialogHandler timeDialogHandler;
    Button createEventButton;
    EditText descriptionET, locationET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        descriptionET = (EditText) findViewById(R.id.addEventDescriptionEditText);
        locationET = (EditText) findViewById(R.id.addEventLocationEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        createEventButton = (Button) findViewById(R.id.addEventCreateEventButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("PLACE_AUTO", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Log.i("PLACE_AUTO", "Error: " + status.toString());
            }
        });
    }

    public void setTimeOnClick(View view) {
        timeDialogHandler = new TimeDialogHandler();
        timeDialogHandler.show(getFragmentManager(), "timePicker");
    }

    private void createEvent() {
        FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        DatabaseReference eventReference = databaseReference.child(fbUser.getUid()).push();

        Event event = new Event(descriptionET.getText().toString(),
                                locationET.getText().toString(),
                                timeDialogHandler.hour.toString(),
                                timeDialogHandler.minute.toString(),
                                fbUser.getDisplayName(),
                                fbUser.getUid(),
                                eventReference.getKey());

        eventReference.setValue(event);

        Toast.makeText(this, "Event Created!", Toast.LENGTH_LONG).show();
        finish();
    }
}
