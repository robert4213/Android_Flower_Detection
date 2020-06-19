package com.test.flowerdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import static com.test.flowerdetection.MainActivity.db;

public class image_list extends AppCompatActivity {
    GridView gridView;
    public static ArrayList<Item> list;
    ImageListAdapter adapter = null;
    SQLiteDatabase DB;
    DataBaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        gridView = (GridView) findViewById(R.id.ImgGridView);
        list = new ArrayList<>();
        adapter = new ImageListAdapter(this, R.layout.image_items, list);
        gridView.setAdapter(adapter);

        //get All data here:
        //Cursor cursor = ShowResult.db.getData("SELECT * FROM DB_TABLE");
        DB = db.getWritableDatabase();
        Cursor cursor = DB.rawQuery("SELECT * FROM user_img", null);

        list.clear();
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("image_name"));
            String time = cursor.getString(cursor.getColumnIndex("time_stamp"));
            byte[] image = cursor.getBlob(cursor.getColumnIndex("image_data"));

            list.add(new Item(name, image, time));
        }
        adapter.notifyDataSetChanged();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(image_list.this, ImgItemDetail.class);
                Item item = list.get(position);

                Bundle bundle = new Bundle();
                bundle.putInt("index", position);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

    }
}
