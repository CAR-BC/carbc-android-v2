package com.example.madhushika.carbc_android_v3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Objects.MoreInfoItem;
import core.blockchain.Block;

public class ShowMoreInfoNotificationView extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_more_info_notification_view);

        Intent i = getIntent();
        Block block = (Block) i.getExtras().get("block");
        final ArrayList<MoreInfoItem> moreInfoItems = new ArrayList<>();
        String data = block.getBlockBody().getTransaction().getData();
        JSONObject object = null;
        try {
            object = new JSONObject(data);
            if (block.getBlockBody().getTransaction().getEvent().equalsIgnoreCase("RegisterVehicle")){
                MoreInfoItem item = new MoreInfoItem("current owner",object.getString("currentOwner"));
                MoreInfoItem item1 = new MoreInfoItem("engine Number",object.getString("engineNumber"));
                MoreInfoItem item2 = new MoreInfoItem("vehicle Class",object.getString("vehicleClass"));
                MoreInfoItem item3 = new MoreInfoItem("condition AndNote",object.getString("conditionAndNote"));
                MoreInfoItem item4 = new MoreInfoItem("make",object.getString("make"));
                MoreInfoItem item5 = new MoreInfoItem("model",object.getString("model"));
                MoreInfoItem item6 = new MoreInfoItem("year Of Manufacture",object.getString("yearOfManufacture"));
                MoreInfoItem item7 = new MoreInfoItem("registration Number",object.getString("registrationNumber"));

                moreInfoItems.add(item);
                moreInfoItems.add(item1);
                moreInfoItems.add(item2);
                moreInfoItems.add(item3);
                moreInfoItems.add(item4);
                moreInfoItems.add(item5);
                moreInfoItems.add(item6);
                moreInfoItems.add(item7);
            }

            if (block.getBlockBody().getTransaction().getEvent().equalsIgnoreCase("ServiceRepair")){
                MoreInfoItem item = new MoreInfoItem("current owner",object.getString("currentOwner"));
                MoreInfoItem item1 = new MoreInfoItem("engine Number",object.getString("engineNumber"));
                MoreInfoItem item2 = new MoreInfoItem("vehicle Class",object.getString("vehicleClass"));
                MoreInfoItem item3 = new MoreInfoItem("condition AndNote",object.getString("conditionAndNote"));
                MoreInfoItem item4 = new MoreInfoItem("make",object.getString("make"));
                MoreInfoItem item5 = new MoreInfoItem("model",object.getString("model"));
                MoreInfoItem item6 = new MoreInfoItem("year Of Manufacture",object.getString("yearOfManufacture"));
                MoreInfoItem item7 = new MoreInfoItem("registration Number",object.getString("registrationNumber"));

                moreInfoItems.add(item);
                moreInfoItems.add(item1);
                moreInfoItems.add(item2);
                moreInfoItems.add(item3);
                moreInfoItems.add(item4);
                moreInfoItems.add(item5);
                moreInfoItems.add(item6);
                moreInfoItems.add(item7);
            }

            if (block.getBlockBody().getTransaction().getEvent().equalsIgnoreCase("RegisterVehicle")){
                MoreInfoItem item = new MoreInfoItem("current owner",object.getString("currentOwner"));
                MoreInfoItem item1 = new MoreInfoItem("engine Number",object.getString("engineNumber"));
                MoreInfoItem item2 = new MoreInfoItem("vehicle Class",object.getString("vehicleClass"));
                MoreInfoItem item3 = new MoreInfoItem("condition AndNote",object.getString("conditionAndNote"));
                MoreInfoItem item4 = new MoreInfoItem("make",object.getString("make"));
                MoreInfoItem item5 = new MoreInfoItem("model",object.getString("model"));
                MoreInfoItem item6 = new MoreInfoItem("year Of Manufacture",object.getString("yearOfManufacture"));
                MoreInfoItem item7 = new MoreInfoItem("registration Number",object.getString("registrationNumber"));

                moreInfoItems.add(item);
                moreInfoItems.add(item1);
                moreInfoItems.add(item2);
                moreInfoItems.add(item3);
                moreInfoItems.add(item4);
                moreInfoItems.add(item5);
                moreInfoItems.add(item6);
                moreInfoItems.add(item7);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




        listView = (ListView)findViewById(R.id.moreInfoList);


        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return moreInfoItems.size();
            }

            @Override
            public Object getItem(int position) {
                return moreInfoItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                MoreInfoItem moreInfoItem = moreInfoItems.get(position);
                View cellUser = null;

                if (convertView == null) {

                    cellUser = LayoutInflater.from(ShowMoreInfoNotificationView.this).inflate(R.layout.cell_more_info,
                            parent, false);

                } else {
                    cellUser = convertView;
                }
                Placeholder ph = (Placeholder) cellUser.getTag();
                TextView key;
                TextView data;


                if (ph == null) {
                    key = (TextView) cellUser.findViewById(R.id.more_info_heading_txt);
                    data = (TextView) cellUser.findViewById(R.id.more_info_data_txt);

                    ph = new Placeholder();
                    ph.key = key;
                    ph.data = data;

                    cellUser.setTag(ph);
                } else {
                    key = ph.key;
                    data = ph.data;

                }

                key.setText(moreInfoItem.getKey());
                data.setText(moreInfoItem.getValue());

                return cellUser;
            }
        });
    }

    private class Placeholder {
        public TextView key;
        public TextView data;
    }
}
