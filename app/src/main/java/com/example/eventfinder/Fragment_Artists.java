package com.example.eventfinder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Artists#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Artists extends Fragment {

    private JSONArray artistsJSONArray;
    private Boolean isNotMusic;

    public Fragment_Artists() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                this.artistsJSONArray = new JSONArray(getArguments().getString("artistsResponseArray"));
                this.isNotMusic = getArguments().getBoolean("isNotMusic");
                Log.e("Fragment_Artists", this.artistsJSONArray.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__artists, container, false);

        RecyclerView artistsResultsRecycler = view.findViewById(R.id.artists_result);
        ArtistsResultsAdapter adapter = new ArtistsResultsAdapter(this.artistsJSONArray, new ArtistsResultsAdapter.CardClicked() {
            @Override
            public void artistLinkClicked(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        artistsResultsRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        artistsResultsRecycler.setAdapter(adapter);
        if (!this.isNotMusic) {
            artistsResultsRecycler.setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.not_music_event).setVisibility(View.VISIBLE);
        }


        return view;
    }
}