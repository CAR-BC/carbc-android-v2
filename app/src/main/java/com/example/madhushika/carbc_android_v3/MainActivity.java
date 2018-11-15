package com.example.madhushika.carbc_android_v3;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import core.blockchain.Block;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private NewTransactionFragment newTransactionFragment;
    private UnregisteredNewTransactionFragment unregisteredNewTransactionFragment;
    TextView notificationCount;
    TextView criticalNotificationCount;
//    FloatingActionButton fab;
    private TransactionNew transactionNew;

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
        ImageButton criticalNotificationBtn = (ImageButton) findViewById(R.id.criticalNotificationBtn);

        notificationCount = (TextView) findViewById(R.id.notificationCount);
        criticalNotificationCount = (TextView) findViewById(R.id.criticalNotificationCount);
//        notificationCount.setText(String.valueOf(notificationList.size()));
//        criticalNotificationCount.setText(String.valueOf(criticalNotificationList.size()));


//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, NotificationActivity.class);
//                startActivity(i);
//            }
//        });
//        fab.setEnabled(false);

        registerReceiver(broadcastReceiver, new IntentFilter("MainActivity"));

        notifBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationList.size() != 0) {
                    Intent i = new Intent(MainActivity.this, NotificationActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "No notifications", Toast.LENGTH_SHORT).show();
                }
            }
        });

        criticalNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (criticalNotificationList.size() != 0) {
                    Intent i = new Intent(MainActivity.this, CriticalNotificationList.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getApplicationContext(), "No critical notifications", Toast.LENGTH_SHORT).show();
                }
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
            transactionNew = new TransactionNew();
            transaction.add(R.id.contentLayout, new TransactionNew(), "addNewtransaction");
        }
        transaction.commit();
        NewTransactionFragment.setActivity(activity);
        NavigationHandler.setManager(manager);
    }

    @Override
    protected void onResume() {
        if ((notificationList.size() != 0)) {
            notificationCount.setVisibility(View.VISIBLE);

            notificationCount.setText(String.valueOf(notificationList.size()));
        } else {
            notificationCount.setVisibility(View.GONE);
        }

        if ((criticalNotificationList.size() != 0)) {
            criticalNotificationCount.setVisibility(View.VISIBLE);

            criticalNotificationCount.setText(String.valueOf(criticalNotificationList.size()));
        } else {
            criticalNotificationCount.setVisibility(View.GONE);
        }
        super.onResume();
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
                        notificationCount.setVisibility(View.VISIBLE);
                        notificationCount.setText(String.valueOf(notificationList.size()));
                    }
                }
            }
            if ((str2 != null)) {
                if (str2.equals("newCriticalBlock")) {
                    if (criticalNotificationList.size() != 0) {
                        notificationCount.setVisibility(View.VISIBLE);
                        criticalNotificationCount.setText(String.valueOf(criticalNotificationList.size()));
                    }
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        // TODO: Check which fragment is the current one and if it's home, exit. Else go to home.
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("StatusFragment");
        Fragment fragment1 = getSupportFragmentManager().findFragmentByTag("InfoFragment");
        if (fragment != null || fragment1 != null){
            displayFragment("addtransaction", R.id.navigation_add_event);
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
        final int id = item.getItemId();

        if (id == R.id.navigation_add_event) {
            displayFragment("addtransaction", id);

        } else if (id == R.id.navigation_view_vehicle) {
            Intent i = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(i);

        } else if (id == R.id.navigation_notifications) {
            Intent i = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(i);

        } else if (id == R.id.navigation_status) {
            displayFragment("prigressBar",R.id.progress_bar_item);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    displayFragment("StatusFragment", id);
                }
            }, 300);
        }
        else if (id == R.id.navigation_info) {
            displayFragment("InfoFragment",id );
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void displayFragment(String tag, int id) {
        Fragment fragment = getFragment(id);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contentLayout, fragment, tag);
        fragmentTransaction.commit();
    }

    private Fragment getFragment(int id) {
        switch (id) {
            case R.id.navigation_add_event:
                if (vehicle_numbers.size() == 0) {
                    return new UnregisteredNewTransactionFragment();
                } else {
                    return new TransactionNew();
                }
            case R.id.navigation_status:
                return new StatusFragment();

            case R.id.navigation_info:
                return new InfoFragment();

            case R.id.progress_bar_item:
                return new ProgressBarFragment();
            default:
                return null;
        }
    }


}
