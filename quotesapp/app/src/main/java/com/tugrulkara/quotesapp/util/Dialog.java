package com.tugrulkara.quotesapp.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tugrulkara.quotesapp.R;

import java.util.Random;

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

        final ConstraintLayout constraintLayout=dialog.findViewById(R.id.cl_quote_of_the_day);
        final TextView textCategory = dialog.findViewById(R.id.text_category);
        final TextView textAuthor = dialog.findViewById(R.id.text_author);
        final TextView textQuote = dialog.findViewById(R.id.text_quote);

        String[] colors={"#e1798f","#b786a4","#efad73","#f08a99","#a3bdd4","#c38080","#80ca9f","#89b8b3","#fe8f8c"};

        Random random=new Random();
        int x = random.nextInt(8);
        constraintLayout.setBackgroundColor(Color.parseColor(colors[x]));

        firebaseFirestore.collection("QuoteOfDay")
                .document("alinti")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        String cat_name = (String) document.get("cat_name");
                        String auth_name = (String) document.get("auth_name");
                        String quote = (String) document.get("quote");

                        textAuthor.setText(auth_name);
                        textCategory.setText(cat_name);
                        textQuote.setText(quote);
                    } else {
                        Toast.makeText(context,"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(context,"Veriler Yüklenemedi Lütfen Uygulamayı Yeniden Başlatın!",Toast.LENGTH_LONG).show();
                }
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
