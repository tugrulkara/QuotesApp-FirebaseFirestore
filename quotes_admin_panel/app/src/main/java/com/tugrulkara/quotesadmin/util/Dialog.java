package com.tugrulkara.quotesadmin.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tugrulkara.quotesadmin.R;

import java.util.HashMap;

public class Dialog {

    public static void quoteOfTheDay(final Context context) {

        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

        final android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.setContentView(R.layout.dialog_quote_of_the_day);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextView textCategory = dialog.findViewById(R.id.text_category);
        final TextView textAuthor = dialog.findViewById(R.id.text_author);
        final TextView textQuote = dialog.findViewById(R.id.text_quote);
        final ImageView image_item = dialog.findViewById(R.id.image_item);

        firebaseFirestore.collection("QuoteOfDay")
                .document("alinti").
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot snapshot=task.getResult();
                if (task.isSuccessful()){
                    String cat_name = (String) snapshot.get("cat_name");
                    String auth_name = (String) snapshot.get("auth_name");
                    String quote = (String) snapshot.get("quote");

                    textAuthor.setText(auth_name);
                    textCategory.setText(cat_name);
                    textQuote.setText(quote);
                }else {
                    Toast.makeText(context,"Veriler Yüklenemedi!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        image_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final android.app.Dialog dialog = new android.app.Dialog(context);
                dialog.setContentView(R.layout.dialog_add_quote_of_the_day);
                dialog.setCancelable(true);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;

                EditText quote_txt =dialog.findViewById(R.id.quote_txt);
                EditText cat_txt =dialog.findViewById(R.id.cat_name);
                EditText auth_txt =dialog.findViewById(R.id.auth_name);
                AppCompatButton bt_post_exit=dialog.findViewById(R.id.bt_post_exit);


                bt_post_exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });


                final AppCompatButton post_submit = (AppCompatButton) dialog.findViewById(R.id.post_submit);
                ((EditText) dialog.findViewById(R.id.quote_txt)).addTextChangedListener(new TextWatcher() {
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

                        if (quote_txt.getText().toString().trim().length()<=0 ||
                                cat_txt.getText().toString().trim().length()<=0 || auth_txt.getText().toString().trim().length()<=0) {

                            Toast.makeText(context.getApplicationContext(), "Lütfen bir alıntı, kategori, yazar giriniz...", Toast.LENGTH_SHORT).show();
                        }else{
                            String cat_name=cat_txt.getText().toString();
                            String auth_name=auth_txt.getText().toString();
                            String quote=quote_txt.getText().toString();
                            String quote_id="alinti";

                            HashMap<String,Object> mData=new HashMap<>();
                            mData.put("cat_name",cat_name);
                            mData.put("auth_name",auth_name);
                            mData.put("quote",quote);
                            mData.put("quote_id",quote_id);
                            mData.put("timestamp", FieldValue.serverTimestamp());

                            firebaseFirestore.collection("QuoteOfDay")
                                    .document(quote_id)
                                    .set(mData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    dialog.dismiss();
                                    Toast.makeText(context.getApplicationContext(),"Alıntı ekleme başarılı...",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }

                    }
                });

                dialog.show();
                dialog.getWindow().setAttributes(lp);

            }
        });

        textQuote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Share.copyToClipboard(context, textQuote.getText().toString());
                Snackbar.showText(v, R.string.copy_to_clipboard);
                return true;
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

}
