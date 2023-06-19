package com.example.testdashboard;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class DataActivity extends AppCompatActivity {
    String[] daftar;
    ListView ListView01;
    protected Cursor cursor;
    DataHelper dbcenter;
    public static DataActivity dta;
    private Spinner spinnersortby;
    private String orderBy;
    String flks = "";
    SharedPreferences sharedPreferences;

    //search configuration
//    private static String[] Suggestion = new String[] {
//            "Nilam", "Mirah", "Jamrud", "GSN", "Kantor", "Cabang", "Pusat", "Kalimas"
//    };
//    private MaterialSearchView mMaterialSV;
    private String filter = "all";

    private List<String> ipList = new ArrayList<>();
    private List<String> ipId = new ArrayList<>();
    private List<String> autotime = new ArrayList<>();

    public static final String PING_CHANNEL = "ping channel";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);

//        mMaterialSV = findViewById(R.id.search_view);
//        mMaterialSV.setSuggestions(Suggestion);
        orderBy = "no";
        FloatingActionButton fab = findViewById(R.id.fab);
        if (sharedPreferences.getString("role", "").equalsIgnoreCase("employe")) {
            fab.setVisibility(View.INVISIBLE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DataActivity.this, AddDataActivity.class);
                startActivity(intent);
            }
        });
        dta = this;
        dbcenter = new DataHelper(this);
        addListenerOnSpinnerItemSelection();
        RefreshList();
