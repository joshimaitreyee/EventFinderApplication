package com.example.eventfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class CardDetails extends AppCompatActivity {

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

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

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