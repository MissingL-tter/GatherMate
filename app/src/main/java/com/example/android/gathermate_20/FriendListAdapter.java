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
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FriendListAdapter extends ArrayAdapter<Friend> {

    private static final String TAG = "FREIND_LIST";

    private Activity context;
    private List<Friend> friendList;

    public FriendListAdapter(Activity context, List<Friend> friendList) {
        super(context, R.layout.friend_list_layout, friendList);
        this.context = context;
        this.friendList = friendList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listViewItem;
        if (convertView != null) {
            listViewItem = convertView;
        }else {
            LayoutInflater inflater = context.getLayoutInflater();
            listViewItem = inflater.inflate(R.layout.friend_list_layout, null, true);
        }

        Friend friend = friendList.get(position);

        //Name
        TextView nameItem = (TextView) listViewItem.findViewById(R.id.friendListName);
        nameItem.setText(friend.name);

        return listViewItem;
    }

}
