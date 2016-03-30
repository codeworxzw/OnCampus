package com.devon_dickson.apps.orgspace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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


public class UpcomingTab extends Fragment implements SearchView.OnQueryTextListener{

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
    private RVAdapter mAdapter;
    // contacts JSONArray
    ArrayList<Event> eventsArray;
    List<Event> events;
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
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
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

        ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
        ab.setTitle("Events");

        mAdapter = new RVAdapter(events, new RVAdapter.OnItemClickListener() {
            @Override public void onItemClick(Event event) {
                openEvent(event.getEventID());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<Event> filteredEventList = filter(events, query);
        mAdapter.animateTo(filteredEventList);
        rv.scrollToPosition(0);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<Event> filter(List<Event> events, String query) {
        query = query.toLowerCase();

        final List<Event> filteredEventList = new ArrayList<>();
        for (Event event : events) {
            final String text = event.getName().toLowerCase();
            if (text.contains(query)) {
                filteredEventList.add(event);
            }
        }
        return filteredEventList;
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
            events = Event.listAll(Event.class);
            eventsArray = new ArrayList<>();

            for (Event e : events) {
                eventsArray.add(e);
            }

            rv.setAdapter(mAdapter);
            mAdapter = new RVAdapter(events, new RVAdapter.OnItemClickListener() {
                @Override public void onItemClick(Event event) {
                    openEvent(event.getEventID());
                }
            });
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