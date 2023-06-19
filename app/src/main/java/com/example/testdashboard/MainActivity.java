package com.example.testdashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    DataHelper dbHelper;
    ImageView btnexport;
    CardView importContainer;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbHelper.onCreate();
        setContentView(R.layout.activity_main);
        dbHelper = new DataHelper(this);
        btnexport = findViewById(R.id.btnexport);
        importContainer = findViewById(R.id.importContainer);

        sharedPreferences = getSharedPreferences("app", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("role", "").equalsIgnoreCase("employe")) {
            importContainer.setVisibility(View.INVISIBLE);
        }

        Preferences preferences = Preferences.getInstance(getApplicationContext());
        if (preferences.isPingNotificationEnable()) {
            PingThread pingThread = PingThread.getInstance(getApplicationContext());
            pingThread.startThread();
        }
    }
    public void DataClicked(View view) {
        Intent dataintent = new Intent(MainActivity.this, DataActivity.class);
        startActivity(dataintent);
    }
    public void SettingClicked(View view){
        Intent settingintent = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(settingintent);
    }
    public void mapsclicked(View view){
        Intent mapsintent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(mapsintent);
    }

//    public void importdata (View view){
//        try {
//            File sd = Environment.getExternalStorageDirectory();
//            File data  = Environment.getDataDirectory();
//
//            if (sd.canWrite()) {
//                String  currentDBPath= "//data//" + "PackageName"
//                        + "//databases//" + "DatabaseName";
//                String backupDBPath  = "/BackupFolder/DatabaseName";
//                File  backupDB= new File(data, currentDBPath);
//                File currentDB  = new File(sd, backupDBPath);
//
//                FileChannel src = new FileInputStream(currentDB).getChannel();
//                FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                dst.transferFrom(src, 0, src.size());
//                src.close();
//                dst.close();
//                Toast.makeText(getBaseContext(), backupDB.toString(),
//                        Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG)
//                    .show();
//        }
//    }

//    public static boolean exportDB(Context context) {
//        String DATABASE_NAME ="Skipsi1.db";
//        String databasePath = context.getDatabasePath(DATABASE_NAME).getPath();
//        String inFileName = databasePath;
//        try {
//            File dbFile = new File(inFileName);
//            FileInputStream fis = new FileInputStream(dbFile);
//
//            String outFileName = Environment.getExternalStorageDirectory() + "/" + DATABASE_NAME;
//
//            OutputStream output = new FileOutputStream(outFileName);
//
//            byte[] buffer = new byte[1024];
//            int length;
//            while ((length = fis.read(buffer)) > 0) {
//                output.write(buffer, 0, length);
//            }
//            //Close the streams
//            output.flush();
//            output.close();
//            fis.close();
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
    public void export(View view) {
        //generate data
        StringBuilder data = new StringBuilder();
        data.append("Time,Distance");
        for (int i = 0; i < 5; i++) {
            data.append("\n" + String.valueOf(i) + "," + String.valueOf(i * i));
        }
        try {
            //saving the file into device
            FileOutputStream out = openFileOutput("data.csv", Context.MODE_PRIVATE);
            out.write((data.toString()).getBytes());
            out.close();
            //exporting
            Context context = getApplicationContext();
            File filelocation = new File(getFilesDir(), "data.csv");
            Uri path = FileProvider.getUriForFile(context, "com.example.testdashboard.fileprovider", filelocation);
            Intent fileIntent = new Intent(Intent.ACTION_SEND);
            fileIntent.setType("text/csv");
            fileIntent.putExtra(Intent.EXTRA_SUBJECT, "Data");
            fileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            fileIntent.putExtra(Intent.EXTRA_STREAM, path);
            startActivity(Intent.createChooser(fileIntent, "Send mail"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
