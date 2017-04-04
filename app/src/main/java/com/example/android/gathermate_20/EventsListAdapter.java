package com.example.android.gathermate_20;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.list_layout, null, true);

        TextView locationItem = (TextView) listViewItem.findViewById(R.id.listItemLocation);
        TextView timeItem = (TextView) listViewItem.findViewById(R.id.listItemTime);
        TextView descriptionItem = (TextView) listViewItem.findViewById(R.id.listItemDescription);
        TextView nameItem = (TextView) listViewItem.findViewById(R.id.listItemName);

        Event event = eventList.get(position);

        locationItem.setText(event.location);
        //Time Parser
        Integer hour = Integer.parseInt(event.hour);
        Integer minute = Integer.parseInt(event.minute);
        String time = String.format("%d:%02d", hour, minute) + " " + "AM";
        if(hour >= 12) {
            if(hour > 12){
                hour -= 12;
            }
            time = String.format("%d:%02d", hour, minute) + " " + "PM";
        }else if(hour == 0) {
            hour = 12;
            time = String.format("%d:%02d", hour, minute) + " " + "AM";
        }
        timeItem.setText(time);
        descriptionItem.setText(event.description);
        nameItem.setText(event.name);

        return listViewItem;
    }

    public Event getItem(int position) {
        return eventList.get(position);
    }
}
