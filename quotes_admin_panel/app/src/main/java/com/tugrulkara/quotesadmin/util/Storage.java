package com.tugrulkara.quotesadmin.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tugrulkara.quotesadmin.R;
import com.tugrulkara.quotesadmin.model.Author;
import com.tugrulkara.quotesadmin.model.Category;
import com.tugrulkara.quotesadmin.model.Quote;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Storage {
    private static SharedPreferences mPreferences;
    private static SQLiteDatabase database;

    private static FirebaseFirestore firebaseFirestore;

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

        database=mContext.getApplicationContext().openOrCreateDatabase("quotes", MODE_PRIVATE,null);

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

        database=mContext.getApplicationContext().openOrCreateDatabase("quotes", MODE_PRIVATE,null);
        try {

            if (database!=null){
                Cursor cursor=database.rawQuery("SELECT * FROM quotes",null);
                int uuidIx=cursor.getColumnIndex("uuid");

                favQuoteIdList.clear();
                while (cursor.moveToNext()){
                    favQuoteIdList.add(cursor.getString(uuidIx));
                }
                cursor.close();
            }

        }catch (Exception e){

        }
    }

    public static void deleteFav(Quote quote,Context mContext,View view){

        database=mContext.getApplicationContext().openOrCreateDatabase("quotes", MODE_PRIVATE,null);

        String sqlNote="DELETE FROM quotes WHERE uuid=?";
        SQLiteStatement sqLiteStatement2=database.compileStatement(sqlNote);
        sqLiteStatement2.bindString(1,quote.getQuote_id());
        sqLiteStatement2.execute();

        Snackbar.showText(view, R.string.remove_favorite);
        //Toast.makeText(mContext,"Favorilerden çıkarıldı",Toast.LENGTH_SHORT).show();

    }

    public static void EditAuth(int position,ArrayList<Author> authList,Context mContext){

        firebaseFirestore=FirebaseFirestore.getInstance();


        final android.app.Dialog dialog = new android.app.Dialog(mContext);
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditText auth_txt =dialog.findViewById(R.id.item_txt);
        AppCompatButton bt_post_exit=dialog.findViewById(R.id.bt_post_exit);

        auth_txt.setText(authList.get(position).getAuth_name());

        bt_post_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        final AppCompatButton post_submit = (AppCompatButton) dialog.findViewById(R.id.post_submit);
        ((EditText) dialog.findViewById(R.id.item_txt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                post_submit.setEnabled(!s.toString().trim().isEmpty());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        post_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String auth_name=auth_txt.getText().toString();

                HashMap<String,Object> mData=new HashMap<>();
                mData.put("auth_name",auth_name);

                firebaseFirestore.collection("Author")
                        .document(authList.get(position).getAuth_id())
                        .update(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toast.makeText(mContext.getApplicationContext(),"Yazar düzenleme başarılı...",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext.getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    public static void DeleteAuth(int position,ArrayList<Author> authList,Context mContext){

        firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Author")
                .document(authList.get(position).getAuth_id())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext.getApplicationContext(),"Yazar silme başarılı...",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext.getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

            }
        });

    }


    public static void EditCat(int position, ArrayList<Category> catList, Context mContext){

        firebaseFirestore=FirebaseFirestore.getInstance();


        final android.app.Dialog dialog = new android.app.Dialog(mContext);
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditText cat_txt =dialog.findViewById(R.id.item_txt);
        AppCompatButton bt_post_exit=dialog.findViewById(R.id.bt_post_exit);

        cat_txt.setText(catList.get(position).getCat_name());

        bt_post_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        final AppCompatButton post_submit = (AppCompatButton) dialog.findViewById(R.id.post_submit);
        ((EditText) dialog.findViewById(R.id.item_txt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                post_submit.setEnabled(!s.toString().trim().isEmpty());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        post_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String cat_name=cat_txt.getText().toString();

                HashMap<String,Object> mData=new HashMap<>();
                mData.put("cat_name",cat_name);

                firebaseFirestore.collection("Category")
                        .document(catList.get(position).getCat_id())
                        .update(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dialog.dismiss();
                        Toast.makeText(mContext.getApplicationContext(),"Kategori düzenleme başarılı...",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext.getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    public static void DeleteCat(int position,ArrayList<Category> catList,Context mContext){

        firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Category")
                .document(catList.get(position).getCat_id())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(mContext.getApplicationContext(),"Kategori silme başarılı...",Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext.getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();

            }
        });

    }

}
