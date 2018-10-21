package com.example.madhushika.carbc_android_v3;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import TransactionFragments.BuyVehicleFragment;
import TransactionFragments.SellVehicleFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewTransactionFragment extends Fragment {
    private static MainActivity activity;

    private Spinner spinner;

    private CardView buyVehicle;
    private CardView sellVehicle;
    private CardView serviceVehicle;
    private CardView registerVehicle;
    private CardView insureVehicle;
    private CardView leasing;
    private CardView leasePayment;
    private CardView emissionTesting;


    public NewTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_new_transaction, container, false);
        spinner =(Spinner)view.findViewById(R.id.vehicle_number);
        List<String> list = new ArrayList<String>();
        list.add("NW-6060");
        list.add("WP-2112");
        list.add("NW-6146");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.item_spinner, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        buyVehicle = (CardView) view.findViewById(R.id.tr_buy_vehicle);
        sellVehicle = (CardView) view.findViewById(R.id.tr_sell_vehicle2);
        serviceVehicle = (CardView) view.findViewById(R.id.tr_service_vehicle);
        registerVehicle = (CardView) view.findViewById(R.id.tr_reg_new_vehicle);
        insureVehicle = (CardView) view.findViewById(R.id.tr_insure_vehicle);
        leasing = (CardView) view.findViewById(R.id.tr_leasing_vehicle);
        leasePayment =(CardView) view.findViewById(R.id.tr_leasing_payment_vehicle);
        emissionTesting = (CardView) view.findViewById(R.id.tr_emision_vehicle);

        buyVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BuyVehicleActivity.class);
                String vehiclNo = spinner.getSelectedItem().toString();
                i.putExtra("vid",vehiclNo);
                startActivity(i);
            }
        });
        sellVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SellVehicleActivity.class);
                String vehiclNo = spinner.getSelectedItem().toString();
                i.putExtra("vid",vehiclNo);
                startActivity(i);
            }
        });
        serviceVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ServiceActivity.class);
                String vehiclNo = spinner.getSelectedItem().toString();
                i.putExtra("vid",vehiclNo);
                startActivity(i);

            }
        });
        registerVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RegisterVehicleActivity.class);
                String vehiclNo = spinner.getSelectedItem().toString();
                i.putExtra("vid",vehiclNo);
                startActivity(i);

            }
        });
        insureVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), InsureActivity.class);
                String vehiclNo = spinner.getSelectedItem().toString();
                i.putExtra("vid",vehiclNo);
                startActivity(i);

            }
        });
        leasing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LeasingActivity.class);
                String vehiclNo = spinner.getSelectedItem().toString();
                i.putExtra("vid",vehiclNo);
                startActivity(i);

            }
        });
        leasePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LeasingPaymentActivity.class);
                String vehiclNo = spinner.getSelectedItem().toString();
                i.putExtra("vid",vehiclNo);
                startActivity(i);

            }
        });
        emissionTesting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), EmmisiontestingActivity.class);
                String vehiclNo = spinner.getSelectedItem().toString();
                i.putExtra("vid",vehiclNo);
                startActivity(i);

            }
        });



        return view;
    }


    public static void setActivity(MainActivity Activity) {
        activity = Activity;
    }

}
