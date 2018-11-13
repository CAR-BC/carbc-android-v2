package com.example.madhushika.carbc_android_v3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import Objects.ServiceStation;
import Objects.ServiceType;
import Objects.SparePartData;

import controller.Controller;
import core.connection.BlockJDBCDAO;
import core.connection.IdentityJDBC;
import core.connection.VehicleJDBCDAO;

public class ServiceActivity extends AppCompatActivity {
    private ListView listView;
    private ListView locationListView;
    private JSONObject jsonObject;
    private TextView vehicleNumber;
    private JSONObject serviceDataJSON;
    private JSONObject serviceStationJson;
    private JSONArray sparePartSellerList = new JSONArray();
    private Button done;
    private TextView vehicleDetailsText;
    String regNo;
    ServiceStation serviceStationSelected;
    Controller controller;
    ArrayList<ServiceStation> stations;
    JSONArray locationList;
    ArrayList<ServiceStation> locationArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        hideActionBar();

        ImageView backBtn = (ImageView) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent i = getIntent();
        vehicleNumber = (TextView) findViewById(R.id.vehicle_number);
        vehicleNumber.setText(i.getExtras().getString("vid"));
        regNo = i.getExtras().getString("vid");
        locationListView = (ListView) findViewById(R.id.service_location_list);
        vehicleDetailsText = (TextView) findViewById(R.id.vehicleDetailsText);

        String vid = i.getExtras().getString("vid");
        locationList = null;
        //get location and request nearby service stations

