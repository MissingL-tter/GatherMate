package com.example.android.gathermate_20;

import android.os.Build;
import android.os.Parcelable;
import android.os.Parcel;
import android.support.annotation.RequiresApi;

import com.google.android.gms.location.places.Place;

public class Event implements Parcelable{

    public String description, location, time, name, uid, eventId;
    public Place place;

    public Event() {

    }

    public Event(String description, String location, String time, String name, String uid, String eventId) {
        this.description = description;
        this.location = location;
        this.time = time;
        this.name = name;
        this.uid = uid;
        this.eventId = eventId;
    }

    public Event(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this.description = data[0];
        this.location = data[1];
        this.time = data[2];
        this.name = data[3];
        this.uid = data[4];
        this.eventId = data[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[] {
                this.description,
                this.location,
                this.time,
                this.name,
                this.uid,
                this.eventId
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

//   public void setTimeToEvent() {
//        int timeToHour = hourOfEvent - currentHour;
//        int timeToMin = minOfEvent - currentMin;
//
//        if (timeToHour > 0) {
//            timeToEvent = (timeToHour * 60) + currentMin;
//        } else
//            timeToEvent = currentMin;
//    }

}
