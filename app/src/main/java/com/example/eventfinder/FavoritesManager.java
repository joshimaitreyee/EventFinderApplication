package com.example.eventfinder;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public FavoritesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences("favorites_storage", 0);
        this.editor = sharedPreferences.edit();
    }

    public Boolean presentInStorage(String eventId) throws JSONException {
        List<JSONObject> favorites = loadFavorites();
        for (JSONObject favorite : favorites) {
            if (favorite.getString("id").equals(eventId)) {
                return true;
            }
        }
        return false;
    }

    public void removeFromStorage(String eventId) throws JSONException {
        List<JSONObject> favorites = loadFavorites();
        int removeAt = -1;
        for (int i = 0; i < favorites.size(); i++) {
            if (favorites.get(i).getString("id").equals(eventId)) {
                removeAt = i;
                break;
            }
        }

        if (removeAt != -1) {
            favorites.remove(removeAt);
            String updatedFavoriteString = favorites.toString();
            this.editor.putString("favorites", updatedFavoriteString);
            this.editor.apply();
        }
    }

    public void addToStorage(JSONObject favoriteEvent) throws JSONException {
        if (!presentInStorage(favoriteEvent.getString("id"))) {
            List<JSONObject> favorites = loadFavorites();
            favorites.add(favoriteEvent);
            this.editor.putString("favorites", favorites.toString());
            this.editor.apply();
        }

    }
    
    public List<JSONObject> loadFavorites() throws JSONException {
        List<JSONObject> favorites = new ArrayList<>();
        if (this.sharedPreferences.contains("favorites")) {
            String favoritesString = this.sharedPreferences.getString("favorites", null);
            JSONArray jsonArray = new JSONArray(favoritesString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                favorites.add(jsonObject);
            }
        }
        return favorites;
    }


}
