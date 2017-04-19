package com.example.android.gathermate_20;

import android.os.Parcelable;
import android.os.Parcel;

import java.util.HashMap;

public class Event implements Parcelable {

    public String description, venueName, ownerName, uid, eventId;
    public Double lat, lng;
    public Long time;
    public int venuePrice;

    // Here because it has to be
    public Event() {

    }

    public Event(String description, String venueName, Double lat, Double lng, Long time, String ownerName, String uid, String eventId, int venuePrice) {
        this.description = description;
        this.venueName = venueName;
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.ownerName = ownerName;
        this.uid = uid;
        this.eventId = eventId;
        this.venuePrice = venuePrice;
    }

    private Event(Parcel in) {
        String[] data = new String[9];

        in.readStringArray(data);
        this.description = data[0];
        this.venueName = data[1];
        this.lat = Double.parseDouble(data[2]);
        this.lng = Double.parseDouble(data[3]);
        this.time = Long.parseLong(data[4]);
        this.ownerName = data[5];
        this.uid = data[6];
        this.eventId = data[7];
        this.venuePrice = Integer.parseInt(data[8]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{
                this.description,
                this.venueName,
                this.lat.toString(),
                this.lng.toString(),
                this.time.toString(),
                this.ownerName,
                this.uid,
                this.eventId,
                String.valueOf(this.venuePrice)
        });
    }

    public HashMap<String, Object> toHashmap() {
        HashMap<String, Object> m = new HashMap<>();
        m.put("ownerName", this.ownerName);
        m.put("description", this.description);
        m.put("venueName", this.venueName);
        m.put("lat", this.lat);
        m.put("lng", this.lng);
        m.put("time", this.time);
        m.put("venuePrice", this.venuePrice);
        return m;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
