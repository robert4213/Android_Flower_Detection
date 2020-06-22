package com.test.flowerdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import static com.test.flowerdetection.image_list.list;

public class ImgItemDetail extends AppCompatActivity {
    ImageView imageView;
    TextView showtime;
    TextView showloc;
    TextView showlabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_item_detail);

        Bundle bundle = getIntent().getExtras();
        int position = bundle.getInt("index");
        Item item = list.get(position);
        String time = item.getDate();
        String label = item.getName();
        String city = item.getCity();
        byte[] img = item.getImage();

        imageView = (ImageView) findViewById(R.id.img_detail);
        showtime = (TextView) findViewById(R.id.img_date);
        showlabel = (TextView) findViewById(R.id.img_label);
        showloc = (TextView) findViewById(R.id.img_loc);

        Bitmap bmp = BitmapFactory.decodeByteArray(img, 0, img.length);
        imageView.setImageBitmap(bmp);
        showtime.setText(time);
        showloc.setText(city);
        showlabel.setText(label);
    }
}
