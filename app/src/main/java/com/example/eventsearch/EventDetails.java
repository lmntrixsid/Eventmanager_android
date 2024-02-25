package com.example.eventsearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.eventsearch.fragments.ArtistFragment;
import com.example.eventsearch.fragments.DetailsFragment;
import com.example.eventsearch.fragments.VenueFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonObject;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.icu.text.SimpleDateFormat;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class EventDetails extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    private JSONObject jsonObject;
    private String eventId;
    private String eventName;
    private String eventUrl;
    private Button outlineHeartEventDetails;
    private Button filledHeartEventDetails;
    private EventDetailsViewModel viewModel;
    @Override
    public void onBackPressed() {
        // Perform your desired action here

        // For example, you can navigate back to the previous screen
        super.onBackPressed();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        outlineHeartEventDetails = findViewById(R.id.outlineHeartEventDetails);
        filledHeartEventDetails = findViewById(R.id.filledHeartEventDetails);

        Bundle bundle = getIntent().getExtras();
        eventId = bundle.getString("event_id");
        eventName = bundle.getString("event_name");
        try {
            jsonObject = new JSONObject(bundle.getString("whole_event"));
            Log.d("EventDetails", "onCreate: "+jsonObject.toString());

        } catch (JSONException e) {
            jsonObject = new JSONObject();
        }



        TextView eventNameTextView = findViewById(R.id.eventname);
        eventNameTextView.setText(eventName);
        eventNameTextView.setSelected(true);
        eventNameTextView.setMovementMethod(new ScrollingMovementMethod());

        viewModel = new ViewModelProvider(this).get(EventDetailsViewModel.class);
        viewModel.setEventId(eventId);


        viewPager = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        setupTabLayout();

        fetcheventdetails(eventId);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button facebookButton = findViewById(R.id.facebook_button);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button twitterButton = findViewById(R.id.twitter_button);

        Button backButton = findViewById(R.id.event_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        facebookButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.facebook.com/sharer/sharer.php?u=" + eventUrl));
            startActivity(intent);
        });

        twitterButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://twitter.com/intent/tweet?url=" + eventUrl));
            startActivity(intent);
        });

        String finalEvent_id = eventId;
        String EventName = eventName;
        SharedPreferences sharedPreferences = this.getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        final boolean[] is_in_favorites = {sharedPreferences.contains(finalEvent_id)};
        if (is_in_favorites[0]) {
            //TODO: make button color white
            outlineHeartEventDetails.setVisibility(View.GONE);
            filledHeartEventDetails.setVisibility(View.VISIBLE);
        } else {
            outlineHeartEventDetails.setVisibility(View.VISIBLE);
            filledHeartEventDetails.setVisibility(View.GONE);
        }
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!is_in_favorites[0]) {
                    // Add to favorites
                    //TODO: make button color white
                    editor.putString(finalEvent_id, jsonObject.toString());
                    editor.commit();
                    is_in_favorites[0] = true;
                    Snackbar.make(view, jsonObject.optString("name", "") + " is added to favorites", Snackbar.LENGTH_SHORT).show();
                } else {
                    // Remove from favorites
                    //TODO: make button color border
                    editor.remove(finalEvent_id);
                    editor.commit();
                    is_in_favorites[0] = false;
                    Snackbar.make(view, jsonObject.optString("name", "") + " is removed from favorites", Snackbar.LENGTH_SHORT).show();
                }

                // Update the visibility of buttons
                outlineHeartEventDetails .setVisibility(is_in_favorites[0] ? View.GONE : View.VISIBLE);
                filledHeartEventDetails .setVisibility(is_in_favorites[0] ? View.VISIBLE : View.GONE);
            }
        };

        filledHeartEventDetails.setOnClickListener(buttonClickListener);
        outlineHeartEventDetails.setOnClickListener(buttonClickListener);

//




//



    }

    private void fetcheventdetails(String eventId) {
        String url = "https://backend-382506.wm.r.appspot.com/event-details?event_id=" + eventId;
        Log.d(url, "fetcheventdetails: ");
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Process
                //
                 eventUrl = getEventUrl(response);
                Log.d("eventUrl", eventUrl);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle the error
            }
        });

        queue.add(jsonObjectRequest);
    }
    private String getEventUrl(JSONObject response) {
        try {
            return response.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
            return "U";
        }
    }
    private void openUrlInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    private void setupTabLayout() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());

        adapter.addFragment(new DetailsFragment(), "Details");
        adapter.addFragment(new ArtistFragment(), "Artist(s)");
        adapter.addFragment(new VenueFragment(), "Venue");

        viewPager.setAdapter(adapter);
        int[] tabIcons = {
                R.drawable.info_icon,
                R.drawable.artist_icon,
                R.drawable.venue_icon
        };
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View customView = LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);

            // Set the icon and text for the custom tab layout
            ImageView tabIcon = customView.findViewById(R.id.tab_icon);
            TextView tabTitle = customView.findViewById(R.id.tab_title);

            tabIcon.setImageResource(tabIcons[position]);
            tabTitle.setText(adapter.getFragmentTitle(position));

            if (position == 0) {
                tabIcon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                tabTitle.setTextColor(getResources().getColor(R.color.green));
            } else {
                tabIcon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                tabTitle.setTextColor(getResources().getColor(R.color.white));
            }

            // Set the custom view for the tab
            tab.setCustomView(customView);
        }).attach();
        View initialTabView = Objects.requireNonNull(tabLayout.getTabAt(0)).getCustomView();
        ImageView initialTabIcon = (ImageView) Objects.requireNonNull(tabLayout.getTabAt(0)).getCustomView().findViewById(R.id.tab_icon);
        ((TextView) initialTabView.findViewById(R.id.tab_title)).setTextColor(getResources().getColor(R.color.white));
        initialTabIcon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        // Add a custom TabLayout.OnTabSelectedListener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Change the selected tab icon tint
                ImageView tabIcon = (ImageView) tab.getCustomView().findViewById(R.id.tab_icon);
                TextView tabTitle = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
                tabIcon.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_IN);
                tabTitle.setTextColor(getResources().getColor(R.color.green));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Reset the unselected tab icon tint
                ImageView tabIcon = (ImageView) tab.getCustomView().findViewById(R.id.tab_icon);
                TextView tabTitle = (TextView) tab.getCustomView().findViewById(R.id.tab_title);
                tabIcon.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                tabTitle.setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

    }

    private static class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        private Bundle detailsBundle;
        private String venueName;

        public ViewPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
//            this.venueName = venueName;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = fragmentList.get(position);
            fragment.setArguments(detailsBundle);
            return fragment;
//

        }
        public void setDetailsBundle(Bundle bundle) {
            this.detailsBundle = bundle;
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        public String getFragmentTitle(int position) {
            return fragmentTitleList.get(position);
        }

    }


}

