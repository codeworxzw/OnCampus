package com.devon_dickson.apps.orgspace;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class Tab1 extends Fragment{


    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "http://www.devon-dickson.com/event.php";

    // JSON Node names
    private static final String TAG_EVENT = "event";
    private static final String TAG_EVENTNAME = "Event Name";
    private static final String TAG_LOCATION = "Location";
    private static final String TAG_RAINLOCATION = "Rain Location";
    private static final String TAG_ORG = "Org";
    private static final String TAG_TIME = "Time";
    private static final String TAG_RSVP = "RSVP";
    private ListView lv;
    //private eve eventTable;
    private SimpleAdapter adapter;
    //private List<Event> values;
    // contacts JSONArray
    JSONArray events = null;
    //private ArrayList<Event> listOfPenises = new ArrayList<Event>();
    // Hashmap for ListView
    ArrayList<HashMap<String, String>> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_1, container, false);
        SugarContext.init(getActivity());

        //new eventDownload().execute();
        eventList = new ArrayList<HashMap<String, String>>();


        //lv = (ListView) getView().findViewById(R.id.eventList);
        // Calling async task to get json
        new GetContacts().execute();

        return v;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        lv = (ListView) getView().findViewById(R.id.eventList);


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

                        String name = c.getString(TAG_EVENTNAME);
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


                        Event eventTableRow = new Event(name, location, rain, org, time, rsvp);
                        eventTableRow.save();
                        //listOfPenises.add(new Event(eventName, location, rain, org, time, rsvp));
                        // tmp hashmap for single contact

                    }

                    //TODO
                    //Put the events in listOfPenises into the internal SQL database
                    //List<Event> eventses = Event.listAll(Event.class);
                    //Log.d("eventsssss at index 0: ", eventses.get(0).getName());


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

            updateList();

            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), eventList,
                    R.layout.event_row, new String[] { TAG_EVENTNAME, TAG_LOCATION, TAG_TIME, TAG_ORG }, new int[] { R.id.eventName,
                    R.id.eventLocation, R.id.eventTime });

            lv.setAdapter(adapter);
        }

        protected void updateList() {
            HashMap<String, String> event = new HashMap<String, String>();

            List<Event> events = Event.listAll(Event.class);
            for (Event e : events) {
                event.put(TAG_EVENTNAME, e.getName());
                event.put(TAG_LOCATION, e.getLocation());
                event.put(TAG_RAINLOCATION, e.getRain());
                event.put(TAG_ORG, e.getOrg());
                event.put(TAG_TIME, e.getTime().toUpperCase());
                event.put(TAG_RSVP, e.getRsvp());

                eventList.add(event);
            }
        }

    }

}