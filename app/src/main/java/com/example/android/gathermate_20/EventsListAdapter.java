package com.example.android.gathermate_20;

import android.app.Activity;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventsListAdapter extends ArrayAdapter<Event> {

    private Activity context;
    private List<Event> eventList;
    LocationHandler locationHandler;

    public EventsListAdapter(Activity context, List<Event> eventList) {
        super(context, R.layout.list_layout, eventList);
        this.context = context;
        this.eventList = eventList;
        locationHandler = new LocationHandler(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        final Event event = eventList.get(position);

        //Venue
        TextView titleItem = (TextView) listViewItem.findViewById(R.id.listVenueAndOwner);
        titleItem.setText(event.venueName + " with " + event.ownerName);

        //Travel Time
        final TextView travelTimeItem = (TextView) listViewItem.findViewById(R.id.listTravelTime);
        locationHandler.createRequest(event, travelTimeItem);

        //Time
        final TextView timeItem = (TextView) listViewItem.findViewById(R.id.listTime);
        final Calendar calendar = Calendar.getInstance();

        Long timeUntilEvent = event.time - calendar.getTimeInMillis();

        new CountDownTimer(timeUntilEvent, 1000) {
            public void onTick(long timeUntilDone) {
                if (timeUntilDone >= 86400000) {
                    Long rem = timeUntilDone % 86400000;
                    timeItem.setText("Starts in " + (timeUntilDone - rem) / 86400000 + " Days " + rem / 3600000 + " Hours");
                } else if (timeUntilDone >= 3600000) {
                    Long rem = timeUntilDone % 3600000;
                    timeItem.setText("Starts in " + (timeUntilDone - rem) / 3600000 + " Hours " + rem / 60000 + " Minutes");
                } else if (timeUntilDone >= 60000) {
                    Long rem = timeUntilDone % 60000;
                    timeItem.setText("Starts in " + (timeUntilDone - rem) / 60000 + " Minutes " + rem / 1000 + " Seconds");
                } else {
                    timeItem.setText("Starts in " + timeUntilDone / 1000 + " Seconds");
                }

                if (!travelTimeItem.getText().equals("...")) {
                    Long travelTime = 0L;
                    Pattern pattern;
                    Matcher matcher;

                    pattern = Pattern.compile("(\\d+(?=\\sd))");
                    matcher = pattern.matcher(travelTimeItem.getText().toString());
                    while (matcher.find()) {
                        travelTime += Integer.parseInt(matcher.group()) * 86400000;
                    }
                    pattern = Pattern.compile("(\\d+(?=\\sh))");
                    matcher = pattern.matcher(travelTimeItem.getText().toString());
                    while (matcher.find()) {
                        travelTime += Integer.parseInt(matcher.group()) * 3600000;
                    }
                    pattern = Pattern.compile("(\\d+(?=\\sm))");
                    matcher = pattern.matcher(travelTimeItem.getText().toString());
                    while (matcher.find()) {
                        travelTime += Integer.parseInt(matcher.group()) * 60000;
                    }

                    if (travelTime != 0L) {
                        if (travelTime - timeUntilDone >= 600000) {
                            travelTimeItem.setTextColor(Color.RED);
                        } else if (travelTime - timeUntilDone > 0) {
                            travelTimeItem.setTextColor(Color.YELLOW);
                        } else if (travelTime - timeUntilDone <= 0) {
                            travelTimeItem.setTextColor(Color.GREEN);
                        }
                    }

                }
            }

            public void onFinish() {
                if(Long.parseLong(travelTimeItem.getText().toString()) >= 120000) {
                    travelTimeItem.setTextColor(Color.RED);
                }else {
                    travelTimeItem.setText("");
                }

                calendar.setTimeInMillis(event.time);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String time = String.format("Started at %d:%02d", hour, minute) + " " + "AM";
                if (hour >= 12) {
                    if (hour > 12) {
                        hour -= 12;
                    }
                    time = String.format("Started at %d:%02d", hour, minute) + " " + "PM";
                } else if (hour == 0) {
                    hour = 12;
                    time = String.format("Started at %d:%02d", hour, minute) + " " + "AM";
                }
                timeItem.setText(time);

            }
        }.start();

        return listViewItem;
    }

    public Event getItem(int position) {
        return eventList.get(position);
    }
}
