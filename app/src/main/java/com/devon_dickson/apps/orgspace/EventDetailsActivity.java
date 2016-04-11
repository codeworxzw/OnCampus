package com.devon_dickson.apps.orgspace;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.GregorianCalendar;
import java.util.List;

public class EventDetailsActivity extends SwipeActivity {
    public static int eventID;
    public static Event event;
    public static TextView textLocation;
    public static TextView textDate;
    public static ImageView imageBanner;
    public static String orgURL;
    public static String bannerURL;
    public static TextView textDescription;
    public static TextView textOrg;
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
        textLocation = (TextView) findViewById(R.id.textLocation);
        textDate = (TextView) findViewById(R.id.textDate);
        imageBanner = (ImageView) findViewById(R.id.backdrop);
        textDescription = (TextView) findViewById(R.id.description);
        cardDate = (CardView) findViewById(R.id.cardDate);
        textOrg = (TextView) findViewById(R.id.textOrg);


        CollapsingToolbarLayout tb = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        tb.setTitle(event.getName());
        textLocation.setText(event.getLocation());
        textDate.setText(event.getStartTimeString());
        textDescription.setText(event.getDescription());
        textOrg.setText(event.getOrg());

        bannerURL = "http://devon-dickson.com/images/events/"+ event.getImage();
        Picasso.with(this).load(bannerURL).error(R.drawable.image_error).resize(720, 304).centerCrop().into(imageBanner);

        cardDate.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                Intent calIntent = new Intent(Intent.ACTION_INSERT);
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

    //Return to previous event, or eventList if this was the first event opened.
    @Override
    protected void onSwipeRight() {
        finish();
        overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }

    //Move to next Event in the database
    //TODO: Change method for deciding what the next acticity should be
    //      Right now, the database is always refreshed with events that haven't yet occurred, in chrono
    //      order. But if I ever get filtering and searching and ordering to work in the Main Activity,
    //      Then eventID+1 will load up an event that should have been filtered out.
    @Override
    protected void onSwipeLeft() {
        try {
            long end = event.getEndTime();
            String newID = Event.find(Event.class, "END_TIME>", end + "", null, "END_TIME asc").get(0).getEventID();
            Intent eventDetailsIntent = new Intent(this, EventDetailsActivity.class);

            eventDetailsIntent.putExtra("EventID", (Integer.parseInt(newID)));

            this.startActivity(eventDetailsIntent);
        }catch (IndexOutOfBoundsException e) {
            Toast.makeText(this, "No more events", Toast.LENGTH_SHORT);
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        super.dispatchTouchEvent(ev);
        return gestureDetector.onTouchEvent(ev);
    }


}

