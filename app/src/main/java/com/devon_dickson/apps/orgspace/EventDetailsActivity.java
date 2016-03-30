package com.devon_dickson.apps.orgspace;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.GregorianCalendar;
import java.util.List;

public class EventDetailsActivity extends AppCompatActivity {
    public static int eventID;
    public static List<Event> eventList;
    public static Event event;
    public static TextView textHero;
    public static TextView textLocation;
    public static TextView textDate;
    public static TextView textOrg;
    public static ImageView imageBanner;
    public static String imageURL;
    public static TextView textDescription;
    public static CardView cardDate;

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
        //textHero = (TextView) findViewById(R.id.textHero);
        textLocation = (TextView) findViewById(R.id.textLocation);
        textDate = (TextView) findViewById(R.id.textDate);
        textOrg = (TextView) findViewById(R.id.textOrg);
        imageBanner = (ImageView) findViewById(R.id.backdrop);
        textDescription = (TextView) findViewById(R.id.description);
        cardDate = (CardView) findViewById(R.id.cardDate);


        CollapsingToolbarLayout tb = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        tb.setTitle(event.getName());
        textLocation.setText(event.getLocation());
        textDate.setText(event.getStartTime());
        textDescription.setText(event.getDescription());
        textOrg.setText(event.getOrg());

        imageURL = "http://devon-dickson.com/images/events/"+ event.getImage();
        Log.d("Image URL", imageURL);
        Picasso.with(this).load(imageURL).error(R.drawable.image_error).resize(720, 304).centerCrop().into(imageBanner);

        cardDate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                //calIntent.setType("vnd.android.cursor.item/event");
                calIntent.setData(CalendarContract.Events.CONTENT_URI);
                calIntent.putExtra(CalendarContract.Events.TITLE, event.getName());
                calIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLocation());
                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());

                //TODO: Pass exact start and end times to calendar app
                GregorianCalendar calDate = new GregorianCalendar(2016, 7, 15);
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                        calDate.getTimeInMillis());
                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                        calDate.getTimeInMillis());

                //TODO: Saving event in Calendar App should return user to this activity
                startActivityForResult(calIntent, 1);
            }
        });
    }
}

