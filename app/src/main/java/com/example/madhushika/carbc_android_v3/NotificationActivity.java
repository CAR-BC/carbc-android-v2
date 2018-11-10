package com.example.madhushika.carbc_android_v3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.ArrayList;

import Objects.BlockInfo;
import Objects.ServiceType;
import core.blockchain.Block;
import core.blockchain.BlockHeader;
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

       // registerReceiver(broadcastReceiver, new IntentFilter("NotificationActivity"));
        setArrayAdapterToNotificationList(MainActivity.notificationList);
    }

//    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String str = intent.getStringExtra("newBlockReceived");
//            if (str.equals("newBlockAdded")){
//                nonAprovedBlocks = new ArrayList<>();
//                setArrayAdapterToNotificationList(nonAprovedBlocks);
//            }
//        }
//    };

    private void setArrayAdapterToNotificationList(final ArrayList<Block> notificationList){
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
                Button request_more;

                if (ph == null) {
                    vehicle_id = (TextView) cellUser.findViewById(R.id.notification_vid);
                    vehicle_description = (TextView) cellUser.findViewById(R.id.notification_description);
                    init_date = (TextView) cellUser.findViewById(R.id.notification_date);
                    confirm_tr = (Button) cellUser.findViewById(R.id.notification_confirm);
                    request_more = (Button) cellUser.findViewById(R.id.notification_request_more);

                    ph = new Placeholder();
                    ph.vehicle_Id = vehicle_id;
                    ph.vehicle_description = vehicle_description;
                    ph.initiate_date = init_date;
                    ph.confirm_tx = confirm_tr;
                    ph.more_btn = request_more;

                    cellUser.setTag(ph);
                } else {
                    vehicle_id = ph.vehicle_Id;
                    vehicle_description = ph.vehicle_description;
                    init_date = ph.initiate_date;
                    confirm_tr = ph.confirm_tx;
                    request_more = ph.more_btn;
                }

                vehicle_id.setText(block.getBlockBody().getTransaction().getAddress());
                vehicle_description.setText(block.getBlockBody().getTransaction().getEvent());
                init_date.setText(block.getBlockBody().getTransaction().getTime());

                confirm_tr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Consensus.getInstance().sendAgreementForBlock(block.getBlockHeader().getHash());
                        Toast.makeText(NotificationActivity.this, "Sent your confirmation", Toast.LENGTH_SHORT).show();
                        MainActivity.notificationList.remove(block);
                    }
                });

                request_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //fill
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
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(broadcastReceiver);
//    }
}
