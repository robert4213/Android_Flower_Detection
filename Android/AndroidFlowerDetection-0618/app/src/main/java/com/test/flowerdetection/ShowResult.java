package com.test.flowerdetection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Bundle bundle  = getIntent().getExtras();
        picUri = (Uri) bundle.get("IMAGE_URI");
        box = (String) bundle.get("Box");

        System.out.println("box2 " + box );

        Bitmap bitmap = null;
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
        //jasonParse();
        try {
            test();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void jasonParse() {
        String url = "http://192.168.1.144:5000/data.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String x = response.toString();
                    System.out.println("Response: " + x);
                    response = new JSONObject(
                            x
                    );
                    category = response.getString("type");
                    //
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
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        requestQueue.add(request);
    }

    public void test() throws JSONException {
 //       JSONObject response = new JSONObject(
 //               "{\"type\": \"rose\", \"box\": {\"top\": 55,\"left\": 175, \"bottom\": 438, \"right\": 497}, \"score\": \"0.97702414\"}"
 //       );
        JSONObject response = new JSONObject(box);
        category = response.getString("type");
        //
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
        db.addEntry(category, drawableBitmap);
    }

}