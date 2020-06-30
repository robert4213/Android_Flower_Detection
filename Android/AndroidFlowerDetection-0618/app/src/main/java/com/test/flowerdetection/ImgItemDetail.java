package com.test.flowerdetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.test.flowerdetection.MainActivity.db;
import static com.test.flowerdetection.MainActivity.list;
import static com.test.flowerdetection.MainActivity.user_name;


public class ImgItemDetail extends AppCompatActivity {
    ImageView imageView;
    TextView showtime;
    TextView showloc;
    TextView showlabel;

    ArrayList<byte[]> SubImagesList;
    ArrayList<String> Categories;
    TypeGridAdapter gridAdapter;
    GridView gridView;
    int[] rect = new int[4];
    private Object Menu;
    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_item_detail);



        gridView = (GridView) findViewById(R.id.img_type_grid);

        Bundle bundle = getIntent().getExtras();
        int position = bundle.getInt("index");
        Item item = list.get(position);
        String time = item.getDate();
        String label = item.getName();
        String city = item.getCity();
        byte[] img = item.getImage();
        key = item.getKey();

        String boxes = item.getBoxes();
        String type_list = item.getType_list();

        imageView = (ImageView) findViewById(R.id.img_detail);
        showtime = (TextView) findViewById(R.id.img_date);
        showlabel = (TextView) findViewById(R.id.img_label);
        showloc = (TextView) findViewById(R.id.img_loc);

        Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
        imageView.setImageBitmap(bmp);
        showtime.setText(time);
        showloc.setText(city);
        showlabel.setText(label);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_img);

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Change to MainActivity!");
                Intent intent = new Intent(ImgItemDetail.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });





        SubImagesList = new ArrayList<>();
        Categories = new ArrayList<>();

        String[] types = type_list.split(",");

        for(String i:types) {
            Categories.add(i);
        }

        String[] box = boxes.split("\\|");
        for(String b: box) {
            String[] point = b.split(",");
            for(int i = 0; i <= 3; i++) {
                rect[i] = Integer.parseInt(point[i]);

            }
            Bitmap bitmap = cutbitmap(rect, bmp);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] image = stream.toByteArray();
            SubImagesList.add(image);
        }

        gridAdapter = new TypeGridAdapter(this, R.layout.show_result_grid, SubImagesList, Categories);
        gridView.setAdapter(gridAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_icon, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_img) {
            db.delete(user_name, key);
            Toast.makeText(ImgItemDetail.this, "Delete Images.", Toast.LENGTH_SHORT).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
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
}
