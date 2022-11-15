package com.tugrulkara.quotesadmin;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.tugrulkara.quotesadmin.fragment.AuthorsFragment;
import com.tugrulkara.quotesadmin.fragment.CategoryFragment;
import com.tugrulkara.quotesadmin.fragment.FavoritesFragment;
import com.tugrulkara.quotesadmin.fragment.QuotesFragment;
import com.tugrulkara.quotesadmin.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    boolean doubleBackToExitPressedOnce = false;
    NavigationView navigationView;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();

        QuotesFragment quotesFragment = new QuotesFragment();
        loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {

                    case R.id.menu_add_quotes:
                        QuotesFragment quotesFragment = new QuotesFragment();
                        loadFrag(quotesFragment, getString(R.string.menu_quotes), fragmentManager);
                        return true;
                    case R.id.menu_add_authors:
                        AuthorsFragment authorsFragment=new AuthorsFragment();
                        loadFrag(authorsFragment, getString(R.string.menu_author), fragmentManager);
                        return true;
                    case R.id.menu_add_category:
                        CategoryFragment categoryFragment=new CategoryFragment();
                        loadFrag(categoryFragment, getString(R.string.menu_category), fragmentManager);
                        return true;
                    case R.id.menu_add_favorites:
                        FavoritesFragment favoritesFragment=new FavoritesFragment();
                        loadFrag(favoritesFragment, getString(R.string.menu_favorites), fragmentManager);
                        return true;
                    case R.id.menu_add_images:
                       Intent intent=new Intent(MainActivity.this,AddImageActivity.class);
                       startActivity(intent);
                        return true;
                    case R.id.menu_add_settings:
                        SettingsFragment settingsFragment=new SettingsFragment();
                        loadFrag(settingsFragment, getString(R.string.menu_settings), fragmentManager);
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


    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
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

                        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
                        firebaseAuth.signOut();

                        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
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