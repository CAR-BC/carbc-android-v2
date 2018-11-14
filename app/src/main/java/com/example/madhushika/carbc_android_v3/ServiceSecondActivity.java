package com.example.madhushika.carbc_android_v3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import core.blockchain.Block;

public class ServiceSecondActivity extends AppCompatActivity {

    TextView vehicle_number;
    TextView service_station;
    ListView service_type_list;
    ArrayList<String> selectedServiceTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_second);

        vehicle_number = (TextView) findViewById(R.id.vehicle_number);
        service_station = (TextView) findViewById(R.id.service_station_address);

    }


    private void setArrayAdapterToServiceTypeList(final ArrayList<String> nameList){

        service_type_list = (ListView) findViewById(R.id.service_type_list);
            service_type_list.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return nameList.size();
                }

                @Override
                public Object getItem(int position) {
                    return nameList.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup viewGroup) {
                    final String serviceStation = nameList.get(position);
                    View cellUser = null;

                    if (convertView == null) {
                        //cellUser = inflater.inflate(R.layout.cell_notification, parent, false);
                        cellUser = LayoutInflater.from(ServiceSecondActivity.this).inflate(R.layout.cell_service_type_list, viewGroup, false);
                    } else {
                        cellUser = convertView;
                    }
                    Placeholder ph = (Placeholder) cellUser.getTag();
                    TextView service_station;
                    CheckBox isSelected;

                    if (ph == null) {
                        service_station = (TextView) cellUser.findViewById(R.id.serviceType);
                        isSelected = (CheckBox) cellUser.findViewById(R.id.isSelected);

                        ph = new Placeholder();
                        ph.service_station = service_station;
                        ph.isSelect = isSelected;


                        cellUser.setTag(ph);
                    } else {
                        service_station = ph.service_station;
                        isSelected = ph.isSelect;

                    }

                    service_station.setText(serviceStation);
                    if (isSelected.isSelected()){
                        selectedServiceTypeList.add(serviceStation);
                    }

                    return cellUser;
                }
            });

    }

    private class Placeholder {
        public TextView service_station;
        public CheckBox isSelect;

    }
}