        IdentityJDBC identityJDBC = new IdentityJDBC();
        try {
            locationList = identityJDBC.getPeersByLocation("Moratuwa");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (locationList.isNull(0)) {
            Toast.makeText(ServiceActivity.this, "Please select correct service station.", Toast.LENGTH_LONG).show();

        }
        locationArray = getServiceStation(locationList);
        setArrayAdaptersToLocationList(locationArray);
        setClickListnerToLocationList();

        controller = new Controller();

        registerReceiver(broadcastReceiver, new IntentFilter("ReceivedTransactionData"));

        //catch the response and stop activity indicator
        //getServiceTypes(response)
        //set adapter
        //enable buttons

        done = (Button) findViewById(R.id.done_btn);
        Button cancel = (Button) findViewById(R.id.cancel_btn);
        done.setEnabled(false);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
                builder.setTitle("Add a Transaction");
                builder.setMessage("Do you really need to add this transaction? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //get json array and add
                        //call blockchain method
                        try {
                            serviceDataJSON.put("SecondaryParty", serviceStationJson);

                            JSONObject thirdParty = new JSONObject();
                            thirdParty.put("SparePartProvider", sparePartSellerList);
                            serviceDataJSON.put("ThirdParty", thirdParty);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(serviceDataJSON);
                        controller.sendTransaction("ServiceRepair", VehicleJDBCDAO.vehicleNumbersWithRegistrationNumbers.get(regNo), serviceDataJSON);
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceActivity.this);
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

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("responseFormServiceStation");
            try {
                serviceDataJSON = new JSONObject(str);
                ArrayList<ServiceType> arrayList = getServiceTypes(new JSONObject(str));
                locationArray = new ArrayList<>();
                locationArray.add(serviceStationSelected);
                setArrayAdaptersToLocationList(locationArray);
                setArrayAdaptersToServiceTypeList(arrayList);
                vehicleDetailsText.setText("Service details");
                done.setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void hideActionBar() {
        //Hide the action bar only if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private ArrayList<ServiceType> getServiceTypes(JSONObject object) {
        ArrayList<ServiceType> serviceTypesArray = null;
        try {

            JSONArray servicetypes = object.getJSONArray("services");
            serviceTypesArray = new ArrayList<>();

            for (int i = 0; i < servicetypes.length(); i++) {
                JSONObject service = servicetypes.getJSONObject(i);
                JSONArray spareParts = null;
                String type = null;
                if (service.has("serviceType")) {
                    type = service.getString("serviceType");
                }
                if (service.has("serviceData")) {
                    spareParts = service.getJSONArray("serviceData");
                }


                ArrayList<SparePartData> sparePartsArray = new ArrayList<>();

                if (spareParts.length() > 0) {
                    for (int j = 0; j < spareParts.length(); j++) {
                        JSONObject sparePart = spareParts.getJSONObject(j);
                        String seller = null;
                        String sparePartName = null;
                        if (sparePart.has("seller")) {
                            sparePartSellerList.put(sparePart.getString("seller"));
                            seller = sparePart.getString("seller");
                        }
                        if (sparePart.has("sparePart")) {
                            sparePartName = sparePart.getString("sparePart");
                        }
                        if (seller != null && sparePartName != null) {
                            SparePartData sparePartData = new SparePartData(seller, sparePartName);
                            sparePartsArray.add(sparePartData);
                        }
                    }
                }
                ServiceType serviceType = new ServiceType(type, sparePartsArray);
                serviceTypesArray.add(serviceType);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return serviceTypesArray;
    }


    private void setArrayAdaptersToServiceTypeList(final ArrayList<ServiceType> serviceTypes) {
        listView = (ListView) findViewById(R.id.service_info);


        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return serviceTypes.size();
            }

            @Override
            public Object getItem(int position) {
                return serviceTypes.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {
                final ServiceType serviceTypeItem = serviceTypes.get(position);
                View cellUser = null;

                if (convertView == null) {

                    cellUser = LayoutInflater.from(ServiceActivity.this).inflate(R.layout.cell_service_info,
                            viewGroup, false);

                } else {
                    cellUser = convertView;
                }

                Placeholder ph = (Placeholder) cellUser.getTag();
                TextView serviceType;
                // ListView spareParts;


                if (ph == null) {
                    serviceType = (TextView) cellUser.findViewById(R.id.service_type_txt);
                    //spareParts = (ListView) cellUser.findViewById(R.id.list_spare_parts);


                    ph = new Placeholder();
                    ph.serviceType = serviceType;
                    // ph.spareParts = spareParts;

                    cellUser.setTag(ph);
                } else {
                    serviceType = ph.serviceType;
                    // spareParts = ph.spareParts;

                }

                serviceType.setText(serviceTypeItem.getServiceType());

                return cellUser;
            }
        });

    }


    private ArrayList<ServiceStation> getServiceStation(JSONArray serviceStationJSonArray) {
        ArrayList<ServiceStation> serviceStations = new ArrayList<>();
        try {
            //JSONArray serviceStationJSonArray = object.getJSONArray("data");

            for (int i = 0; i < serviceStationJSonArray.length(); i++) {
                JSONObject station = serviceStationJSonArray.getJSONObject(i);

                String name = station.getString("name");
                String address = station.getString("location");
                String publicKey = station.getString("publicKey");
                String role = station.getString("role");
                ServiceStation serviceStation = new ServiceStation(name, address, publicKey, role);
                serviceStations.add(serviceStation);
            }
            System.out.println();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return serviceStations;
    }


    private void setClickListnerToLocationList() {

        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                serviceStationJson = new JSONObject();
                JSONObject serviceStationElem = new JSONObject();
                try {
                    serviceStationElem.put("name", locationArray.get(position).getName());
                    serviceStationElem.put("publicKey", locationArray.get(position).getPublicKey());
                    serviceStationJson.put("serviceStation", serviceStationElem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                serviceStationSelected = locationArray.get(position);
                System.out.println("*********************service station name*********************");
                System.out.println(locationArray.get(position).getName());
                controller.requestTransactionDataTest("ServiceRepair", regNo, "2018/05/21", locationArray.get(position).getPublicKey());
//                        stations.add(serviceStation);
                //serviceStationJson = new JSONObject((Map) serviceStation);
            }
        });
    }

    private void setArrayAdaptersToLocationList(final ArrayList<ServiceStation> locationList) {

        locationListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return locationList.size();
            }

            @Override
            public Object getItem(int position) {
                return locationList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {

                ServiceStation serviceStation = locationList.get(position);
                View cellUser = null;

                if (convertView == null) {

                    cellUser = LayoutInflater.from(ServiceActivity.this).inflate(R.layout.cell_service_location,
                            viewGroup, false);

                } else {
                    cellUser = convertView;
                }

                PlaceholderLocation ph = (PlaceholderLocation) cellUser.getTag();
                final TextView name;
                TextView address;
                final Button select;

                if (ph == null) {
                    name = (TextView) cellUser.findViewById(R.id.service_location_txt);
                    address = (TextView) cellUser.findViewById(R.id.service_location_address_txt);
                    // select = (Button) cellUser.findViewById(R.id.select_location);

                    ph = new PlaceholderLocation();
                    ph.name = name;
                    ph.address = address;
                    //ph.select = select;

                    cellUser.setTag(ph);
                } else {
                    name = ph.name;
                    address = ph.address;
                    // select = ph.select;

                }

                name.setText(serviceStation.getName());
                address.setText(serviceStation.getAddress());

                return cellUser;
            }
        });
    }

    public JSONArray getSparePartSellerList() {
        return sparePartSellerList;
    }

    public void setSparePartSellerList(JSONArray sparePartSellerList) {
        this.sparePartSellerList = sparePartSellerList;
    }


    private class Placeholder {
        public TextView serviceType;
        public ListView spareParts;
    }

    private class PlaceholderSP {
        public TextView sparepart;
    }

    private class PlaceholderLocation {
        public TextView name;
        public TextView address;
        public Button select;

    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }
}
