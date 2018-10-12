package com.example.madhushika.carbc_android_v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchingEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching_empty);

        SharedPreferences preferences = getSharedPreferences("com.example.madhushika.carbc_android_v2", 0);
        boolean login_statusStored = preferences.getBoolean("login_status", false);

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
