package com.devon_dickson.apps.orgspace;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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


public class Tab1 extends Fragment {

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
    private SimpleAdapter adapter;
    // contacts JSONArray
    JSONArray events = null;
    ArrayList<HashMap<String, String>> eventList;

    //@Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);
        SugarContext.init(getActivity());

        eventList = new ArrayList<HashMap<String, String>>();
        new GetContacts().execute();
        return v;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        //getActivity().getActionBar().show();
        lv = (ListView) view.findViewById(R.id.eventList);
        Log.d("Is this happening?", "YES");
        Log.d("Is eventList Empty?", "" + eventList.size());
        /*ListAdapter adapter = new SimpleAdapter(
                getActivity(), eventList,
                R.layout.event_row, new String[] { TAG_EVENTNAME, TAG_LOCATION, TAG_TIME, TAG_ORG }, new int[] { R.id.eventName,
                R.id.eventLocation, R.id.eventTime });

        lv.setAdapter(adapter);*/
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
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
    }

    protected void populateList() {
        eventList.clear();
        List<Event> events = Event.listAll(Event.class);
        for (Event e : events) {
            HashMap<String, String> event = new HashMap<String, String>();
            event.put(TAG_EVENTID, e.getEventID());
            event.put(TAG_EVENTNAME, e.getName());
            event.put(TAG_LOCATION, e.getLocation());
            event.put(TAG_DESC, e.getDescription());
            event.put(TAG_ORG, e.getOrg());
            event.put(TAG_START, e.getStartTime().toUpperCase());
            event.put(TAG_END, e.getEndTime().toUpperCase());
            event.put(TAG_IMG, e.getImage());
            event.put(TAG_FACE, e.getFacebook());

            eventList.add(event);
        }
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
            populateList();
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), eventList,
                    R.layout.event_row, new String[] { TAG_EVENTNAME, TAG_LOCATION, TAG_START, TAG_ORG }, new int[] { R.id.eventName,
                    R.id.eventLocation, R.id.eventTime });

            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Event ID", "" + eventList.get(position).get(TAG_EVENTID));
                    openEvent(eventList.get(position).get(TAG_EVENTID));
                }
            });
        }
    }
    public void openEvent(String eventID) {
        int ID = Integer.parseInt(eventID);
        // Create fragment and give it an argument specifying the article it should show
        FragmentEvent frag = new FragmentEvent();
        Bundle args = new Bundle();
        args.putInt("EventID", ID);
        frag.setArguments(args);
        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragManager.beginTransaction();

// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_place, frag);
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();
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
                //JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray events = new JSONArray(jsonStr);
                // Getting JSON Array node
                //events = jsonObj.getJSONArray("");

                // looping through All Contacts
                for (int i = 0; i < events.length(); i++) {
                    //JSONObject c = events.getJSONObject(i);
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