package com.example.madhushika.carbc_android_v3;


import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Objects.StatusItem;
import controller.Controller;
import core.connection.HistoryDAO;
import core.consensus.AgreementCollector;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {

    public static ListView listView;
    public static BaseAdapter baseAdapter;
    public static ArrayList<StatusItem> allHistory;
    public static ArrayList<StatusItem> historyRecords;

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

        MyApp.getContext().registerReceiver(broadcastReceiver, new IntentFilter("StatusFragment"));

        View view = inflater.inflate(R.layout.fragment_status, container, false);
        listView = (ListView) view.findViewById(R.id.status_list);
        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return historyRecords.size();
            }

            @Override
            public Object getItem(int position) {
                return historyRecords.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                StatusItem statusItem = historyRecords.get(position);
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
                ProgressBar progressBar;

                if (ph == null) {
                    job = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_event);
                    date1 = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_date);
                    condition = (ImageView) cellUser.findViewById(R.id.cell_my_vehicle_status_condition);
                    registrationNumber = (TextView) cellUser.findViewById(R.id.cell_my_vehicle_status_registrationNumber);
                    progressBar = (ProgressBar) cellUser.findViewById(R.id.progressBar);

                    ph = new Placeholder();
                    ph.job = job;
                    ph.datet = date1;
                    ph.condition = condition;
                    ph.registrationNumber = registrationNumber;
                    ph.progressBar = progressBar;

                    cellUser.setTag(ph);
                } else {
                    job = ph.job;
                    date1 = ph.datet;
                    condition = ph.condition;
                    registrationNumber = ph.registrationNumber;
                    progressBar = ph.progressBar;
                }

                if (statusItem.getJob().equalsIgnoreCase("BuyVehicle")) {
                    job.setText("Buy new Vehicle");
                    progressBar.setMax(12);

                }
                if (statusItem.getJob().equalsIgnoreCase("RegisterVehicle")) {
                    job.setText("Register new vehicle");
                    progressBar.setMax(12);

                }
                if (statusItem.getJob().equalsIgnoreCase("ExchangeOwnership")) {
                    job.setText("Sell vehicle");
                    progressBar.setMax(12);

                }
                if (statusItem.getJob().equalsIgnoreCase("ServiceRepair")) {
                    job.setText("Service & repair");
                    progressBar.setMax(12);

                }

                date1.setText(statusItem.getDate1());
                registrationNumber.setText(statusItem.getRegistrationNumber());
//                progressBar.setMax(3);
                progressBar.setProgress(statusItem.getValue());


                if (statusItem.getCondition().equalsIgnoreCase("pending")) {
                    condition.setImageDrawable(getResources().getDrawable(R.drawable.ic_progress_24dp));
                    progressBar.setVisibility(View.VISIBLE);
                }
                if (statusItem.getCondition().equalsIgnoreCase("accepted")) {
                    condition.setImageDrawable(getResources().getDrawable(R.drawable.ic_success_24dp));
                    progressBar.setVisibility(View.GONE);
                }
                if (statusItem.getCondition().equalsIgnoreCase("Failed")) {
                    condition.setImageDrawable(getResources().getDrawable(R.drawable.ic_fail_24dp));
                    progressBar.setVisibility(View.GONE);

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
        };

        setClickListnerToStatusList();

        return view;
    }

    private void setArrayAdapterTostatusList() {

        HistoryDAO historyDAO = new HistoryDAO();
        allHistory = historyDAO.getAllHistory();

        //TODO: should change this
//        if (allHistory.size() > 0 && historyRecords.size() > 0) {
//            for (int i = 0; i < allHistory.size(); i++) {
//                for (int j = 0; j < historyRecords.size(); j++) {
//                    if (allHistory.get(i).getBlockHash().equals(historyRecords.get(j).getBlockHash())) {
//                        System.out.println("**************************************************************");
//                        System.out.println("history record" + historyRecords.get(j).getCondition());
//                        System.out.println("all history " + allHistory.get(i).getCondition());
//                        historyRecords.get(j).setCondition(allHistory.get(i).getCondition());
//                    }
//                }
//            }
//        } else {
        historyRecords = allHistory;
        // }

        listView.setAdapter(baseAdapter);

    }

    private void setClickListnerToStatusList() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (historyRecords.get(position).getCondition().equalsIgnoreCase("Failed")) {
                    //TODO:resend block
                    HistoryDAO historyDAO = new HistoryDAO();
                    try {
                        JSONObject object = historyDAO.getBlockDataToResendBlock(historyRecords.get(position).getBlockHash());
                        Controller controller = new Controller();
                        controller.sendTransaction(object.getString("event"),
                                historyRecords.get(position).getRegistrationNumber(), new JSONObject(object.getString("data")));

                        historyRecords.get(position).setCondition("Pending");
                        Intent intent = new Intent("StatusFragment");
                        intent.putExtra("resendBlock", "resendBlock");
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        MyApp.getContext().sendBroadcast(intent);

                        historyDAO.deleteRecord(historyRecords.get(position).getBlockHash());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private class Placeholder {
        public TextView job;
        public TextView datet;
        public ImageView condition;
        public TextView registrationNumber;
        public ProgressBar progressBar;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getStringExtra("agreementReceived");
            String string1 = intent.getStringExtra("resendBlock");
            if (string != null) {
                if (string.equals("agreementReceived")) {
                    ListAdapter adapter = StatusFragment.listView.getAdapter();
                    StatusFragment.baseAdapter.notifyDataSetChanged();
                }
            }

            if (string1 != null) {
                if (string1.equals("resendBlock")) {
                    ListAdapter adapter = StatusFragment.listView.getAdapter();
                    StatusFragment.baseAdapter.notifyDataSetChanged();
                }
            }
        }
    };
}
