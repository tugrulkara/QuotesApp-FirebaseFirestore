package com.tugrulkara.quotesadmin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.tugrulkara.quotesadmin.util.NetworkUtils;
import com.tugrulkara.quotesadmin.util.Snackbar;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (NetworkUtils.isConnected(SplashActivity.this)) {
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
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }, SPLASH_DURATION);
    }
}