package com.example.madhushika.carbc_android_v3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Objects.StatusItem;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    private Spinner spinner;
    private ListView listView;

    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        spinner =(Spinner)view.findViewById(R.id.vehicle_number);
        List<String> list = new ArrayList<String>();
        list.add("NW-6060");
        list.add("WP-2112");
        list.add("NW-6146");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.item_spinner, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        listView = (ListView)view.findViewById(R.id.status_list);

        final StatusItem[] statusItems = new StatusItem[3];
        StatusItem item1 = new StatusItem("Service","12/05/2018","pending");
        StatusItem item2 = new StatusItem("Insurance","13/05/2018","added");
        StatusItem item3 = new StatusItem("Emision testing","02/05/2018","reject");

        statusItems[0] = item1;
        statusItems[1] = item2;
        statusItems[2] = item3;

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return statusItems.length;
            }

            @Override
            public Object getItem(int position) {
                return statusItems[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                StatusItem statusItem = statusItems[position];
                View cellUser = null;

                if (convertView == null) {

                    cellUser = inflater.inflate(R.layout.cell_status, parent, false);

                } else {
                    cellUser = convertView;
                }
                Placeholder ph = (Placeholder) cellUser.getTag();
                TextView job;
                TextView date1;
                TextView condition;

                if (ph == null) {
                    job = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_event);
                    date1 = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_date);
                    condition = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_condition);

                    ph = new Placeholder();
                    ph.job = job;
                    ph.datet = date1;
                    ph.condition = condition;

                    cellUser.setTag(ph);
                } else {
                    job = ph.job;
                    date1 = ph.datet;
                    condition = ph.condition;
                }

                job.setText(statusItem.getJob());
                date1.setText(statusItem.getDate1());
                condition.setText(statusItem.getCondition());

                return cellUser;
            }
        });

        return view;
    }

    private class Placeholder {
        public TextView job;
        public TextView datet;
        public TextView condition;

    }

}
