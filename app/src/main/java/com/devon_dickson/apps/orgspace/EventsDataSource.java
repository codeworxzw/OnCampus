package com.devon_dickson.apps.orgspace;

/**
 * Created by ddickson1 on 2/5/2016.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class EventsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_NAME,
            DatabaseHelper.COLUMN_LOCATION,
            DatabaseHelper.COLUMN_RAIN,
            DatabaseHelper.COLUMN_ORG,
            DatabaseHelper.COLUMN_TIME};

    public EventsDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Event addEvent(int eventId, String eventName, String location, String rain, String org, String time, String RSVP) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ID, eventId);
        values.put(DatabaseHelper.COLUMN_NAME, eventName);
        values.put(DatabaseHelper.COLUMN_LOCATION, location);
        values.put(DatabaseHelper.COLUMN_RAIN, rain);
        values.put(DatabaseHelper.COLUMN_ORG, org);
        values.put(DatabaseHelper.COLUMN_TIME, time);
        values.put(DatabaseHelper.COLUMN_RSVP, RSVP);

        long insertId = database.insert(DatabaseHelper.TABLE_NAME, null,
                values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
                allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Event newEvent = cursorToEvent(cursor);
        cursor.close();
        return newEvent;
    }

    public void deleteEvent(Event event) {
        long id = event.getId();
        System.out.println("Event deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<Event>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Event event = cursorToEvent(cursor);
            events.add(event);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return events;
    }

    private Event cursorToEvent(Cursor cursor) {
        Event event = new Event(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
        return event;
    }
}
