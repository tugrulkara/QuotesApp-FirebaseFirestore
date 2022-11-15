package com.tugrulkara.quotesapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tugrulkara.quotesapp.NotificationHelper;
import com.tugrulkara.quotesapp.model.Quote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {

    public List<Quote> quotesList= new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("Alarm çalışıyor");

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Quote")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()){

                    quotesList.clear();

                    for (QueryDocumentSnapshot snapshot : task.getResult()){

                        Map<String, Object> data = snapshot.getData();

                        String quote_name = (String) data.get("quote_name");
                        String quote_id = (String) data.get("quote_id");
                        String auth_name = (String) data.get("auth_name");
                        String cat_name = (String) data.get("cat_name");

                        Quote quote=new Quote();

                        quote.setQuote_id(quote_id);
                        quote.setQuote_txt(quote_name);
                        quote.setAuthor(auth_name);
                        quote.setCategory(cat_name);

                        quotesList.add(quote);

                    }
                    System.out.println("quotesList sorgusu yapıldı");

                    if (!(quotesList.size() <= 0)){
                        NotificationHelper notificationHelper = new NotificationHelper(context);
                        NotificationCompat.Builder nb = notificationHelper.getChannelNotification(quotesList);
                        notificationHelper.getManager().notify(5, nb.build());
                        System.out.println("Bildirim paylaşıldı");
                    }

                }else {
                    System.out.println("quotesList sorgusu yapılamadı");
                }
            }
        });

    }

}
