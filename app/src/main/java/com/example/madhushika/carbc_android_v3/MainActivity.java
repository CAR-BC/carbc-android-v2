package com.example.madhushika.carbc_android_v3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private NewTransactionFragment newTransactionFragment;

    private static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton notifBtn = (ImageButton) findViewById(R.id.notificationBtn);
        ImageButton searchBtn = (ImageButton) findViewById(R.id.searchBtn);

        notifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(i);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });


        MainActivity.activity = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();

//        newTransactionFragment = (NewTransactionFragment) getFragmentManager().findFragmentById(R.);
//        initiate fragment objects
        newTransactionFragment = new NewTransactionFragment();

//        open home fragment
        transaction.add(R.id.contentLayout, new NewTransactionFragment(), "addtransactionFragment");

//        transaction.add(R.id.contentLayout, new StatusFragment(), "StatusFragment");
//        transaction.add(R.id.contentLayout, new RemindersFragment(), "RemindersFragment");
//        transaction.add(R.id.contentLayout, new InfoFragment(), "InfoFragment");
//        transaction.add(R.id.contentLayout, new UnregisteredNewTransactionFragment(), "UnregisteredNewTransactionFragment");

        transaction.commit();

        NewTransactionFragment.setActivity(activity);


        NavigationHandler.setManager(manager);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navigation_add_event) {
            NavigationHandler.navigateTo("addtransactionFragment");
            // Handle the camera action
        } else if (id == R.id.navigation_view_vehicle) {
//            NavigationHandler.navigateTo("SearchVehicleFragment");
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(i);


        } else if (id == R.id.navigation_notifications) {
            //NavigationHandler.navigateTo("NotificationFragment");
            Intent i = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(i);

        } else if (id == R.id.navigation_status) {
            NavigationHandler.navigateTo("StatusFragment");

        } else if (id == R.id.navigation_reminders) {
            NavigationHandler.navigateTo("RemindersFragment");

        } else if (id == R.id.navigation_info) {
            NavigationHandler.navigateTo("InfoFragment");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
