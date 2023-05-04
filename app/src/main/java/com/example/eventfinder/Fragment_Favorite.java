package com.example.eventfinder;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Favorite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Favorite extends Fragment {

    private FavoriteResultsAdapter favoriteResultsAdapter;

    public Fragment_Favorite() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__favorite, container, false);

        RecyclerView favoriteResultsRecycler = view.findViewById(R.id.favorite_results_recycler);
        try {
            favoriteResultsAdapter = new FavoriteResultsAdapter(requireContext(), new FavoriteResultsAdapter.CardClicked() {
                @Override
                public void onCardClicked(JSONObject cardInfo) {
                    Intent intent = new Intent(requireContext(), CardDetails.class);
                    intent.putExtra("cardInfo", cardInfo.toString());
                    startActivity(intent);
                }
            }, new FavoriteResultsAdapter.OnEmptyFavorites() {
                @Override
                public void sendEmptyMessage() {
                    view.findViewById(R.id.empty_favorites).setVisibility(View.VISIBLE);
                }
            });

            favoriteResultsRecycler.setAdapter(favoriteResultsAdapter);
            favoriteResultsRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            if (favoriteResultsAdapter.getItemCount() == 0) {
                view.findViewById(R.id.empty_favorites).setVisibility(View.VISIBLE);
            } else {
                favoriteResultsRecycler.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            this.favoriteResultsAdapter.reload();
            this.favoriteResultsAdapter.notifyDataSetChanged();
            if (this.favoriteResultsAdapter.getItemCount() == 0) {
                requireView().findViewById(R.id.empty_favorites).setVisibility(View.VISIBLE);
            } else {
                requireView().findViewById(R.id.empty_favorites).setVisibility(View.GONE);
                requireView().findViewById(R.id.favorite_results_recycler).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}