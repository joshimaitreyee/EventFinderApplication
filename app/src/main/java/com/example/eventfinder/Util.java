package com.example.eventfinder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import ch.hsr.geohash.GeoHash;

public final class Util {

    private Util() {

    }

    public static void displayMessage(View view, Context context, String message) {

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray_light)));
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.black));
        ViewGroup.MarginLayoutParams config = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        config.setMargins(60, 0, 60, 40);
        snackbar.getView().setLayoutParams(config);
        snackbar.show();

    }

    public static String getTime(String timeString) {

        SimpleDateFormat requiredFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = requiredFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat returnDate = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return date != null ? returnDate.format(date) : null;
    }

    public static String getSegment(String category) {
        if (Objects.equals(category, "Music")) {
            return "KZFzniwnSyZfZ7v7nJ";
        } else if (Objects.equals(category, "Sports")) {
            return "KZFzniwnSyZfZ7v7nE";
        } else if (Objects.equals(category, "Arts & Theatre")) {
            return "KZFzniwnSyZfZ7v7na";
        } else if (Objects.equals(category, "Film")) {
            return "KZFzniwnSyZfZ7v7nn";
        } else if (Objects.equals(category, "Miscellaneous")) {
            return "KZFzniwnSyZfZ7v7n1";
        } else {
            return "";
        }
    }

    public static String getGeoCode(String latitude, String longitude) {
        return GeoHash.geoHashStringWithCharacterPrecision(Double.parseDouble(latitude), Double.parseDouble(longitude), 7);
    }
}

