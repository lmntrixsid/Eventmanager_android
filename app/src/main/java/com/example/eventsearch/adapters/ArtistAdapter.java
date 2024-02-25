package com.example.eventsearch.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventsearch.R;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder> {

    private final JSONArray artistArray;

    public ArtistAdapter(JSONArray artistArray) {
        this.artistArray = artistArray;
    }

    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artistrow, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        try {
            JSONObject artistObject = artistArray.getJSONObject(position);
            holder.artistNameTextView.setText(artistObject.getJSONObject("artist").getString("name"));
//            holder.artistFollowersTextView.setText(artistObject.getJSONObject("artist").getJSONObject("followers").getString("total"));
            int followers = Integer.parseInt(artistObject.getJSONObject("artist").getJSONObject("followers").getString("total"));
            String formattedFollowers;

            if (followers >= 1000000) {
                formattedFollowers = String.format(Locale.getDefault(), "%.1fM", (float) followers / 1000000);
            } else if (followers >= 1000) {
                formattedFollowers = String.format(Locale.getDefault(), "%.1fK", (float) followers / 1000);
            } else {
                formattedFollowers = String.valueOf(followers);
            }

            holder.artistFollowersTextView.setText(formattedFollowers+" Followers");
            holder.spotifyLinkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String url = artistObject.getJSONObject("artist").getJSONObject("external_urls").getString("spotify");
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        v.getContext().startActivity(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            holder.progressbartext.setText(artistObject.getJSONObject("artist").getString("popularity"));
            holder.progressBar.setProgress(Integer.parseInt(artistObject.getJSONObject("artist").getString("popularity")));
            Glide.with(holder.itemView.getContext()).load(artistObject.getJSONObject("artist").getJSONArray("images").getJSONObject(0).getString("url")).into(holder.artistImage);
            Glide.with(holder.itemView.getContext()).load(artistObject.getJSONArray("albums").getJSONObject(0).getJSONArray("images").getJSONObject(0).getString("url")).into(holder.artistalbum1);
            Glide.with(holder.itemView.getContext()).load(artistObject.getJSONArray("albums").getJSONObject(1).getJSONArray("images").getJSONObject(0).getString("url")).into(holder.artistalbum2);
            Glide.with(holder.itemView.getContext()).load(artistObject.getJSONArray("albums").getJSONObject(2).getJSONArray("images").getJSONObject(0).getString("url")).into(holder.artistalbum3);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return artistArray.length();
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {
        public TextView artistNameTextView;
        public TextView spotifyLinkTextView;
        public TextView artistFollowersTextView;




        public ImageView artistImage;
        public ImageView artistalbum1;
        public ImageView artistalbum2;
        public ImageView artistalbum3;

        public TextView progressbartext;

        CircularProgressIndicator progressBar;

        public


        ArtistViewHolder(View itemView) {
            super(itemView);
            artistNameTextView = itemView.findViewById(R.id.artist_name);
            spotifyLinkTextView = itemView.findViewById(R.id.spotifylink);
            artistFollowersTextView = itemView.findViewById(R.id.afollowers);
            artistImage = itemView.findViewById(R.id.artist_image);
            artistalbum1 = itemView.findViewById(R.id.album1img);
            artistalbum2 = itemView.findViewById(R.id.album2img);
            artistalbum3 = itemView.findViewById(R.id.albumimg3);
            progressbartext = itemView.findViewById(R.id.progressbartext);
            progressBar = itemView.findViewById(R.id.progressBar);


            // Get references to other views in the artist row layout
            // ...
        }
    }
}
