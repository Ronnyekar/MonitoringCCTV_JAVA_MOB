package com.example.testdashboard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback{

//    SupportMapFragment mapFragment;
//    private static final String TAG = "MapsActivity";
//    private GoogleMap maps;

    double lat;
    double lng;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Maps Is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map Is Ready");
        nMap = googleMap;
        if (mLocationPermissionGranted){
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }
        }
        SQLiteDatabase db = dbcenter.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM demodtb2", null);
        cursor.moveToFirst();
        for (int cc = 0; cc < cursor.getCount(); cc++){
            double lat = cursor.getDouble(5);
            double lng = cursor.getDouble(6);
            String name = cursor.getString(1);
            String ip = cursor.getString(3);
            LatLng pss = new LatLng(lat, lng);
            nMap.addMarker(new MarkerOptions().position(pss).title(name + " " + ip));
            cursor.moveToNext();
        }
//        LatLng tanjungperak = new LatLng(lat, lng);
//        nMap.addMarker(new MarkerOptions().position(tanjungperak).title("Tanjung Perak"));
//        nMap.moveCamera(CameraUpdateFactory.newLatLng(tanjungperak));
//        nMap.setMyLocationEnabled(true);
        nMap.getUiSettings().setMyLocationButtonEnabled(false);
    }
    public static void AddLocation(){

    }
    private static final String TAG = "MapsActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    private ImageView mgps;

    private Boolean mLocationPermissionGranted = false;
    private GoogleMap nMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    DataHelper dbcenter;
    Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mgps = (ImageView) findViewById(R.id.ic_gps);
        dbcenter = new DataHelper(this);

        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        getLocationPermission();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, AddDataActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: get Device Location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MapsActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityExeption : " + e.getMessage());
        }
    }
    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to : lat : " + latLng.latitude + ", lng : " + latLng.longitude);
        nMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
    private void initMap(){
        Log.d(TAG, "initMap: Initializing Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        mapFragment.getMapAsync(MapsActivity.this);

        mgps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: gps icon clicked");
                getDeviceLocation();
            }
        });
    }
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: Get Location Permission");
        String[] permission = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this,permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this,permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for (int i = 0; i < grantResults.length; i++)
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: Permission Denied");
                            return;
                        }
                    Log.d(TAG, "onRequestPermissionsResult: Permission Granted");
                    mLocationPermissionGranted = true;
                        initMap();
                    nMap.setMyLocationEnabled(true);
                }
            }
        }
    }
}

//        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
//        mapFragment.getMapAsync(this);
//
//    }
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        maps = googleMap;
//        LatLng tanjungperak = new LatLng(-7.198374, 112.732828);
//        maps.addMarker(new MarkerOptions().position(tanjungperak).title("Tanjung Perak"));
//        maps.moveCamera(CameraUpdateFactory.newLatLng(tanjungperak));
//    }
//    private void getDeviceLocation(){
//        Log.d(TAG, "getDeviceLocation: get current location");
//
//    }