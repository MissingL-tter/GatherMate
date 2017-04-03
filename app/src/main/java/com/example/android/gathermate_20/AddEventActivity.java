package com.example.android.gathermate_20;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEventActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    Button createEventButton;
    EditText descriptionET, locationET;
    GoogleSignInAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        descriptionET = (EditText) findViewById(R.id.addEventDescriptionEditText);
        locationET = (EditText) findViewById(R.id.addEventLocationEditText);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser fbUser = firebaseAuth.getCurrentUser();

        user = getIntent().getParcelableExtra("User");
        createEventButton = (Button) findViewById(R.id.addEventCreateEventButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createEvent();
            }
        });
    }

    private void createEvent() {
        FirebaseUser fbUser = firebaseAuth.getCurrentUser();
        Event event = new Event();
                event.setDescription(descriptionET.getText().toString());
                event.setLocation(locationET.getText().toString());

                event.setName(user.getGivenName() + " " + user.getFamilyName());
                event.setUid(fbUser.getUid());

        databaseReference.child(fbUser.getUid()).push().setValue(event);

        Toast.makeText(this, "Event Created!", Toast.LENGTH_LONG).show();
        finish();
    }
}
