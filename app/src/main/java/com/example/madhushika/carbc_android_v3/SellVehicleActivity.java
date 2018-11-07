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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import controller.Controller;
import core.blockchain.Block;
import core.blockchain.BlockBody;
import core.blockchain.BlockHeader;
import core.blockchain.BlockInfo;
import core.blockchain.Transaction;
import core.connection.APICaller;
import core.connection.BlockJDBCDAO;
import core.connection.Identity;
import network.Node;
import network.communicationHandler.MessageSender;

public class SellVehicleActivity extends AppCompatActivity {
    private TextView vehicleNumber;
    private EditText previousOwner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_vehicle);
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

        vehicleNumber = (TextView) findViewById(R.id.vehicle_number);
        previousOwner = (EditText) findViewById(R.id.buy_vehicle_previous_owner);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !previousOwner.getText().toString().isEmpty()) {
                    
                    AlertDialog.Builder builder = new AlertDialog.Builder(SellVehicleActivity.this);
                    builder.setTitle("Add a Transaction");
                    builder.setMessage("Do you really need to add this transaction? ");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            //call blockchain method

                            BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
                            BlockInfo blockInfo = new BlockInfo();
                            blockInfo.setAddress("address");
                            blockInfo.setBlockNumber(256);
                            // blockInfo.setBlockTime();
                            blockInfo.setData("data");
                            blockInfo.setHash("hash5");
                            blockInfo.setSender("sender");
                            blockInfo.setEvent("event");
                            blockInfo.setValidity(true);
                            blockInfo.setPreviousHash("previous hash");
                            blockInfo.setTransactionId("I22222");

                            Identity identity = new Identity("hash4","pub key","role","name");

                            try {
                                blockJDBCDAO.addBlockToBlockchain(blockInfo,identity);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            finish();
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            // User cancelled the dialog
                            //new APICaller().execute("http://192.168.8.102/carbc/getPreviousHash.php","GET","v","f");
                            BlockJDBCDAO blockJDBCDAO = new BlockJDBCDAO();
                            try {
                                JSONObject jsonObject = blockJDBCDAO.getBlockchain(1);
                                //blockJDBCDAO.getBlockchain(1);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Toast.makeText(SellVehicleActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();

                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SellVehicleActivity.this);
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
