package com.company.eventcountdown;

import java.io.Serializable;

public class Event implements Serializable {
    String eventDate;
    int eventIcon;
    String eventName;
    String eventNotification;
    String eventTime;
    String uid;
    String identifier;

    public Event() {}

    public Event(String date, int icon, String name, String notification, String time, String id, String identity) {
        eventDate = date;
        eventIcon = icon;
        eventName = name;
        eventNotification = notification;
        eventTime = time;
        uid = id;
        identifier = identity;
    }

    public String getEventDate() { return eventDate; }
    public int getEventIcon() { return eventIcon; }
    public String getEventName() { return eventName; }
    public String getEventNotification() { return eventNotification;}
    public String getEventTime() { return eventTime; }
    public String getUID() { return uid; }
    public String getIdentifier() { return identifier; }
}

