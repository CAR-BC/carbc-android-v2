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

public class LeasingActivity extends AppCompatActivity {
    private TextView vid;
    private EditText leasingCompany;
    private EditText leasingNumber;
    private EditText leasingInfo;
    //private EditText leasingAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leasing);
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

        vid = (TextView) findViewById(R.id.vehicle_number);
        leasingCompany = (EditText) findViewById(R.id.Leasing_leasing_company);
        leasingNumber = (EditText) findViewById(R.id.lease_no);
        leasingInfo = (EditText) findViewById(R.id.leasing_info);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!leasingCompany.getText().toString().isEmpty() && !leasingNumber.getText().toString().isEmpty() &&
                        !leasingInfo.getText().toString().isEmpty()) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(LeasingActivity.this);
                    builder.setTitle("Add a Transaction");
                    builder.setMessage("Do you really need to add this transaction? ");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            JSONObject object = new JSONObject();
                            try {
                                object.put("Vehicle id", vid.getText().toString());
                                object.put("leasing company", leasingCompany.getText().toString());
                                object.put("leasing number", leasingNumber.getText().toString());
                                // object.put("leasing amount",leasingAmount.getText().toString());
                                object.put("info", leasingInfo.getText().toString());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
                } else {
                    Toast.makeText(LeasingActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LeasingActivity.this);
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
