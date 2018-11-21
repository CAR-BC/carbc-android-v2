package com.example.madhushika.carbc_android_v3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Objects.ReminderItem;
import core.connection.BlockJDBCDAO;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private Spinner spinner;
    private ListView listView;

    private TextView engine_no;
    private TextView chassis_number;
    private TextView make;
    private TextView vmodel;
    private TextView rating;

    String registrationNumber;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        spinner = (Spinner) view.findViewById(R.id.vehicle_number);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.item_spinner, MainActivity.vehicle_numbers);
        dataAdapter.setDropDownViewResource(R.layout.vehicle_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        engine_no = view.findViewById(R.id.search_engine_no_txt);
        chassis_number = view.findViewById(R.id.search_class_txt);
        make = view.findViewById(R.id.search_make_txt);
        rating = view.findViewById(R.id.search_rating_txt);
        vmodel = view.findViewById(R.id.search_model_txt);

        listView = (ListView) view.findViewById(R.id.info_list);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                registrationNumber = spinner.getSelectedItem().toString();

                final BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
                JSONObject data = blockJDBCDAO.getRegistrationInfoByRegistrationNumber(registrationNumber);

                try {
                    if (data.getBoolean("status")){
                        try {
                            JSONObject vehicleData = data.getJSONObject("data");
                            engine_no.setText(vehicleData.getString("engine_number"));
                            chassis_number.setText(vehicleData.getString("chassis_number"));
                            vmodel.setText(vehicleData.getString("model"));
                            make.setText(vehicleData.getString("make"));
                            rating.setText(vehicleData.getString("rating"));
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
                        Toast.makeText(getActivity(),"No such vehicle in the system",Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final JSONArray vehicledata = blockJDBCDAO.getVehicleInfoByRegistrationNumber(registrationNumber);
                System.out.println(vehicledata);
                if (vehicledata.length() > 0) {
                    listView.setAdapter(new BaseAdapter() {
                        @Override
                        public int getCount() {
                            return vehicledata.length();
                        }

                        @Override
                        public Object getItem(int position) {
                            JSONObject obj = null;
                            try {
                                obj = vehicledata.getJSONObject(position);
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
                        public View getView(int position, View convertView, ViewGroup parent) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = vehicledata.getJSONObject(position);
                                System.out.println("++++++++++++++++++jsonObject+++++++++++++++++++++++");
                                System.out.println(String.valueOf(position) + jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            View cellUser = null;

                            if (convertView == null) {

                                cellUser = inflater.inflate(R.layout.cell_reminder, parent, false);

                            } else {
                                cellUser = convertView;
                            }

                            Placeholder ph = (Placeholder) cellUser.getTag();
                            TextView job;
                            TextView date1;

                            if (ph == null) {
                                job = (TextView) cellUser.findViewById(R.id.reminder_job);
                                date1 = (TextView) cellUser.findViewById(R.id.reminder_date);

                                ph = new Placeholder();
                                ph.job = job;
                                ph.datet = date1;

                                cellUser.setTag(ph);
                            } else {
                                job = ph.job;
                                date1 = ph.datet;
                            }

                            try {
                                JSONObject data = new JSONObject(jsonObject.getString("data"));
                                job.setText(jsonObject.getString("event"));

                                if (jsonObject.getString("event").equalsIgnoreCase("BuyVehicle")) {
                                    job.setText("Buy this Vehicle");
                                }
                                if (jsonObject.getString("event").equalsIgnoreCase("RegisterVehicle")) {
                                    job.setText("Register this vehicle");
                                }
                                if (jsonObject.getString("event").equalsIgnoreCase("ExchangeOwnership")) {
                                    job.setText("Sell this vehicle");
                                }
                                if (jsonObject.getString("event").equalsIgnoreCase("ServiceRepair")) {
                                    job.setText("Service & repair");
                                }

                                System.out.println("**********************************************************");
                                System.out.println(data.getString("serviced_date"));
                                date1.setText(data.getString("serviced_date"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            return cellUser;
                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });







        return view;
    }

    private void setArrayAdapterToTransactionList(){

    }

    private class Placeholder {
        public TextView job;
        public TextView datet;

    }

}
