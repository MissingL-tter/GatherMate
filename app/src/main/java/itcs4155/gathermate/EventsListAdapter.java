package itcs4155.gathermate;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Todd on 3/29/2017.
 */

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

        TextView descriptionItem = (TextView) listViewItem.findViewById(R.id.listItemDescription);
        TextView locationItem = (TextView) listViewItem.findViewById(R.id.listItemLocation);
        TextView nameItem = (TextView) listViewItem.findViewById(R.id.listItemName);

        Event event = eventList.get(position);
        descriptionItem.setText(event.getDescription());
        locationItem.setText(event.getLocation());
        nameItem.setText(event.getName());



        return listViewItem;
    }

    public Event getItem(int position) {
        return eventList.get(position);
    }
}
