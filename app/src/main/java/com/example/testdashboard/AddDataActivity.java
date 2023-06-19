package com.example.testdashboard;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AddDataActivity extends AppCompatActivity /*implements AdapterView.OnItemSelectedListener*/ {
    DataHelper dbHelper;
    Button ton1;
    ImageView ton2;
    EditText text2, text3, text4, text5;
    RadioButton nilam, mirah, jamrud, gsn, cabang, pusat, kalimas;
    String lks = "";
    private Spinner spinnerlokasi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        dbHelper = new DataHelper(this);

        addListenerOnSpinnerItemSelection();

        text2 = findViewById(R.id.editText2);
        text3 = findViewById(R.id.editText3);
        text4 = findViewById(R.id.editText4);
        text5 = findViewById(R.id.editText5);
        ton1 = findViewById(R.id.button1);
        ton2 = findViewById(R.id.btncancel);

        ton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("insert into demodtb2 (nama, ip, lokasi, latitude, longitude) values('" +
                        text2.getText().toString() + "','" +
                        text3.getText().toString() + "','" +
                        lks + "','" +
                        text4.getText().toString() + "','" +
                        text5.getText().toString() + "')");
                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
                DataActivity.dta.RefreshList();
                finish();
            }
        });
        ton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
    }
    public void addListenerOnSpinnerItemSelection(){
        spinnerlokasi = (Spinner) findViewById(R.id.spinnerlokasi);
        spinnerlokasi.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            lks = parent.getItemAtPosition(pos).toString();
            Toast.makeText(parent.getContext(), parent.getItemAtPosition(pos).toString(),Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}