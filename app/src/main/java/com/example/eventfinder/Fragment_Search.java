package com.example.eventfinder;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Search} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Search extends Fragment {

    private SearchResultsAdapter searchResultsAdapter;

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
        searchResultsAdapter = new SearchResultsAdapter(requireContext(), new ArrayList<>(), null);
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
            String segment = Util.getSegment(categoryDropdown.getSelectedItem().toString());
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
                        LinearLayout backLayout = requireView().findViewById(R.id.back_arrow_search_layout);
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
                                        view.findViewById(R.id.search_results_recycler).setVisibility(View.GONE);
                                        view.findViewById(R.id.empty_search).setVisibility(View.GONE);
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
                    FetchUtil.getUserLocation(requireContext(), new Consumer<String>() {
                        @Override
                        public void accept(String userLocation) {
                            String latitude = userLocation.split(",")[0].trim();
                            String longitude = userLocation.split(",")[1].trim();
                            String geocode = Util.getGeoCode(latitude, longitude);

                            FetchUtil.searchEvents(requireContext(), keyword_value, distance_value, segment, geocode, new Consumer<JSONObject>() {
                                @Override
                                public void accept(JSONObject jsonObject) {
                                    try {
                                        processEvents(jsonObject);
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });


                        }
                    });
                } else {
                    FetchUtil.getCoordinatesOfLocation(requireContext(), location_value, new Consumer<JSONObject>() {
                        @Override
                        public void accept(JSONObject locationObject) {

                            try {
                                String latitude = locationObject.getString("lat");
                                String longitude = locationObject.getString("lng");
                                String geocode = Util.getGeoCode(latitude, longitude);

                                FetchUtil.searchEvents(requireContext(), keyword_value, distance_value, segment, geocode, new Consumer<JSONObject>() {
                                    @Override
                                    public void accept(JSONObject jsonObject) {
                                        try {
                                            processEvents(jsonObject);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
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
                    FetchUtil.getSuggestedKeywords(getContext(), currentKeyword, autocomplete_strings -> {
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


    private void processEvents(JSONObject eventsJSON) throws JSONException {

        List<JSONObject> eventsArrayForAdapter = new ArrayList<>();
        RecyclerView search_results_recycler = requireView().findViewById(R.id.search_results_recycler);

        JSONArray eventsAllDataArray = new JSONArray();

        if (eventsJSON.has("_embedded")) {
            eventsAllDataArray = Objects.requireNonNull(eventsJSON.optJSONObject("_embedded")).optJSONArray("events");
        } else {
            requireView().findViewById(R.id.search_loading).setVisibility(View.INVISIBLE);
            requireView().findViewById(R.id.empty_search).setVisibility(View.VISIBLE);
            return;
        }

        for (int i = 0; i < Objects.requireNonNull(eventsAllDataArray).length(); i++) {
            JSONObject event = eventsAllDataArray.getJSONObject(i);

            String id = event.optString("id", "");
            String name = event.optString("name", "");
            String imageURL = event.optJSONArray("images") != null ? Objects.requireNonNull(event.optJSONArray("images")).optJSONObject(0).optString("url") : "";
            String venue = event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");
            String categoryEvent = event.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name");
            String date = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
            String time = Util.getTime(event.getJSONObject("dates").getJSONObject("start").getString("localTime"));
            String ticketURL = event.getString("url");

            eventsArrayForAdapter.add(new JSONObject()
                    .put("id", id)
                    .put("name", name)
                    .put("imageURL", imageURL)
                    .put("venue", venue)
                    .put("categoryEvent", categoryEvent)
                    .put("date", date)
                    .put("time", time)
                    .put("ticketURL", ticketURL)
            );

        }

        searchResultsAdapter = new SearchResultsAdapter(requireContext(), eventsArrayForAdapter, null);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        search_results_recycler.setLayoutManager(linearLayoutManager);
        search_results_recycler.setAdapter(searchResultsAdapter);


        requireView().findViewById(R.id.search_loading).setVisibility(View.GONE);
        search_results_recycler.setVisibility(View.VISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        searchResultsAdapter.notifyDataSetChanged();
    }
}