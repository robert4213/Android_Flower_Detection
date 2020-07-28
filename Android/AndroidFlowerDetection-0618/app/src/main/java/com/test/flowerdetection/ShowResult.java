package com.test.flowerdetection;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.provider.Telephony;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.test.flowerdetection.MainActivity.db;
import static com.test.flowerdetection.MainActivity.user_name;


public class ShowResult extends AppCompatActivity {
    private ImageView imageView;
    private TextView show_category;
    private TextView wikiText;
    private Uri picUri;
    private String category;
    private String all_category;
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
    public static String link;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String file_key;
    private String file_path;
    ViewPagerAdapter adapter;
    ArrayList<byte[]> SubImagesList;
    ArrayList<String> Categories;
    TypeGridAdapter gridAdapter;
    GridView gridView;
    private String join_boxes;
    private String join_categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.app_name));
        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        Bundle bundle = getIntent().getExtras();
        picUri = (Uri) bundle.get("IMAGE_URI");
        box = (String) bundle.get("Box");
        file_path = (String) bundle.get("FILE_PATH");
        file_key = (String) bundle.get("FILE_KEY");

        System.out.println("file key " + file_key);

        System.out.println("box2 " + box);

//        bitmap = null;
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
 //       } catch (IOException e) {
 //           e.printStackTrace();
 //       }
        imageView = (ImageView) findViewById(R.id.res_img);
        show_category = (TextView) findViewById(R.id.category);

        gridView = (GridView) findViewById(R.id.res_grid);

        File imgFile = new File(file_path);
        Bitmap orig_bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        bitmap = rotateImage(orig_bitmap, imgFile.getAbsolutePath());
        imageView.setImageBitmap(bitmap);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Change to MainActivity!");
                Intent intent = new Intent(ShowResult.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        SubImagesList = new ArrayList<>();
        Categories = new ArrayList<>();

        all_category = "";
        join_boxes = "";
        join_categories = "";

        //System.out.println("Test box: " + box.length());

        if(box == null || box.length() < 10) {

            System.out.println("Dialog pop up");
            AlertDialog.Builder builder = new AlertDialog.Builder(ShowResult.this);
            builder.setTitle("Note:")
                    .setMessage("No Follower Detected. Please Try Another Image.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ShowResult.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
            // Create the Alert dialog
            AlertDialog alertDialog = builder.create();

            // Show the Alert Dialog box
            alertDialog.show();

        } else {

            try {
                test();
                show_category.setText(all_category);

                viewPager.setAdapter(adapter);
                tabLayout.setupWithViewPager(viewPager);

                gridAdapter = new TypeGridAdapter(this, R.layout.show_result_grid, SubImagesList, Categories);
                gridView.setAdapter(gridAdapter);

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                getImgLocaiton();
                System.out.println("Location after upload: " + latitude + longitude);
                System.out.println("City: " + city);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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


    public void show() throws JSONException {
        JSONObject response = new JSONObject(box);
        category = response.getString("type");

        JSONObject box = response.getJSONObject("box");
        rect[0] = box.getInt("top");
        rect[1] = box.getInt("left");
        rect[2] = box.getInt("bottom");
        rect[3] = box.getInt("right");
        show_category.setText(category);
        link = response.getString("link");
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap drawableBitmap = drawable.getBitmap();
        imageView.setImageBitmap(drawRectangles(drawableBitmap, rect));
        System.out.println("Wiki Link" + link);
        webView.loadUrl(link);
    }

    public void test() throws JSONException {
        JSONObject response = new JSONObject(box);
        JSONArray arr = response.getJSONArray("object");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonobject = arr.getJSONObject(i);
            category = jsonobject.getString("type");
            if(join_categories == "") {
                join_categories = category;
            } else {
                join_categories += "," + category;
            }

            JSONObject box = jsonobject.getJSONObject("box");
            rect[0] = box.getInt("top");
            rect[1] = box.getInt("left");
            rect[2] = box.getInt("bottom");
            rect[3] = box.getInt("right");
            link = jsonobject.getString("link");
            String rect_string = rect[0] + "," + rect[1] + "," + rect[2] + "," + rect[3];
            if(join_boxes == "") {
                join_boxes = rect_string;
            } else {
                join_boxes += "|" + rect_string;
            }

            if(!Categories.contains(category)) {
                adapter.addFragment(WebFragment.newInstance(link), category);
                if(all_category == "") {
                    all_category = category;} else {
                    all_category += ", " + category;
                }
            }

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap drawableBitmap = drawable.getBitmap();
            imageView.setImageBitmap(drawRectangles(drawableBitmap, rect));

            Bitmap b = cutbitmap(rect, bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] image = stream.toByteArray();
            SubImagesList.add(image);
            Categories.add(category);
        }
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
                if (location != null && !checkfilekey(file_key)) {
                    currentLocation = location;
                    latitude = currentLocation.getLatitude();
                    longitude = currentLocation.getLongitude();
                    city = getCity(latitude,longitude);
                    Bitmap rectBitmap = rectbitmap(bitmap);
                    Bitmap bmp = Bitmap.createScaledBitmap(rectBitmap, 128, 128, false);
                    System.out.println("Location upload: " + latitude + " , " + longitude + " " + city);
                    db.addEntry(user_name, all_category, file_path, file_key, join_boxes, join_categories, bmp, latitude, longitude, city);
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
                        System.out.println("101 Permission Granted Location upload!");
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

    private Bitmap cutbitmap(int[] rect, Bitmap bitmap) {
        Bitmap origialBitmap = bitmap;
        Bitmap cutBitmap = Bitmap.createBitmap(60,
                60, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cutBitmap);
        Rect desRect = new Rect(0, 0, 60, 60);
        Rect srcRect = new Rect(rect[1], rect[0], rect[3],
                rect[2]);
        canvas.drawBitmap(origialBitmap, srcRect, desRect, null);
        return cutBitmap;
    }

    private Bitmap rectbitmap(Bitmap bitmap) {
        Bitmap origialBitmap = bitmap;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap cutBitmap = null;

        if(width < height) {
            cutBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(cutBitmap);
            Rect desRect = new Rect(0, 0, width, width);
            Rect srcRect = new Rect(0, height/2 - width/2, width, height/2 + width/2);
            canvas.drawBitmap(origialBitmap, srcRect, desRect, null );
        } else {
            cutBitmap = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(cutBitmap);
            Rect desRect = new Rect(0, 0, height, height);
            Rect srcRect = new Rect(width/2 - height/2, 0, height, height/2 + width/2);
            canvas.drawBitmap(origialBitmap, srcRect, desRect, null );
        }
        return cutBitmap;
    }

    public boolean checkfilekey(String file_key) {
        SQLiteDatabase DB = db.getWritableDatabase();
        String sql = "SELECT * FROM user_img WHERE user_name='" + user_name + "' " + "AND file_key='" + file_key + "'";
        Cursor cursor = DB.rawQuery(sql, null);
        return cursor.getCount() == 0 ? false : true;
    }

    private Bitmap rotateImage(Bitmap bitmap, String ImageLocation) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(ImageLocation);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        System.out.println("Orientation: " + orientation);
        Matrix matrix = new Matrix();
        Bitmap rotatedBitmap = null;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                System.out.println("Orientation is 90");
                matrix.postRotate(90);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                break;
            default:
                rotatedBitmap = bitmap;
        }

        return rotatedBitmap;
    }

}