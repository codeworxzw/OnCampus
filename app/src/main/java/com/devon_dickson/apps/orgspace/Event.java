package com.devon_dickson.apps.orgspace;

/**
 * Created by devon on 7/3/2015.
 */
class Event {
    String name;
    String location;
    String time;
    int orgPhoto;

    Event(String name, String time, String location, int orgPhoto) {
        this.name = name;
        this.time = time;
        this.location = location;
        this.orgPhoto = orgPhoto;
    }
}
