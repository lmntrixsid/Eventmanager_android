package com.example.eventsearch.fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventsearch.EventDetailsViewModel;
import com.example.eventsearch.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;



import org.json.JSONException;
import org.json.JSONObject;

public class VenueFragment extends Fragment implements OnMapReadyCallback{
    private MapView mapView;
    private GoogleMap googleMap;
    private Marker venueMarker;
    private double venueLatitude;
    private double venueLongitude;

    private EventDetailsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {




        return inflater.inflate(R.layout.fragment_venue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeMapView(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EventDetailsViewModel.class);

        viewModel.getVenueName().observe(getViewLifecycleOwner(), venueName -> {
            TextView venueNameTextView = view.findViewById(R.id.edit_name);
            venueNameTextView.setText(venueName);
            fetchvenuedetails(venueName);

            // Use venueName as needed
        });
    }
    private void initializeMapView(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mapView = view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }



    private void fetchvenuedetails(String venue) {
        String url = "https://backend-382506.wm.r.appspot.com/venue-details?event_venue_name=" + venue;
        Log.d(url, "fetchvenuedetails: ");
        RequestQueue queue = Volley.newRequestQueue(requireActivity().getApplicationContext());
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {

                // Process the JSON response
                String address = getAddress(response);
                TextView addressTextView = requireView().findViewById(R.id.edit_address);
                addressTextView.setSelected(true);
                addressTextView.setMovementMethod(new ScrollingMovementMethod());
                addressTextView.setText(address);

                String city = getCity(response);
                TextView cityTextView = requireView().findViewById(R.id.edit_city);
                cityTextView.setSelected(true);
                cityTextView.setMovementMethod(new ScrollingMovementMethod());
                cityTextView.setText(city);

                String phone = getPhoneNumber(response);
                TextView phoneTextView = requireView().findViewById(R.id.edit_contact);
                phoneTextView.setSelected(true);
                phoneTextView.setMovementMethod(new ScrollingMovementMethod());
                phoneTextView.setText(phone);

                String openHours = getOpenHours(response);
                System.out.println(openHours);
                TextView openHoursTextView = requireView().findViewById(R.id.edit_hour);
                openHoursTextView.setText(openHours);

                String generalRule = getGeneralRule(response);
                System.out.println(generalRule);
                TextView generalRuleTextView = requireView().findViewById(R.id.edit_general);
                generalRuleTextView.setText(generalRule);

                String childRule = getChildRule(response);
                System.out.println(childRule);
                TextView childRuleTextView = requireView().findViewById(R.id.edit_child);
                childRuleTextView.setText(childRule);

                String lat = getLatitude(response);


                String lon = getLongitude(response);
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lon);

                updateVenueLocation(latitude, longitude);





            }
        }, new Response.ErrorListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
            }
        });

        queue.add(jsonObjectRequest);
    }
    private String getAddress(JSONObject response) {
        try {
            String line1 = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("address").optString("line1", "");

            System.out.println(line1);
            return line1;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    private String getCity(JSONObject response) {
        try {
            String city = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("city").optString("name", "");
            String state = response.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getJSONObject("state").optString("name", "");
            return  city + ", " + state;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getPhoneNumber(JSONObject response) {
        try {
            return response.getJSONObject("_embedded")
                    .getJSONArray("venues")
                    .getJSONObject(0)
                    .getJSONObject("boxOfficeInfo")
                    .optString("phoneNumberDetail", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getOpenHours(JSONObject response) {
        try {
            return response.getJSONObject("_embedded")
                    .getJSONArray("venues")
                    .getJSONObject(0)
                    .getJSONObject("boxOfficeInfo")
                    .optString("openHoursDetail", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    private String getGeneralRule(JSONObject response) {
        try {
            return response.getJSONObject("_embedded")
                    .getJSONArray("venues")
                    .getJSONObject(0)
                    .getJSONObject("generalInfo")
                    .optString("generalRule", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getChildRule(JSONObject response) {
        try {
            return response.getJSONObject("_embedded")
                    .getJSONArray("venues")
                    .getJSONObject(0)
                    .getJSONObject("generalInfo")
                    .optString("childRule", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getLatitude(JSONObject response) {
        try {
            return response.getJSONObject("_embedded")
                    .getJSONArray("venues")
                    .getJSONObject(0)
                    .getJSONObject("location")
                    .optString("latitude", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private String getLongitude(JSONObject response) {
        try {
            return response.getJSONObject("_embedded")
                    .getJSONArray("venues")
                    .getJSONObject(0)
                    .getJSONObject("location")
                    .optString("longitude", "");
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
    private void updateVenueLocation(double latitude, double longitude) {
        venueLatitude = latitude;
        venueLongitude = longitude;

        if (googleMap != null) {
            LatLng venueLatLng = new LatLng(latitude, longitude);

            if (venueMarker != null) {
                venueMarker.remove();
            }

            venueMarker = googleMap.addMarker(new MarkerOptions()
                    .position(venueLatLng)
                    .title("Venue"));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venueLatLng, 12));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        updateVenueLocation(venueLatitude, venueLongitude);

    }





        // ... (existing code)
}
