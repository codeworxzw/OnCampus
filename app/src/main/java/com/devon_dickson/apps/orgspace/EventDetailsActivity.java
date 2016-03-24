package com.devon_dickson.apps.orgspace;

import android.app.Activity;
import android.content.Intent;
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

public class EventDetailsActivity extends Activity {
    public static int eventID;
    public static List<Event> eventList;
    public static Event event;
    public static TextView textHero;
    public static TextView textLocation;
    public static TextView textDate;
    public static ImageView imageBanner;
    public static String imageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        if(extras !=null)
        {
            eventID = intent.getIntExtra("EventID", 1);
        }

    Log.d("eventID", "" + eventID);
        event = Event.find(Event.class, "EVENT_ID="+eventID).get(0);
        textHero = (TextView) findViewById(R.id.textHero);
        textLocation = (TextView) findViewById(R.id.textLocation);
        textDate = (TextView) findViewById(R.id.textDate);
        imageBanner = (ImageView) findViewById(R.id.imgHero);

        textHero.setText(event.getName());
        textLocation.setText(event.getLocation());
        textDate.setText(event.getStartTime());

        imageURL = "http://devon-dickson.com/images/events/"+ event.getImage();
        Log.d("Image URL", imageURL);
        Picasso.with(this).load(imageURL).error(R.drawable.image_error).resize(720, 304).centerCrop().into(imageBanner);
    }
}
