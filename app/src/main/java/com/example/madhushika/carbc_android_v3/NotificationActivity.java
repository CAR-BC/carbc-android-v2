package com.example.madhushika.carbc_android_v3;

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

import Objects.BlockInfo;

public class NotificationActivity extends AppCompatActivity {

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

        ListView notification = (ListView) findViewById(R.id.list_view_notification);

        BlockInfo b = new BlockInfo("NW-6342", "Serviced at ASB Motors", "12/08/2018");
        BlockInfo b2 = new BlockInfo("WP-4852", "Changed tires", "15/08/2018");
        BlockInfo b3 = new BlockInfo("EP-6342", "Serviced at ACD Motors", "02/08/2018");
        final BlockInfo[] blockInfoList = new BlockInfo[3];
        blockInfoList[0] = b;
        blockInfoList[1] = b;
        blockInfoList[2] = b;

        notification.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return blockInfoList.length;
            }

            @Override
            public Object getItem(int position) {
                return blockInfoList[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup) {
                BlockInfo blockInfo = blockInfoList[position];
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

                vehicle_id.setText(blockInfo.getVid());
                vehicle_description.setText(blockInfo.getDescr());
                init_date.setText(blockInfo.getInit_date());

                confirm_tr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //fill
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
}
