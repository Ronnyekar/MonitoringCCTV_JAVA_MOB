package com.example.testdashboard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditDataActivity extends AppCompatActivity {
    protected Cursor cursor;
    DataHelper dbHelper;
    Button ton1;
    ImageView ton2;
    EditText etnama, etip, etlat, etlong;
    String lks = "";
    String id;
    Spinner spinnerlokasi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_data);
        dbHelper = new DataHelper(this);
        addListenerOnSpinnerItemSelection();
        etnama = findViewById(R.id.etnama);
        etip = findViewById(R.id.etip);
        etlat = findViewById(R.id.etlat);
        etlong = findViewById(R.id.etlong);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM demodtb2 WHERE nama = '" +
                getIntent().getStringExtra("nama") + "'",null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0){
            cursor.moveToPosition(0);
            id = cursor.getString(0);
            etnama.setText(cursor.getString(1).toString());
            etip.setText(cursor.getString(2).toString());
            etlat.setText(cursor.getString(5).toString());
            etlong.setText(cursor.getString(6).toString());
        }
        ton1 = findViewById(R.id.button1);
        ton2 = findViewById(R.id.btncancel);
        // daftarkan even onClick pada btnSimpan
        ton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("update demodtb2 set " +
                        "nama='" + etnama.getText().toString() + "', " +
                        "ip='" + etip.getText().toString() + "', " +
                        "lokasi='" + lks + "', " +
                        "latitude='" + etlat.getText().toString() + "', " +
                        "longitude='" + etlong.getText().toString() + "' " +
                        "where no="+id);
//                db.execSQL("update demodtb2 set nama='"+
//                        text2.getText().toString() +
//                        text3.getText().toString() +
//                        lks +
//                        text4.getText().toString() +
//                        text5.getText().toString() + "' where no='" + "'");
                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
                DataActivity.dta.RefreshList();
                finish();
            }
        });
        ton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                finish();

            }
        });
    }
    public void addListenerOnSpinnerItemSelection(){
        spinnerlokasi = (Spinner) findViewById(R.id.spinnerlokasi);
        spinnerlokasi.setOnItemSelectedListener(new EditDataActivity.CustomOnItemSelectedListener());
    }
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            lks = parent.getItemAtPosition(pos).toString();
//            Toast.makeText(parent.getContext(), parent.getItemAtPosition(pos).toString(),Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}