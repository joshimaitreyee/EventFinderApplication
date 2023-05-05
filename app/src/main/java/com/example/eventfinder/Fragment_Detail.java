package com.example.eventfinder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Detail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Detail extends Fragment {

    private JSONObject detailsJSON;


    public Fragment_Detail() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                this.detailsJSON = new JSONObject(getArguments().getString("detailResponse"));
                Log.e("Fragment_Detail", this.detailsJSON.toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment__detail, container, false);

        JSONArray attractionsArray = Objects.requireNonNull(this.detailsJSON.optJSONObject("_embedded")).optJSONArray("attractions");
        String artists_teams = "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Objects.requireNonNull(attractionsArray).length(); i++) {
            JSONObject attractionObj = attractionsArray.optJSONObject(i);
            String attractionName = attractionObj.optString("name");
            if (!attractionName.isEmpty()) {
                sb.append(attractionName).append(" | ");
            }
        }
        artists_teams = sb.substring(0, sb.length() - 2);


        TextView artistsView = view.findViewById(R.id.attractions);
        artistsView.setText(artists_teams);
        artistsView.setSelected(true);

        String venue = Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONObject("_embedded")).optJSONArray("venues")).optJSONObject(0).optString("name");

        TextView venueView = view.findViewById(R.id.venue);
        venueView.setText(venue);
        venueView.setSelected(true);

        String date = Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONObject("dates")).optJSONObject("start")).optString("localDate");

        TextView dateView = view.findViewById(R.id.date);
        dateView.setText(date);

        String time = Util.getTime(Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONObject("dates")).optJSONObject("start")).optString("localTime"));

        TextView timeView = view.findViewById(R.id.time);
        timeView.setText(time);

        String segment = Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONArray("classifications"))
                .optJSONObject(0).optJSONObject("segment")).optString("name");
        String genre = Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONArray("classifications"))
                .optJSONObject(0).optJSONObject("genre")).optString("name");
        String subGenre = Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONArray("classifications"))
                .optJSONObject(0).optJSONObject("subGenre")).optString("name");
        String type = Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONArray("classifications"))
                .optJSONObject(0).optJSONObject("type")).optString("name");
        String subType = Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONArray("classifications"))
                .optJSONObject(0).optJSONObject("subType")).optString("name");

        List<String> genres = new ArrayList<>();
        if (!segment.equals("Undefined")) {
            genres.add(segment);
        }
        if (!genre.equals("Undefined")) {
            genres.add(genre);
        }
        if (!subGenre.equals("Undefined")) {
            genres.add(subGenre);
        }
        if (!type.equals("Undefined")) {
            genres.add(type);
        }
        if (!subType.equals("Undefined")) {
            genres.add(subType);
        }
        String genresStr = TextUtils.join(" | ", genres);

        TextView genreView = view.findViewById(R.id.genres);
        genreView.setText(genresStr);
        genreView.setSelected(true);


        String priceRanges = Objects.requireNonNull(this.detailsJSON.optJSONArray("priceRanges")).optJSONObject(0).optString("min", "") + " - " +
                Objects.requireNonNull(this.detailsJSON.optJSONArray("priceRanges")).optJSONObject(0).optString("max", "") + " (" +
                Objects.requireNonNull(this.detailsJSON.optJSONArray("priceRanges")).optJSONObject(0).optString("currency", "") + ")";

        TextView priceView = view.findViewById(R.id.price_range);
        priceView.setText(priceRanges);
        priceView.setSelected(true);

        String statusCode = Objects.requireNonNull(Objects.requireNonNull(this.detailsJSON.optJSONObject("dates")).optJSONObject("status")).optString("code");

        CardView statusCard = view.findViewById(R.id.ticket_status_holder);
        TextView ticketStatus = view.findViewById(R.id.ticket_status);

        switch (statusCode) {
            case "onsale":
                statusCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green));
                ticketStatus.setText("On Sale");
                break;
            case "offsale":
                statusCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red));
                ticketStatus.setText("Off Sale");
                break;
            case "cancelled":
            case "canceled":
                statusCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black));
                ticketStatus.setText("Canceled");
                break;
            case "postponed":
                statusCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange));
                ticketStatus.setText("Postponed");
                break;
            case "rescheduled":
                statusCard.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange));
                ticketStatus.setText("Rescheduled");
                break;
        }

        String ticketUrlValue = this.detailsJSON.optString("url");

        TextView ticketUrlView = view.findViewById(R.id.ticket_url);
        SpannableString content = new SpannableString(ticketUrlValue);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        ticketUrlView.setText(content);
        ticketUrlView.setSelected(true);

        ticketUrlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(ticketUrlValue));
                startActivity(intent);
            }
        });

        ImageView seatView = view.findViewById(R.id.seat_image_url);
        String seatUrl = Objects.requireNonNull(this.detailsJSON.optJSONObject("seatmap")).optString("staticUrl");
        Glide.with(requireContext()).load(seatUrl).into(seatView);

        return view;
    }
}