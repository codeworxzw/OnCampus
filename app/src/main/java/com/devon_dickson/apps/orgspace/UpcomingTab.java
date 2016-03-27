package com.devon_dickson.apps.orgspace;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.orm.SugarContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by ddickson1 on 1/02/2016.
 */


public class UpcomingTab extends Fragment {

    private SwipeRefreshLayout swipeContainer;
    private ProgressDialog pDialog;


    // URL to get contacts JSON
    private static String url = "http://198.199.81.7/api/v1/events";

    // JSON Node names
    private static final String TAG_EVENT = "event";
    private static final String TAG_EVENTID = "id";
    private static final String TAG_EVENTNAME = "name";
    private static final String TAG_LOCATION = "location";
    private static final String TAG_ORG = "org";
    private static final String TAG_START = "startTime";
    private static final String TAG_END = "endTime";
    private static final String TAG_DESC = "description";
    private static final String TAG_IMG = "image";
    private static final String TAG_FACE = "facebook";
    private ListView lv;
    private RecyclerView rv;
    private ActionBar ab;
    private LinearLayoutManager llm;
    private ViewPager viewPager;
    // contacts JSONArray
    JSONArray events = null;
    ArrayList<HashMap<String, String>> eventList;

    //@Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_upcoming_tab, container, false);
        SugarContext.init(getActivity());

        eventList = new ArrayList<HashMap<String, String>>();
        new GetContacts().execute();
        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        //lv = (ListView) view.findViewById(R.id.eventList);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetContacts().execute();
                Log.d("Pull to refresh", "Success!");
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        ab = getActivity().getActionBar();
        ab.setTitle("Events");
    }

    public class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("getContacts status", "preExecute");

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            updateEvents();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.d("getContacts status", "postExecute");
            List<Event> events = Event.listAll(Event.class);
            rv.setAdapter(new RVAdapter(events, new RVAdapter.OnItemClickListener() {
                @Override public void onItemClick(Event event) {
                   openEvent(event.getEventID());
                }
            }));

        }
    }

    public void openEvent(String eventID) {
        int ID = Integer.parseInt(eventID);

        Intent eventDetailsIntent = new Intent(getActivity(), EventDetailsActivity.class);

        eventDetailsIntent.putExtra("EventID", ID);

        getActivity().startActivity(eventDetailsIntent);
    }

    public void updateEvents() {
        Log.d("getContacts status", "Executing");
        Event.deleteAll(Event.class);
        // Creating service handler class instance
        ServiceHandler sh = new ServiceHandler();

        // Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

        Log.d("Response: ", "> " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONArray events = new JSONArray(jsonStr);

                // looping through All Contacts
                for (int i = 0; i < events.length(); i++) {
                    JSONObject c = events.getJSONObject(i);

                    String eventID = c.getString(TAG_EVENTID);
                    String name = c.getString(TAG_EVENTNAME);
                    String location = c.getString(TAG_LOCATION);
                    String desc = c.getString(TAG_DESC);
                    String org = c.getString(TAG_ORG);
                    String startTime = c.getString(TAG_START);
                    String endTime = c.getString(TAG_END);
                    String image = c.getString(TAG_IMG);
                    String facebook = c.getString(TAG_FACE);

                    SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, h:mm a", Locale.US);
                    SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                    try {
                        Date startDate = parserSDF.parse(startTime);
                        startTime = format.format(startDate);

                        Date endDate = parserSDF.parse(endTime);
                        endTime = format.format(endDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Event eventTableRow = new Event(eventID, name, location, desc, org, startTime, endTime, image, facebook);
                    eventTableRow.save();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
    }
}