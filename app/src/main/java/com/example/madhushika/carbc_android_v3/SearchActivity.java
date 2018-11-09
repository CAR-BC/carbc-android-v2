package com.example.madhushika.carbc_android_v3;

import android.arch.core.executor.TaskExecutor;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

import core.blockchain.Block;
import core.connection.BlockJDBCDAO;
import core.consensus.Consensus;

public class SearchActivity extends AppCompatActivity {

    private TextView owner;
    private TextView engine_no;
    private TextView vehicle_class;
    private TextView condition;
    private TextView make;
    private TextView vmodel;
    private TextView year;
    private TextView rating;
    private EditText reg_no;

    private ListView moreInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        hideActionBar();

        owner = findViewById(R.id.search_owner_txt);
        engine_no = findViewById(R.id.search_engine_no_txt);
        vehicle_class = findViewById(R.id.search_class_txt);
        condition = findViewById(R.id.search_condition_txt);
        make = findViewById(R.id.search_make_txt);
        rating = findViewById(R.id.search_rating_txt);
        vmodel = findViewById(R.id.search_model_txt);
        year = findViewById(R.id.search_year_txt);

        reg_no = findViewById(R.id.search_vehicle_vid);
        final BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();


        ImageView backBtn = (ImageView) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //    registerReceiver(broadcastReceiver, new IntentFilter("SearchActivity"));


        ImageView searchBtn = (ImageView) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search from server and fill the view

                JSONObject data = blockJDBCDAO.getRegistrationInfoByRegistrationNumber(reg_no.getText().toString());

                try {
                    owner.setText(data.getString("current_owner"));
                    engine_no.setText(data.getString("engine_number"));
                    vehicle_class.setText(data.getString("vehicle_class"));
                    vmodel.setText(data.getString("model"));
                    year.setText(data.getString("year_of_manufacture"));
                    condition.setText(data.getString("condition_and_note"));
                    make.setText(data.getString("make"));
                    rating.setText(data.getString("rating"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Button reqBtn = (Button) findViewById(R.id.req_more);
        reqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request data from backend
                JSONArray vehicledata = blockJDBCDAO.getVehicleInfoByRegistrationNumber(reg_no.getText().toString());
                System.out.println(vehicledata);
                if (vehicledata.length()>0){
                    setArrayAdapterToMoreInfoList(vehicledata);
                }

            }
        });
    }

    private void setArrayAdapterToMoreInfoList(final JSONArray moreInfo) {
        moreInfoList = (ListView) findViewById(R.id.info_list);

        System.out.println(moreInfo);
        moreInfoList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return moreInfo.length();
            }

            @Override
            public Object getItem(int position) {
                JSONObject obj = null;
                try {
                    obj = moreInfo.getJSONObject(position);
                    System.out.println(obj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return obj;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = moreInfo.getJSONObject(position);
                    System.out.println("++++++++++++++++++jsonObject+++++++++++++++++++++++");
                    System.out.println(String .valueOf(position)+jsonObject  );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                View cellUser = null;

                if (convertView == null) {

                    //cellUser = inflater.inflate(R.layout.cell_notification, parent, false);
                    cellUser = LayoutInflater.from(SearchActivity.this).inflate(R.layout.cell_search_more_info, viewGroup, false);


                } else {
                    cellUser = convertView;
                }
                SearchActivity.Placeholder ph = (SearchActivity.Placeholder) cellUser.getTag();
                TextView eventData;
                TextView init_date;
                TextView rating;

                if (ph == null) {
                    eventData = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_event);
                    init_date = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_date);
                    rating = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_rating);


                    ph = new Placeholder();
                    ph.eventdata = eventData;
                    ph.initiate_date = init_date;
                    ph.rating = rating;


                    cellUser.setTag(ph);
                } else {
                    eventData = ph.eventdata;
                    init_date = ph.initiate_date;
                    rating = ph.rating;

                }

                try {
                    System.out.println("************************************************");
                    System.out.println(jsonObject.getString("data"));
                    JSONObject data = new JSONObject(jsonObject.getString("data"));
                    eventData.setText(jsonObject.getString("event"));
                    init_date.setText(data.getString("serviced_date"));
                    rating.setText(jsonObject.getString("rating"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                return cellUser;
            }
        });
    }

    private class Placeholder {

        public TextView eventdata;
        public TextView initiate_date;
        public TextView rating;

    }

    public void hideActionBar() {
        //Hide the action bar only if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }


}
