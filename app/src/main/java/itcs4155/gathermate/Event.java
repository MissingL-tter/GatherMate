package itcs4155.gathermate;

/**
 * Created by Todd on 3/29/2017.
 */

public class Event {
    String description, location, name, uid;

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

    public String getUid() { return uid; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUid (String uid) { this.uid = uid; }
}
