package com.bfr.sdkv2_vision;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bfr.basicbuddyapplication.R;
import com.bfr.buddy.utils.Common;
import com.bfr.buddy.utils.Configs;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddy.utils.values.FloatingWidgetVisibility;
import com.bfr.buddy.vision.shared.Detections;
import com.bfr.buddy.vision.shared.IVisionRsp;
import com.bfr.buddy.vision.shared.Tracking;
import com.bfr.buddy.vision.shared.enums.TrackingMode;
import com.bfr.buddy.vision.shared.enums.VisionAlgorithm;
import com.bfr.buddy.vision.shared.ArucoMarkers;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

public class MainActivity extends BuddyActivity {

    //
    private final String TAG = "SDKv2_Vision";
    // UI elements
    private Button mStartCameraBtn, mStopCameraBtn;
    private Button mStartTrackingBtn, mStopTrackingBtn, mGetTrackingBtn;
    private Button mFaceDetectBtn, mPersonDetectBtn, mAprilTagBtn, mColorBtn;

    private RadioButton mNoneRadioBtn , mCamFrameRadioBtn, mCvFrameRadioBtn;

    private ImageView mPreviewCamera;
    private TextView mTextview;

    // Elements to detect with computer vision
    private ArucoMarkers mArucos;
    private Detections mFaces;
    private Detections mPersons;
    private Tracking mTracking;

    // Motion detection
    private EditText mEditTextMvtThres;



    //Calback Vision
    private IVisionRsp mVisionClbk = new IVisionRsp.Stub() {
        @Override
        public void onSuccess(String s) throws RemoteException {
        }

        @Override
        public void onFailed(String s) throws RemoteException {

        }
    };

    private Handler mHandler = new Handler();

