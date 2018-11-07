package com.example.madhushika.carbc_android_v3;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class RegisterVehicleActivity extends AppCompatActivity {
    private EditText vehicleNumber;
    private EditText registrationNumber;
    private EditText currentOwner;
    private EditText engineNumber;
    private EditText vehicleClass;
    private EditText condition;
    private EditText make;
    private EditText model;
    private EditText manufacturingYear;

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

        vehicleNumber = (EditText)findViewById(R.id.reg_vehicle_id);
        registrationNumber = (EditText) findViewById(R.id.registration_number);
        currentOwner = (EditText) findViewById(R.id.current_owner);
        engineNumber = (EditText) findViewById(R.id.engine_number);
        vehicleClass = (EditText) findViewById(R.id.vehicle_class);
        condition = (EditText) findViewById(R.id.condition);
        make = (EditText) findViewById(R.id.make);
        model = (EditText) findViewById(R.id.model);
        manufacturingYear = (EditText) findViewById(R.id.manufacturing_year);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!vehicleNumber.getText().toString().isEmpty() && !registrationNumber.getText().toString().isEmpty() &&
                        !currentOwner.getText().toString().isEmpty() && !engineNumber.getText().toString().isEmpty()
                && !vehicleClass.getText().toString().isEmpty() && !condition.getText().toString().isEmpty()
                        && !make.getText().toString().isEmpty() && !model.getText().toString().isEmpty() &&
                        !manufacturingYear.getText().toString().isEmpty()){


                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterVehicleActivity.this);
                builder.setTitle("Add a Transaction");
                builder.setMessage("Do you really need to add this transaction? " );
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //call blockchain method

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
