package com.test.flowerdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import static com.test.flowerdetection.MainActivity.db;

public class Map extends FragmentActivity implements OnMapReadyCallback {
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;

    GoogleMap map;
    private double latitude;
    private double longitude;

    SQLiteDatabase DB;
    List<Item> Markerlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        DB = db.getWritableDatabase();


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();
        Markerlist = getAllMarker(DB);
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();
                    System.out.println("Permission Granted Location: " + latitude + " , " + longitude);
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(Map.this);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    System.out.println("Permission Granted Location: " + latitude + " , " + longitude);
                } else {
                    Toast.makeText(this, "Location Permission is not granted!", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fetchLastLocation();
                        //map.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Location Permission is not granted!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng cur = new LatLng(latitude, longitude);
        map.addMarker(new MarkerOptions().position(cur));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cur, 15.0f));
        for(Item item:Markerlist) {
            Bitmap bmp = BitmapFactory.decodeByteArray(item.getImage(), 0, item.getImage().length);
            Bitmap b = Bitmap.createScaledBitmap(bmp, 120, 120, false);
            map.addMarker(new MarkerOptions().position(new LatLng(item.getLatitude(), item.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(b))
                    .title(item.getName())
            );
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }


    }

    public List<Item> getAllMarker(SQLiteDatabase DB) {
        List<Item> Markerlist = new ArrayList<>();
        Cursor cursor = DB.rawQuery("SELECT * FROM user_img", null);
        ArrayList<LatLng> markerCoordinates = new ArrayList<>();
        float COORDINATE_OFFSET = 0.000085f;
        int offsetType = 0;

        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("image_name"));
            String time = cursor.getString(cursor.getColumnIndex("time_stamp"));
            byte[] image = cursor.getBlob(cursor.getColumnIndex("image_data"));
            double lat = cursor.getDouble(cursor.getColumnIndex("loc_lat"));
            double lon = cursor.getDouble(cursor.getColumnIndex("loc_lon"));
            String city = cursor.getString(cursor.getColumnIndex("loc_city"));
            LatLng old_cor = new LatLng(lat, lon);
            LatLng new_cor = getLatLng(old_cor, COORDINATE_OFFSET, offsetType, markerCoordinates);
            markerCoordinates.add(new_cor);
            Markerlist.add(new Item(name, image, time, new_cor.latitude, new_cor.longitude, city));
        }
        cursor.close();
        db.close();
        return Markerlist;
    }


    private LatLng getLatLng(LatLng latLng, float COORDINATE_OFFSET, int offsetType, ArrayList<LatLng> markerCoordinates ) {
        LatLng updatedLatLng = latLng;

        while (markerCoordinates.contains(updatedLatLng)) {
            double latitude = 0;
            double longitude = 0;
            if (offsetType == 0) {
                latitude = latLng.latitude + COORDINATE_OFFSET;
                longitude = latLng.longitude;
            } else if (offsetType == 1) {
                latitude = latLng.latitude;
                longitude = latLng.longitude + COORDINATE_OFFSET;
            } else if (offsetType == 2) {
                latitude = latLng.latitude - COORDINATE_OFFSET;
                longitude = latLng.longitude;
            } else if (offsetType == 3) {
                latitude = latLng.latitude;
                longitude = latLng.longitude - COORDINATE_OFFSET;
            } else if (offsetType == 4) {
                latitude = latLng.latitude + COORDINATE_OFFSET;
                longitude = latLng.longitude - COORDINATE_OFFSET;
            } else if (offsetType == 5) {
                latitude = latLng.latitude + COORDINATE_OFFSET;
                longitude = latLng.longitude + COORDINATE_OFFSET;
            } else if (offsetType == 6) {
                latitude = latLng.latitude - COORDINATE_OFFSET;
                longitude = latLng.longitude + COORDINATE_OFFSET;
            } else if (offsetType == 7) {
                latitude = latLng.latitude - COORDINATE_OFFSET;
                longitude = latLng.longitude - COORDINATE_OFFSET;
            } else {
                return latLng;
            }
            offsetType += 1;
            updatedLatLng = new LatLng(latitude, longitude);
        }
        return updatedLatLng;
    }
}