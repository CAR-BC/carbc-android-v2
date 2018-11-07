package com.example.madhushika.carbc_android_v3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameGiven;
    private String roleGiven;
    private EditText idGiven;
    private EditText emailGiven;
    private EditText passwordGiven;
    private EditText confirmPasswordGiven;

    private TextView idLable;

    private Spinner spinner;

    private Button register_btn;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        hideActionBar();

        ImageView backBtn = (ImageView) findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        usernameGiven = (EditText) findViewById(R.id.signup_name_txt);
        spinner =(Spinner)findViewById(R.id.signup_role_spinner);
        idGiven = (EditText) findViewById(R.id.signup_id_txt);
        emailGiven = (EditText) findViewById(R.id.signup_email_txt);
        passwordGiven = (EditText) findViewById(R.id.signup_password_txt);
        confirmPasswordGiven = (EditText) findViewById(R.id.signup_confirm_password_txt);

        register_btn = (Button) findViewById(R.id.sign_in_button);

        ArrayList<String> rolelist = new ArrayList<>();
        rolelist.add("Normal User");
        rolelist.add("Insurance Company");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,rolelist);

        spinner.setAdapter(adapter);
        SharedPreferences preferences = getSharedPreferences("com.example.madhushika.carbc_android_v2", 0);
        final SharedPreferences.Editor editor = preferences.edit();

        emailGiven.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0){
                    if (!isValidEmail(s)){
                        emailGiven.setTextColor(R.color.colorRed);
                        //red
//                        Toast.makeText(RegisterActivity.this,"not a valid email",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //green
                        emailGiven.setTextColor(R.color.colorgGreen);
                        Toast.makeText(RegisterActivity.this,"valid email",Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordGiven.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = passwordGiven.getText().toString();

                if(s.equals(password)){
                    Toast.makeText(RegisterActivity.this,"confirmed",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = usernameGiven.getText().toString();
                String id = idGiven.getText().toString();
                String email = emailGiven.getText().toString();
                String confirmPassword = confirmPasswordGiven.getText().toString();
                password = passwordGiven.getText().toString();
                roleGiven = spinner.getSelectedItem().toString();

                if (isValidEmail(email) && checkStrongPassword(password) && password.equals(confirmPassword)
                        && !username.equals(" ") && !id.equals(" ")){



                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    RegisterActivity.this.finish();

                    editor.putBoolean("login_status",true);
                    editor.putString("user_name",username);
                    editor.putString("password",password);
                    editor.putString("email",email);
                    editor.putString("id",id);
                    editor.putString("role",roleGiven);

                    //TODO: create key pair

                    editor.commit();
                }
                else {
                    Toast.makeText(RegisterActivity.this,"Enter correct details",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return  android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean checkStrongPassword(String password){
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean isAtLeast8   = password.length() >= 8;//Checks for at least 8 characters
        boolean hasSpecial   = !password.matches("[A-Za-z0-9 ]*");
        boolean found=false;
        for(char c : password.toCharArray()){
            if(Character.isDigit(c)){
                found = true;
            } else if(found){
                // If we already found a digit before and this char is not a digit, stop looping
                break;
            }
        }
        return hasUppercase && hasLowercase && isAtLeast8 && hasSpecial && found;
    }

    private void hideActionBar() {
        //Hide the action bar only if it exists
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
