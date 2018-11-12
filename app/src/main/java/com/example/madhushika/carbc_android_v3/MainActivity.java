package com.example.madhushika.carbc_android_v3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Objects.ServiceType;
import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import core.blockchain.Block;
import core.connection.BlockJDBCDAO;
import core.connection.VehicleJDBCDAO;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private NewTransactionFragment newTransactionFragment;
    private UnregisteredNewTransactionFragment unregisteredNewTransactionFragment;
    TextView notificationCount;
    TextView criticalNotificationCount;
    FloatingActionButton fab;

    public static ArrayList<String> vehicle_numbers;
    public static ArrayList<Block> notificationList;
    public static ArrayList<Block> criticalNotificationList;
    private static MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton notifBtn = (ImageButton) findViewById(R.id.notificationBtn);
        ImageButton searchBtn = (ImageButton) findViewById(R.id.searchBtn);

        notificationCount = (TextView) findViewById(R.id.notificationCount);
        criticalNotificationCount = (TextView) findViewById(R.id.criticalNotificationCount);
//        notificationCount.setText(String.valueOf(notificationList.size()));
//        criticalNotificationCount.setText(String.valueOf(criticalNotificationList.size()));


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(i);
            }
        });
        fab.setEnabled(false);

        registerReceiver(broadcastReceiver, new IntentFilter("MainActivity"));

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

        if (vehicle_numbers.size() == 0) {
            unregisteredNewTransactionFragment = new UnregisteredNewTransactionFragment();
            transaction.add(R.id.contentLayout, new UnregisteredNewTransactionFragment(), "addUnregisteredNewTransactionFragment");
        } else {
            newTransactionFragment = new NewTransactionFragment();
            transaction.add(R.id.contentLayout, new NewTransactionFragment(), "addtransactionFragment");
        }

//        if (MainActivity.vehicle_numbers.size()==0){
//            Menu menuNav=navigationView.getMenu();
//            MenuItem nav_item2 = menuNav.findItem(R.id.navigation_status);
//            nav_item2.setEnabled(false);
//            MenuItem nav_item3 = menuNav.findItem(R.id.navigation_info);
//            nav_item3.setEnabled(false);
//            MenuItem nav_item4 = menuNav.findItem(R.id.navigation_reminders);
//            nav_item4.setEnabled(false);
//        }else {
//            Menu menuNav=navigationView.getMenu();
//            MenuItem nav_item2 = menuNav.findItem(R.id.navigation_status);
//            nav_item2.setEnabled(true);
//            MenuItem nav_item3 = menuNav.findItem(R.id.navigation_info);
//            nav_item3.setEnabled(true);
//            MenuItem nav_item4 = menuNav.findItem(R.id.navigation_reminders);
//            nav_item4.setEnabled(true);
//        }
        transaction.commit();
        NewTransactionFragment.setActivity(activity);
        NavigationHandler.setManager(manager);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = "";
            String str2 = "";
            String str3="";
            str = intent.getStringExtra("newNomApprovedBlockReceived");
            str2 = intent.getStringExtra("newCriticalBlockReceived");
            str3 = intent.getStringExtra("confirmationSent");
            if ((str != null)) {

                if (str.equals("newBlock")|| str3.equals("confirmationSent")) {
                    if ((notificationList.size() != 0)) {
                        notificationCount.setText(String.valueOf(notificationList.size()));
                    }
                }
            }
            if ((str2 != null)) {
                if (str2.equals("newCriticalBlock")) {
                    if (criticalNotificationList.size() != 0) {
                        criticalNotificationCount.setText(String.valueOf(criticalNotificationList.size()));
                        fab.setEnabled(true);
                    }
                }
            }
        }
    };

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
            if (vehicle_numbers.size() == 0) {
                NavigationHandler.navigateTo("addtransactionFragment");
                Toast.makeText(MainActivity.this, "This option will be enable ", Toast.LENGTH_SHORT).show();
            } else {
                NavigationHandler.navigateTo("StatusFragment");
            }

        } else if (id == R.id.navigation_reminders) {
            if (vehicle_numbers.size() == 0) {
                NavigationHandler.navigateTo("addtransactionFragment");
                Toast.makeText(MainActivity.this, "This option will be enable ", Toast.LENGTH_SHORT).show();
            } else {
                NavigationHandler.navigateTo("RemindersFragment");
            }

        } else if (id == R.id.navigation_info) {
            if (vehicle_numbers.size() == 0) {
                NavigationHandler.navigateTo("addtransactionFragment");
                Toast.makeText(MainActivity.this, "This option will be enable ", Toast.LENGTH_SHORT).show();

            } else {
                NavigationHandler.navigateTo("InfoFragment");
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
