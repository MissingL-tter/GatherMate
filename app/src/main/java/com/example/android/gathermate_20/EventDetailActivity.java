package com.example.android.gathermate_20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EventDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "EVENT_DETAIL";

    private final Activity context = this;

    Event event;
    TextView venueNameView;
    TextView dateView;
    TextView timeView;
    TextView descriptionView;
    TextView ownerNameView;
    Button deleteButton;
    Button navigateButton;

    LocationManager locationManager;
    Location location;
    double userLat;
    double userLong;

    private DatabaseReference databaseEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Intent intent = getIntent();
        event = intent.getParcelableExtra("event");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        userLat = location.getLatitude();
        userLong = location.getLongitude();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(EventDetailActivity.this);


        //Location
        venueNameView = (TextView) findViewById(R.id.detailVenueNameContent);
        venueNameView.setText(event.venueName);

        //Date and Time
        dateView = (TextView) findViewById(R.id.detailDateContent);
        timeView = (TextView) findViewById(R.id.detailTimeContent);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(event.time);
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONTH);
        Integer day = calendar.get(Calendar.DAY_OF_MONTH);
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer minute = calendar.get(Calendar.MINUTE);

        String timeText = String.format("%d:%02d", hour, minute) + " " + "AM";
        if (hour >= 12) {
            if (hour > 12) {
                hour -= 12;
            }
            timeText = String.format("%d:%02d", hour, minute) + " " + "PM";
        } else if (hour == 0) {
            hour = 12;
            timeText = String.format("%d:%02d", hour, minute) + " " + "AM";
        }
        dateView.setText(month + "/" + day + "/" + year);
        timeView.setText(timeText);

        //Description
        descriptionView = (TextView) findViewById(R.id.detailDetailsContent);
        descriptionView.setText(event.description);

        //Name
        ownerNameView = (TextView) findViewById(R.id.detailOwnerNameContent);

        //Delete
        deleteButton = (Button) findViewById(R.id.eventDeleteButton);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(event.uid)) {
            ownerNameView.setText("You");
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> deleteEvent(event.uid, event.eventId));
        } else {
            ownerNameView.setText(event.ownerName);
            deleteButton.setVisibility(View.INVISIBLE);
        }

        databaseEvents = FirebaseDatabase.getInstance().getReference();

        //Navigate
        navigateButton = (Button) findViewById(R.id.navigateToEventButton);
        navigateButton.setOnClickListener(view -> {
            double lat = event.lat;
            double lng = event.lng;

            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + String.valueOf(lat) + "," + String.valueOf(lng));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_event_detail_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.appBarSettings:
                System.out.println("SETTINGS_CLICKED");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteEvent(String uid, String eventId) {
        databaseEvents.child("eventdb").child(uid).child(eventId).removeValue();
        Intent intent;
        try {
            intent = new Intent(context, Class.forName(getCallingActivity().getClassName()));
        } catch (ClassNotFoundException e) {
            intent = new Intent(context, EventsActivity.class);
        }
        finish();
        startActivity(intent);
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        // get locations for user and event
        LatLng eventLoc = new LatLng(event.lat, event.lng);
        LatLng userLoc = new LatLng(userLat,userLong);
        // put a marker down for user and event
        Marker eventMarker = mMap.addMarker(new MarkerOptions().position(eventLoc).title(event.venueName));
        Marker userMarker = mMap.addMarker(new MarkerOptions().position(userLoc).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(eventLoc));

        // animate the camera to show both events on the screen
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(eventMarker.getPosition());
        builder.include(userMarker.getPosition());
        LatLngBounds bounds = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300, 225, 0));
    }
}
