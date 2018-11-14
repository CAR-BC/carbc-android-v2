package com.example.madhushika.carbc_android_v3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class ServiceSecondActivity extends AppCompatActivity {

    TextView vehicle_number;
    TextView service_station;
    ListView service_type_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_second);

        vehicle_number = (TextView) findViewById(R.id.vehicle_number);
        service_station = (TextView) findViewById(R.id.service_station_address);
        service_type_list = (ListView) findViewById(R.id.service_type_list);
    }

}
