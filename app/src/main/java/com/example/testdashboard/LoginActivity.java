package com.example.testdashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    Button login;
    EditText uname, pwd;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.btnlogin);
        uname = findViewById(R.id.etid);
        pwd = findViewById(R.id.etpwd);

        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String usernameKey = uname.getText().toString();
                String passwordKey = pwd.getText().toString();

                if (usernameKey.equals("admin") && passwordKey.equals("admin")){
                    Toast.makeText(getApplicationContext(), "LOGIN SUKSES",
                    Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putString("role", "admin").apply();
                    Intent loginintent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginintent);
                    finish();
                }else if (usernameKey.equals("employe") && passwordKey.equals("employe")){
                    Toast.makeText(getApplicationContext(), "LOGIN SUKSES",
                            Toast.LENGTH_SHORT).show();
                    sharedPreferences.edit().putString("role", "employe").apply();
                    Intent loginintent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(loginintent);
                    finish();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Username atau Password Anda salah!")
                            .setNegativeButton("Retry", null).create().show();
                }
            }
        });
    }
}


