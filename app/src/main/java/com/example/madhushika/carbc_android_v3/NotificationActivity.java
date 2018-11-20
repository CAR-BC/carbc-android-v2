package com.example.madhushika.carbc_android_v3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import Objects.BlockInfo;
import Objects.ServiceType;
import chainUtil.ChainUtil;
import chainUtil.KeyGenerator;
import core.blockchain.Block;
import core.blockchain.BlockHeader;
import core.consensus.Agreement;
import core.consensus.Consensus;

public class NotificationActivity extends AppCompatActivity {
    private ListView notification;

    //public static ArrayList <Block> nonAprovedBlocks = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        hideActionBar();

        ImageView backBtn = (ImageView) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerReceiver(broadcastReceiver, new IntentFilter("MainActivity"));

        ArrayList<Block> arrayList = new ArrayList<>();
        arrayList.addAll(MainActivity.criticalNotificationList);
        arrayList.addAll(MainActivity.notificationList);
        setArrayAdapterToNotificationList(arrayList);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String str1 = intent.getStringExtra("newNomApprovedBlockReceived");
            //TODO: Test this again
            String str = intent.getStringExtra("confirmationSent");
            if (str != null) {
                if (str.equals("confirmationSent")) {
                    ArrayList<Block> arrayList = new ArrayList<>();
                    arrayList.addAll(MainActivity.criticalNotificationList);
                    arrayList.addAll(MainActivity.notificationList);
                    setArrayAdapterToNotificationList(arrayList);
                }
            }
            if (str1 != null) {
                if (str1.equals("newBlock")) {
                    ArrayList<Block> arrayList = new ArrayList<>();
                    arrayList.addAll(MainActivity.criticalNotificationList);
                    arrayList.addAll(MainActivity.notificationList);
                    setArrayAdapterToNotificationList(arrayList);
                }
            }

        }
    };

    private void setArrayAdapterToNotificationList(final ArrayList<Block> notificationList) {
        notification = (ListView) findViewById(R.id.list_view_notification);

        notification.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return notificationList.size();
            }

            @Override
            public Object getItem(int position) {
                return notificationList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {
                final Block block = notificationList.get(position);


                View cellUser = null;

                if (convertView == null) {
                    //cellUser = inflater.inflate(R.layout.cell_notification, parent, false);
                    cellUser = LayoutInflater.from(NotificationActivity.this).inflate(R.layout.cell_notification, viewGroup, false);
                } else {
                    cellUser = convertView;
                }
                Placeholder ph = (Placeholder) cellUser.getTag();
                TextView vehicle_id;
                TextView vehicle_description;
                TextView init_date;
                Button confirm_tr;
                //Button request_more;
                ImageButton discard;

                if (ph == null) {
                    vehicle_id = (TextView) cellUser.findViewById(R.id.notification_vid);
                    vehicle_description = (TextView) cellUser.findViewById(R.id.notification_description);
                    init_date = (TextView) cellUser.findViewById(R.id.notification_date);
                    confirm_tr = (Button) cellUser.findViewById(R.id.notification_confirm);
                   // request_more = (Button) cellUser.findViewById(R.id.notification_request_more);
                    discard = (ImageButton) cellUser.findViewById(R.id.discardBtn);

                    ph = new Placeholder();
                    ph.vehicle_Id = vehicle_id;
                    ph.vehicle_description = vehicle_description;
                    ph.initiate_date = init_date;
                    ph.confirm_tx = confirm_tr;
                    //ph.more_btn = request_more;
                    ph.discardBtn = discard;

                    cellUser.setTag(ph);
                } else {
                    vehicle_id = ph.vehicle_Id;
                    vehicle_description = ph.vehicle_description;
                    init_date = ph.initiate_date;
                    confirm_tr = ph.confirm_tx;
                   // request_more = ph.more_btn;
                    discard = ph.discardBtn;
                }

                if (MainActivity.criticalNotificationList.contains(block)) {
                    cellUser.setBackgroundColor(getResources().getColor(R.color.colorRejectedRed));
                }

                vehicle_id.setText(block.getBlockBody().getTransaction().getAddress());

                if (block.getBlockBody().getTransaction().getEvent().equalsIgnoreCase("BuyVehicle")) {
                    vehicle_description.setText("Buy new Vehicle");
                }
                if (block.getBlockBody().getTransaction().getEvent().equalsIgnoreCase("RegisterVehicle")) {
                    vehicle_description.setText("Register new vehicle");
                }
                if (block.getBlockBody().getTransaction().getEvent().equalsIgnoreCase("ExchangeOwnership")) {
                    vehicle_description.setText("Sell vehicle");
                }
                if (block.getBlockBody().getTransaction().getEvent().equalsIgnoreCase("ServiceRepair")) {
                    vehicle_description.setText("Service & repair");
                }
                init_date.setText(block.getBlockBody().getTransaction().getTime());

                confirm_tr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Consensus.getInstance().sendAgreementForBlock(block.getBlockHeader().getHash());
                        String blockHash = block.getBlockHeader().getHash();
                        String digitalSignature = ChainUtil.digitalSignature(block.getBlockHeader().getHash());
                        String signedBlock = digitalSignature;
                        Agreement agreement = new Agreement(digitalSignature, signedBlock, blockHash,
                                KeyGenerator.getInstance().getPublicKeyAsString());
                        Consensus.getInstance().handleAgreement(agreement);
                        Toast.makeText(NotificationActivity.this, "Sent your confirmation", Toast.LENGTH_SHORT).show();
                        MainActivity.notificationList.remove(block);
                        MainActivity.criticalNotificationList.remove(block);

                        Intent intent = new Intent("MainActivity");
                        intent.putExtra("newNomApprovedBlockReceived", "newBlock");
                        intent.putExtra("confirmationSent", "confirmationSent");
                        System.out.println("+++++++++++newNomApprovedBlockReceived++++++++++");
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        MyApp.getContext().sendBroadcast(intent);

                    }
                });

//                request_more.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intents = new Intent(NotificationActivity.this, ShowMoreInfoNotificationView.class);
//                        intents.putExtra("block", block);
//                        startActivity(intents);
//                        //fill
//                    }
//                });

                discard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainActivity.notificationList.remove(block);
                        MainActivity.criticalNotificationList.remove(block);
                        Intent intent = new Intent("MainActivity");
                        intent.putExtra("newNomApprovedBlockReceived", "newBlock");
                        intent.putExtra("confirmationSent", "confirmationSent");
                        System.out.println("+++++++++++newNomApprovedBlockReceived++++++++++");
                        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                        MyApp.getContext().sendBroadcast(intent);
                    }
                });
                return cellUser;
            }
        });
    }

    private void hideActionBar() {
        //Hide the action bar only if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private class Placeholder {
        public TextView vehicle_Id;
        public TextView vehicle_description;
        public TextView initiate_date;
        public Button more_btn;
        public Button confirm_tx;
        public ImageButton discardBtn;
    }

    @Override
    protected void onPause() {
        super.onResume();
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

}
