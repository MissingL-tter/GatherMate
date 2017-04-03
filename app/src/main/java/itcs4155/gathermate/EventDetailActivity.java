package itcs4155.gathermate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Dillon on 3/31/2017.
 */

public class EventDetailActivity extends AppCompatActivity {

    private static final String TAG = "EVENT_DETAIL";

    TextView nameView;
    TextView descView;
    TextView locationView;
    Button deleteButton;
    boolean isOwner;

    private DatabaseReference databaseEvents;
    private String eventId;
    private String uid;
    private GoogleSignInAccount user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Intent intent = getIntent();
        user = getIntent().getParcelableExtra("User");
        String name = intent.getStringExtra("detailName");
        String desc = intent.getStringExtra("detailDesc");
        String loc = intent.getStringExtra("locationDetail");
        eventId = intent.getStringExtra("eventId");
        uid = intent.getStringExtra("uid");
        isOwner = intent.getBooleanExtra("isOwner",false);
        nameView = (TextView) findViewById(R.id.detailName);
        deleteButton = (Button) findViewById(R.id.eventDeleteButton);
        // for testing only. can remove this block when done - Daniel
        if (isOwner) {
            nameView.setText(name+ " (me)");
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) { deleteEvent(v,uid,eventId); }
            });
        } else {
            nameView.setText(name);
            deleteButton.setVisibility(View.INVISIBLE);
        }
        descView = (TextView) findViewById(R.id.detailDesc);
        descView.setText(desc);
        locationView = (TextView) findViewById(R.id.locationDetail);
        locationView.setText(loc);

        databaseEvents = FirebaseDatabase.getInstance().getReference();
    }

    public void deleteEvent (View v, String uid, String eventId) {
        databaseEvents.child(uid).child(eventId).removeValue();
        Intent intent = new Intent(this, EventsActivity.class);
        startActivity(intent);
    }
}
