package com.example.android.gathermate_20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationHandler implements LocationListener {

    private static final String TAG = "LOCATION";

    public final String API_KEY = "AIzaSyBxZCGrI9rkcMDZnipjqI9MMIswRGAzwso";

    private Activity context;
    EventsListAdapter contextAdapter;
    Snackbar snackbar;

    LocationManager locationManager;
    Criteria criteria = new Criteria();
    String provider;

    Location location;
    RequestQueue requestQueue;
    double lat;
    double lng;

    public LocationHandler(Activity context, EventsListAdapter contextAdapter) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        requestQueue = Volley.newRequestQueue(context);
        this.contextAdapter = contextAdapter;

        snackbar = Snackbar.make(context.findViewById(R.id.coordinator), "Location Services Disabled", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Enable", v -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
        });

        update();
    }

    public void update() {
        //Check permissions
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 11);
        } else {
            if (locationEnabled()) {
                //Set provider and get location
                provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 1000, 0, this);
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
                //Hide Snackbar if showing
                snackbar.dismiss();
            } else {
                //Show Snackbar if hidden
                snackbar.show();
            }
        }
    }

    public void createRequest(final Event event, final TextView travelTimeItem) {

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + lat + "," + lng + "&destination=" + event.lat + "," + event.lng + "&key=" + API_KEY;

            // Request a string response from the provided URL.
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    mapsResponse -> {
                        try {
                            JSONObject duration = mapsResponse.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration");
                            travelTimeItem.setText(duration.getString("text") + " away");
                        } catch (JSONException e) {
                            travelTimeItem.setText("");
                            Log.e(TAG, "Maps Response: No Route Found");
                        }
                    },
                    error -> {
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(jsonRequest);

        } else {
            travelTimeItem.setText("");
            Log.e(TAG, "No Location Permission");
        }
    }

    public boolean locationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        lat = location.getLatitude();
        lng = location.getLongitude();
        contextAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        update();
    }

    @Override
    public void onProviderDisabled(String provider) {
        update();
    }
}
