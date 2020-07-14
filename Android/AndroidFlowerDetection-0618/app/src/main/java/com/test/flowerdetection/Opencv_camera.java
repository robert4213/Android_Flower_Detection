package com.test.flowerdetection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.view.SurfaceView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.*;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Opencv_camera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    int counter = 0;
    int[] baseline = new int[1];
    String rect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_camera);


        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);


        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch(status){

                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }


            }

        };




    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {


        Mat frame = inputFrame.rgba();

        //String rect = "{\"object\": [{\"type\": \"lily\", \"box\": {\"top\": 100, \"left\": 250, \"bottom\": 320, \"right\": 500}, \"score\": \"0.97702414\", \"link\": \"https://en.wikipedia.org/wiki/Lilium\"}, {\"type\": \"rose\", \"box\": {\"top\": 50, \"left\": 130, \"bottom\": 280, \"right\": 300}, \"score\": \"0.97702414\", \"link\": \"https://en.wikipedia.org/wiki/Rose\"}]}";

        if(counter % 5 == 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String date = simpleDateFormat.format(new Date());
            String filename = date + ".png";
            //String folder = Environment.getExternalStorageDirectory().getPath() + "/video";

            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    Config.IMAGE_DIRECTORY_NAME);
            String folder = mediaStorageDir.getPath();
            String filePath = "/data/user/0/com.test.flowerdetection/files/fd" + "/" + filename;
            System.out.println("Video file path: " + filePath);
            Imgcodecs.imwrite(filePath, frame);
            File f = new File(filePath);
            VideoFrameUploadTask videoFrameUploadTask = new VideoFrameUploadTask(f, frame);
            videoFrameUploadTask.execute();
        }

        try {
            if(rect != null) {
                drawbox(frame, rect);
            }
        } catch (JSONException E) {
            E.printStackTrace();
        }
        counter = counter + 1;
        return frame;
    }


    @Override
    public void onCameraViewStarted(int width, int height) {

    }


    @Override
    public void onCameraViewStopped() {

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
    }

    public void drawbox(Mat frame, String rect) throws JSONException {

        JSONObject response = new JSONObject(rect);
        JSONArray arr = response.getJSONArray("object");
        for(int i = 0; i < arr.length(); i++) {
            JSONObject jsonobject = arr.getJSONObject(i);
            String category = jsonobject.getString("type");
            JSONObject box = jsonobject.getJSONObject("box");
            int y1 = box.getInt("top");
            int x1 = box.getInt("left");
            int y2 = box.getInt("bottom");
            int x2 = box.getInt("right");

            Imgproc.rectangle (
                    frame,                    //Matrix obj of the image
                    new Point(x1, y1),        //p1
                    new Point(x2, y2),       //p2
                    new Scalar(255, 0, 0),           //Scalar object for color
                    2                          //Thickness of the line
            );

            Size size = Imgproc.getTextSize(category, Core.FONT_HERSHEY_PLAIN, 1.2, 2, baseline);

            Imgproc.rectangle (
                    frame,                    //Matrix obj of the image
                    new Point(x1 + size.width, y1 - size.height - 10),        //p1
                    new Point(x1, y1),       //p2
                    new Scalar(255, 0, 0),           //Scalar object for color
                    -1                          //Thickness of the line
            );

            Imgproc.putText(frame, category,
                    new Point(x1, y1 - 5),
                    Core.FONT_HERSHEY_PLAIN, 1.2, new Scalar(255,255,255), 2, 4, false);
        }
    }

    private class VideoFrameUploadTask extends AsyncTask<String, Integer, String> {

        String IMGUR_CLIENT_ID = "123";
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();

        private File f;
        private Mat frame;

        public VideoFrameUploadTask(File f, Mat frame) {
            this.f = f;
            this.frame = frame;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            String predictResult = null;
            String BASE_URL = Opencv_camera.this.getString(R.string.posturl) + "/upload";
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

        @Override
        protected void onPostExecute(String s) {
            rect = s;
        }

    }

}