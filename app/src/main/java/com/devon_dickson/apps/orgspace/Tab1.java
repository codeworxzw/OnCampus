package com.devon_dickson.apps.orgspace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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


    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://www.devon-dickson.com/event.php";

    // JSON Node names
    private static final String TAG_EVENT = "event";
    private static final String TAG_ID = "Event ID";
    private static final String TAG_EVENTNAME = "Event Name";
    private static final String TAG_LOCATION = "Location";
    private static final String TAG_RAINLOCATION = "Rain Location";
    private static final String TAG_ORG = "Org";
    private static final String TAG_TIME = "Time";
    private static final String TAG_RSVP = "RSVP";
    private ListView lv;
    private SimpleAdapter adapter;
    private List<Event> values;
    private ArrayList<HashMap<String, String>> valuesMap;
    private EventsDataSource datasource;
    // contacts JSONArray
    JSONArray events = null;
    private ArrayList<Event> listOfEvents = new ArrayList<Event>();
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);

        //new eventDownload().execute();
        eventList = new ArrayList<HashMap<String, String>>();


        //lv = (ListView) getView().findViewById(R.id.eventList);
        // Calling async task to get json
        new GetContacts().execute();

        return v;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        lv = (ListView) getView().findViewById(R.id.eventList);
        // use the SimpleCursorAdapter to show the
        // elements in a ListView

        downloadEvents(lv);


        if(valuesMap==null) {
            valuesMap.add(0, new HashMap<String, String>());
        }

        Log.d("Values map is: ", "" + valuesMap.get(0));
        adapter = new SimpleAdapter(
                getActivity(), valuesMap,
                R.layout.event_row, new String[] { TAG_EVENTNAME, TAG_LOCATION, TAG_TIME, TAG_ORG }, new int[] { R.id.eventName,
                R.id.eventLocation, R.id.eventTime });
        lv.setAdapter(adapter);
    }

    private ArrayList<HashMap<String, String>> downloadEvents(ListView lv) {
        //For SQLite DB*******************************
        datasource = new EventsDataSource(getActivity());
        datasource.open();
        values = datasource.getAllEvents();
        if(values!=null) {
            for(int i = 0; i < values.size(); i++) {
                HashMap<String, String> eventMap = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                eventMap.put(TAG_ID, Integer.toString(values.get(i).getId()));
                eventMap.put(TAG_EVENTNAME, values.get(i).getName());
                eventMap.put(TAG_LOCATION, values.get(i).getLocation());
                eventMap.put(TAG_RAINLOCATION, values.get(i).getRain());
                eventMap.put(TAG_ORG, values.get(i).getOrg());
                eventMap.put(TAG_TIME, values.get(i).getTime().toUpperCase());
                eventMap.put(TAG_RSVP, values.get(i).getRsvp());

                // adding contact to contact list
                valuesMap.add(eventMap);
            }
        }
        //******************************************
        return valuesMap;
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    events = jsonObj.getJSONArray(TAG_EVENT);

                    // looping through All Contacts
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject c = events.getJSONObject(i);

                        int eventID = c.getInt(TAG_ID);
                        String eventName = c.getString(TAG_EVENTNAME);
                        String location = c.getString(TAG_LOCATION);
                        String rain = c.getString(TAG_RAINLOCATION);
                        String org = c.getString(TAG_ORG);
                        String time = c.getString(TAG_TIME);
                        String rsvp = c.getString(TAG_RSVP);

                        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, h:mm a", Locale.US);
                        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        try {
                            Date date = parserSDF.parse(time);
                            time = format.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        listOfEvents.add(new Event(eventID, eventName, location, rain, org, time, rsvp));
                        // tmp hashmap for single contact
                        HashMap<String, String> event = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        event.put(TAG_EVENTNAME, eventName);
                        event.put(TAG_LOCATION, location);
                        event.put(TAG_RAINLOCATION, rain);
                        event.put(TAG_ORG, org);
                        event.put(TAG_TIME, time.toUpperCase());
                        event.put(TAG_RSVP, rsvp);

                        // adding contact to contact list
                        eventList.add(event);
                        Event eventTest = datasource.addEvent(eventID, eventName, location, rain, org, time.toUpperCase(), rsvp);
                        adapter.notifyDataSetChanged();
                    }

                    //TODO
                    //Put the events in listOfEvents into the internal SQL database



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), eventList,
                    R.layout.event_row, new String[] { TAG_EVENTNAME, TAG_LOCATION, TAG_TIME, TAG_ORG }, new int[] { R.id.eventName,
                    R.id.eventLocation, R.id.eventTime });

            lv.setAdapter(adapter);
        }

    }

}