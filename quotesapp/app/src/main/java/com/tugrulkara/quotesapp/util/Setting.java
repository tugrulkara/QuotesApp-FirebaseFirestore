package com.tugrulkara.quotesapp.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tugrulkara.quotesapp.R;

public class Setting {

    public static void openPublisherPage(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:SimurgApps")));
    }

    public static void openAppPage(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
    }

    public static void openPrivacyPolicy(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://")));
    }

    public static void contactUs(Context context) {
        context.startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "simurgapps@gmail.com")), "Send mail"));
    }

    public static void shareApp(Context context) {
        Intent intent = new Intent().setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, context.getString(R.string.download_this_app)).setType("text/plain");
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_this_app)));
    }

}