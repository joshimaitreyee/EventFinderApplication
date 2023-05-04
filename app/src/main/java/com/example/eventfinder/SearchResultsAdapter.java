package com.example.eventfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private Context context;
    private List<JSONObject> searchResults;
    private CardClicked listener;

    private FavoritesManager favoritesManager;

    public SearchResultsAdapter(Context context, List<JSONObject> searchResults, CardClicked listener) {
        this.context = context;
        this.searchResults = searchResults;
        this.listener = listener;
        this.favoritesManager = new FavoritesManager(context);
    }


    @NonNull
    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_card, parent, false);
        view.findViewById(R.id.search_card_name).setSelected(true);
        view.findViewById(R.id.search_card_venue).setSelected(true);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchResultsAdapter.ViewHolder holder, int position) {
        JSONObject currentCard = searchResults.get(position);

        ImageView heartImage = holder.heart;
        try {
            if (this.favoritesManager.presentInStorage(currentCard.optString("id"))) {
                heartImage.setImageResource(R.drawable.heart_filled);
            } else {
                heartImage.setImageResource(R.drawable.heart_outline);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String eventId = currentCard.optString("id");
                    String eventName = currentCard.optString("name");
                    if (favoritesManager.presentInStorage(eventId)) {
                        favoritesManager.removeFromStorage(eventId);
                        holder.heart.setImageResource(R.drawable.heart_outline);
                        Util.displayMessage(holder.itemView, context, eventName + " removed from favorites");
                    } else {
                        favoritesManager.addToStorage(currentCard);
                        holder.heart.setImageResource(R.drawable.heart_filled);
                        Util.displayMessage(holder.itemView, context, eventName + " added to favorites");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        holder.category.setText(currentCard.optString("categoryEvent"));
        holder.name.setText(currentCard.optString("name"));
        holder.venue.setText(currentCard.optString("venue"));
        holder.time.setText(currentCard.optString("time"));
        holder.date.setText(currentCard.optString("date"));
        Glide.with(holder.itemView.getContext()).load(currentCard.optString("imageURL")).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView heart;
        TextView category;
        ImageView image;
        TextView venue;
        TextView name;
        TextView time;
        TextView date;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            heart = itemView.findViewById(R.id.search_card_heart);
            category = itemView.findViewById(R.id.search_card_category);
            image = itemView.findViewById(R.id.search_card_image);
            venue = itemView.findViewById(R.id.search_card_venue);
            name = itemView.findViewById(R.id.search_card_name);
            time = itemView.findViewById(R.id.search_card_time);
            date = itemView.findViewById(R.id.search_card_date);

        }
    }

    interface CardClicked {
        public void onCardClicked(JSONObject object);
    }


}
