package com.tugrulkara.quotesapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.tugrulkara.quotesapp.util.NetworkUtils;
import com.tugrulkara.quotesapp.util.Setting;
import com.tugrulkara.quotesapp.util.Snackbar;

public class SplashActivity extends AppCompatActivity {

    private ImageView splash_image;
    private FirebaseFirestore firebaseFirestore;
    private static final int SPLASH_DURATION = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splash_image=findViewById(R.id.splash_image);
        Picasso.get().load(R.mipmap.ic_launcher).into(splash_image);

        firebaseFirestore=FirebaseFirestore.getInstance();

        if (NetworkUtils.isConnected(SplashActivity.this)) {
            //updateController();
            splashScreen();
        } else {
            Snackbar.networkUnavailable(this, v -> NetworkUtils.isConnected(SplashActivity.this));
            //Toast.makeText(SplashActivity.this, getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }
    }

    public void splashScreen(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }, SPLASH_DURATION);
    }


    //App Update Controller
    public void updateController(){

        firebaseFirestore.collection("AppUpdateController")
                .document("update")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot snapshot=task.getResult();

                    if (snapshot.exists()) {

                        String getVersionCode=(String) task.getResult().get("versionCode");

                        int getCode=Integer.parseInt(getVersionCode);
                        int appVersionCode=Integer.parseInt(getString(R.string.versionCode));

                        if (getCode>appVersionCode){
                            new AlertDialog.Builder(SplashActivity.this)
                                    .setTitle("Güncelleme Bildirimi")
                                    .setMessage("Yeni Güncelleme Var! Hemen Güncelle!")
                                    .setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Setting.openAppPage(SplashActivity.this);
                                            finish();
                                        }
                                    }).setNegativeButton("Daha Sonra Hatırlat", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    splashScreen();
                                }
                            }).create().show();

                        }else{
                            splashScreen();
                        }
                        //System.out.println("Update kontrolü yapıldı");

                    } else {
                    }

                }
            }
        });

    }
}