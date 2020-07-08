package com.test.flowerdetection;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCVActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    JavaCameraView javaCameraView;
    Mat mRGBA,mRGBAT;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(OpenCVActivity.this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status) {
                case BaseLoaderCallback.SUCCESS: {
                    javaCameraView.enableView();
                    break;
                }
                default:
                    super.onManagerConnected(status);
                    break;
            }

        }
    };
    // Test open cv
    private static String TAG1 = "MainActivity";

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG1, "OPen cv is configured or coneected Successfully");
        } else {
            Log.d(TAG1, "OPenCV not wordking or loaded");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_c_v);
        javaCameraView = (JavaCameraView) findViewById(R.id.my_camera);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
       // javaCameraView.setCvCameraViewListener(this);
        javaCameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener() {
            @Override
            public void onCameraViewStarted(int width, int height) {
                mRGBA = new Mat(height,width, CvType.CV_8UC4);
            }

            @Override
            public void onCameraViewStopped() {

            }

            @Override
            public Mat onCameraFrame(Mat inputFrame) {
                return null;
            }


            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                mRGBA = inputFrame.rgba();
                mRGBAT = mRGBA.t();
                Core.flip(mRGBA.t(),mRGBAT,1);
                Imgproc.resize(mRGBAT,mRGBAT,mRGBA.size());
                return mRGBAT;
            }
        });


    }

    @Override
    public void onCameraViewStarted(int width, int height) {
              mRGBA = new Mat(height,width, CvType.CV_8UC4);
    }

    @Override
    public void onCameraViewStopped() {
        mRGBA.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRGBA = inputFrame.rgba();
        mRGBAT = mRGBA.t();
        Core.flip(mRGBA.t(),mRGBAT,1);
        Imgproc.resize(mRGBAT,mRGBAT,mRGBA.size());
        return mRGBAT;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(javaCameraView != null) {
            javaCameraView.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG1, "OPen cv is configured or coneected Successfully");
            baseLoaderCallback.onManagerConnected(BaseLoaderCallback.SUCCESS);
        } else {
            Log.d(TAG1, "OPenCV not wordking or loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0,this,baseLoaderCallback);
        }
    }
}