package com.example.eventsearch.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventsearch.EventDetails;
import com.example.eventsearch.R;
import com.example.eventsearch.adapters.EventAdapter;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class EventListFragment extends Fragment {

    public ProgressBar progressBar;
    public TextView noEventsFoundText;
     private EventAdapter Eventnewadpt = null;

     private Context context = null;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);

        Bundle args = getArguments();
        Log.d("EventListFragment", "Arguments: " + args);
//        assert args != null;
        assert args != null;
        String keyword = args.getString("keyword");
        String category = args.getString("category");
        int distance = args.getInt("distance");
        int radius = Integer.parseInt(String.valueOf(distance));
        String location = args.getString("location");
        boolean isLocationEnabled = args.getBoolean("isLocationEnabled", false);

        Log.d("EventListFragment", "keyword: " + keyword);
        Log.d("EventListFragment", "category: " + category);
        Log.d("EventListFragment", "distance: " + distance);
        Log.d("EventListFragment", "location: " + location);
        Log.d("EventListFragment", "isLocationEnabled: " + isLocationEnabled);
        context= view.getContext();

        try {
            eventSearch(keyword, radius, category, location, isLocationEnabled, getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Get the child fragment manager
        FragmentManager fragmentManager1 = getChildFragmentManager();
        @SuppressLint("CommitTransaction") FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();

        // Add a button to the layout that switches to the FormFragment
        Button backButton;
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the FormFragment
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                fragmentTransaction.replace(R.id.fragment_container, new SearchFormFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
    public void eventSearch(String keyword, int distance, String category, String location, boolean isLocationEnabled, Context context) throws Exception {
        String API_KEY = "AIzaSyAGqX7DAlgi0sORju9bmDw6ztiFnpLyZR8";
        final double[] latitude = {0.0};
        final double[] longitude = {0.0};
        RequestQueue queue = Volley.newRequestQueue(context);

        if (isLocationEnabled) {
            String url = "https://ipinfo.io/?token=6242d3f8b6bfa2";
            JsonObjectRequest ipinfoRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String[] loc = response.getString("loc").split(",");
                                latitude[0] = Double.parseDouble(loc[0]);
                                longitude[0] = Double.parseDouble(loc[1]);
                                Log.d("EventListFragment", "latitude: " + latitude[0]);
                                Log.d("EventListFragment", "longitude: " + longitude[0]);

                                searchEvents(keyword, distance, category, latitude[0], longitude[0], context);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            queue.add(ipinfoRequest);
        } else {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=" + API_KEY;
            JsonObjectRequest geocodeRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                latitude[0] = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                longitude[0] = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                Log.d("EventListFragment", "latitude: " + latitude[0]);
                                Log.d("EventListFragment", "longitude: " + longitude[0]);
                                searchEvents(keyword, distance, category, latitude[0], longitude[0], context);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            queue.add(geocodeRequest);
        }
    }


    public void searchEvents(String keyword, int distance, String category, double latitude, double longitude, Context context ) {
        String url = "https://backend-382506.wm.r.appspot.com/event-search?keyword=" + keyword + "&radius="+ distance + "&category=" +category+ "&lat=" + latitude + "&long=" +longitude;
        Log.d("URL", url);
        RequestQueue requestQueue = Volley.newRequestQueue(context);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    progressBar = getActivity().findViewById(R.id.progress_bar);
                    noEventsFoundText = getActivity().findViewById(R.id.no_events_found);
                    JSONObject pageObject = response.getJSONObject("page");
                    int totalElements = pageObject.getInt("totalElements");
                    if (totalElements != 0) {
                        JSONArray eventsArray = response.getJSONObject("_embedded").getJSONArray("events");
                        if (eventsArray.length() == 0) {
                            progressBar.setVisibility(View.GONE);
                            noEventsFoundText.setVisibility(View.VISIBLE);
                        } else{
                            progressBar.setVisibility(View.GONE);
                            RecyclerView recyclerView = getView().findViewById(R.id.event_list);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            Eventnewadpt = new EventAdapter(context, eventsArray);

                            Eventnewadpt.setOnClickListener(new EventAdapter.OnClickListener() {
                                @Override
                                public void onClick(int position, JSONObject event_one) {
                                    Intent intent = new Intent(context, EventDetails.class);

                                    // put the event id and name into a Bundle
                                    Bundle bundle = new Bundle();
                                    bundle.putString("event_id", event_one.optString("id"));
                                    bundle.putString("event_name", event_one.optString("name"));
                                    intent.putExtras(bundle);


                                    // start the EventDetails activity
                                    context.startActivity(intent);

                                }
                            });

                            recyclerView.setAdapter(Eventnewadpt);
                            Log.d("EVENTS", eventsArray.toString());
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        noEventsFoundText.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Response code: " + error.networkResponse.statusCode);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }





}