//        autoping();
    }
    public void RefreshList(){
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        if(filter.equalsIgnoreCase("all")) {
            cursor = db.rawQuery("SELECT * FROM demodtb2 ORDER BY " + orderBy + " ASC;", null);
        } else {
            cursor = db.rawQuery("SELECT * FROM demodtb2 WHERE lokasi='"+ filter +"' ORDER BY " + orderBy + " ASC;", null);
        }
        daftar = new String[cursor.getCount()];
        cursor.moveToFirst();
        this.ipList.clear();
        this.ipId.clear();
        for (int cc = 0; cc < cursor.getCount(); cc++){
            cursor.moveToPosition(cc);
            //daftar[cc] = cursor.getString(0).toString();
            daftar[cc] = cursor.getString(1).toString();
            this.ipList.add(cursor.getString(2).toString());
            this.ipId.add(cursor.getString(0).toString());
        }
        ListView01 = findViewById(R.id.listView1);
        ListView01.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, daftar));
        ListView01.setSelected(true);

        if (sharedPreferences.getString("role", "").equalsIgnoreCase("employe")) {
            ListView01.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3){
                    Intent i = new Intent(getApplicationContext(), LihatDataActivity.class);
                    i.putExtra("nama", daftar[arg2]);
                    startActivity(i);
                }
            });
        } else {
            ListView01.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3){
                    final String selection = daftar[arg2];
                    final CharSequence[] dialogitem = {"Lihat Data", "Edit Data", "Hapus Data"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
                    builder.setTitle("PILIHAN : ");
                    builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (item){
                                case 0 :
                                    Intent i = new Intent(getApplicationContext(), LihatDataActivity.class);
                                    i.putExtra("nama", selection);
                                    startActivity(i);
                                    break;
                                case 1 :
                                    Intent in = new Intent(getApplicationContext(), EditDataActivity.class);
                                    in.putExtra("nama", selection);
                                    startActivity(in);
                                    break;
                                case 2 :
                                    SQLiteDatabase db = dbcenter.getWritableDatabase();
                                    db.execSQL("delete from demodtb2 where nama = '" + selection + "'");
                                    RefreshList();
                                    break;
                            }
                        }
                    });
                    builder.create().show();
                }
            });
            ((ArrayAdapter) ListView01.getAdapter()).notifyDataSetInvalidated();
        }
    }
    public void RadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.lall:
                filter = "all";
                break;
            case R.id.lnilam:
                filter = "Nilam";
                break;
            case R.id.lmirah:
                filter = "Mirah";
                break;
            case R.id.ljamrud:
                filter = "Jamrud";
                break;
            case R.id.lgsn:
                filter = "GSN";
                break;
        }
        RefreshList();
    }
    public void pingExecute(View view) {
        for (int i = 0; i < this.ipList.size(); i++) {
            final String id = this.ipId.get(i);
            Ping.onAddress(ipList.get(i)).setTimeOutMillis(1000).setTimes(5).doPing(new Ping.PingListener() {
                @Override
                public void onResult(PingResult pingResult) {
                    SQLiteDatabase db = dbcenter.getWritableDatabase();
                    ContentValues args = new ContentValues();
                    args.put("status", "Up");
                    String whereClause = "no = " + id;
                    db.update("demodtb2", args, whereClause, null);
                }
                @Override
                public void onFinished(PingStats pingStats) {
                }
                @Override
                public void onError(Exception e) {
                    SQLiteDatabase db = dbcenter.getWritableDatabase();
                    ContentValues args = new ContentValues();
                    args.put("status", "Down");
                    String whereClause = "no = " + id;
                    db.update("demodtb2", args, whereClause, null);
                    notification();
                }
            });
        }
        Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_LONG).show();
    }
    public void addListenerOnSpinnerItemSelection(){
        spinnersortby = (Spinner) findViewById(R.id.spnrsortby);
        spinnersortby.setOnItemSelectedListener(new DataActivity.CustomOnItemSelectedListener());
    }
    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Log.d("SPINNER", "onItemSelected: ");
            if (id == 0) orderBy = "no";
            else if (id == 1) orderBy = "lokasi";
            else orderBy = "status";
            RefreshList();
        }
        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SQLiteDatabase db = dbcenter.getReadableDatabase();
                if(filter.equalsIgnoreCase("all")) {
                    cursor = db.rawQuery("SELECT * FROM demodtb2 WHERE nama like '%"+ query +"%'", null);
                } else {
                    cursor = db.rawQuery("SELECT * FROM demodtb2 WHERE nama like '%" + query + "%' and lokasi=" + "'" + filter + "'", null);
                }
                daftar = new String[cursor.getCount()];
                cursor.moveToFirst();
                ipList.clear();
                ipId.clear();
                for (int cc = 0; cc < cursor.getCount(); cc++){
                    cursor.moveToPosition(cc);
                    //daftar[cc] = cursor.getString(0).toString();
                    daftar[cc] = cursor.getString(1).toString();
                    ipList.add(cursor.getString(2).toString());
                    ipId.add(cursor.getString(0).toString());
                }
                ListView01 = findViewById(R.id.listView1);
                ListView01.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, daftar));
                ListView01.setSelected(true);
                ListView01.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3){
                        final String selection = daftar[arg2];
                        final CharSequence[] dialogitem = {"Lihat Data", "Edit Data", "Hapus Data"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
                        builder.setTitle("PILIHAN : ");
                        builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                switch (item){
                                    case 0 :
                                        Intent i = new Intent(getApplicationContext(), LihatDataActivity.class);
                                        i.putExtra("nama", selection);
                                        startActivity(i);
                                        break;
                                    case 1 :
                                        Intent in = new Intent(getApplicationContext(), EditDataActivity.class);
                                        in.putExtra("nama", selection);
                                        startActivity(in);
                                        break;
                                    case 2 :
                                        SQLiteDatabase db = dbcenter.getWritableDatabase();
                                        db.execSQL("delete from demodtb2 where nama = '" + selection + "'");
                                        RefreshList();
                                        break;
                                }
                            }
                        });
                        builder.create().show();
                    }
                });
                ((ArrayAdapter) ListView01.getAdapter()).notifyDataSetInvalidated();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }
            @Override
            public void onSearchViewClosed() {
            }
        });
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;

        // Inflate the menu; this adds items to the action bar if it is present.
        /*MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
x        MenuItem menuItem = menu.findItem(R.id.action_search);
        mMaterialSV.setMenuItem(menuItem);
        return super.onCreateOptionsMenu(menu);*/
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void notification (){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, PING_CHANNEL)
                .setSmallIcon(R.drawable.ic_message)
                .setContentTitle("Laporan")
                .setContentText("Terdapat CCTV Bermasalah");
        Intent resultIntent = new Intent(this, DataActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DataActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, mBuilder.build());
    }


//    public void autoping(){
//        int interval = 180000;
//        SQLiteDatabase db = dbcenter.getReadableDatabase();
//        cursor = db.rawQuery("SELECT * FROM tabletime ",null);
//        cursor.moveToFirst();
//        if (cursor.getCount()>0)
//        {
//            cursor.moveToPosition(0);
//            interval = cursor.getInt(0);
//        }
//        int menit = interval * 60000;
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                pingExecute(null);
//            }
//        }, menit);//satuan detik (1000 = 1 detik)
//    }


//    Haversine Formula
//    public double Haversine(View view){
//        double UserLat, UserLong, CCTVLat, CCTVLong;
//        UserLat = -7.197741;
//        UserLong = 112.733286;
//        CCTVLat = -7.209817;
//        CCTVLong = 112.722723;
//
//        int R = 6371; // km (Earth radius)
//        double dLat = toRadians(CCTVLat-UserLat);
//        double dLon = toRadians(CCTVLong-UserLong);
//        UserLat = toRadians(UserLat);
//        CCTVLat = toRadians(CCTVLat);
//
//        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(UserLat) * Math.cos(CCTVLat);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        double hasil = R * c;
//        return hasil;
//
//    }
//    public double toRadians(double deg) {
//        return deg * (Math.PI/180);
//    }
}