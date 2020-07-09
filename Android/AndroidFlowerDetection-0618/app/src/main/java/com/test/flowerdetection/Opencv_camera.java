package com.test.flowerdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
import org.opencv.imgproc.Imgproc;

public class Opencv_camera extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;
    int counter = 0;
    int[] baseline = new int[1];


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

        String rect = "{\"object\": [{\"type\": \"lily\", \"box\": {\"top\": 100, \"left\": 250, \"bottom\": 320, \"right\": 500}, \"score\": \"0.97702414\", \"link\": \"https://en.wikipedia.org/wiki/Lilium\"}, {\"type\": \"rose\", \"box\": {\"top\": 50, \"left\": 130, \"bottom\": 280, \"right\": 300}, \"score\": \"0.97702414\", \"link\": \"https://en.wikipedia.org/wiki/Rose\"}]}";

        if(counter % 10 == 0) {

        }

        try {
            drawbox(frame, rect);
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

}