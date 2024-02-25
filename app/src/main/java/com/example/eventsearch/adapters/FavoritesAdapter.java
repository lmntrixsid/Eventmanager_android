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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.eventsearch.EventDetails;
import com.example.eventsearch.FavoriteItem;
import com.example.eventsearch.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private JSONArray favoriteItems;

    public FavoritesAdapter(JSONArray favoriteItems, Context context) {
        this.favoriteItems = favoriteItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favouritesrow, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String event_id = "";

        try {
            JSONObject event = favoriteItems.getJSONObject(position);
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
                    intent.putExtras(bundle);


                    // start the EventDetails activity
                    holder.itemView.getContext().startActivity(intent);
                }
            });
            holder.eventName.setText(event.optString("name", ""));
            holder.eventName.setSelected(true);
            holder.eventName.setMovementMethod(new ScrollingMovementMethod());
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
            holder.eventDate.setText(dateTime);
            JSONObject venue = Objects.requireNonNull(Objects.requireNonNull(event.optJSONObject("_embedded")).optJSONArray("venues")).optJSONObject(0);
            holder.eventVenue.setText(venue.optString("name", ""));
            holder.eventVenue.setSelected(true);
            holder.eventVenue.setMovementMethod(new ScrollingMovementMethod());
            holder.eventTime.setText(eventTime);
            JSONObject classification = Objects.requireNonNull(event.optJSONArray("classifications")).optJSONObject(0);
            holder.eventCategory.setText(Objects.requireNonNull(classification.optJSONObject("segment")).optString("name", ""));

            String imageUrl = Objects.requireNonNull(event.optJSONArray("images")).optJSONObject(0).optString("url", "");
            int radius = 12;
            if (!imageUrl.isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(radius)))
                        .centerCrop()
                        .into(holder.eventImage);
            } else {
                holder.eventImage.setImageDrawable(null);
            }

            String finalEvent_id = event_id;
            String EventName = holder.eventName.getText().toString();
            Boolean is_in_favorites=true;
//            SharedPreferences sharedPreferences = holder.itemView.getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
//            is_in_favorites = sharedPreferences.getAll().containsKey(finalEvent_id);
            if(is_in_favorites) {
                //TODO: make button color white

                holder.favButtonNonSelected.setVisibility(View.GONE);
                holder.favButtonSelected.setVisibility(View.VISIBLE);
            } else {
                //TODO: make button color border

                holder.favButtonSelected.setVisibility(View.GONE);
                holder.favButtonNonSelected.setVisibility(View.VISIBLE);
            }
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!is_in_favorites) {
                        //TODO: make button color white
                        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                        JSONObject jsonObject = new JSONObject();

                        editor.putString(finalEvent_id, event.toString());
                        editor.commit();
                        Snackbar.make(view, event.optString("name", "")+" is added to favorites", Snackbar.LENGTH_SHORT).show();
                    } else {
                        //TODO: make button color border
                        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
                        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(finalEvent_id);
                        editor.commit();
                        favoriteItems.remove(position);
                        notifyItemRemoved(position);
                        Snackbar.make(view, event.optString("name", "")+" is removed from favorites", Snackbar.LENGTH_SHORT).show();
                    }


                    // Handle button click event
                }
            };

            holder.favButtonNonSelected.setOnClickListener(listener);
            holder.favButtonSelected.setOnClickListener(listener);



        } catch (JSONException e) {
            // Set all views to empty values and clear image
            holder.eventName.setText("");
            holder.eventDate.setText("");
            holder.eventVenue.setText("");
            holder.eventTime.setText("");
            holder.eventCategory.setText("");
            holder.eventImage.setImageDrawable(null);
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
        return favoriteItems.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventName, eventDate, eventVenue, eventTime, eventCategory;
        Button favButtonNonSelected, favButtonSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.event_image);
            eventName = itemView.findViewById(R.id.event_name);
            eventDate = itemView.findViewById(R.id.event_date);
            eventVenue = itemView.findViewById(R.id.event_venue);
            eventTime = itemView.findViewById(R.id.event_time);
            eventCategory = itemView.findViewById(R.id.event_category);
            favButtonNonSelected = itemView.findViewById(R.id.fav_button_nonselected);
            favButtonSelected = itemView.findViewById(R.id.fav_button_selected);
        }
    }
}
