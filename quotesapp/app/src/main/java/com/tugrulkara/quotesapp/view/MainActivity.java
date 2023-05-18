package com.tugrulkara.quotesapp.view;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.multidex.MultiDex;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.tugrulkara.quotesapp.R;
import com.tugrulkara.quotesapp.view.fragment.AuthorsFragment;
import com.tugrulkara.quotesapp.view.fragment.CategoryFragment;
import com.tugrulkara.quotesapp.view.fragment.FavoritesFragment;
import com.tugrulkara.quotesapp.view.fragment.QuotesFragment;
import com.tugrulkara.quotesapp.util.AlarmReceiver;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    boolean doubleBackToExitPressedOnce = false;
    public static NavigationView navigationView;
    Toolbar toolbar;
    public static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart) {
            showStartDialog();
        }

        Toast.makeText(MainActivity.this,"Yükleniyor...",Toast.LENGTH_LONG).show();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();

        QuotesFragment quotesFragment = new QuotesFragment();
        loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);

        bottomNavigationView=findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.quotes_nav_bottom:

                        QuotesFragment quotesFragment = new QuotesFragment();
                        navigationView.setCheckedItem(R.id.menu_add_quotes);
                        loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);
                        return true;

                    case R.id.authors_nav_bottom:

                        AuthorsFragment authorsFragment=new AuthorsFragment();
                        navigationView.setCheckedItem(R.id.menu_add_authors);
                        loadFrag(authorsFragment, getString(R.string.menu_author), fragmentManager);
                        return true;

                    case R.id.category_nav_bottom:

                        CategoryFragment categoryFragment=new CategoryFragment();
                        navigationView.setCheckedItem(R.id.menu_add_category);
                        loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                        return true;

                    case R.id.favorites_nav_bottom:

                        FavoritesFragment favoritesFragment=new FavoritesFragment();
                        navigationView.setCheckedItem(R.id.menu_add_favorites);
                        loadFrag(favoritesFragment, getString(R.string.menu_favorites), fragmentManager);

                        return true;
                }
                return true;
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {

                    case R.id.menu_add_quotes:

                        bottomNavigationView.setSelectedItemId(R.id.quotes_nav_bottom);
                        return true;

                    case R.id.menu_add_authors:

                        bottomNavigationView.setSelectedItemId(R.id.authors_nav_bottom);
                        return true;

                    case R.id.menu_add_category:

                        bottomNavigationView.setSelectedItemId(R.id.category_nav_bottom);
                        return true;

                    case R.id.menu_add_favorites:

                        bottomNavigationView.setSelectedItemId(R.id.favorites_nav_bottom);
                        return true;

                    case R.id.menu_add_quotes_maker:

                        menuItem.setChecked(false);
                        Intent intent=new Intent(MainActivity.this,QuotesMakerActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.menu_add_settings:

                        Intent intent_settings=new Intent(MainActivity.this,SettingsActivity.class);
                        startActivity(intent_settings);
                        return true;

                    case R.id.menu_go_logout:

                        logOut();
                        return true;

                    default:
                        return true;
                }
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_side_nav);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        //ft.addToBackStack(null);
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Title);
        }
    }

    private void logOut() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.menu_log_out))
                .setMessage(getString(R.string.logout_msg))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_logout)
                .show();
    }
    private void showStartDialog() {

        setAlarm();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
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

        Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        int ALARM1_ID = 10000;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, ALARM1_ID, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        System.out.println("Alarm Ayarlandı");
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0 ) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_key), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

}