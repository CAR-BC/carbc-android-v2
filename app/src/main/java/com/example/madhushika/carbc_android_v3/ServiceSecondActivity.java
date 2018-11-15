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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.Controller;
import core.blockchain.Block;

public class ServiceSecondActivity extends AppCompatActivity {

    TextView vehicle_number;
    TextView service_station;
    ListView service_type_list;
    String vehicleNumber;
    String station;
    String publicKey;
    ImageView backBtn;
    ArrayList<String> selectedServiceTypeList;
    Button doneBtn;
    Button cancelBtn;
    String timestamp;
    private JSONObject serviceDataJSON;
    private JSONObject serviceStationJson;
    private JSONArray sparePartSellerList = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_second);

        vehicle_number = (TextView) findViewById(R.id.vehicle_number);
        service_station = (TextView) findViewById(R.id.service_station_address);
        backBtn = (ImageView) findViewById(R.id.back_button);
        doneBtn = (Button) findViewById(R.id.done_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        selectedServiceTypeList = new ArrayList<>();

        Intent i = getIntent();
        if (i != null)
            vehicleNumber = i.getExtras().getString("vid");
            station = i.getExtras().getString("station");
            publicKey = i.getExtras().getString("publicKey");

        vehicle_number.setText(vehicleNumber);
        service_station.setText(station);
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

                        try {
                            serviceDataJSON = new JSONObject();
                            serviceDataJSON.put("vehicle_id", vehicleNumber);
                            serviceDataJSON.put("serviced_date", timestamp);
                            serviceDataJSON.put("cost", 100000);
                            JSONArray services = new JSONArray();

                            for (int k = 0; k < selectedServiceTypeList.size(); k++){
                                JSONObject service = new JSONObject();
                                service.put("serviceType", selectedServiceTypeList.get(k));
                                service.put("serviceData", new JSONArray());

                                services.put(service);
                            }
                            serviceDataJSON.put("services", services);
                            serviceDataJSON.put("SecondaryParty", serviceStationJson);

                            JSONObject thirdParty = new JSONObject();
                            thirdParty.put("SparePartProvider", sparePartSellerList);
                            serviceDataJSON.put("ThirdParty", thirdParty);


                            Controller controller = new Controller();
                            controller.sendTransaction("ServiceRepair", vehicleNumber, serviceDataJSON);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

       String[] arrayList = new String[]{"Change the engine oil", "Replace the oil filter", "Replace the air filter",
               "Replace the fuel filter", "Replace the cabin filter",
                "Replace the spark plugs", "Tune the engine", "Check level and refill brake fluid or clutch fluid",
               "Check Brake Pads or Liners, Brake Discs or Drums, and replace if worn out",
               "Check level and refill power steering fluid", "Grease and lubricate components",
               "Inspect and replace the timing belt or timing chain if needed", "Check condition of the tires",
               "Check for proper operation of all lights, wipers ",
               "Check for any Error codes in the ECU and take corrective action",
               "Wash the vehicle and clean the interiors", "Use scan tool read trouble code", "replacing head lights"};
List<String> list =  Arrays.asList(arrayList);
setArrayAdapterToServiceTypeList(list);
    }


    private void setArrayAdapterToServiceTypeList(final List<String> nameList) {

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
                System.out.println("************************");
                System.out.println(serviceStation + position);
                View cellUser = null;

                Placeholder ph;
                TextView service_station;
                CheckBox cBox;

                if (convertView == null) {
                    //cellUser = inflater.inflate(R.layout.cell_notification, parent, false);
                    cellUser = LayoutInflater.from(ServiceSecondActivity.this).inflate(R.layout.cell_service_type_list, viewGroup, false);

                    ph = new Placeholder();
                    service_station = (TextView) cellUser.findViewById(R.id.serviceType);
                    cBox = (CheckBox) cellUser.findViewById(R.id.isSelected);


                    ph.service_station = service_station;
                    ph.isSelect = cBox;

                    cellUser.setTag(ph);
                } else {
                    cellUser = convertView;
                    ph = (Placeholder) cellUser.getTag();
                    service_station = ph.service_station;
                    cBox = ph.isSelect;
                }


                service_station.setText(serviceStation);
                if (cBox.isChecked()) {
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