    //Element to display frame from Camera grand angle every 100ms
    private  Runnable mRunnablePreviewFrame = new Runnable() {
        @Override
        public void run() {
            try {
                //display frame grand angle

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(mCamFrameRadioBtn.isChecked()){
                                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getGrandAngleFrame());
                                mHandler.postDelayed(this, 5);
                            }
                            else if(mCvFrameRadioBtn.isChecked()){
                                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                                mHandler.postDelayed(this, 5);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");

        //Link to UI
        mStartCameraBtn = findViewById(R.id.buttonStartCamera);
        mStopCameraBtn = findViewById(R.id.buttonStopCamera);
        mStartTrackingBtn = findViewById(R.id.buttonStartTracking);
        mStopTrackingBtn = findViewById(R.id.buttonStopTracking);
        mGetTrackingBtn = findViewById(R.id.buttonGetTracking);

        mFaceDetectBtn = findViewById(R.id.buttonFaceDetect);
        mPersonDetectBtn = findViewById(R.id.buttonPersonDetect);
        mAprilTagBtn = findViewById(R.id.buttonAprilTag);
        mColorBtn = findViewById(R.id.buttonColor);

        mNoneRadioBtn = findViewById(R.id.noneRadioButton);
        mNoneRadioBtn.toggle();
        mCamFrameRadioBtn = findViewById(R.id.cameraFrameRadio);
        mCvFrameRadioBtn = findViewById(R.id.cvFrameRadio);

        mPreviewCamera = findViewById(R.id.previewCam);
        mTextview = findViewById(R.id.resultText);

        // instantiate vision objects
        mArucos = new ArucoMarkers();
        mFaces = new Detections();
        mPersons = new Detections();
        mTracking = new Tracking();


        /*** Start Stop Camera*/

        mStartCameraBtn.setOnClickListener(view -> {
            Log.i(TAG, "Start button before startcam");
            BuddySDK.UI.setCloseWidgetVisibility(FloatingWidgetVisibility.ALWAYS);

             BuddySDK.Vision.startCamera(0, new IVisionRsp.Stub() {
                @Override
                public void onSuccess(String s) throws RemoteException {
                    Log.i(TAG, "Camera started success " + s);
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Camera started !",
                            Toast.LENGTH_LONG).show());
                }

                @Override
                public void onFailed(String s) {
                    Log.e(TAG, "Failed starting camera " + s);
                }
            });
        });
        mStopCameraBtn.setOnClickListener(view -> {
            BuddySDK.UI.setCloseWidgetVisibility(FloatingWidgetVisibility.ON_TOUCH);
            BuddySDK.Vision.stopCamera(0, new IVisionRsp.Stub() {
                @Override
                public void onSuccess(String s) {
                    Log.i(TAG, "Camera stopped success " + s);
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                            "Camera stopped !",
                            Toast.LENGTH_LONG).show());
                } // end onSuccess

                @Override
                public void onFailed(String s) {
                    Log.e(TAG, "Failed stopping camera " + s);
                }
            });
        });
        findViewById(R.id.camStatusBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuddySDK.UI.setCloseWidgetVisibility(FloatingWidgetVisibility.NEVER);
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(),
                            "Camera Status:\nStarted: " +
                                    BuddySDK.Vision.getStatus(0).isStarted()
                            +"\nTracking: " + BuddySDK.Vision.getStatus(0).isTracking()
                            +"\nMotion: " + BuddySDK.Vision.getStatus(0).isDetectingMotion(),
                            Toast.LENGTH_LONG).show();
                    mTextview.clearComposingText();
                    mTextview.setText("Camera Status:\nStarted: " +
                            BuddySDK.Vision.getStatus(0).isStarted()
                            +"\nTracking: " + BuddySDK.Vision.getStatus(0).isTracking()
                            +"\nMotion: " + BuddySDK.Vision.getStatus(0).isDetectingMotion());
                });
            }
        });

        /*** Display Camera frame of Computer vision frame*/
        mCamFrameRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.post(mRunnablePreviewFrame);
            }
        });
        mCvFrameRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.post(mRunnablePreviewFrame);
            }
        });
    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.i(TAG, "onPause");

    }

    /*
    Will be called once all services are initialized.
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onSDKReady() {
        Log.i(TAG, "SdkReady");
        BuddySDK.UI.setViewAsFace(findViewById(R.id.view_face));


        BuddySDK.UI.setCloseWidgetVisibility(FloatingWidgetVisibility.ON_TOUCH);
        /*** AprilTag detection*/

        mAprilTagBtn.setOnClickListener(view -> {

                    try {
                        mArucos = BuddySDK.Vision.detectArucoMarkers();
                    } catch (IllegalStateException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                        });
                    }
                    // display
                    if (mArucos.getNumOfArucoMarkers() > 0) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(),
                                    "1st Aruco detected:\n" +
                                            mArucos.getIds()[0] + " " +
                                            mArucos.getX()[0] + " " +
                                            mArucos.getY()[0],
                                    Toast.LENGTH_LONG).show();
                            mTextview.clearComposingText();
                            mTextview.setText("1st Aruco detected:\n" +
                                    mArucos.getIds()[0] + " " +
                                    mArucos.getX()[0] + " " +
                                    mArucos.getY()[0]);
                        });


                    } // end if myArucos size >0
                }

                );


        /*** Face Detection */

        mFaceDetectBtn.setOnClickListener(view -> {

            try {
                mFaces = BuddySDK.Vision.detectFace();
                // display
                if (mFaces.getNumOfDetections()>0)
                {
                    Log.i(TAG, "1st Face detected: " +
                            mFaces.getScore()[0] + " " +
                            mFaces.getLeftPos()[0] + " " +
                            mFaces.getTopPos()[0]);
                    runOnUiThread(() -> {Toast.makeText(getApplicationContext(),
                            "1st Face detected:\n" +
                                     mFaces.getScore()[0] + " " +
                                    mFaces.getLeftPos()[0] + " " +
                                    mFaces.getTopPos()[0],
                            Toast.LENGTH_LONG).show();
                        mTextview.clearComposingText();
                        mTextview.setText("1st Face detected:\n" +
                                mFaces.getScore()[0] + " " +
                                mFaces.getLeftPos()[0] + " " +
                                mFaces.getTopPos()[0]);
                    });
                } // end if size >0

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                });
            }

        });


        /*** Person Detection */

        mPersonDetectBtn.setOnClickListener(view -> {

            try {
                mPersons = BuddySDK.Vision.detectPerson();
                // display
                if (mPersons.getNumOfDetections()>=0)
                {
                    Log.i(TAG, "1st Person detected: " + mPersons.getScore()[0] + "\n" +
                            mPersons.getRightPos()[0] + " \n" +
                            mPersons.getLeftPos()[0] + " \n" +
                            mPersons.getTopPos()[0] + " \n" +
                            mPersons.getBottomPos()[0]);
                    runOnUiThread(() -> {Toast.makeText(getApplicationContext(),
                            "1st Person detected:\n" +
                                    mPersons.getRightPos()[0] + " \n" +
                                    mPersons.getLeftPos()[0] + " \n" +
                                    mPersons.getTopPos()[0] + " \n" +
                                    mPersons.getBottomPos()[0],
                            Toast.LENGTH_LONG).show();
                    mTextview.clearComposingText();
                    mTextview.setText("1st Person detected:\n" +
                            mPersons.getRightPos()[0] + " \n" +
                            mPersons.getLeftPos()[0] + " \n" +
                            mPersons.getTopPos()[0] + " \n" +
                            mPersons.getBottomPos()[0]);
                    });
                } // end if  size >0

            } catch (IllegalStateException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                });
            }

        });


        /*** Tracking */
        mStartTrackingBtn.setOnClickListener(view -> {
            try {
                BuddySDK.Vision.startTracking( TrackingMode.FAST);
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                        "Tracking started",
                        Toast.LENGTH_SHORT).show());
            } catch (IllegalStateException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                });
            }
        });

        mStopTrackingBtn.setOnClickListener(view -> {

                BuddySDK.Vision.stopTracking();
                runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                        "Tracking stopped",
                        Toast.LENGTH_SHORT).show());

        });

        mGetTrackingBtn.setOnClickListener(view -> {
            try {
                mTracking = BuddySDK.Vision.getTracking();
                runOnUiThread(() -> {Toast.makeText(getApplicationContext(),
                        "Target tracked " + mTracking.isTrackingSuccessfull()
                                + " at " + mTracking.getTopPos() + "," + mTracking.getLeftPos(),
                        Toast.LENGTH_SHORT).show();
                    mTextview.clearComposingText();
                    mTextview.setText("Target tracked " + mTracking.isTrackingSuccessfull()
                            + " at " +
                            "left " + mTracking.getLeftPos() + " \nRight " +
                            mTracking.getRightPos() + " \nTop " +
                            mTracking.getTopPos() + " \nBottom" +
                            mTracking.getBottomPos());
                });

            } catch (IllegalStateException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                });
            }


        });

        /*** Motion detection*/
        mEditTextMvtThres = findViewById(R.id.editTextmotionThres);

        findViewById(R.id.button_startmotion).setOnClickListener(view -> {
            try {
                BuddySDK.Vision.startMotionDetection();
            } catch (IllegalStateException e) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                });
            }
        });
        findViewById(R.id.button_stopmotion).setOnClickListener(view -> {
            BuddySDK.Vision.stopMotionDetection();
        });
        findViewById(R.id.button_getmotion).setOnClickListener(view -> {
            try {
                Log.i(TAG, "Mvt= " +   BuddySDK.Vision.motionDetect() + " "+
                        BuddySDK.Vision.getMotionDetection().getAmplitude()
                        + " " + BuddySDK.Vision.getMotionDetection().getX()
                        + " " + BuddySDK.Vision.getMotionDetection().getY()
                );
                Log.i(TAG, "Mvt with Thres = " +   BuddySDK.Vision.motionDetectWithThres(20.0f)
                );
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(getApplicationContext(),
                                "Mvt= " +   BuddySDK.Vision.motionDetect() + " "+
                                        BuddySDK.Vision.getMotionDetection().getAmplitude()
                                        + " " + BuddySDK.Vision.getMotionDetection().getX()
                                        + " " + BuddySDK.Vision.getMotionDetection().getY() + " |  Mvt with Thres = " +   BuddySDK.Vision.motionDetectWithThres(20.0f),
                                Toast.LENGTH_LONG).show();
                    } catch (IllegalStateException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                        });
                    }
                    mTextview.clearComposingText();
                    try {
                        mTextview.setText("Mvt= " +   BuddySDK.Vision.motionDetect() + " "+
                                BuddySDK.Vision.getMotionDetection().getAmplitude()
                                + " " + BuddySDK.Vision.getMotionDetection().getX()
                                + " " + BuddySDK.Vision.getMotionDetection().getY()  + " |  Mvt with Thres = " +   BuddySDK.Vision.motionDetectWithThres(20.0f));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        findViewById(R.id.button_setmvtthres).setOnClickListener(view -> {
            try {
                BuddySDK.Vision.setMotionThres(Float.parseFloat(mEditTextMvtThres.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        /*** Color detection ***/

        mColorBtn.setOnClickListener(view -> {

                runOnUiThread(() -> {
                    try {
                        Log.i(TAG, "Color detected  " + BuddySDK.Vision.colorDetect());
                        mTextview.setText("Color :\n" +
                                BuddySDK.Vision.colorDetect());

                        Toast.makeText(getApplicationContext(),
                                "Color :\n" +
                                        BuddySDK.Vision.colorDetect(),
                                Toast.LENGTH_LONG).show();
                    } catch (IllegalStateException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
                        });
                    }
                    mTextview.clearComposingText();

                });
        });


    }

    @Override
    public void onEvent(EventItem eventItem) {

    }
}