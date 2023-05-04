package com.example.eventfinder;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import ch.hsr.geohash.GeoHash;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Search extends Fragment {


    public Fragment_Search() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__search, container, false);


        formSetup(view);

        return view;
    }

    private void formSetup(View view) {
        AutoCompleteTextView keywordInput = view.findViewById(R.id.keyword_input);
        keywordInput.setThreshold(1);


        Spinner categoryDropdown = view.findViewById(R.id.category_input);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.category_array, R.layout.category_layout);
        categoryAdapter.setDropDownViewResource(R.layout.category_dropdown_layout);
        categoryDropdown.setAdapter(categoryAdapter);

        ProgressBar keywordSpinner = view.findViewById(R.id.spinner_keyword);
        keywordSpinner.setVisibility(View.GONE);

        TextView distance = view.findViewById(R.id.distance_input);
        SwitchCompat autoDetect = view.findViewById(R.id.auto_detect_switch);
        TextView location = view.findViewById(R.id.location_input);

        autoDetect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    location.setVisibility(View.GONE);
                    view.findViewById(R.id.location_line).setVisibility(View.GONE);

                } else {
                    location.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.location_line).setVisibility(View.VISIBLE);
                }
            }
        });

        ((Button) view.findViewById(R.id.button_clear)).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                keywordSpinner.setVisibility(View.GONE);
                keywordInput.setText("");
                categoryDropdown.setSelection(0);
                distance.setText("10");
                location.setText("");
                autoDetect.setChecked(false);

            }
        });

        ((Button) view.findViewById(R.id.button_search)).setOnClickListener(view1 -> {
            String keyword_value = keywordInput.getText().toString();
            String location_value = location.getText().toString();
            String segment = getSegment(categoryDropdown.getSelectedItem().toString());
            String distance_value = distance.getText().toString();
            Boolean autoDetect_value = autoDetect.isChecked();

            if (!isValidForm(keyword_value, distance_value, location_value, autoDetect_value)) {
                Util.displayMessage(getView(), requireContext(), "Please fill all fields");
            } else {


                // form animation code starts
                CardView searchForm = requireView().findViewById(R.id.search_form);

                TranslateAnimation formSlideOutAnimation;
                formSlideOutAnimation = new TranslateAnimation(0f, searchForm.getWidth(), 0f, 0f);


                AlphaAnimation formFadeOutAnimation = new AlphaAnimation(1f, 0f);

                AnimationSet animation = new AnimationSet(true);
                animation.addAnimation(formSlideOutAnimation);
                animation.addAnimation(formFadeOutAnimation);
                animation.setDuration(600);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        requireView().findViewById(R.id.search_loading).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        searchForm.setVisibility(View.GONE);
                        RelativeLayout backLayout = requireView().findViewById(R.id.back_arrow_search_layout);
                        backLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view1) {


                                TranslateAnimation formSlideInAnimation = new TranslateAnimation(searchForm.getWidth(), 0f, 0f, 0f);

                                AlphaAnimation formFadeInAnimation = new AlphaAnimation(0f, 1f);

                                AnimationSet animationSet = new AnimationSet(true);
                                animationSet.addAnimation(formSlideInAnimation);
                                animationSet.addAnimation(formFadeInAnimation);
                                animationSet.setDuration(500);
                                animationSet.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        requireView().findViewById(R.id.back_arrow_search_layout).setVisibility(View.GONE);
                                        //view.findViewById(R.id.events_recycler_view).setVisibility(View.GONE);
                                        //view.findViewById(R.id.no_events_card).setVisibility(View.GONE);
                                        searchForm.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                                searchForm.startAnimation(animationSet);
                            }
                        });

                        backLayout.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                searchForm.startAnimation(animation);

                if (autoDetect_value) {
                    getUserLocation(new Consumer<String>() {
                        @Override
                        public void accept(String userLocation) {
                            String latitude = userLocation.split(",")[0].trim();
                            String longitude = userLocation.split(",")[1].trim();
                            String geocode = getGeoCode(latitude, longitude);

                            searchEvents(keyword_value, distance_value, segment, geocode, new Consumer<JSONObject>() {
                                @Override
                                public void accept(JSONObject jsonObject) {
                                    Log.e("Fragment_Search", jsonObject.toString());
                                }
                            });


                        }
                    });
                } else {
                    getCoordinatesOfLocation(location_value, new Consumer<JSONObject>() {
                        @Override
                        public void accept(JSONObject locationObject) {

                            try {
                                String latitude = locationObject.getString("lat");
                                String longitude = locationObject.getString("lng");
                                String geocode = getGeoCode(latitude, longitude);

                                searchEvents(keyword_value, distance_value, segment, geocode, new Consumer<JSONObject>() {
                                    @Override
                                    public void accept(JSONObject jsonObject) {
                                        Log.e("Fragment_Search", jsonObject.toString());
                                    }
                                });


                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    });

                }


            }
        });


        keywordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence keyword, int i, int i1, int i2) {
                String currentKeyword = keyword.toString();
                if (!currentKeyword.trim().isEmpty()) {
                    keywordSpinner.setVisibility(View.VISIBLE);
                    getSuggestedKeywords(currentKeyword, autocomplete_strings -> {
                        keywordSpinner.setVisibility(View.GONE);
                        ArrayAdapter<String> keywordsAdapter = new ArrayAdapter<>(requireContext(), R.layout.keyword_autocomplete_layout, autocomplete_strings);
                        keywordInput.setAdapter(keywordsAdapter);
                        keywordsAdapter.notifyDataSetChanged();

                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private boolean isValidForm(String keyword, String distance, String location, Boolean autoDetect) {
        return !distance.trim().isEmpty() && !keyword.trim().isEmpty() && (autoDetect || !location.trim().isEmpty());
    }

    private void getSuggestedKeywords(String keyword, Consumer<List<String>> callOnFinished) {
        String suggestionsUrl = "https://assignment8csci571.uw.r.appspot.com/suggestions/" + keyword;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, suggestionsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<String> keywordsResponse = new ArrayList<>();
                JSONArray attractions;
                try {
                    attractions = response.getJSONObject("_embedded").getJSONArray("attractions");

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 0; i < attractions.length(); i++) {
                    try {

                        keywordsResponse.add(attractions.getJSONObject(i).getString("name"));
                    } catch (JSONException e) {
                        Log.e("getAutocompleteSuggestions", e.toString());
                    }
                }

                callOnFinished.accept(keywordsResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Fragment_Search", error.toString());
            }
        });

        Volley.newRequestQueue(requireContext()).add(req);
    }


    private void getUserLocation(Consumer<String> callOnFinished) {

        StringRequest request = new StringRequest(Request.Method.GET, "https://ipinfo.io/json?token=ae60cdde61b4e8", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callOnFinished.accept(new JSONObject(response).getString("loc"));
                } catch (JSONException e) {
                    Log.e("Fragment_Search", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Fragment_Search", error.toString());
            }
        });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    private String getGeoCode(String latitude, String longitude) {
        return GeoHash.geoHashStringWithCharacterPrecision(Double.parseDouble(latitude), Double.parseDouble(longitude), 7);
    }

    private String getSegment(String category) {
        if (Objects.equals(category, "Music")) {
            return "KZFzniwnSyZfZ7v7nJ";
        } else if (Objects.equals(category, "Sports")) {
            return "KZFzniwnSyZfZ7v7nE";
        } else if (Objects.equals(category, "Arts & Theatre")) {
            return "KZFzniwnSyZfZ7v7na";
        } else if (Objects.equals(category, "Film")) {
            return "KZFzniwnSyZfZ7v7nn";
        } else if (Objects.equals(category, "Miscellaneous")) {
            return "KZFzniwnSyZfZ7v7n1";
        } else {
            return "";
        }
    }

    private void getCoordinatesOfLocation(String location, Consumer<JSONObject> callOnFinished) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + location + "&key=AIzaSyAGndCCi5rqyRS7ik8oTvB_G2DLRzgGywM";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject loc = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                    callOnFinished.accept(loc);
                } catch (JSONException e) {
                    Log.e("Fragment_Search", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Fragment_Search", error.toString());
            }
        });
        Volley.newRequestQueue(requireContext()).add(request);
    }


    private void searchEvents(String keyword, String distance, String segment, String geocode, Consumer<JSONObject> callOnFinished) {
        String eventsUrl = "https://assignment8csci571.uw.r.appspot.com/events?keyword=" + keyword + "&segmentId=" + segment + "&radius=" + distance + "&geoPoint=" + geocode;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, eventsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callOnFinished.accept(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Fragment_Search", error.toString());
            }
        });
        Volley.newRequestQueue(requireContext()).add(req);
    }


}