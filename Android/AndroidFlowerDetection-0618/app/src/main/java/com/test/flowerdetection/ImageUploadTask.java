package com.test.flowerdetection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static androidx.core.content.ContextCompat.startActivity;

public class ImageUploadTask extends AsyncTask<String, Integer, String> {

    //private static final String BASE_URL = "http://10.0.2.2:5000/upload";

    //private static final String BASE_URL = getString(R.string.posturl) + "/upload";
    private static final String IMGUR_CLIENT_ID = "123";
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS).build();

    private File f;
    private Context mContext;
    Uri bitmap;
    private String box;
    private String filepath;

    // Constructor
    public ImageUploadTask(Context context, File f, Uri bitmap, String filepath) {
        this.f = f;
        this.mContext = context;
        this.bitmap = bitmap;
        this.filepath = filepath;
    }


    //执行上传图片线程，并返回线程执行的结果
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String...params) {
        String predictResult = null;
        String BASE_URL = mContext.getString(R.string.posturl) + "/upload";
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("title", "Square Logo")
                    .addFormDataPart("file", UUID.randomUUID().toString() + ".png",
                            RequestBody.create(MEDIA_TYPE_PNG, f))
                    .build();
            System.out.println("Baseurl: " + BASE_URL);
            System.out.println("Request Body is " + requestBody.toString());
            //设置为自己的ip地址 BASE_URL
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .header("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                    .url(BASE_URL)
                    .post(requestBody)
                    .build();
            try (okhttp3.Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                System.out.println("This is the response");
                predictResult = response.body().string();
                System.out.println(predictResult);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return predictResult;
    }


    // 在这里需要把ShowResult方法写进来
    @Override
    protected void onPostExecute(String s) {
        // 执行上传线程后，会得到预测结果，以String的形式返回，在onPostExecute里面要写showResult UI
        //Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, ShowResult.class);
        intent.putExtra("IMAGE_URI",bitmap);
        intent.putExtra("Box", s);
        System.out.println("Box: " + s);
        String[] file_elements = filepath.split("/");
        String file_key = file_elements[file_elements.length - 1];
        intent.putExtra("FILE_KEY", file_key);
        mContext.startActivity(intent);
        ((Activity)(mContext)).finish();
    }


}