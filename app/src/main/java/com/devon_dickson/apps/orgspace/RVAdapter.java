package com.devon_dickson.apps.orgspace;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by devon on 7/3/2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder> {

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        TextView eventName;
        TextView eventTime;
        TextView eventLocation;
        ImageView orgPhoto;
        private ClickListener clickListener;


        EventViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            eventName = (TextView)itemView.findViewById(R.id.event_name);
            eventTime = (TextView)itemView.findViewById(R.id.event_time);
            eventLocation = (TextView)itemView.findViewById(R.id.event_location);
            orgPhoto = (ImageView)itemView.findViewById(R.id.org_photo);
            itemView.setOnClickListener(this);
        }

        public interface ClickListener {

            /**
             * Called when the view is clicked.
             *
             * @param v view that is clicked
             * @param position of the clicked item
             */
            public void onClick(View v, int position, String title);

        }

        /* Setter for listener. */
        public void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View v) {
            clickListener.onClick(v, getPosition(), eventName.getText().toString());
        }
    }

    List<Event> events;

    RVAdapter(List<Event> events){
        this.events = events;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_card, viewGroup, false);
        EventViewHolder pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder EventViewHolder, final int i) {
        EventViewHolder.eventName.setText(events.get(i).name);
        EventViewHolder.eventTime.setText(events.get(i).time);
        EventViewHolder.eventLocation.setText(events.get(i).location);
        EventViewHolder.orgPhoto.setImageResource(events.get(i).orgPhoto);

        EventViewHolder.setClickListener(new EventViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int pos, String title) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}