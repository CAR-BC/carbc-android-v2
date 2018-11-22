package com.example.madhushika.carbc_android_v3;

import android.app.ActivityManager;
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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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
    String datePicked;
    ArrayList<String> selectedServiceTypeList;
    Button doneBtn;
    Button cancelBtn;
    String timestamp;
    private JSONObject serviceDataJSON;
    private JSONObject serviceStationJson;
    private JSONArray sparePartSellerList = new JSONArray();

    HashMap<String, String[]> serviceTypes = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_second);

        vehicle_number = (TextView) findViewById(R.id.vehicle_number);
        service_station = (TextView) findViewById(R.id.service_station_address);
        doneBtn = (Button) findViewById(R.id.done_btn);
        cancelBtn = (Button) findViewById(R.id.cancel_btn);

        selectedServiceTypeList = new ArrayList<>();

        Intent i = getIntent();
        if (i != null)
            vehicleNumber = i.getExtras().getString("vid");
        station = i.getExtras().getString("station");
        publicKey = i.getExtras().getString("publicKey");
        datePicked = i.getExtras().getString("datePicked");
        System.out.println("DDDDDDDDDDDDDDDDDDD22222222222222" + datePicked);

        try {
            serviceStationJson = new JSONObject();
            serviceStationJson.put("name", station);
            serviceStationJson.put("publicKey", publicKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        vehicle_number.setText(vehicleNumber);
        service_station.setText(station);

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
                            serviceDataJSON.put("serviced_date", convertStringToTimestamp(datePicked));
                            serviceDataJSON.put("cost", 100000);
                            JSONArray services = new JSONArray();

                            for (int k = 0; k < selectedServiceTypeList.size(); k++) {
                                JSONObject service = new JSONObject();
                                service.put("serviceType", selectedServiceTypeList.get(k));
                                JSONArray array = new JSONArray();
                                JSONObject jsonObject = new JSONObject();
                                array.put(jsonObject);
                                service.put("serviceData", array);


                                services.put(service);
                            }
                            serviceDataJSON.put("services", services);
                            JSONObject serviceStation = new JSONObject();
                            serviceStation.put("serviceStation", serviceStationJson);
                            serviceDataJSON.put("SecondaryParty", serviceStation);

                            JSONObject thirdParty = new JSONObject();
                            thirdParty.put("SparePartProvider", sparePartSellerList);
                            serviceDataJSON.put("ThirdParty", thirdParty);


                            Controller controller = new Controller();
                            controller.sendTransaction("ServiceRepair", vehicleNumber, serviceDataJSON);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Intent intent = new Intent(ServiceActivity,ServiceActivit);
                        ServiceActivity.serviceActivity.finish();
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


        serviceTypes.put("ServiceStation", new String[]{"Change the engine oil", "Replace the oil filter", "Replace the air filter",
                "Replace the fuel filter", "Replace the cabin filter",
                "Replace the spark plugs"});
        serviceTypes.put("Ashan Service Center", new String[]{"Tune the engine", "Change the engine oil", "Replace the spark plugs",
                "Replace the cabin filter",
                "Replace the fuel filter"});
        serviceTypes.put("Kumudu Service Center", new String[]{"Grease and lubricate components",
                "Inspect and replace the timing belt or timing chain if needed", "Check condition of the tires",
                "Check for proper operation of all lights, wipers ",
                "Check for any Error codes in the ECU and take corrective action"});
        serviceTypes.put("Thinara Filling Stations", new String[]{"Wash the vehicle and clean the interiors",
                "Use scan tool read trouble code", "replacing head lights"});
        serviceTypes.put("Lanka IOC Fuel Station", new String[]{"Replace the air filter",
                "Replace the fuel filter", "Replace the cabin filter",
                "Replace the spark plugs", "Tune the engine", "Check level and refill brake fluid or clutch fluid",
                "Check Brake Pads or Liners, Brake Discs or Drums, and replace if worn out",
                "Check level and refill power steering fluid"});
        serviceTypes.put("Auto Miraj", new String[]{"Check level and refill power steering fluid", "Grease and lubricate components",
                "Inspect and replace the timing belt or timing chain if needed", "Check condition of the tires",
                "Check for proper operation of all lights, wipers ",
                "Check for any Error codes in the ECU and take corrective action"});
        serviceTypes.put("Shantha Motors", new String[]{"Replace the fuel filter", "Replace the cabin filter",
                "Replace the spark plugs", "Tune the engine", "Check level and refill brake fluid or clutch fluid"});
        serviceTypes.put("Motor Cycle Repair Center", new String[]{"Replace the cabin filter",
                "Replace the spark plugs", "Tune the engine", "Check for any Error codes in the ECU and take corrective action",
                "Wash the vehicle and clean the interiors"});
        serviceTypes.put("Caypetco", new String[]{"Check Brake Pads or Liners, Brake Discs or Drums, and replace if worn out",
                "Check level and refill power steering fluid", "Grease and lubricate components",
                "Inspect and replace the timing belt or timing chain if needed"});


        String[] arrayList = new String[]{"Change the engine oil", "Replace the oil filter", "Replace the air filter",
                "Replace the fuel filter", "Replace the cabin filter",
                "Replace the spark plugs", "Tune the engine", "Check level and refill brake fluid or clutch fluid",
                "Check Brake Pads or Liners, Brake Discs or Drums, and replace if worn out",
                "Check level and refill power steering fluid", "Grease and lubricate components",
                "Inspect and replace the timing belt or timing chain if needed", "Check condition of the tires",
                "Check for proper operation of all lights, wipers ",
                "Check for any Error codes in the ECU and take corrective action",
                "Wash the vehicle and clean the interiors", "Use scan tool read trouble code", "replacing head lights"};
        List<String> list = Arrays.asList(serviceTypes.get(station));
        setArrayAdapterToServiceTypeList(list);
    }

    public static Timestamp convertStringToTimestamp(String time) {
        System.out.println("service time: " + time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date parsedDate = null;
        Timestamp timestamp = null;
        try {
            parsedDate = dateFormat.parse(time);
            timestamp = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return timestamp;
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
                final CheckBox cBox;

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

                cBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cBox.isChecked()) {
                            System.out.println("***************************");
                            selectedServiceTypeList.add(serviceStation);
                        } else {
                            selectedServiceTypeList.remove(serviceStation);
                        }
                    }
                });

                return cellUser;
            }
        });
    }

    private class Placeholder {
        public TextView service_station;
        public CheckBox isSelect;
    }
}
