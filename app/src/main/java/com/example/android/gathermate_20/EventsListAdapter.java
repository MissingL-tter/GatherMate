package com.example.android.gathermate_20;

import android.app.Activity;
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

import java.util.List;

public class EventsListAdapter extends ArrayAdapter<Event> {

    private Activity context;
    private List<Event> eventList;

    public EventsListAdapter(Activity context, List<Event> eventList){
        super(context, R.layout.list_layout, eventList);
        this.context = context;
        this.eventList = eventList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        Event event = eventList.get(position);

        //Location
        TextView locationItem = (TextView) listViewItem.findViewById(R.id.listItemLocation);
        locationItem.setText(event.location);

        //Time
        final TextView timeItem = (TextView) listViewItem.findViewById(R.id.listItemTime);
        Long eventTime = Long.parseLong(event.time);
        Calendar calendar = Calendar.getInstance();
        Long currentTime = calendar.getTimeInMillis();
//        calendar.setTimeInMillis(eventTime);

        Long timeUntilEvent = eventTime - currentTime;

        new CountDownTimer(timeUntilEvent, 1000) {
            public void onTick(long timeUntilDone) {
                if(timeUntilDone >= 86400000) {
                    Long rem = timeUntilDone%86400000;
                    timeItem.setText((timeUntilDone-rem)/86400000 + " Days " + rem/3600000 + " Hours");
                }else if(timeUntilDone >= 3600000) {
                    Long rem = timeUntilDone%3600000;
                    timeItem.setText((timeUntilDone-rem)/3600000 + " Hours " + rem/60000 + " Minutes");
                }else if(timeUntilDone >= 60000) {
                    Long rem = timeUntilDone%60000;
                    timeItem.setText((timeUntilDone-rem)/60000 + " Minutes " + rem/1000 + " Seconds");
                }else {
                    timeItem.setText(timeUntilDone/1000 + " Seconds");
                }
            }

            public void onFinish() {
                timeItem.setText("Event has started.");
            }
        }.start();

        //Description
        TextView descriptionItem = (TextView) listViewItem.findViewById(R.id.listItemDescription);
        descriptionItem.setText(event.description);

        //Name
        TextView nameItem = (TextView) listViewItem.findViewById(R.id.listItemName);
        nameItem.setText(event.name);

        return listViewItem;
    }

    public Event getItem(int position) {
        return eventList.get(position);
    }
}
