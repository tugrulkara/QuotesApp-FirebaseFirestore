package com.tugrulkara.quotesapp.util;


import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.tugrulkara.quotesapp.R;

import de.mateware.snacky.Snacky;


public class Snackbar {

    public static void networkUnavailable(Activity activity, View.OnClickListener clickListener) {
        try {
            Snacky.builder().setActivity(activity)
                    .setTextSize(16)
                    .setTextColor(Color.WHITE)
                    .setActionTextColor(Color.WHITE)
                    .setDuration(Snacky.LENGTH_LONG)
                    .setText(R.string.conne_msg1)
                    .setActionText(R.string.reload)
                    .setActionClickListener(clickListener)
                    .error()
                    .show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    public static void showText(Activity activity, int stringResId) {
        try {
            Snacky.builder()
                    .setActivity(activity)
                    .setTextSize(16)
                    .setTextColor(Color.WHITE)
                    .setDuration(Snacky.LENGTH_SHORT)
                    .setText(activity.getString(stringResId))
                    .setBackgroundColor(ContextCompat.getColor(activity, R.color.red_900))
                    .build()
                    .show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public static void showText(View view, int stringResId) {
        try {
            Snacky.builder()
                    .setView(view)
                    .setTextSize(16)
                    .setTextColor(Color.WHITE)
                    .setDuration(Snacky.LENGTH_SHORT)
                    .setText(view.getContext().getString(stringResId))
                    .setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.red_900))
                    .build()
                    .show();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
