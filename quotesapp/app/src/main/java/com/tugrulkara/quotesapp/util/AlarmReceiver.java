package com.tugrulkara.quotesapp.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

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
    public List<Quote> sliderList=new ArrayList<>();
    int i=0;

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

                    //Delete(context);

                   /* try {
                        if (!(quotesList.size() <= 0)){

                            for(i = 0; i < 3; i++) {
                                Random random=new Random();
                                int x =random.nextInt(quotesList.size());

                                addSlider(quotesList,context,x);

                                //System.out.println("eklendi: "+i);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/

                    //getSliderData(context);

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

    public static void addSlider(List<Quote> quotesList, Context mContext,int x){

        SQLiteDatabase database=mContext.getApplicationContext().openOrCreateDatabase("slider", MODE_PRIVATE,null);

        String quote_name=String.valueOf(quotesList.get(x).getQuote_txt());
        String auth_name=String.valueOf(quotesList.get(x).getAuthor());
        String uuid=String.valueOf(quotesList.get(x).getQuote_id());

        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS slider(id INTEGER PRIMARY KEY,quote_name VARCHAR, auth_name VARCHAR,uuid VARCHAR)");

            String sqlString="INSERT INTO slider(quote_name,auth_name,uuid) VALUES(?,?,?)";
            SQLiteStatement sqLiteStatement= database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,quote_name);
            sqLiteStatement.bindString(2,auth_name);
            sqLiteStatement.bindString(3,uuid);

            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getSliderData(Context mContext){

        SQLiteDatabase database=mContext.getApplicationContext().openOrCreateDatabase("slider", MODE_PRIVATE,null);

        try {

            Cursor cursor=database.rawQuery("SELECT * FROM slider ORDER BY id DESC",null);
            int quoteIx=cursor.getColumnIndex("quote_name");
            int authIx=cursor.getColumnIndex("auth_name");
            int uuidIx=cursor.getColumnIndex("uuid");

            sliderList.clear();

            while (cursor.moveToNext()){

                Quote quote=new Quote();

                quote.setQuote_txt(cursor.getString(quoteIx));
                quote.setAuthor(cursor.getString(authIx));
                quote.setQuote_id(cursor.getString(uuidIx));

                sliderList.add(quote);

            }
            cursor.close();

            //System.out.println("sliderList sorgusu yapıldı");
        }catch (Exception e){


        }

    }


    public void Delete(Context mContext){

        SQLiteDatabase database=mContext.getApplicationContext().openOrCreateDatabase("slider", MODE_PRIVATE,null);

        try {

            String sqlSlider="DELETE FROM slider";
            SQLiteStatement sqLiteStatement2=database.compileStatement(sqlSlider);
            sqLiteStatement2.execute();

            //System.out.println("Sqlite tablosu silindi");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
