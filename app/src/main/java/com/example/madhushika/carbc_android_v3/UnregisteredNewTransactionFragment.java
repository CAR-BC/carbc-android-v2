package com.example.madhushika.carbc_android_v3;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class UnregisteredNewTransactionFragment extends Fragment {
    private CardView buyVehicle;
    private CardView registerVehicle;


    public UnregisteredNewTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_unregistered_new_transaction, container, false);
        buyVehicle = (CardView) view.findViewById(R.id.tr_buy_vehicle);
        registerVehicle = (CardView) view.findViewById(R.id.tr_reg_new_vehicle);

        buyVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), BuyVehicleActivity.class);
//                Intent i = new Intent(getActivity(), MapsActivity.class);
                startActivity(i);
            }
        });

        registerVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RegisterVehicleActivity.class);
                startActivity(i);

            }
        });
        return view;
    }

}
