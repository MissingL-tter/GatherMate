package com.example.android.gathermate_20;

import android.os.Build;
import android.os.Parcelable;
import android.os.Parcel;
import android.support.annotation.RequiresApi;

public class Event implements Parcelable{

    public String description, location, hour, minute, name, uid, eventId;

    public Event() {

    }

    public Event(String description, String location, String hour, String minute, String name, String uid, String eventId) {
        this.description = description;
        this.location = location;
        this.hour = hour;
        this.minute = minute;
        this.name = name;
        this.uid = uid;
        this.eventId = eventId;
    }

    public Event(Parcel in) {
        String[] data = new String[7];

        in.readStringArray(data);
        this.description = data[0];
        this.location = data[1];
        this.hour = data[2];
        this.minute = data[3];
        this.name = data[4];
        this.uid = data[5];
        this.eventId = data[6];
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
                this.hour,
                this.minute,
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

//    int hourOfEvent;
//    int minOfEvent;
//    int currentHour;
//    int currentMin;
//
//    public int getCurrentHour() {
//        return currentHour;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    public void setCurrentHour(int currentHour) {
//
//        this.currentHour = currentHour;
//    }
//
//    public int getCurrentMin() {
//        return currentMin;
//    }
//
//    public void setCurrentMin(int currentMin) {
//        this.currentMin = currentMin;
//    }
//
//    int timeToEvent;
//
//    public int getHourOfEvent() {
//        return hourOfEvent;
//    }
//
//    public void setHourOfEvent(int hourOfEvent) {
//        this.hourOfEvent = hourOfEvent;
//    }
//
//    public int getMinOfEvent() {
//        return minOfEvent;
//    }
//
//    public void setMinOfEvent(int minOfEvent) {
//        this.minOfEvent = minOfEvent;
//    }
//
//    public int getTimeToEvent() {
//        return timeToEvent;
//    }
//
//    public void setTimeToEvent() {
//        int timeToHour = hourOfEvent - currentHour;
//        int timeToMin = minOfEvent - currentMin;
//
//        if (timeToHour > 0) {
//            timeToEvent = (timeToHour * 60) + currentMin;
//        } else
//            timeToEvent = currentMin;
//    }
}