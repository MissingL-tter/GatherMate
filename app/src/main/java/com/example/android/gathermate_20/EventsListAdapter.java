package com.example.android.gathermate_20;

import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventsListAdapter extends ArrayAdapter<Event> {

    private static final String TAG = "EVENTS_LIST";

    private Activity context;
    private List<Event> eventList;
    LocationHandler locationHandler;

    public EventsListAdapter(Activity context, List<Event> eventList) {
        super(context, R.layout.event_list_layout, eventList);
        this.context = context;
        this.eventList = eventList;
        locationHandler = new LocationHandler(context, this);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listViewItem;
        if (convertView != null) {
            listViewItem = convertView;
        } else {
            LayoutInflater inflater = context.getLayoutInflater();
            listViewItem = inflater.inflate(R.layout.event_list_layout, null, true);
        }

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

        new CountDownTimer(timeUntilEvent, 10) {
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

                updateTravelColor(travelTimeItem, timeUntilDone);
            }

            public void onFinish() {


                travelTimeItem.setTextColor(travelTimeItem.getTextColors().getDefaultColor());

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

    private void updateTravelColor(TextView travelTimeItem, Long timeUntilDone) {
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
