package com.example.testdashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    DataHelper dbcenter;
    Button btnsimpan, logout;
    ImageView btncancel;
    EditText setwaktu;
    Switch switchnotif;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferences = getSharedPreferences("app",Context.MODE_PRIVATE);
        btnsimpan = findViewById(R.id.simpansetting);
        btncancel = findViewById(R.id.cancelsetting);
        setwaktu = findViewById(R.id.edittextsetwaktu);
        switchnotif = findViewById(R.id.switchnotif);
        dbcenter = new DataHelper(this);
        boolean notifikasi;
        final Preferences preferences = Preferences.getInstance(this);
        notifikasi = preferences.isPingNotificationEnable();
        switchnotif.setChecked(notifikasi);
        switchnotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.setEnablePingNotification(isChecked);
            }
        });

//        String value = setwaktu.toString();
//        final int waktu = Integer.parseInt(value.toString());
        btnsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SQLiteDatabase db = dbcenter.getWritableDatabase();
                db.execSQL("delete from tabletime");
                db.execSQL("insert into tabletime(waktu) values(" +
                        setwaktu.getText().toString() + ")");
                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        //        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent logoutintent = new Intent(SettingActivity.this, LoginActivity.class);
//                startActivity(logoutintent);
//            }
//        });
    }
    public void logoutclicked(View view) {
        sharedPreferences.edit().remove("employe");
        Intent logoutintent = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(logoutintent);
    }
}