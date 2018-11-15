package com.example.madhushika.carbc_android_v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import Objects.ReminderItem;
import chainUtil.KeyGenerator;
import controller.Controller;
import core.connection.VehicleJDBCDAO;
import network.Node;
import network.communicationHandler.MessageSender;
//import controller.Controller;

public class LaunchingEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching_empty);

        Controller controller = new Controller();
        controller.startNode();
        Node.getInstance().deletePeers();
        MessageSender.requestIP();

       // KeyGenerator.getInstance().generateKeyPair();
       // System.out.println("publickey: " + KeyGenerator.getInstance().getPublicKeyAsString());

//        KeyGenerator.getInstance().generateKeyPair();
//        System.out.println("publickey: " + KeyGenerator.getInstance().getPublicKeyAsString());


        VehicleJDBCDAO vehicleJDBCDAO = new VehicleJDBCDAO();
        MainActivity.vehicle_numbers = new ArrayList<>();
        MainActivity.vehicle_numbers = vehicleJDBCDAO.getRegistrationNumbers(KeyGenerator.getInstance().getPublicKeyAsString());
        MainActivity.notificationList = new ArrayList<>();
        MainActivity.criticalNotificationList = new ArrayList<>();

        StatusFragment.historyRecords = new ArrayList<>();



        SharedPreferences preferences = getSharedPreferences("com.example.madhushika.carbc_android_v2", 0);
        boolean login_statusStored = preferences.getBoolean("login_status", false);
        boolean ownershipStored = preferences.getBoolean("ownership", false);

        if (login_statusStored){
            Intent intent = new Intent(LaunchingEmptyActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            LaunchingEmptyActivity.this.finish();
        }else {
            Intent intent = new Intent(LaunchingEmptyActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            LaunchingEmptyActivity.this.finish();
        }
    }

}
