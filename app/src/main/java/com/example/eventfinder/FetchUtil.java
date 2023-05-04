package com.example.eventfinder;

import android.content.Context;
import android.util.Log;

import androidx.core.util.Consumer;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FetchUtil {

    private FetchUtil() {

    }

    public static void searchEvents(Context context, String keyword, String distance, String segment, String geocode, Consumer<JSONObject> callOnFinished) {
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
        Volley.newRequestQueue(context).add(req);
    }


    public static void getCoordinatesOfLocation(Context context, String location, Consumer<JSONObject> callOnFinished) {
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
        Volley.newRequestQueue(context).add(request);
    }

    public static void getUserLocation(Context context, Consumer<String> callOnFinished) {

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

        Volley.newRequestQueue(context).add(request);
    }


    public static void getSuggestedKeywords(Context context, String keyword, Consumer<List<String>> callOnFinished) {
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

        Volley.newRequestQueue(context).add(req);
    }


}
