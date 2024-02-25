package com.example.eventsearch.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsearch.FavoriteItem;
import com.example.eventsearch.R;
import com.example.eventsearch.adapters.FavoritesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavouritesFragment extends Fragment {

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RecyclerView recyclerView = getView().findViewById(R.id.favourite_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        JSONArray jsonArray = new JSONArray();
        // Add your favorite items to the list
        // Example:

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        Log.d("Favorites", "onCreateView: shared_pref :"+sharedPreferences.getAll().toString());
        for(String fav_id: sharedPreferences.getAll().keySet()) {
            try {
                jsonArray.put(new JSONObject((String) sharedPreferences.getAll().get(fav_id)));
            } catch (JSONException e) {
            }
        }
        if(jsonArray.length()>0) {

            TextView no_fav = (TextView) getActivity().findViewById(R.id.no_fav_found);
            no_fav.setVisibility(View.GONE);
            FavoritesAdapter adapter = new FavoritesAdapter(jsonArray, getActivity());
            recyclerView.setAdapter(adapter);
        } else {
            FavoritesAdapter adapter = new FavoritesAdapter(jsonArray, getActivity());
            recyclerView.setAdapter(adapter);
            TextView no_fav = (TextView) getActivity().findViewById(R.id.no_fav_found);
            no_fav.setVisibility(View.VISIBLE);
        }

    }
}
