package com.example.madhushika.carbc_android_v3;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import Objects.ReminderItem;
import Test.ChainUtilTest;
import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import controller.Controller;
import core.blockchain.Block;
import core.blockchain.BlockBody;
import core.blockchain.BlockHeader;
import core.blockchain.Transaction;
import network.Client.RequestMessage;
import network.Node;
import network.Protocol.MessageCreator;
import network.communicationHandler.MessageSender;

public class SellVehicleActivity extends AppCompatActivity {

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

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SellVehicleActivity.this);
                builder.setTitle("Add a Transaction");
                builder.setMessage("Do you really need to add this transaction? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        //call blockchain method
                        Controller controller = new Controller();
                        try {
                            JSONObject jsonObject = new JSONObject();
                            JSONObject jsonObjectNewOwner = new JSONObject();
                            JSONObject jsonSecondary = new JSONObject();

                            jsonObjectNewOwner.put("name", "Ashan");
                            jsonObjectNewOwner.put("publicKey", KeyGenerator.getInstance().getPublicKeyAsString());

                            jsonSecondary.put("NewOwner", jsonObjectNewOwner);
                            jsonObject.put("SecondaryParty", jsonSecondary);
                            jsonObject.put("ThirdParty", new JSONArray());

                            System.out.println(jsonObject);
//
                            String sender = KeyGenerator.getInstance().getPublicKeyAsString();
                            String nodeID = Node.getInstance().getNodeConfig().getNodeID();
                            Transaction transaction = new Transaction("V",sender,"ExchangeOwnership", jsonObject.toString(), nodeID);

                            BlockBody blockBody = new BlockBody();
                            blockBody.setTransaction(transaction);
                            String blockHash = ChainUtil.getInstance().getBlockHash(blockBody);
                            BlockHeader blockHeader = new BlockHeader(blockHash);

                            Block block = new Block(blockHeader, blockBody);
                            MessageSender.getInstance().broadCastBlockTest(block);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        controller.sendBlock("v", "service", "data", "192.168.8.105", 49222);
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
