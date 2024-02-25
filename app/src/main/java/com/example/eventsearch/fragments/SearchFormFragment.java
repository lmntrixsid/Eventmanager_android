package com.example.eventsearch.fragments;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eventsearch.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchFormFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_form, container, false);
        EditText keywordEditText = view.findViewById(R.id.autocomplete_keyword);

        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) keywordEditText;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.dropdown_item_layout);
        autoCompleteTextView.setAdapter(adapter);
        adapter.setDropDownViewResource(R.layout.dropdown_item_layout);


        autoCompleteTextView.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        String apiUrl = "https://backend-382506.wm.r.appspot.com/suggest?keyword=";
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();

                    String url = apiUrl + keyword;
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        JSONArray jsonArray = jsonObject.getJSONObject("_embedded").getJSONArray("attractions");
                                        List<String> suggestions = new ArrayList<>();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            String name = jsonArray.getJSONObject(i).getString("name");
//                                            Log.d(TAG, "onResponse: "+name);
                                            suggestions.add(name);
                                        }
                                        adapter.clear();
                                        adapter.addAll(suggestions);
                                        adapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    requestQueue.add(stringRequest);

            }
        });



                Spinner categorySpinner = view.findViewById(R.id.category_spinner);

                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                        R.array.category_array, R.layout.custom_spinner_item);
                adapter2.setDropDownViewResource(R.layout.custom_spinner_item);
                categorySpinner.setAdapter(adapter2);

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedCategory = parent.getItemAtPosition(position).toString();
                        // Do something with the selected category
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });

                Button searchButton = view.findViewById(R.id.search_button);
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText keywordEditText = view.findViewById(R.id.autocomplete_keyword);
                        EditText locationEditText = view.findViewById(R.id.location);
                        EditText distanceEditText = view.findViewById(R.id.distance);
                        boolean isLocationEnabled = ((Switch) view.findViewById(R.id.location_switch)).isChecked();

                        String keyword = keywordEditText.getText().toString().trim();
                        String location = locationEditText.getText().toString().trim();
                        Spinner categorySpinner = view.findViewById(R.id.category_spinner);
                        String categoryValue = categorySpinner.getSelectedItem().toString();
                        int position = categorySpinner.getSelectedItemPosition();
                        String[] categoryValues = getResources().getStringArray(R.array.category_array_values);
                        String category = categoryValues[position];


                        Integer distance = distanceEditText.getText().toString().trim().isEmpty() ? 10 : Integer.parseInt(distanceEditText.getText().toString().trim());

                        Bundle bundle = new Bundle();

                        bundle.putString("keyword", keyword);
                        bundle.putString("location", location);
                        bundle.putString("category", category);
                        bundle.putInt("distance", distance);
                        bundle.putBoolean("isLocationEnabled", isLocationEnabled);

                        EventListFragment eventListFragment = new EventListFragment();
                        eventListFragment.setArguments(bundle);



                        if (TextUtils.isEmpty(keyword)) {
                            Snackbar.make(v, "Keyword cannot be empty", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(location) && !isLocationEnabled) {
                            Snackbar.make(v, "Location cannot be empty", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

//
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                        fragmentTransaction.replace(R.id.fragment_container,  eventListFragment);
                        fragmentTransaction.commit();
                    }
                });

                @SuppressLint("UseSwitchCompatOrMaterialCode") Switch locationSwitch = view.findViewById(R.id.location_switch);
                EditText locationEditText = view.findViewById(R.id.location);

                locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            locationEditText.setVisibility(View.GONE);
                            locationSwitch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.green)));
                        } else {
                            locationEditText.setVisibility(View.VISIBLE);
                            locationSwitch.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey)));
                        }
                    }
                });


                Button clearButton = view.findViewById(R.id.clear_btn);
                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Reset all fields
                        Spinner categorySpinner = view.findViewById(R.id.category_spinner);
                        categorySpinner.setSelection(0); // Set the first item as selected

                        EditText keywordEditText = view.findViewById(R.id.autocomplete_keyword);
                        keywordEditText.setText("");

                        EditText distanceEditText = view.findViewById(R.id.distance);
                        distanceEditText.setText("10");

                        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch locationSwitch = view.findViewById(R.id.location_switch);
                        locationSwitch.setChecked(false); // Uncheck the switch

                        EditText locationEditText = view.findViewById(R.id.location);
                        locationEditText.setText("");

                    }
                });



        return view;



    }



}