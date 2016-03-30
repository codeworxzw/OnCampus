package com.devon_dickson.apps.orgspace;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by ddickson1 on 3/26/2016.
 *
 * This is an adapter for the RecyclerView found in the UpcomingTab Class/Fragment
 * It supplies fields and methods to insert data programmatically into the appropriate views in the layout
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder>{

    public static class EventViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView eventName;
        TextView eventTime;
        TextView eventLocation;
        TextView eventOrg;
        ImageView eventImage;
        int eventID;

        public EventViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cardEvent);
            eventName = (TextView)itemView.findViewById(R.id.listName);
            eventTime = (TextView)itemView.findViewById(R.id.listDate);
            eventLocation = (TextView)itemView.findViewById(R.id.listLocation);
            eventImage = (ImageView)itemView.findViewById(R.id.listImage);
        }

        public void bind(final Event event, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(event);
                }
            });
        }
    }

    public void animateTo(List<Event> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Event> newModels) {
        for (int i = events.size() - 1; i >= 0; i--) {
            final Event model = events.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Event> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Event model = newModels.get(i);
            if (!events.contains(model)) {
                addItem(i, model);
            }
        }
    }


    private void applyAndAnimateMovedItems(List<Event> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Event model = newModels.get(toPosition);
            final int fromPosition = events.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Event removeItem(int position) {
        final Event model = events.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Event model) {
        events.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Event model = events.remove(fromPosition);
        events.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    public interface OnItemClickListener {
        void onItemClick(Event event);
    }

    List<Event> events;
    EventViewHolder pvh;
    OnItemClickListener listener;

    RVAdapter(List<Event> events, OnItemClickListener listener){
        this.events = events;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        try{
            return events.size();
        }catch(Exception e) {
            return 0;
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_row, viewGroup, false);
        pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        eventViewHolder.eventName.setText(events.get(i).getName());
        eventViewHolder.eventTime.setText(events.get(i).getStartTime());
        eventViewHolder.eventLocation.setText(events.get(i).getLocation());
        eventViewHolder.bind(events.get(i), listener);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}