package com.example.eventsearch.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventsearch.EventDetailsViewModel;
import com.example.eventsearch.R;
import com.example.eventsearch.adapters.ArtistAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArtistFragment extends Fragment {

    private RecyclerView artistRecyclerView;
    private ArtistAdapter artistAdapter;

    private EventDetailsViewModel viewModel;
    private TextView noEventsTextView;
    private ProgressBar artistProgressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        artistProgressBar = view.findViewById(R.id.progress_bar);

        // Get the ViewModel from the parent activity
        noEventsTextView = view.findViewById(R.id.no_events_found);
        artistRecyclerView = view.findViewById(R.id.artist_list);
        viewModel = new ViewModelProvider(requireActivity()).get(EventDetailsViewModel.class);

        String artistName = viewModel.getArtistName();
        System.out.println("Artist Name: " + artistName + " On Artist Fragment");
        if(!artistName.equals("")) {
            fetchartistdetails(artistName);
        }
        else {
            noEventsTextView.setVisibility(View.VISIBLE);
            artistProgressBar.setVisibility(View.GONE);
        }

    }
    private void fetchartistdetails(String artist) {
//
        String url = "https://backend-382506.wm.r.appspot.com//search-artists?keyword=" + artist;
        System.out.println("URL: " + url + " On Artist Fragment");
        artistProgressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());

        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, url, new Response.Listener() {
            @Override

                public void onResponse(Object response) {
                    try {
                        JSONArray artists = new JSONArray(response.toString());
                        Log.d("ArtistFragment", "Artists: " + artists.toString());



                        artistRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
                        artistAdapter = new ArtistAdapter(artists);
                        artistRecyclerView.setAdapter(artistAdapter);

                        artistProgressBar.setVisibility(View.GONE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    artistProgressBar.setVisibility(View.GONE);
                }
            });

        queue.add(jsonObjectRequest);
        }
    }

