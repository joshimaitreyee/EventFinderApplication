package com.example.eventfinder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public final class Util {

    private Util() {

    }

    public static void displayMessage(View view, Context context, String message) {

        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray)));
        snackbar.setTextColor(ContextCompat.getColor(context, R.color.black));
        ViewGroup.MarginLayoutParams config = (ViewGroup.MarginLayoutParams) snackbar.getView().getLayoutParams();
        config.setMargins(60, 0, 60, 40);
        snackbar.getView().setLayoutParams(config);
        snackbar.show();

    }
}

