package com.example.madhushika.carbc_android_v3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class EmmisiontestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emmisiontesting);
        hideActionBar();
        ImageView backBtn = (ImageView) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
