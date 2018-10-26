package com.example.madhushika.carbc_android_v3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private Button login_btn;
    private TextView newUser_btn;
    private TextView fogot_pw_btn;
    private EditText usernametxt;
    private EditText passwordtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences preferences = getSharedPreferences("com.example.madhushika.carbc_android_v2", 0);
        final String userNameStored = preferences.getString("user_name", "NONE");
        final String passwordStored = preferences.getString("password", "NONE");
        String emailStored = preferences.getString("email", "NONE");
        String idStored = preferences.getString("id", "NONE");
        String roleStored = preferences.getString("role", "NONE");
        final boolean login_statusStored = preferences.getBoolean("login_status", false);
        final SharedPreferences.Editor editor = preferences.edit();


        login_btn = (Button) findViewById(R.id.log_in);
        newUser_btn = (TextView) findViewById(R.id.new_user_register);
        fogot_pw_btn = (TextView) findViewById(R.id.forget_password);
        usernametxt = (EditText) findViewById(R.id.login_name);
        passwordtxt = (EditText) findViewById(R.id.login_password);


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!login_statusStored) {
                    String usernameGiven = usernametxt.getText().toString();
                    String passwordGiven = passwordtxt.getText().toString();

                    if (usernameGiven.equals(userNameStored) && passwordGiven.equals(passwordStored)) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        editor.putBoolean("login_status", true);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter correct username and " +
                                "password to continue", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        newUser_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                LoginActivity.this.finish();
                //editor.putBoolean("login_status",true);
            }
        });

        fogot_pw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        editor.commit();
    }
}
