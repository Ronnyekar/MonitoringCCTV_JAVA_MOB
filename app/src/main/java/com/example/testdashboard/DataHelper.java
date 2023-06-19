package com.example.testdashboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "projectcctv.db";
    private static final int DATABASE_VERSION = 1;
    DataHelper dbcenter;

    public DataHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String sql1 = "CREATE TABLE demodtb2 (no integer primary key autoincrement, nama text null, ip text null, lokasi text null, status text null, latitude text null, longitude text null );";
        String sql2 = "CREATE TABLE tabletime (waktu integer primary key);";
        Log.d("Data","onCreate: " + sql1);
        Log.d("Data","onCreate: " + sql2);
        db.execSQL(sql1);
        db.execSQL(sql2);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2){
        db.execSQL("DROP TABLE IF EXISTS demodtb2");
        db.execSQL("DROP TABLE IF EXISTS tabletime");
        onCreate(db);
    }
}