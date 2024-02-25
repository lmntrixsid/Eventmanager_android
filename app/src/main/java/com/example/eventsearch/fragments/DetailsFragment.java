package com.example.eventsearch.fragments;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.eventsearch.EventDetailsViewModel;
import com.example.eventsearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Locale;


public class DetailsFragment extends Fragment {

    private EventDetailsViewModel viewModel;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new ViewModelProvider(requireActivity()).get(EventDetailsViewModel.class);

        System.out.println("Details Fragment");

        String eventId = viewModel.getEventId();
        fetcheventdetails(eventId);
        System.out.println("Event ID: " + eventId);


        return inflater.inflate(R.layout.fragment_details, container, false);
    }
    private void fetcheventdetails(String eventId) {
        String url = "https://backend-382506.wm.r.appspot.com/event-details?event_id=" + eventId;
        Log.d(url, "fetcheventdetails: ");
        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Process the JSON response
                    String eventName = response.getString("name");
                    System.out.println("Event Name: " + eventName);

                    String date = getDate(response);
                    System.out.println("Date: " + date);
                    String time = getTime(response);
                    System.out.println("Time: " + time);
                    String artist = getArtist(response);
                    System.out.println("Artist: " + artist);
                    String spotify_artist = getSpotifyArtist(response);
                    viewModel.setArtistName(spotify_artist);


                    String venue = getVenue(response);
                    System.out.println("Venue: " + venue);
                    viewModel.setVenueName(venue);
                  
                    String genre = getGenre(response);
                    System.out.println("Genre: " + genre);
                    String price = getPrice(response);
                    System.out.println("Price: " + price);
                    String TicketStatus = getTicketStatus(response);
                    System.out.println("Ticket Status: " + TicketStatus);
                    String eventUrl = getEventUrl(response);
                    System.out.println("Event URL: " + eventUrl);
                    String venueMap = getVenueMap(response);
                    System.out.println("Venue Map: " + venueMap);

                    TextView artistTextView = requireView().findViewById(R.id.edit_artist);  // Replace `R.id.edit_artist` with the actual ID of your TextView
                    artistTextView.setText(artist);
                    artistTextView.setSelected(true);
                    artistTextView.setMovementMethod(new ScrollingMovementMethod());

                    TextView dateTextView = requireView().findViewById(R.id.edit_date);  // Replace `R.id.edit_date` with the actual ID of your TextView
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        Date inputDate = inputFormat.parse(date);

                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                        SimpleDateFormat monthFormat = new SimpleDateFormat("MMM", Locale.getDefault());
                        String monthAbbreviation = monthFormat.format(inputDate);

                        String formattedDate = outputFormat.format(inputDate).replaceFirst("MMM", monthAbbreviation);

                        dateTextView.setText(formattedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (java.text.ParseException e) {
                        throw new RuntimeException(e);
                    }

                    TextView timeTextView = requireView().findViewById(R.id.edit_time);  // Replace `R.id.edit_time` with the actual ID of your TextView
                    timeTextView.setText(time);

                    TextView venueTextView = requireView().findViewById(R.id.edit_venue);  // Replace `R.id.edit_venue` with the actual ID of your TextView
                    venueTextView.setText(venue);
                    venueTextView.setSelected(true);
                    venueTextView.setMovementMethod(new ScrollingMovementMethod());

                    TextView genreTextView = requireView().findViewById(R.id.edit_genres);  // Replace `R.id.edit_genre` with the actual ID of your TextView
                    genreTextView.setText(genre);
                    genreTextView.setSelected(true);
                    genreTextView.setMovementMethod(new ScrollingMovementMethod());

                    TextView priceTextView = requireView().findViewById(R.id.edit_price);  // Replace `R.id.edit_price` with the actual ID of your TextView
                    priceTextView.setText(price);
                    priceTextView.setSelected(true);
                    priceTextView.setMovementMethod(new ScrollingMovementMethod());

                    CardView statusCardView = requireView().findViewById(R.id.statusCard);
                    TextView statusTextView = requireView().findViewById(R.id.edit_status);
                    String ticketstats = TicketStatus;

                    if (ticketstats.equals("onsale")) {
                        statusCardView.setCardBackgroundColor(getResources().getColor(R.color.open));
                        statusTextView.setText("On Sale");
                        statusTextView.setTextColor(getResources().getColor(R.color.white));
                    } else if (ticketstats.equals("offsale")) {
                        statusCardView.setCardBackgroundColor(getResources().getColor(R.color.close));
                        statusTextView.setText("Off Sale");
                        statusTextView.setTextColor(getResources().getColor(R.color.white));
                    } else if (ticketstats.equals("cancelled")) {
                        statusCardView.setCardBackgroundColor(getResources().getColor(R.color.cancel));
                        statusTextView.setText("Cancelled");
                        statusTextView.setTextColor(getResources().getColor(R.color.black));
                    } else if (ticketstats.equals("postponed")) {
                        statusCardView.setCardBackgroundColor(getResources().getColor(R.color.postpone));
                        statusTextView.setText("Postponed");
                        statusTextView.setTextColor(getResources().getColor(R.color.black));
                    } else if (ticketstats.equals("rescheduled")) {
                        statusCardView.setCardBackgroundColor(getResources().getColor(R.color.delete));
                        statusTextView.setText("Rescheduled");
                        statusTextView.setTextColor(getResources().getColor(R.color.white));
                    } else {
                        // Set default text and background color if price is unknown or not provided
                        statusCardView.setCardBackgroundColor(getResources().getColor(R.color.black));
                        statusTextView.setText("");
                        statusTextView.setTextColor(getResources().getColor(R.color.black));
                    }
                    TextView byticketsat = requireView().findViewById(R.id.edit_buyAt);
                    final String Url = eventUrl;
                    byticketsat.setText(eventUrl);
                    byticketsat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Url));
                            startActivity(browserIntent);
                        }
                    });
                    byticketsat.setSelected(true);
                    byticketsat.setMovementMethod(new ScrollingMovementMethod());
                    ImageView seatmapImageView = requireView().findViewById(R.id.seatmap);
                    String venueMaps = venueMap; // Replace with the actual URL of the image

                    Glide.with(requireContext())
                            .load(venueMaps)
                            .into(seatmapImageView);





                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
            }
        });

        queue.add(jsonObjectRequest);
    }

    private String getArtist(JSONObject response) {
        try {
            JSONArray artists = response.getJSONObject("_embedded").getJSONArray("attractions");
            StringBuilder artistNames = new StringBuilder();
            for (int i = 0; i < artists.length(); i++) {
                if (i > 0) {
                    artistNames.append(" | ");
                }
                artistNames.append(artists.getJSONObject(i).getString("name"));
            }
            return artistNames.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }


    }

    private String getSpotifyArtist(JSONObject response) {
        try {
            JSONArray artists = response.getJSONObject("_embedded").getJSONArray("attractions");
            StringBuilder artistNames = new StringBuilder();
            for (int i = 0; i < artists.length(); i++) {
                if(artists.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name").equals("Music")) {
                    if (artistNames.length() > 0) {
                        artistNames.append(" | ");
                    }
                    artistNames.append(artists.getJSONObject(i).getString("name"));
                } else if(artists.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name").equals("music")) {
                    if (artistNames.length() > 0) {
                        artistNames.append(" | ");
                    }
                    artistNames.append(artists.getJSONObject(i).getString("name"));
                }

            }
            return artistNames.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }


    }
    private String getDate(JSONObject response) {
        try {
            JSONObject dates = response.getJSONObject("dates");
            JSONObject start = dates.getJSONObject("start");
            return start.getString("localDate");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getTime(JSONObject response) {
        try {
            JSONObject dates = response.getJSONObject("dates");
            JSONObject start = dates.getJSONObject("start");
            return start.has("localTime") ? start.getString("localTime") : "";
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getVenue(JSONObject response) {
        try {
            return response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getGenre(JSONObject response) {
        String[] genreVals = {"subGenre", "genre", "segment", "subType", "type"};
        StringBuilder val = new StringBuilder();
        for (String genreVal : genreVals) {
            String temp;
            try {
                temp = response.getJSONArray("classifications").getJSONObject(0).getJSONObject(genreVal).getString("name");
            } catch (JSONException e) {
                temp = "";
            }
            if (!temp.isEmpty() && !temp.equalsIgnoreCase("Undefined")) {
                if (val.length() > 0) {
                    val.append(" | ");
                }
                val.append(temp);
            }
        }
        return val.toString();
    }

    private String getPrice(JSONObject response) {
        try {
            JSONArray priceRanges = response.optJSONArray("priceRanges");
            if (priceRanges != null) {
                JSONObject priceRange = priceRanges.getJSONObject(0);
                double minPrice = priceRange.getDouble("min");
                double maxPrice = priceRange.getDouble("max");
                String currency = priceRange.optString("currency", "USD");
                return minPrice + " - " + maxPrice + " " + currency;
            } else {
                return "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }


    private String getTicketStatus(JSONObject response) {
        String status;
        try {
            status = response.getJSONObject("dates").getJSONObject("status").getString("code");

        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
        return status;
    }

    private String getEventUrl(JSONObject response) {
        try {
            return response.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
            return "U";
        }
    }


    private String getVenueMap(JSONObject response) {
        try {

            Log.d("vneuemap", response.getJSONObject("seatmap").getString("staticUrl"));
            return response.getJSONObject("seatmap").getString("staticUrl");

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the ViewModel from the parent activity
        viewModel = new ViewModelProvider(requireActivity()).get(EventDetailsViewModel.class);



        // ... (existing code)
    }

}

