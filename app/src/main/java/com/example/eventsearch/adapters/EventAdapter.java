package com.example.eventsearch.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventsearch.EventDetails;
import com.example.eventsearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private JSONArray mEventList;
    private Context mContext;

    private OnClickListener onClickListener;

    public EventAdapter(Context context, JSONArray eventList) {
        mContext = context;
        mEventList = eventList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mEventImage;
        public TextView mEventName;
        public TextView mEventDate;
        public TextView mEventVenue;
        public TextView mEventTime;
        public TextView mEventCategory;
        public Button mMyButton;
        public Button mMyButton2;

        public ViewHolder(View itemView) {
            super(itemView);

            mEventImage = itemView.findViewById(R.id.event_image);
            mEventName = itemView.findViewById(R.id.event_name);
            mEventDate = itemView.findViewById(R.id.event_date);
            mEventVenue = itemView.findViewById(R.id.event_venue);
            mEventTime = itemView.findViewById(R.id.event_time);
            mEventCategory = itemView.findViewById(R.id.event_category);
            mMyButton = itemView.findViewById(R.id.event_not_selected);
            mMyButton2 = itemView.findViewById(R.id.event_is_selected);
        }
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String event_id = "";

        try {
            JSONObject event = mEventList.getJSONObject(position);
            // Set event data to views
            event_id = event.optString("id", "");
            Log.d("InAdapter", "onBindViewHolder: position: " + position + " event_id: " + event_id);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    onClickListener.onClick(position, event);
                    Intent intent = new Intent(holder.itemView.getContext(), EventDetails.class);

                    // put the event id and name into a Bundle
                    Bundle bundle = new Bundle();
                    bundle.putString("event_id", event.optString("id"));
                    Log.d("InAdapter", "onBindViewHolder: id: " + event.optString("id"));
                    bundle.putString("event_name", event.optString("name"));
                    bundle.putString("whole_event",event.toString());
                    intent.putExtras(bundle);


                    // start the EventDetails activity
                    holder.itemView.getContext().startActivity(intent);
                }
            });
            holder.mEventName.setText(event.optString("name", ""));
            holder.mEventName.setSelected(true);
            holder.mEventName.setMovementMethod(new ScrollingMovementMethod());
            String eventDate = "";
            String eventTime = "";
            JSONObject dates = event.optJSONObject("dates");
            if (dates != null) {
                JSONObject start = dates.optJSONObject("start");
                if (start != null) {
                    eventDate = start.optString("localDate", "");
                    eventTime = start.optString("localTime", "");
                }
            }
            String dateTime = eventDate;
            holder.mEventDate.setText(dateTime);
            JSONObject venue = Objects.requireNonNull(Objects.requireNonNull(event.optJSONObject("_embedded")).optJSONArray("venues")).optJSONObject(0);
            holder.mEventVenue.setText(venue.optString("name", ""));
            holder.mEventVenue.setSelected(true);
            holder.mEventVenue.setMovementMethod(new ScrollingMovementMethod());
            holder.mEventTime.setText(eventTime);
            JSONObject classification = Objects.requireNonNull(event.optJSONArray("classifications")).optJSONObject(0);
            holder.mEventCategory.setText(Objects.requireNonNull(classification.optJSONObject("segment")).optString("name", ""));

            String imageUrl = Objects.requireNonNull(event.optJSONArray("images")).optJSONObject(0).optString("url", "");
            int radius = 12;
            if (!imageUrl.isEmpty()) {
                Glide.with(mContext)
                        .load(imageUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(radius)))
                        .centerCrop()
                        .into(holder.mEventImage);
            } else {
                holder.mEventImage.setImageDrawable(null);
            }

            String finalEvent_id = event_id;
            String EventName = holder.mEventName.getText().toString();
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
            final boolean[] is_in_favorites = {sharedPreferences.contains(finalEvent_id)};
            if (is_in_favorites[0]) {
                //TODO: make button color white
                holder.mMyButton.setVisibility(View.GONE);
                holder.mMyButton2.setVisibility(View.VISIBLE);
            } else {
                holder.mMyButton.setVisibility(View.VISIBLE);
                holder.mMyButton2.setVisibility(View.GONE);
            }
            View.OnClickListener buttonClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences = mContext.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (!is_in_favorites[0]) {
                        // Add to favorites
                        //TODO: make button color white
                        editor.putString(finalEvent_id, event.toString());
                        editor.commit();
                        is_in_favorites[0] = true;
                        Snackbar.make(view, event.optString("name", "") + " is added to favorites", Snackbar.LENGTH_SHORT).show();
                    } else {
                        // Remove from favorites
                        //TODO: make button color border
                        editor.remove(finalEvent_id);
                        editor.commit();
                        is_in_favorites[0] = false;
                        Snackbar.make(view, event.optString("name", "") + " is removed from favorites", Snackbar.LENGTH_SHORT).show();
                    }

                    // Update the visibility of buttons
                    holder.mMyButton.setVisibility(is_in_favorites[0] ? View.GONE : View.VISIBLE);
                    holder.mMyButton2.setVisibility(is_in_favorites[0] ? View.VISIBLE : View.GONE);
                }
            };

            holder.mMyButton.setOnClickListener(buttonClickListener);
            holder.mMyButton2.setOnClickListener(buttonClickListener);


        } catch (JSONException e) {
            // Set all views to empty values and clear image
            holder.mEventName.setText("");
            holder.mEventDate.setText("");
            holder.mEventVenue.setText("");
            holder.mEventTime.setText("");
            holder.mEventCategory.setText("");
            holder.mEventImage.setImageDrawable(null);
        }

        // Set click listener for button


//        String finalEvent_id = event_id;
//        String EventName = holder.mEventName.getText().toString();
//        holder.mMyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SharedPreferences sharedPreferences = mContext.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
//                @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
//                JSONObject jsonObject = new JSONObject();
//
//                editor.putString("Event_name", );
//
//
//
//
//                // Handle button click event
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mEventList.length();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface OnClickListener {
        void onClick(int position, JSONObject event);
    }
}


