package com.example.android.gathermate_20;

import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Todd on 3/29/2017.
 */

public class Event {
    String description, location, name;
    int hourOfEvent;
    int minOfEvent;
    int currentHour;
    int currentMin;

    public int getCurrentHour() {
        return currentHour;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setCurrentHour(int currentHour) {

        this.currentHour = currentHour;
    }

    public int getCurrentMin() {
        return currentMin;
    }

    public void setCurrentMin(int currentMin) {
        this.currentMin = currentMin;
    }

    int timeToEvent;

    public int getHourOfEvent() {
        return hourOfEvent;
    }

    public void setHourOfEvent(int hourOfEvent) {
        this.hourOfEvent = hourOfEvent;
    }

    public int getMinOfEvent() {
        return minOfEvent;
    }

    public void setMinOfEvent(int minOfEvent) {
        this.minOfEvent = minOfEvent;
    }

    public int getTimeToEvent() {
        return timeToEvent;
    }

    public void setTimeToEvent() {
        int timeToHour = hourOfEvent - currentHour;
        int timeToMin = minOfEvent - currentMin;

        if(timeToHour > 0){
            timeToEvent = (timeToHour * 60) + currentMin;
        }
        else
            timeToEvent = currentMin;
    }

    public Event() {

    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }
}
