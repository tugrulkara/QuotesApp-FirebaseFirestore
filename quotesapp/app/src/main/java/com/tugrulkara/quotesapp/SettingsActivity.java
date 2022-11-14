package com.tugrulkara.quotesapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.tugrulkara.quotesapp.util.AlarmReceiver;
import com.tugrulkara.quotesapp.util.Setting;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private CardView share_app,rate_app,contact_us_app,privacy;

    private Toolbar toolbar;

    Switch mySwitch = null;

    boolean notif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        share_app=findViewById(R.id.share_app);
        rate_app=findViewById(R.id.rate_app);
        contact_us_app=findViewById(R.id.contact_us_app);
        privacy=findViewById(R.id.privacy_policy_app);

        mySwitch = (Switch) findViewById(R.id.notif_switch);
        mySwitch.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) this);

        SharedPreferences prefs = getSharedPreferences("notif", MODE_PRIVATE);
        notif = prefs.getBoolean("notif", true);

        if (notif){
            mySwitch.setChecked(true);
        }else{
            mySwitch.setChecked(false);
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ayarlar");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        rate_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.openAppPage(SettingsActivity.this);
            }
        });

        share_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.shareApp(SettingsActivity.this);
            }
        });

        contact_us_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.contactUs(SettingsActivity.this);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Setting.openPrivacyPolicy(SettingsActivity.this);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            // do something when check is selected
            setAlarm();

            SharedPreferences prefs = getSharedPreferences("notif", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notif", true);
            editor.apply();

        } else {
            //do something when unchecked
            Intent myIntent = new Intent(SettingsActivity.this, AlarmReceiver.class);
            int ALARM1_ID = 10000;
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    SettingsActivity.this, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) SettingsActivity.this.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);

            SharedPreferences prefs = getSharedPreferences("notif", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("notif", false);
            editor.apply();

            System.out.println("Alarm kapatıldı");
        }
    }

    public void setAlarm() {
        // Quote in Morning at 08:32:00 AM
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 13);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar cur = Calendar.getInstance();

        if (cur.after(calendar)) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent myIntent = new Intent(SettingsActivity.this, AlarmReceiver.class);
        int ALARM1_ID = 10000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                SettingsActivity.this, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) SettingsActivity.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        System.out.println("Alarm Ayarlandı");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

}