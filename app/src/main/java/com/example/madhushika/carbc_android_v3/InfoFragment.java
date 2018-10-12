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

import Objects.ReminderItem;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private Spinner spinner;
    private ListView listView;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        spinner =(Spinner)view.findViewById(R.id.vehicle_number);
        List<String> list = new ArrayList<String>();
        list.add("NW-6060");
        list.add("WP-2112");
        list.add("NW-6146");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.item_spinner, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        listView = (ListView)view.findViewById(R.id.info_list);

        final ReminderItem[] reminderItems = new ReminderItem[3];
        ReminderItem item1 = new ReminderItem("NW-2563","Leasing payment","12/05/2018");
        ReminderItem item2 = new ReminderItem("NW-6395","insurance payment","13/05/2018");
        ReminderItem item3 = new ReminderItem("WP-3025","annual service","02/05/2018");

        reminderItems[0] = item1;
        reminderItems[1] = item2;
        reminderItems[2] = item3;

        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return reminderItems.length;
            }

            @Override
            public Object getItem(int position) {
                return reminderItems[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ReminderItem reminderItem = reminderItems[position];
                View cellUser = null;

                if (convertView == null) {

                    cellUser = inflater.inflate(R.layout.cell_reminder, parent, false);

                } else {
                    cellUser = convertView;
                }
                Placeholder ph = (Placeholder) cellUser.getTag();
                TextView vid;
                TextView job;
                TextView date1;

                if (ph == null) {
                    vid = (TextView) cellUser.findViewById(R.id.reminder_vid);
                    job = (TextView) cellUser.findViewById(R.id.reminder_job);
                    date1 = (TextView) cellUser.findViewById(R.id.reminder_date);

                    ph = new Placeholder();
                    ph.job = job;
                    ph.datet = date1;
                    ph.vid = vid;

                    cellUser.setTag(ph);
                } else {
                    job = ph.job;
                    date1 = ph.datet;
                    vid = ph.vid;
                }

                job.setText(reminderItem.getjob());
                date1.setText(reminderItem.getDate1());
                vid.setText(reminderItem.getVid());

                return cellUser;
            }
        });



        return view;
    }

    private class Placeholder {
        public TextView job;
        public TextView datet;
        public TextView vid;

    }

}
