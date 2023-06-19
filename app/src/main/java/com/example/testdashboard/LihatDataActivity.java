package com.example.testdashboard;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

public class LihatDataActivity extends AppCompatActivity {
    protected Cursor cursor;
    DataHelper dbHelper;
    ImageView btnback, btnloc;
    Button btnping;
    TextView text1, text2, text3, text4, text5, text6, text7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_data);
        dbHelper = new DataHelper(this);

        text1 = findViewById(R.id.tvnilainomor);
        text2 = findViewById(R.id.tvnilainama);
        text3 = findViewById(R.id.tvnilaiip);
        text4 = findViewById(R.id.tvnilailokasi);
        text5 = findViewById(R.id.tvnilaistatus);
        text6 = findViewById(R.id.tvnilailatitude);
        text7 = findViewById(R.id.tvnilailongitude);
        btnback = findViewById(R.id.btnback);
        btnping = findViewById(R.id.buttonping);
        btnloc = findViewById(R.id.btnaddlocation);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM demodtb2 WHERE nama = '" +
                getIntent().getStringExtra("nama") + "'",null);
        cursor.moveToFirst();
        if (cursor.getCount()>0) {
            cursor.moveToPosition(0);
            text1.setText(cursor.getString(0).toString());
            text2.setText(cursor.getString(1).toString());
            text3.setText(cursor.getString(2).toString());
            text4.setText(cursor.getString(3).toString());
            if (cursor.getString(4) != null) {
                text5.setText(cursor.getString(4).toString());
            } else {
                text5.setText("-");
            }
            if (cursor.getString(5) != null) {
                text6.setText(cursor.getString(5).toString());
            } else {
                text6.setText("-");
            }
            if (cursor.getString(6) != null) {
                text7.setText(cursor.getString(6).toString());
            } else {
                text7.setText("-");
            }
        }
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        btnloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                double lat = Double.parseDouble(text6.getText().toString());
                double lng = Double.parseDouble(text7.getText().toString());
//                intent.putExtra("lat", lat);
//                intent.putExtra("lng", lng);
//                startActivity(intent);

                String loc = "geo:0,0?q=" + lat + "," + lng + "(" + Uri.parse(text2.getText().toString()) + ")";
                Uri gmmIntentUri = Uri.parse(loc);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        btnping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Ping.onAddress(text3.toString()).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
                    @Override
                    public void onResult(PingResult pingResult) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues args = new ContentValues();
                        args.put("status", "Up");
                        String whereClause = text1.getText().toString();
                        db.update("demodtb2", args, whereClause, null);
                    }
                    @Override
                    public void onFinished(PingStats pingStats) {
                    }
                    @Override
                    public void onError(Exception e) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues args = new ContentValues();
                        args.put("status", "Down");
                        String whereClause = text1.getText().toString();
                        db.update("demodtb2", args, whereClause, null);
                    }
                });
            }
        });
    }
}
//    public void pingExecute(View view) {
//        Ping.onAddress(text3.toString()).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
//            @Override
//            public void onResult(PingResult pingResult) {
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                ContentValues args = new ContentValues();
//                args.put("status", "Up");
//                String whereClause = text1.toString();
//                db.update("demodtb2", args, whereClause, null);
//            }
//            @Override
//            public void onFinished(PingStats pingStats) {
//            }
//            @Override
//            public void onError(Exception e) {
//                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                ContentValues args = new ContentValues();
//                args.put("status", "Down");
//                String whereClause = text1.toString();
//                db.update("demodtb2", args, whereClause, null);
//            }
//        });
//    }

