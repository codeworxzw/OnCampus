package com.devon_dickson.apps.orgspace;

/**
 * Created by ddickson1 on 2/5/2016.
 */


public class Event {
    private int id;
    private String name;
    private String location;
    private String rain;
    private String org;
    private String time;
    private String rsvp;

    public Event(int id, String name, String location, String rain, String org, String time, String rsvp) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rain = rain;
        this.org = org;
        this.time = time;
        this.rsvp = rsvp;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRsvp() {
        return rsvp;
    }

    public void setRsvp(String rsvp) {
        this.rsvp = rsvp;
    }
}
