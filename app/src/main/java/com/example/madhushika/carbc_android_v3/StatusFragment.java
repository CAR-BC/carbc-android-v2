package com.example.madhushika.carbc_android_v3;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Objects.StatusItem;
import core.connection.HistoryDAO;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    private ListView listView;
    LayoutInflater inflater;


    public StatusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        setArrayAdapterTostatusList();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        listView = (ListView)view.findViewById(R.id.status_list);
        return view;
    }

    private void setArrayAdapterTostatusList( ){

        HistoryDAO historyDAO = new HistoryDAO();
        final ArrayList<StatusItem> allHistory = historyDAO.getAllHistory();
        Log.d("Padmal", "Finished fetching");
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return allHistory.size();
            }

            @Override
            public Object getItem(int position) {
                return allHistory.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                StatusItem statusItem = allHistory.get(position);
                View cellUser = null;

                if (convertView == null) {

                    cellUser = inflater.inflate(R.layout.cell_status, parent, false);

                } else {
                    cellUser = convertView;
                }
                Placeholder ph = (Placeholder) cellUser.getTag();
                TextView job;
                TextView date1;
                final ImageView condition;
                TextView registrationNumber;

                if (ph == null) {
                    job = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_event);
                    date1 = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_date);
                    condition = (ImageView) cellUser.findViewById(R.id.cell_my_vehicle_status_condition);
                    registrationNumber =(TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_registrationNumber);

                    ph = new Placeholder();
                    ph.job = job;
                    ph.datet = date1;
                    ph.condition = condition;
                    ph.registrationNumber = registrationNumber;

                    cellUser.setTag(ph);
                } else {
                    job = ph.job;
                    date1 = ph.datet;
                    condition = ph.condition;
                    registrationNumber = ph.registrationNumber;
                }

                job.setText(statusItem.getJob());
                date1.setText(statusItem.getDate1());
                registrationNumber.setText(statusItem.getRegistrationNumber());

                if (statusItem.getCondition().equalsIgnoreCase("pending")){
                    condition.setImageDrawable(getResources().getDrawable(R.drawable.ic_progress_24dp));
                } if (statusItem.getCondition().equalsIgnoreCase("accepted")){
                    condition.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_24dp));
                } if (statusItem.getCondition().equalsIgnoreCase("Failed")){
                    condition.setImageDrawable(getResources().getDrawable(R.drawable.ic_fail_24dp));
                    /*condition.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: resend block
                            if(condition.getText().toString().equalsIgnoreCase("Failed")){
                                condition.setText("Pending");
                                condition.setBackgroundResource(R.color.colorYellow);
                            }
                        }
                    });*/
                }
                return cellUser;
            }
        });
    }

    private class Placeholder {
        public TextView job;
        public TextView datet;
        public ImageView condition;
        public TextView registrationNumber;

    }

}
