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


public class FavoriteResultsAdapter extends RecyclerView.Adapter<FavoriteResultsAdapter.ViewHolder> {

    private Context context;
    private List<JSONObject> favoriteResults;
    private CardClicked cardListener;
    private OnEmptyFavorites emptyListener;


    private FavoritesManager favoritesManager;

    public FavoriteResultsAdapter(Context context, CardClicked cardListener, OnEmptyFavorites emptyListener) throws JSONException {
        this.context = context;
        this.cardListener = cardListener;
        this.favoritesManager = new FavoritesManager(context);
        this.favoriteResults = this.favoritesManager.loadFavorites();
        this.emptyListener = emptyListener;
    }

    public void reload() throws JSONException {
        this.favoriteResults = this.favoritesManager.loadFavorites();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_card, parent, false);
        view.findViewById(R.id.search_card_name).setSelected(true);
        view.findViewById(R.id.search_card_venue).setSelected(true);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject currentCard = favoriteResults.get(position);

        holder.heart.setImageResource(R.drawable.heart_filled);

        holder.heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String eventId = currentCard.optString("id");
                    String eventName = currentCard.optString("name");
                    favoritesManager.removeFromStorage(eventId);
                    int posToRemove = holder.getAdapterPosition();
                    favoriteResults.remove(posToRemove);
                    notifyItemRemoved(posToRemove);

                    Util.displayMessage(holder.itemView, context, eventName + " removed from favorites");

                    if (getItemCount() == 0) {
                        emptyListener.sendEmptyMessage();
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardListener.onCardClicked(currentCard);
            }
        });

    }

    @Override
    public int getItemCount() {
        return favoriteResults.size();
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

    interface OnEmptyFavorites {
        public void sendEmptyMessage();
    }


}
