package com.example.madhushika.carbc_android_v3;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import chainUtil.KeyGenerator;
import controller.Controller;
import core.connection.VehicleJDBCDAO;


public class BuyVehicleActivity extends AppCompatActivity {
    private EditText vid;
    private EditText owner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_vehicle);
        hideActionBar();
        ImageView backBtn = (ImageView) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button done = (Button) findViewById(R.id.done_btn);
        Button cancel = (Button) findViewById(R.id.cancel_btn);

        vid = (EditText) findViewById(R.id.buy_vehicle_vehicle_no);
        owner = (EditText) findViewById(R.id.buy_vehicle_previous_owner);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!vid.getText().toString().isEmpty()) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(BuyVehicleActivity.this);
                    builder.setTitle("Add a Transaction");
                    builder.setMessage("Do you really need to add this transaction? ");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            VehicleJDBCDAO vehicleJDBCDAO = new VehicleJDBCDAO();
                            String preOwner = vehicleJDBCDAO.getCurrentOwner(vid.getText().toString());
                            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
                            System.out.println(preOwner);

                            if (preOwner!= null){
                                //call blockchain method
                                JSONObject object = new JSONObject();
                                try {

                                    object.put("newOwner", KeyGenerator.getInstance().getPublicKeyAsString());
//                                    object.put("registrationNumber", vid.getText().toString());
                                    object.put("vehicleId", vid.getText().toString());

//                                    object.put("PreOwner", preOwner);
                                    JSONObject secondaryParty = new JSONObject();
                                    JSONObject PreOwner = new JSONObject();
                                    PreOwner.put("name", "Ashan");
                                    PreOwner.put("publicKey",preOwner);
                                    secondaryParty.put("PreOwner", PreOwner);
                                    object.put("SecondaryParty", secondaryParty);

                                    JSONObject thirdParty = new JSONObject();
                                    object.put("ThirdParty", thirdParty);

                                    Controller controller = new Controller();
                                    controller.sendTransaction("BuyVehicle", vid.getText().toString()
                                           , object);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //send transaction
                                finish();
                            }else {
                                Toast.makeText(BuyVehicleActivity.this,"No such vehicle in the system",Toast.LENGTH_SHORT).show();
                            }

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
                } else {
                    Toast.makeText(BuyVehicleActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BuyVehicleActivity.this);
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

    private void hideActionBar() {
        //Hide the action bar only if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
