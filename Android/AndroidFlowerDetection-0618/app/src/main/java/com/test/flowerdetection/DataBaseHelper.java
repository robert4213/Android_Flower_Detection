package com.test.flowerdetection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataBaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "user_img.db";

    // Table Names
    private static final String DB_TABLE = "user_img";

    // column names
    private static final String KEY_NAME = "image_name";
    private static final String KEY_IMAGE = "image_data";
    private static final String KEY_TIME = "time_stamp";
    private static final String KEY_LAT = "loc_lat";
    private static final String KEY_LON = "loc_lon";
    private static final String KEY_CITY = "loc_city";

    // Table create statement
    private static final String CREATE_TABLE_IMAGE = "CREATE TABLE " + DB_TABLE + "("+
            KEY_NAME + " TEXT," +
            KEY_TIME + " TEXT," +
            KEY_IMAGE + " BLOB," +
            KEY_LAT + " DOUBLE," +
            KEY_LON + " DOUBLE," +
            KEY_CITY + " TEXT);";

    private static final String CREATE_TABLE_IMAGE_IF_NOT_EXIST = "CREATE TABLE IF NOT EXISTS " + DB_TABLE + "("+
            KEY_NAME + " TEXT," +
            KEY_TIME + " TEXT," +
            KEY_IMAGE + " BLOB," +
            KEY_LAT + " DOUBLE," +
            KEY_LON + " DOUBLE," +
            KEY_CITY + " TEXT);";


    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void queryData(){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(CREATE_TABLE_IMAGE_IF_NOT_EXIST);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating table

        //db.execSQL(CREATE_TABLE_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);

        // create new table
        onCreate(db);
    }

    public void addEntry( String name,  Bitmap bitmap, Double lat, Double lon, String city) throws SQLiteException {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new  ContentValues();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] image = stream.toByteArray();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        cv.put(KEY_NAME,    name);
        cv.put(KEY_IMAGE,   image);
        cv.put(KEY_TIME,dateFormat.format(date));
        cv.put(KEY_LAT, lat);
        cv.put(KEY_LON, lon);
        cv.put(KEY_CITY, city);
        database.insert( DB_TABLE, null, cv );
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

}
