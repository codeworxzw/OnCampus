package com.devon_dickson.apps.orgspace;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

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
 * Created by Edwin on 15/02/2015.
 */
public class Tab2 extends Fragment {
    //RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //ProgressDialog
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

    // events JSONArray
    JSONArray events = null;
    private List<Event> eventlist;
    private RecyclerView rv;

    // Hashmap for ListView
    //ArrayList<HashMap<String, String>> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_2,container,false);

        //eventList = new ArrayList<HashMap<String, String>>();


        // Calling async task to get json
        new GetEvents().execute();

        return v;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        /*******************************************************************************************
         * mAdapter = new MyAdapter(myDataset);
         * mRecyclerView.setAdapter(myAdapter);
         ******************************************************************************************/
    }

    private class GetEvents extends AsyncTask<Void, Void, Void> {

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
                    eventlist = new ArrayList<>();
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    events = jsonObj.getJSONArray(TAG_EVENT);

                    // looping through All Events
                    for (int i = 0; i < events.length(); i++) {
                        JSONObject c = events.getJSONObject(i);

                        String eventName = c.getString(TAG_EVENTNAME);
                        String location = c.getString(TAG_LOCATION);
                        String rain = c.getString(TAG_RAINLOCATION);
                        String org = c.getString(TAG_ORG);
                        String time = c.getString(TAG_TIME);

                        SimpleDateFormat format = new SimpleDateFormat("EEE, MMM d, h:mm a", Locale.US);
                        SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                        try {
                            Date date = parserSDF.parse(time);
                            time = format.format(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        String rsvp = c.getString(TAG_RSVP);

                        // tmp hashmap for single event
                        // HashMap<String, String> event = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        //event.put(TAG_EVENTNAME, eventName);
                        //event.put(TAG_LOCATION, location);
                        //event.put(TAG_RAINLOCATION, rain);
                        // event.put(TAG_ORG, org);
                        //event.put(TAG_TIME, time.toUpperCase());
                        //event.put(TAG_RSVP, rsvp);

                        // adding event to the eventList ArrayList<HashMap>
                        //eventList.add(event);


                        eventlist.add(new Event(eventName, time.toUpperCase(), location, R.drawable.pace));
                    }
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

            RVAdapter adapter = new RVAdapter(eventlist);
            mRecyclerView.setAdapter(adapter);
        }

    }
}