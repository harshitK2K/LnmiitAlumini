package com.example.android.finalproject;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


public class MessageAdapter extends ArrayAdapter<EventList> {

    public MessageAdapter(Context context, int resource, List<EventList> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        EventList eventList = getItem(position);
        ImageView layoutImageView = convertView.findViewById(R.id.imageIconView);
        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
        TextView locationTextView = convertView.findViewById(R.id.locationTextView);
        TextView lastDateTextView = convertView.findViewById(R.id.belowDateTextView);
        if(!(eventList.getImageUri().isEmpty())){
            Log.v("MessageAdapter","YES");
            Glide.with(layoutImageView.getContext())
                    .load(eventList.getImageUri())
                    .into(layoutImageView);
        }
        else{
            layoutImageView.setVisibility(View.GONE);
        }
        titleTextView.setText(eventList.getTitle());
        descriptionTextView.setText(eventList.getDescription());
        locationTextView.setText(eventList.getEventLocation());
        lastDateTextView.setText(eventList.getStartDate() + "   " + eventList.getStartTime());
        return convertView;
    }
}
