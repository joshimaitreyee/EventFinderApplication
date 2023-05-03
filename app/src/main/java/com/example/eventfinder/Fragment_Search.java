package com.example.eventfinder;

import android.os.Bundle;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                Log.e("getAutocompleteSuggestions", error.toString());
            }
        });

        Volley.newRequestQueue(requireContext()).add(req);
    }


}