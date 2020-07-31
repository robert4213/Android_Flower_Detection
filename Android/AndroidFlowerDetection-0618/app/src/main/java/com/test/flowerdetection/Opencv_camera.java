package com.test.flowerdetection;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.*;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
    Mat mat;
    Size reshape;
    int frame_height;
    int frame_width;
    boolean isVisible;
    String predictResult;
    Mat bmp_rect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_camera);


        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);

        double width = cameraBridgeViewBase.getWidth();
        double height = cameraBridgeViewBase.getHeight();
        reshape = new Size(width, height);
        System.out.println("Preview Size: " + width + "/" + height);
        predictResult = "Start";
        bmp_rect = null;


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



        Mat mRgba = inputFrame.rgba();
        Mat mRgbaT = new Mat();
        Core.flip(mRgba.t(), mRgbaT, 1);

        if(isVisible = true) {

            if (predictResult == "Start" || predictResult != null) {
                try {

                    if (rect != null && rect.length() > 10) {
                        bmp_rect = drawBitmap(rect);
                    }
                } catch (JSONException E) {
                    E.printStackTrace();
                }

//            //Original save file
                //Cut image to square
                Mat mSquare = mRgbaT.submat(mRgbaT.height()/2 - mRgbaT.width()/2, mRgbaT.height()/2 + mRgbaT.width()/2, 0, mRgbaT.width());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String date = simpleDateFormat.format(new Date());
            String filename = "VIDEO_" + date + ".png";
            Imgproc.cvtColor(mSquare, mSquare, Imgproc.COLOR_BGR2RGB);

                String filePath = "/data/user/0/com.test.flowerdetection/files/fd" + "/" + filename;
                System.out.println("Video file path: " + filePath);


                Imgcodecs.imwrite(filePath, mSquare);
            File f = new File(filePath);
            VideoFrameUploadTask videoFrameUploadTask = new VideoFrameUploadTask(f, mSquare, filePath);
            videoFrameUploadTask.execute();

                //New save file try
//                filePath = "";
//                try {
//                    filePath = createImageFile();
//                    System.out.println("Video Frame file path: " + filePath);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (filePath != "") {
//                    Imgcodecs.imwrite(filePath, mRgbaT);
//                    File f = new File(filePath);
//                    VideoFrameUploadTask videoFrameUploadTask = new VideoFrameUploadTask(f, mRgbaT, filePath);
//                    videoFrameUploadTask.execute();
//                }
            }

//            try {
//                String prev_rect = "";
//                bmp_rect = null;
//
//                if (prev_rect != rect && rect != null && rect.length() > 10) {
//                    System.out.println("Ready to draw rect. " + rect);
//                    bmp_rect = drawBitmap(rect);
//
////                System.out.println("Image size: col: " + mRgba.cols() + ", row: " + mRgba.rows());
////                System.out.println("bmp_rect size: col: " + bmp_rect.cols() + ", row: " + bmp_rect.rows());
//                    Core.addWeighted(mRgba, 1, bmp_rect, 1, 1, mRgba);
//                    prev_rect = rect;
//
//                } else {
//                    if(bmp_rect != null) {
//                        Core.addWeighted(mRgba, 1, bmp_rect, 1, 1, mRgba);
//                    }
//                }
//            } catch (JSONException E) {
//                E.printStackTrace();
//            }
            if(bmp_rect != null) {Core.addWeighted(mRgba, 1, bmp_rect, 1, 1, mRgba);}
            counter = counter + 1;
        } else {
            System.out.println("isVisible: " + isVisible);
        }

        return mRgba;
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        isVisible = true;
        rect = "";
        frame_height = height;
        frame_width = width;

    }


    @Override
    public void onCameraViewStopped() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        predictResult = "Start";
        bmp_rect = null;

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"There's a problem, yo!", Toast.LENGTH_SHORT).show();
        }

        else
        {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
            isVisible = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraBridgeViewBase!=null){

            cameraBridgeViewBase.disableView();
        }
        System.out.println("Current Status: stopped");
        isVisible = false;
        finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraBridgeViewBase!=null){
            cameraBridgeViewBase.disableView();
        }
        System.out.println("Current Status: destroy");
        isVisible = false;
        finish();
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

    private Mat drawBitmap(String rect) throws JSONException {


        Bitmap bmp = Bitmap.createBitmap(frame_width, frame_height, Bitmap.Config.ARGB_8888);

        BitmapDrawable bd = new BitmapDrawable(bmp);
        bd.setAlpha(50);
        Bitmap bitmap = bd.getBitmap();

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        JSONObject response = new JSONObject(rect);
        JSONArray arr = response.getJSONArray("object");
        for(int i = 0; i < arr.length(); i++) {
            JSONObject jsonobject = arr.getJSONObject(i);
            String category = jsonobject.getString("type");
            JSONObject box = jsonobject.getJSONObject("box");
            int top0 = box.getInt("top") + frame_width/2 - frame_height/2;
            int left0 = box.getInt("left");
            int bottom0 = box.getInt("bottom") + frame_width/2 - frame_height/2;
            int right0 = box.getInt("right");

            int top = frame_height - right0;
            int left = top0;
            int bottom = frame_height - left0;
            int right = bottom0;
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            canvas.drawRect(left, top, right, bottom, paint);
            canvas.save();
            canvas.rotate(270, left, frame_height - left0);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(25);
            canvas.drawText(category, left, frame_height - left0, paint);
            canvas.restore();
        }

        Mat mat = new Mat();

        Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, mat);
        return mat;
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
        private String filepath;

        public VideoFrameUploadTask(File f, Mat frame, String filepath) {
            this.f = f;
            this.frame = frame;
            this.filepath = filepath;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... strings) {
            predictResult = null;
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
                    System.out.println("predicted results: " + predictResult);
                    rect = predictResult;
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
            //rect = s;
            //System.out.println("Video box: " + rect);

            if (f.exists()) {
                String deleteCmd = "rm -r " + filepath;
                System.out.println("Delete cmd: " + deleteCmd);
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec(deleteCmd);
                } catch (IOException e) {}
            }

        }

    }

    public static Mat rotate(Mat src, double angle)
    {
        Mat dst = new Mat();
        if(angle == 180 || angle == -180) {
            Core.flip(src, dst, -1);
        } else if(angle == 90 || angle == -270) {
            Core.flip(src.t(), dst, 1);
        } else if(angle == 270 || angle == -90) {
            Core.flip(src.t(), dst, 0);
        }

        return dst;
    }

    private String createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        String mFileName = "VIDEO_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".png", storageDir);
        return mFile.getPath();
    }



}