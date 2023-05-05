package com.example.eventfinder;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ArtistsResultsAdapter extends RecyclerView.Adapter<ArtistsResultsAdapter.ViewHolder> {

    private final JSONArray artists;
    private final CardClicked listener;

    public ArtistsResultsAdapter(JSONArray artists, CardClicked listener) {
        this.artists = artists;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ArtistsResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.artists_result_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsResultsAdapter.ViewHolder holder, int position) {
        JSONObject currentCard;
        try {
            currentCard = (JSONObject) artists.get(position);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Glide.with(holder.itemView.getContext()).load(currentCard.optString("album1ImageUrl")).into(holder.topAlbum1);
        Glide.with(holder.itemView.getContext()).load(currentCard.optString("album2ImageUrl")).into(holder.topAlbum2);
        Glide.with(holder.itemView.getContext()).load(currentCard.optString("album3ImageUrl")).into(holder.topAlbum3);
        Glide.with(holder.itemView.getContext()).load(currentCard.optString("artistImageUrl")).into(holder.artistPhoto);

        holder.artistName.setText(currentCard.optString("artistName"));
        holder.artistPopularity.setText(currentCard.optString("artistPopularity"));
        holder.artistFollowers.setText(currentCard.optString("artistFollowers"));

        holder.artistPopularityCircle.setProgress(Integer.parseInt(currentCard.optString("artistPopularity")));
        SpannableString ss = new SpannableString("Check out on Spotify");
        ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
        holder.artistSpotify.setText(ss);
        holder.artistSpotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.artistLinkClicked(currentCard.optString("artistSpotifyUrl"));
            }
        });


    }

    @Override
    public int getItemCount() {
        return artists.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircularProgressIndicator artistPopularityCircle;
        TextView artistFollowers;
        TextView artistSpotify;
        ImageView artistPhoto;
        TextView artistName;
        TextView artistPopularity;
        ImageView topAlbum1;
        ImageView topAlbum2;
        ImageView topAlbum3;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistPopularityCircle = itemView.findViewById(R.id.artistPopularityBar);
            topAlbum1 = itemView.findViewById(R.id.topAlbum1);
            artistFollowers = itemView.findViewById(R.id.followers);
            artistSpotify = itemView.findViewById(R.id.artist_url_spotify);
            artistPhoto = itemView.findViewById(R.id.artistPhoto);
            topAlbum2 = itemView.findViewById(R.id.topAlbum2);
            artistName = itemView.findViewById(R.id.name_artist);
            artistPopularity = itemView.findViewById(R.id.artist_popularity);
            topAlbum3 = itemView.findViewById(R.id.topAlbum3);


        }
    }

    interface CardClicked {
        public void artistLinkClicked(String url);
    }


}
