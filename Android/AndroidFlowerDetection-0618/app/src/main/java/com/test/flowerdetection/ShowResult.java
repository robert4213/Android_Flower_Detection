package com.test.flowerdetection;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.test.flowerdetection.MainActivity.db;


public class ShowResult extends AppCompatActivity {
    private ImageView imageView;
    private TextView show_category;
    private TextView wikiText;
    private Uri picUri;
    private String category;
    private WebView webView;
    private int[] rect = new int[4];
    private RequestQueue requestQueue;
    private String box;
    private double latitude;
    private double longitude;
    private String city;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    Location currentLocation;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Bundle bundle = getIntent().getExtras();
        picUri = (Uri) bundle.get("IMAGE_URI");
        box = (String) bundle.get("Box");

        System.out.println("box2 " + box);

        bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView = (ImageView) findViewById(R.id.res_img);
        requestQueue = Volley.newRequestQueue(this);
        show_category = (TextView) findViewById(R.id.category);

        webView = (WebView) findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient());

        imageView.setImageBitmap(bitmap);


        try {
            show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //jasonParse();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            return;
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getImgLocaiton();
        System.out.println("Location after upload: " + latitude + longitude);
        System.out.println("City: " + city);



    }


    private String getCity(double lat, double lon) {
        String city = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try{
            addresses = geocoder.getFromLocation(lat,lon, 10);
            if(addresses.size() > 0) {
                for(Address adr: addresses) {
                    if(adr.getLocality() != null && adr.getLocality().length() > 0) {
                        city =adr.getLocality();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return city;
    }

    private Bitmap drawRectangles(Bitmap bitmap, int[] rects) {
        int left, top, right, bottom;
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        top = rects[0];
        left = rects[1];
        bottom = rects[2];
        right = rects[3];
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawRect(left, top, right, bottom, paint);
        return mutableBitmap;
    }

//    public void jasonParse() {
//        String url = "http://192.168.1.144:5000/data.json";
//       JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    String x = response.toString();
//                    System.out.println("Response: " + x);
//                    response = new JSONObject(
//                            x
//                    );
//                    category = response.getString("type");
//                    //
//                   JSONObject box = response.getJSONObject("box");
//                    rect[0] = box.getInt("top");
//                    rect[1] = box.getInt("left");
//                    rect[2] = box.getInt("bottom");
//                    rect[3] = box.getInt("right");
//                    show_category.setText(category);
//                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//                    Bitmap drawableBitmap = drawable.getBitmap();
//                    imageView.setImageBitmap(drawRectangles(drawableBitmap, rect));
//                    webView.loadUrl("https://en.wikipedia.org/wiki/" + category);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//
//        requestQueue.add(request);
//    }

    public void show() throws JSONException {
        JSONObject response = new JSONObject(box);
        category = response.getString("type");

        JSONObject box = response.getJSONObject("box");
        rect[0] = box.getInt("top");
        rect[1] = box.getInt("left");
        rect[2] = box.getInt("bottom");
        rect[3] = box.getInt("right");
        show_category.setText(category);
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap drawableBitmap = drawable.getBitmap();
        imageView.setImageBitmap(drawRectangles(drawableBitmap, rect));
        webView.loadUrl("https://en.wikipedia.org/wiki/" + category);
        //db.addEntry(category, drawableBitmap, latitude, longitude, city);
    }

    private void getImgLocaiton() {
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
                    city = getCity(latitude,longitude);
                    System.out.println("Location upload: " + latitude + " , " + longitude + " " + city);
                    db.addEntry(category, bitmap, latitude, longitude, city);
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
                    System.out.println("Permission Granted Location upload: " + latitude + " , " + longitude);
                } else {
                    Toast.makeText(this, "Location Permission is not granted!", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        getImgLocaiton();
                    }
                } else {
                    Toast.makeText(this, "Location Permission is not granted!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            }
        }
    }

}