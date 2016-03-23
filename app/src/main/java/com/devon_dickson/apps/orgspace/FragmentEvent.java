package com.devon_dickson.apps.orgspace;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FragmentEvent extends Fragment {
    public static int eventID;
    public static List<Event> eventList;
    public static Event event;
    public static TextView textHero;
    public static TextView textLocation;
    public static TextView textDate;
    public static ImageView imageBanner;
    public static String imageURL;


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
        //getActivity().getActionBar().hide();
        event = Event.find(Event.class, "EVENT_ID="+eventID).get(0);
        textHero = (TextView)getView().findViewById(R.id.textHero);
        textLocation = (TextView)getView().findViewById(R.id.textLocation);
        textDate = (TextView)getView().findViewById(R.id.textDate);
        imageBanner = (ImageView)getView().findViewById(R.id.imgHero);

        textHero.setText(event.getName());
        textLocation.setText(event.getLocation());
        textDate.setText(event.getStartTime());

        imageURL = "http://devon-dickson.com/images/events/"+ event.getImage();
        Log.d("Image URL", imageURL);
        Picasso.with(getActivity()).load(imageURL).error(R.drawable.image_error).resize(720, 304).centerCrop().into(imageBanner);

    }

}
