package com.example.eventfinder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Detail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Detail extends Fragment {

    private JSONObject detailsJSON;


    public Fragment_Detail() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                this.detailsJSON = new JSONObject(getArguments().getString("detailResponse"));
                Log.e("Fragment_Detail", this.detailsJSON.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__detail, container, false);
    }
}