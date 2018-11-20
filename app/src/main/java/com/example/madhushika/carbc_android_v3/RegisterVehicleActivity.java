package com.example.madhushika.carbc_android_v3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import chainUtil.KeyGenerator;
import controller.Controller;

public class RegisterVehicleActivity extends AppCompatActivity {
    //private EditText vehicleNumber;
    private EditText registrationNumber;
    private EditText chassisNumber;
    private EditText engineNumber;
    private Spinner make;
    private Spinner model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_vehicle);
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

        registrationNumber = (EditText) findViewById(R.id.registration_number);
        engineNumber = (EditText) findViewById(R.id.engine_number);
        chassisNumber = (EditText) findViewById(R.id.chassis_number);
        make = (Spinner) findViewById(R.id.make);
        model = (Spinner)findViewById(R.id.model);

        ArrayList<String> makeList = new ArrayList<>();
        makeList.add("Select");
        makeList.add("Toyota");
        makeList.add("Honda");

        ArrayList<String> modelList = new ArrayList<>();
        modelList.add("Select");
        modelList.add("Axio");
        modelList.add("Audi");

//        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(chassisNumber.getWindowToken(), 0);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getApplicationContext(),
                R.layout.item_spinner_register_vehicle, makeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        make.setAdapter(dataAdapter);

        ArrayAdapter<String> dataAdaptermodel = new ArrayAdapter<String>(this.getApplicationContext(),
                R.layout.item_spinner_register_vehicle, modelList);
        dataAdaptermodel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        model.setAdapter(dataAdaptermodel);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!registrationNumber.getText().toString().isEmpty() &&
                        !engineNumber.getText().toString().isEmpty() && !chassisNumber.getText().toString().isEmpty()){


                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterVehicleActivity.this);
                builder.setTitle("Add a Transaction");
                builder.setMessage("Do you really need to add this transaction? " );
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //call blockchain method
                        Controller controller = new Controller();

                        JSONObject object  = new JSONObject();
                        try {
                            object.put("currentOwner",KeyGenerator.getInstance().getPublicKeyAsString());
                            object.put("engineNumber",engineNumber.getText().toString());
                            object.put("chassisNumber",chassisNumber.getText().toString());
                            object.put("make",make.getSelectedItem().toString());
                            object.put("model",model.getSelectedItem().toString());
                            object.put("registrationNumber",registrationNumber.getText().toString());
                            object.put("SecondaryParty",new JSONObject());
                            object.put("ThirdParty",new JSONObject());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences preferences = getSharedPreferences("com.example.madhushika.carbc_android_v2", 0);
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("ownership", true);
                        editor.commit();


                        controller.sendTransaction("RegisterVehicle", registrationNumber.getText().toString(), object);
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
            else {
                    Toast.makeText(RegisterVehicleActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterVehicleActivity.this);
                builder.setTitle("Cancel a Transaction");
                builder.setMessage("Do you really need to cancel this transaction? " );
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
