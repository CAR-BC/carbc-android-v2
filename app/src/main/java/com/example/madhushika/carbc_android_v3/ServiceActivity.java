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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import Objects.ServiceStation;
import Objects.ServiceType;
import Objects.SparePartData;

import controller.Controller;

public class ServiceActivity extends AppCompatActivity {
    private ListView listView;
    private ListView locationListView;
    private JSONObject jsonObject;
    private TextView vehicleNumber;
    private JSONObject serviceDataJSON;
    private JSONObject serviceStationJson;

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

        String vid = i.getExtras().getString("vid");


        //get location and request nearby service stations



        //getServiceStation(received json)

        //setArrayAdaptersToLocationList
        final Controller controller = new Controller();

        controller.requestTransactionDataTest("repair&service","23456","2018/05/21", "pqr567");
        registerReceiver(broadcastReceiver, new IntentFilter("ReceivedTransactionData"));




        //catch the response and stop activity indicator
        //getServiceTypes(response)
        //set adapter
        //enable buttons

        Button done = (Button) findViewById(R.id.done_btn);
        Button cancel = (Button) findViewById(R.id.cancel_btn);

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
                            serviceDataJSON.put("secondaryParty", serviceStationJson);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        controller.sendTransaction("ServiceRepair","23456",serviceDataJSON);
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
                System.out.println(arrayList);
                setArrayAdaptersToServiceTypeList(arrayList);
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
                String type = service.getString("serviceType");

                JSONArray spareParts = service.getJSONArray("serviceData");
                ArrayList<SparePartData> sparePartsArray = new ArrayList<>();

                for (int j = 0; j < spareParts.length(); j++) {
                    JSONObject sparePart = spareParts.getJSONObject(j);
                    String seller = sparePart.getString("seller");
                    String sparePartName = sparePart.getString("sparePart");

                    SparePartData sparePartData = new SparePartData(seller, sparePartName);

                    sparePartsArray.add(sparePartData);
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
                ListView spareParts;


                if (ph == null) {
                    serviceType = (TextView) cellUser.findViewById(R.id.service_type_txt);
                    spareParts = (ListView) cellUser.findViewById(R.id.list_spare_parts);


                    ph = new Placeholder();
                    ph.serviceType = serviceType;
                    ph.spareParts = spareParts;

                    cellUser.setTag(ph);
                } else {
                    serviceType = ph.serviceType;
                    spareParts = ph.spareParts;

                }

                serviceType.setText(serviceTypeItem.getServiceType());
                spareParts.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return serviceTypeItem.getSpareParts().size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return serviceTypeItem.getSpareParts().get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                        return position;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        final SparePartData sparePart = serviceTypeItem.getSpareParts().get(position);
                        View cellUserSP = null;

                        if (convertView == null) {

                            cellUserSP = LayoutInflater.from(ServiceActivity.this).inflate(R.layout.cell_service_type,
                                    parent, false);

                        } else {
                            cellUserSP = convertView;
                        }

                        PlaceholderSP ph = (PlaceholderSP) cellUserSP.getTag();
                        TextView sparePartTxt;


                        if (ph == null) {
                            sparePartTxt = (TextView) cellUserSP.findViewById(R.id.spare_parts_txt);


                            ph = new PlaceholderSP();
                            ph.sparepart = sparePartTxt;

                            cellUserSP.setTag(ph);
                        } else {
                            sparePartTxt = ph.sparepart;

                        }

                        sparePartTxt.setText(sparePart.getSparePart());

                        return cellUserSP;
                    }
                });

                return cellUser;
            }
        });

    }


    private ArrayList<ServiceStation> getServiceStation(JSONObject object) {
        ArrayList<ServiceStation> serviceStations = new ArrayList<>();
        try {
            JSONArray serviceStationJSonArray = object.getJSONArray("data");

            for (int i = 0; i < serviceStationJSonArray.length(); i++) {
                JSONObject station = serviceStationJSonArray.getJSONObject(i);

                String name = station.getString("name");
                String address = station.getString("location");
                String publicKey = station.getString("publicKey");
                String role = station.getString("role");
                ServiceStation serviceStation = new ServiceStation(name,address,publicKey,role);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return serviceStations;
    }


    private void setArrayAdaptersToLocationList(final ArrayList<ServiceStation> locationList) {
        locationListView = (ListView) findViewById(R.id.service_location_list);

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

                final ServiceStation serviceStation = locationList.get(position);
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
                Button select;

                if (ph == null) {
                    name = (TextView) cellUser.findViewById(R.id.service_location_txt);
                    address = (TextView) cellUser.findViewById(R.id.service_location_address_txt);
                    select = (Button) cellUser.findViewById(R.id.select_location);

                    ph = new PlaceholderLocation();
                    ph.name = name;
                    ph.address = address;
                    ph.select = select;

                    cellUser.setTag(ph);
                } else {
                    name = ph.name;
                    address = ph.address;
                    select = ph.select;

                }

                name.setText(serviceStation.getName());
                address.setText(serviceStation.getAddress());
                select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        serviceStationJson = new JSONObject((Map) serviceStation);
                    }
                });

                return cellUser;
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
