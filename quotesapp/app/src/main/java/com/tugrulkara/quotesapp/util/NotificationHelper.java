package com.tugrulkara.quotesapp.util;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.view.SplashActivity;
import com.tugrulkara.quotesapp.model.Quote;

import java.util.List;
import java.util.Random;

public class NotificationHelper extends ContextWrapper {
    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    public NotificationCompat.Builder getChannelNotification(List<Quote> quotesList) {

        long when = System.currentTimeMillis();

        Intent notificationIntent = new Intent(getBaseContext(), SplashActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        /*Intent intent1=new Intent(getBaseContext(), SplashActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(getBaseContext(),1,intent1,0);*/

        Random random=new Random();
        int x =random.nextInt(quotesList.size());

        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(quotesList.get(x).getAuthor())
                .setContentText(quotesList.get(x).getQuote_txt()).setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setLargeIcon(BitmapFactory.decodeResource(getBaseContext().getResources(), R.mipmap.ic_launcher))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(quotesList.get(x).getQuote_txt()))
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});  // Declair VIBRATOR Permission in AndroidManifest.xml

    }
}