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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
                Log.e("FetchUtil", error.toString());
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
                    Log.e("FetchUtil", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FetchUtil", error.toString());
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
                    Log.e("FetchUtil", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FetchUtil", error.toString());
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
                JSONArray attractions = null;
                try {
                    attractions = response.getJSONObject("_embedded").getJSONArray("attractions");

                } catch (JSONException e) {

                    attractions = new JSONArray();
                }

                for (int i = 0; i < Objects.requireNonNull(attractions).length(); i++) {
                    try {

                        keywordsResponse.add(attractions.getJSONObject(i).getString("name"));
                    } catch (JSONException e) {
                        Log.e("FetchUtil", e.toString());
                    }
                }

                callOnFinished.accept(keywordsResponse);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FetchUtil", error.toString());
            }
        });

        Volley.newRequestQueue(context).add(req);
    }

    public static void fetchSpecificEvent(Context context, String eventId, Consumer<JSONObject> callOnFinished) {
        String eventsUrl = "https://assignment8csci571.uw.r.appspot.com/events/" + eventId;
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, eventsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                callOnFinished.accept(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FetchUtil", error.toString());
            }
        });
        Volley.newRequestQueue(context).add(req);
    }

    public static void getArtistsData(Context context, List<String> artistNames, Consumer<Map<String, JSONObject>> callOnFinished) {
        int totalNumberOfArtists = artistNames.size();

        Map<String, JSONObject> artistsMap = new HashMap<>();

        for (int i = 0; i < artistNames.size(); i++) {
            String artistName = artistNames.get(i);
            String artistUrl = "https://assignment8csci571.uw.r.appspot.com/artists/" + artistName;
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, artistUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject artistResponse) {
                    artistsMap.put(artistName, artistResponse);
                    if (artistsMap.size() == totalNumberOfArtists) {
                        callOnFinished.accept(artistsMap);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("FetchUtil", error.toString());
                }
            });
            Volley.newRequestQueue(context).add(req);
        }

    }


    public static void fetchAlbumsFromArtistIds(Context context, List<String> artistIDs, Consumer<Map<String, JSONObject>> callOnFinished) {
        int totalNumberOfArtists = artistIDs.size();

        Map<String, JSONObject> artistAlbumsMap = new HashMap<>();

        for (int i = 0; i < artistIDs.size(); i++) {
            String artistID = artistIDs.get(i);
            String albumURL = "https://assignment8csci571.uw.r.appspot.com/albums/" + artistID;
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, albumURL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject albumsResponse) {
                    artistAlbumsMap.put(artistID, albumsResponse);
                    if (artistAlbumsMap.size() == totalNumberOfArtists) {
                        callOnFinished.accept(artistAlbumsMap);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("FetchUtil", error.toString());
                }
            });
            Volley.newRequestQueue(context).add(req);
        }

    }


}
