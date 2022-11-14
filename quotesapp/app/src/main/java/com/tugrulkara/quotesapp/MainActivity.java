package com.tugrulkara.quotesapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
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

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.tugrulkara.quotesapp.fragment.AuthorsFragment;
import com.tugrulkara.quotesapp.fragment.CategoryFragment;
import com.tugrulkara.quotesapp.fragment.FavoritesFragment;
import com.tugrulkara.quotesapp.fragment.QuotesFragment;
import com.tugrulkara.quotesapp.util.AlarmReceiver;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private FrameLayout adContainerView;
    //private InterstitialAd mInterstitialAd;
    private boolean mInterstitialLoaded=true;//appodeal

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

        Appodeal.initialize(this, "ee3effe77385b6d328109a32393480096104d245dff12b24", Appodeal.BANNER | Appodeal.INTERSTITIAL, true);
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);
        Appodeal.isLoaded(Appodeal.INTERSTITIAL);
        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                // Called when interstitial is loaded
            }
            @Override
            public void onInterstitialFailedToLoad() {
                // Called when interstitial failed to load
            }
            @Override
            public void onInterstitialShown() {
                // Called when interstitial is shown
            }
            @Override
            public void onInterstitialShowFailed() {
                // Called when interstitial show failed
            }
            @Override
            public void onInterstitialClicked() {
                // Called when interstitial is clicked
            }
            @Override
            public void onInterstitialClosed() {
                // Called when interstitial is closed
            }
            @Override
            public void onInterstitialExpired()  {
                // Called when interstitial is expired
            }
        });

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart) {
            showStartDialog();
        }

        Toast.makeText(MainActivity.this,"Yükleniyor...",Toast.LENGTH_LONG).show();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());

        adContainerView = findViewById(R.id.ad_view_container);

        /*adContainerView.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.testGecis), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                //Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                //Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });*/

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();

        /*HomePageFragment homePageFragment = new HomePageFragment();
        loadFrag(homePageFragment, getString(R.string.home_page), fragmentManager);*/

        QuotesFragment quotesFragment = new QuotesFragment();
        loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);

        bottomNavigationView=findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    /*case R.id.home_page_nav_bottom:
                        HomePageFragment homePageFragment = new HomePageFragment();
                        navigationView.setCheckedItem(R.id.home_page_nav_drawer);
                        loadFrag(homePageFragment, getString(R.string.home_page), fragmentManager);
                        return true;*/

                    case R.id.quotes_nav_bottom:
                        
                        if (mInterstitialLoaded){
                            //mInterstitialAd.show(MainActivity.this);
                            Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            mInterstitialLoaded=false;
                            QuotesFragment quotesFragment = new QuotesFragment();
                            navigationView.setCheckedItem(R.id.menu_add_quotes);
                            loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);
                        } else {
                            QuotesFragment quotesFragment = new QuotesFragment();
                            navigationView.setCheckedItem(R.id.menu_add_quotes);
                            loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);
                        }

                        return true;
                    case R.id.authors_nav_bottom:

                        if (mInterstitialLoaded) {
                            //mInterstitialAd.show(MainActivity.this);
                            Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            mInterstitialLoaded=false;
                            AuthorsFragment authorsFragment=new AuthorsFragment();
                            navigationView.setCheckedItem(R.id.menu_add_authors);
                            loadFrag(authorsFragment, getString(R.string.menu_author), fragmentManager);
                        } else {
                            AuthorsFragment authorsFragment=new AuthorsFragment();
                            navigationView.setCheckedItem(R.id.menu_add_authors);
                            loadFrag(authorsFragment, getString(R.string.menu_author), fragmentManager);
                        }
                        return true;
                    case R.id.category_nav_bottom:

                        if (mInterstitialLoaded) {
                            //mInterstitialAd.show(MainActivity.this);
                            Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            mInterstitialLoaded=false;
                            CategoryFragment categoryFragment=new CategoryFragment();
                            navigationView.setCheckedItem(R.id.menu_add_category);
                            loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                        } else {
                            CategoryFragment categoryFragment=new CategoryFragment();
                            navigationView.setCheckedItem(R.id.menu_add_category);
                            loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                        }
                        return true;
                    case R.id.favorites_nav_bottom:

                        if (mInterstitialLoaded) {
                            //mInterstitialAd.show(MainActivity.this);
                            Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            mInterstitialLoaded=false;
                            FavoritesFragment favoritesFragment=new FavoritesFragment();
                            navigationView.setCheckedItem(R.id.menu_add_favorites);
                            loadFrag(favoritesFragment, getString(R.string.menu_favorites), fragmentManager);
                        } else {
                            FavoritesFragment favoritesFragment=new FavoritesFragment();
                            navigationView.setCheckedItem(R.id.menu_add_favorites);
                            loadFrag(favoritesFragment, getString(R.string.menu_favorites), fragmentManager);
                        }
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

                    /*case R.id.home_page_nav_drawer:
                        //HomePageFragment homePageFragment = new HomePageFragment();
                        bottomNavigationView.setSelectedItemId(R.id.home_page_nav_bottom);
                        //loadFrag(homePageFragment, getString(R.string.home_page), fragmentManager);
                        return true;*/
                    case R.id.menu_add_quotes:
                        if (mInterstitialLoaded) {
                            //mInterstitialAd.show(MainActivity.this);
                            Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            mInterstitialLoaded=false;
                            //QuotesFragment quotesFragment = new QuotesFragment();
                            bottomNavigationView.setSelectedItemId(R.id.quotes_nav_bottom);
                            //loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);
                        } else {
                            //QuotesFragment quotesFragment = new QuotesFragment();
                            bottomNavigationView.setSelectedItemId(R.id.quotes_nav_bottom);
                            //loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);
                        }

                        return true;
                    case R.id.menu_add_authors:
                        if (mInterstitialLoaded) {
                            //mInterstitialAd.show(MainActivity.this);
                            Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            mInterstitialLoaded=false;
                            //AuthorsFragment authorsFragment=new AuthorsFragment();
                            bottomNavigationView.setSelectedItemId(R.id.authors_nav_bottom);
                            //loadFrag(authorsFragment, getString(R.string.menu_author), fragmentManager);
                        } else {
                            //AuthorsFragment authorsFragment=new AuthorsFragment();
                            bottomNavigationView.setSelectedItemId(R.id.authors_nav_bottom);
                            //loadFrag(authorsFragment, getString(R.string.menu_author), fragmentManager);
                        }

                        return true;
                    case R.id.menu_add_category:
                        if (mInterstitialLoaded) {
                            //mInterstitialAd.show(MainActivity.this);
                            Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            mInterstitialLoaded=false;
                            //CategoryFragment categoryFragment=new CategoryFragment();
                            bottomNavigationView.setSelectedItemId(R.id.category_nav_bottom);
                            //loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                        } else {
                            //CategoryFragment categoryFragment=new CategoryFragment();
                            bottomNavigationView.setSelectedItemId(R.id.category_nav_bottom);
                            //loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                        }

                        return true;
                    case R.id.menu_add_favorites:
                        if (mInterstitialLoaded) {
                            //mInterstitialAd.show(MainActivity.this);
                            Appodeal.show(MainActivity.this, Appodeal.INTERSTITIAL);
                            mInterstitialLoaded=false;
                            //FavoritesFragment favoritesFragment=new FavoritesFragment();
                            bottomNavigationView.setSelectedItemId(R.id.favorites_nav_bottom);
                            //loadFrag(favoritesFragment, getString(R.string.menu_favorites), fragmentManager);
                        } else {
                            //FavoritesFragment favoritesFragment=new FavoritesFragment();
                            bottomNavigationView.setSelectedItemId(R.id.favorites_nav_bottom);
                            //loadFrag(favoritesFragment, getString(R.string.menu_favorites), fragmentManager);
                        }

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


    private void loadBanner() {
        // Create an ad request.
        mAdView = new AdView(this);
        mAdView.setAdUnitId(getString(R.string.testBanner));
        adContainerView.removeAllViews();
        adContainerView.addView(mAdView);

        AdSize adSize = getAdSize();
        mAdView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
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