package com.example.madhushika.carbc_android_v3;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import core.blockchain.Block;

public class ServiceSecondActivity extends AppCompatActivity {

    TextView vehicle_number;
    TextView service_station;
    ListView service_type_list;
    String vehicleNumber;
    ImageView backBtn;
    ArrayList<String> selectedServiceTypeList;
    Button doneBtn;
    Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_second);

        vehicle_number = (TextView) findViewById(R.id.vehicle_number);
        service_station = (TextView) findViewById(R.id.service_station_address);
        backBtn = (ImageView) findViewById(R.id.back_button);
        doneBtn = (Button) findViewById(R.id.done_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        Intent i = this.getIntent();
        if (i != null)
            vehicleNumber = i.getExtras().getString("vid");

        vehicle_number.setText(vehicleNumber);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceSecondActivity.this);
                builder.setTitle("Add a Transaction");
                builder.setMessage("Do you really need to add this transaction? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceSecondActivity.this);
                builder.setTitle("Cancel a Transaction");
                builder.setMessage("Do you really need to cancel this transaction? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


    private void setArrayAdapterToServiceTypeList(final ArrayList<String> nameList) {

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
                if (isSelected.isChecked()) {
                    selectedServiceTypeList.add(serviceStation);
                } else {
                    selectedServiceTypeList.remove(serviceStation);
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
