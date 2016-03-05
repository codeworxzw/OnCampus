package com.devon_dickson.apps.orgspace;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FragmentEvent extends Fragment {
    public static int eventID;
    public static List<Event> eventList;
    public static Event event;
    public static TextView textHero;
    public static TextView textLocation;
    public static TextView textDate;


    public FragmentEvent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventID = getArguments().getInt("EventID");
            //eventList = Event.find(Event.class, "eventID=?", "" + eventID);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_event, container, false);


    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Log.d("Event Name", event.getName());
        event = Event.find(Event.class, "EVENT_ID="+eventID).get(0);
        textHero = (TextView)getView().findViewById(R.id.textHero);
        textLocation = (TextView)getView().findViewById(R.id.textLocation);
        textDate = (TextView)getView().findViewById(R.id.textDate);

        textHero.setText(event.getName());
        textLocation.setText(event.getLocation());
        textDate.setText(event.getTime());
    }
}
