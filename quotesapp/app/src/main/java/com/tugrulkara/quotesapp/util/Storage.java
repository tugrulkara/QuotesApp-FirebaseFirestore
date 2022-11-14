package com.tugrulkara.quotesapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.view.View;

import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.model.Quote;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class Storage {

    private static SharedPreferences mPreferences;

    private static SharedPreferences getPreferences(Context context) {
        if (mPreferences == null) {
            mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return mPreferences;
    }

    public static boolean getPremium(Context context) {
        return getPreferences(context).getBoolean("premium", false);
    }


    public static void addFavorite(Quote quote, Context mContext, View view){

        SQLiteDatabase database=mContext.getApplicationContext().openOrCreateDatabase("quotes", MODE_PRIVATE,null);

        String quote_name=String.valueOf(quote.getQuote_txt());
        String auth_name=String.valueOf(quote.getAuthor());
        String cat_name=String.valueOf(quote.getCategory());
        String uuid=String.valueOf(quote.getQuote_id());

        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS quotes(id INTEGER PRIMARY KEY,quote_name VARCHAR, auth_name VARCHAR,cat_name VARCHAR,uuid VARCHAR)");

            String sqlString="INSERT INTO quotes(quote_name,auth_name,cat_name,uuid) VALUES(?,?,?,?)";
            SQLiteStatement sqLiteStatement= database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,quote_name);
            sqLiteStatement.bindString(2,auth_name);
            sqLiteStatement.bindString(3,cat_name);
            sqLiteStatement.bindString(4,uuid);

            sqLiteStatement.execute();

            Snackbar.showText(view, R.string.add_favorite);
            //Toast.makeText(mContext,"Favorilere eklendi",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void getFavIdData(ArrayList<String> favQuoteIdList,Context mContext){

        SQLiteDatabase database=mContext.getApplicationContext().openOrCreateDatabase("quotes", MODE_PRIVATE,null);

        try {
            Cursor cursor=database.rawQuery("SELECT * FROM quotes",null);
            int uuidIx=cursor.getColumnIndex("uuid");

            favQuoteIdList.clear();
            while (cursor.moveToNext()){
                favQuoteIdList.add(cursor.getString(uuidIx));
            }
            cursor.close();

        }catch (Exception e){

        }

    }

    public static void deleteFav(Quote quote,Context mContext,View view){

        SQLiteDatabase database=mContext.getApplicationContext().openOrCreateDatabase("quotes", MODE_PRIVATE,null);

        try {
            String sqlNote="DELETE FROM quotes WHERE uuid=?";
            SQLiteStatement sqLiteStatement2=database.compileStatement(sqlNote);
            sqLiteStatement2.bindString(1,quote.getQuote_id());
            sqLiteStatement2.execute();

            Snackbar.showText(view, R.string.remove_favorite);
            //Toast.makeText(mContext,"Favorilerden çıkarıldı",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
