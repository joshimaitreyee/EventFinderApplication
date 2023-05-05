package com.example.eventfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CardDetails extends AppCompatActivity {

    private static final String DETAILS = "DETAILS";
    private static final String ARTISTS = "ARTIST(S)";
    private static final String VENUE = "VENUE";
    /*
    E/CardDetails: {"id":"G5vYZ9KDbiIMY","name":"Ed Sheeran: +-=÷x Tour","imageURL":"https:\/\/s1.ticketm.net\/dam\/a\/7d5\/97b67038-f926-4676-be88-ebf94cb5c7d5_1802151_TABLET_LANDSCAPE_3_2.jpg","venue":"Levi's® Stadium","categoryEvent":"Music","date":"2023-09-16","time":"06:00 PM","ticketURL":"https:\/\/www.ticketmaster.com\/ed-sheeran-x-tour-santa-clara-california-09-16-2023\/event\/1C005D3ED8B28DF0"}

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_EventFinder);
        setContentView(R.layout.activity_card_details);

        try {
            JSONObject cardDetails = new JSONObject(getIntent().getStringExtra("cardInfo"));
            activitySetup(cardDetails);
            bringData(cardDetails);


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void bringData(JSONObject cardDetails) throws JSONException {
        String eventId = cardDetails.getString("id");
        FetchUtil.fetchSpecificEvent(getApplicationContext(), eventId, new Consumer<JSONObject>() {
            @Override
            public void accept(JSONObject eventResponse) {
                try {
                    boolean notAMusicEvent = false;
                    JSONObject venueParam = (JSONObject) eventResponse.getJSONObject("_embedded").getJSONArray("venues").get(0);
                    List<String> artistIDs = new ArrayList<>();
                    List<String> artistNames = Util.extractArtistNames(eventResponse);
                    if (artistNames.size() == 0) {
                        artistNames.add("Ed Sheeran");
                        notAMusicEvent = true;
                    }


                    boolean finalNotAMusicEvent = notAMusicEvent;
                    FetchUtil.getArtistsData(getApplicationContext(), artistNames, new Consumer<Map<String, JSONObject>>() {
                        @Override
                        public void accept(Map<String, JSONObject> artistsMap) {

                            for (int i = 0; i < artistNames.size(); i++) {
                                try {
                                    artistIDs.add(Objects.requireNonNull(artistsMap.get(artistNames.get(i))).getJSONObject("artists").getJSONArray("items").getJSONObject(0).getString("id"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            FetchUtil.fetchAlbumsFromArtistIds(getApplicationContext(), artistIDs, new Consumer<Map<String, JSONObject>>() {
                                @Override
                                public void accept(Map<String, JSONObject> albumsMap) {
                                    try {
                                        JSONArray artistsResponseArray = createArtistsObject(artistNames, artistsMap, albumsMap);
                                        Bundle bundle = new Bundle();
                                        bundle.putBoolean("isNotMusic", finalNotAMusicEvent);
                                        bundle.putString("detailResponse", eventResponse.toString());
                                        bundle.putString("venueResponse", venueParam.toString());
                                        bundle.putString("artistsResponseArray", artistsResponseArray.toString());


                                        TabLayout cardDetailsTabLayout = findViewById(R.id.card_details_tab);
                                        ViewPager2 viewPager2Card = findViewById(R.id.card_detail_viewpager2);
                                        AdapterCardDetailViewPager2 adapter = new AdapterCardDetailViewPager2(getSupportFragmentManager(), getLifecycle(), bundle);
                                        viewPager2Card.setAdapter(adapter);

                                        new TabLayoutMediator(cardDetailsTabLayout, viewPager2Card, new TabLayoutMediator.TabConfigurationStrategy() {
                                            @Override
                                            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                                                switch (position) {
                                                    case 0:
                                                        tab.setText(DETAILS);
                                                        tab.setIcon(R.drawable.info_icon);
                                                        break;
                                                    case 1:
                                                        tab.setText(ARTISTS);
                                                        tab.setIcon(R.drawable.artist_icon);
                                                        break;
                                                    case 2:
                                                        tab.setText(VENUE);
                                                        tab.setIcon(R.drawable.venue_icon);
                                                        break;
                                                }
                                            }
                                        }).attach();

                                        findViewById(R.id.spinner_card_detail).setVisibility(View.GONE);
                                        cardDetailsTabLayout.setVisibility(View.VISIBLE);
                                        viewPager2Card.setVisibility(View.VISIBLE);

                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }


                                }
                            });

                        }
                    });


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }


    private JSONArray createArtistsObject(List<String> artistNames, Map<String, JSONObject> artistsMap, Map<String, JSONObject> albumsMap) throws JSONException {

        JSONArray processedArtists = new JSONArray();

        for (int i = 0; i < artistNames.size(); i++) {
            String artistName = artistNames.get(i);
            JSONObject artistObject = (JSONObject) Objects.requireNonNull(artistsMap.get(artistName)).getJSONObject("artists").getJSONArray("items").get(0);
            String artistId = artistObject.getString("id");
            String artistSpotifyUrl = artistObject.getJSONObject("external_urls").getString("spotify");
            String artistPopularity = artistObject.getString("popularity");
            String artistFollowers = Util.getFollowers(artistObject.getJSONObject("followers").getString("total"));
            String artistImageUrl = artistObject.getJSONArray("images").getJSONObject(0).getString("url");

            JSONArray artistAlbums = Objects.requireNonNull(albumsMap.get(artistId)).getJSONArray("items");

            List<String> topThreeAlbums = new ArrayList<>();

            for (int j = 0; j < artistAlbums.length(); j++) {
                topThreeAlbums.add(artistAlbums.getJSONObject(j).getJSONArray("images").getJSONObject(0).getString("url"));
            }

            processedArtists.put(new JSONObject()
                    .put("artistId", artistId)
                    .put("artistName", artistName)
                    .put("artistSpotifyUrl", artistSpotifyUrl)
                    .put("artistPopularity", artistPopularity)
                    .put("artistFollowers", artistFollowers)
                    .put("artistImageUrl", artistImageUrl)
                    .put("album1ImageUrl", topThreeAlbums.get(0))
                    .put("album2ImageUrl", topThreeAlbums.get(1))
                    .put("album3ImageUrl", topThreeAlbums.get(2))
            );

        }

        return processedArtists;


    }


    private void activitySetup(JSONObject cardDetails) throws JSONException {

        FavoritesManager favoritesManager = new FavoritesManager(getApplicationContext());
        findViewById(R.id.card_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView cardTitle = findViewById(R.id.card_detail_title);
        cardTitle.setText(cardDetails.getString("name"));
        cardTitle.setSelected(true);

        findViewById(R.id.facebook_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse("https://www.facebook.com/sharer/sharer.php?u=" + cardDetails.getString("ticketURL")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                startActivity(intent);

            }
        });


        findViewById(R.id.twitter_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse("https://twitter.com/intent/tweet?text=Check" + cardDetails.getString("name") + " on Ticketmaster. " + cardDetails.getString("ticketURL")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                startActivity(intent);

            }
        });

        ImageView heart = findViewById(R.id.heart_card_details);
        if (favoritesManager.presentInStorage(cardDetails.getString("id"))) {
            heart.setImageResource(R.drawable.heart_filled);
        } else {
            heart.setImageResource(R.drawable.heart_outline);
        }

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String eventId = cardDetails.getString("id");
                    String eventName = cardDetails.getString("name");
                    if (favoritesManager.presentInStorage(eventId)) {
                        favoritesManager.removeFromStorage(eventId);
                        heart.setImageResource(R.drawable.heart_outline);
                        Util.displayMessage(findViewById(android.R.id.content), getApplicationContext(), eventName + " removed from favorites");
                    } else {
                        favoritesManager.addToStorage(cardDetails);
                        heart.setImageResource(R.drawable.heart_filled);
                        Util.displayMessage(findViewById(android.R.id.content), getApplicationContext(), eventName + " added to favorites");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }
}