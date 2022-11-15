package com.tugrulkara.quotesadmin.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;

import com.tugrulkara.quotesadmin.R;

public class Share {

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getResources().getString(R.string.menu_quotes),
                text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

    public static void withText(final Context context, final String quoteText) {
        new Thread(() -> {
            StringBuilder quoteBuilder = new StringBuilder(quoteText);
            if (!Storage.getPremium(context)) {
                quoteBuilder.append("\n\n").append(context.getString(R.string.download_this_app));
            }
            context.startActivity(Intent.createChooser(new Intent().setAction(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, quoteBuilder.toString()).setType("text/plain"), context.getString(R.string.share)));
        }).start();
    }

}
