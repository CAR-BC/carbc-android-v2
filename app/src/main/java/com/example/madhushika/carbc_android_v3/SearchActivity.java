package com.example.madhushika.carbc_android_v3;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

import java.util.ArrayList;

import Objects.EventData;
import core.connection.BlockJDBCDAO;

public class SearchActivity extends AppCompatActivity {

    private TextView registration_number;
    private TextView engine_no;
    private TextView chassis_number;
    private TextView make;
    private TextView vmodel;
    private TextView rating;

    private ListView moreInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        hideActionBar();

        engine_no = findViewById(R.id.search_engine_no_txt);
        chassis_number = findViewById(R.id.search_class_txt);
        make = findViewById(R.id.search_make_txt);
        rating = findViewById(R.id.search_rating_txt);
        vmodel = findViewById(R.id.search_model_txt);
        registration_number = findViewById(R.id.search_vehicle_vid);

        final BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();


        //ImageView backBtn = (ImageView) findViewById(R.id.back_button);

        final Button reqBtn = (Button) findViewById(R.id.req_more);

        reqBtn.setEnabled(false);
        ImageView searchBtn = (ImageView) findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search from server and fill the view
                reqBtn.setEnabled(false);
                JSONArray array = new JSONArray();
                setArrayAdapterToMoreInfoList(array);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(registration_number.getWindowToken(), 0);
                JSONObject data = blockJDBCDAO.getRegistrationInfoByRegistrationNumber(registration_number.getText().toString());

                try {
                    if (data.getBoolean("status")){
                        try {
                            JSONObject vehicleData = data.getJSONObject("data");
                            engine_no.setText(vehicleData.getString("engine_number"));
                            chassis_number.setText(vehicleData.getString("chassis_number"));
                            vmodel.setText(vehicleData.getString("model"));
                            make.setText(vehicleData.getString("make"));
                            rating.setText(vehicleData.getString("rating"));

                            reqBtn.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        engine_no.setText("N/A");
                        chassis_number.setText("N/A");
                        vmodel.setText("N/A");
                        make.setText("N/A");
                        rating.setText("N/A");
                        Toast.makeText(SearchActivity.this,"No such vehicle in the system",Toast.LENGTH_LONG).show();
                        reqBtn.setEnabled(false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });




        reqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //request data from backend
                JSONArray vehicledata = blockJDBCDAO.getVehicleInfoByRegistrationNumber(registration_number.getText().toString());
                System.out.println(vehicledata);
                if (vehicledata.length() > 0) {
                    setArrayAdapterToMoreInfoList(vehicledata);
                }else {
                    Toast.makeText(SearchActivity.this,"No more transactions",Toast.LENGTH_SHORT);
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
                    System.out.println(String.valueOf(position) + jsonObject);
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

    private ArrayList<EventData> getEventDataArray(JSONArray eventDataJSONArray) {

        for (int i = 0; i < eventDataJSONArray.length(); i++) {
            try {
                JSONObject object = eventDataJSONArray.getJSONObject(i);
                JSONObject data = new JSONObject(object.getString("data"));
                String event = object.getString("event");
                String date = data.getString("serviced_date");
                String rating = object.getString("rating");


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    private class Placeholder {

        public TextView eventdata;
        public TextView initiate_date;
        public TextView rating;

    }

    public void hideActionBar() {
        //Hide the action bar only if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Search Vehicle");
            //getSupportActionBar().hide();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
