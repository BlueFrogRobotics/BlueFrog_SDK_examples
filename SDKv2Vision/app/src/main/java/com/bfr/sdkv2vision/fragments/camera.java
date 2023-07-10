package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfr.buddy.vision.shared.CameraStatus;
import com.bfr.buddy.vision.shared.IVisionRsp;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;


public class camera extends Fragment {

    private Button mStartBtn, mStopBtn, mGetBtn;
    private TextView resultText;
    private ImageView mPreviewCamera;
    private Handler mHandler = new Handler();
    private CheckBox displayBox;

    //Element to display frame from Camera
    private  Runnable mRunnablePreviewFrame = new Runnable() {
        @Override
        public void run() {
            try {
                //display frame grand angle
                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getGrandAngleFrame());
                mHandler.postDelayed(this, 30);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

        //Link to UI
        mStartBtn = view.findViewById(R.id.buttonStart);
        mStopBtn = view.findViewById(R.id.buttonStop);
        mGetBtn = view.findViewById(R.id.buttonGetStatus);
        mPreviewCamera = view.findViewById(R.id.previewCam);
        resultText = view.findViewById(R.id.resultText);
        displayBox = view.findViewById(R.id.displayChckbox);
        displayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mHandler.post(mRunnablePreviewFrame);
                else
                    mHandler.removeCallbacksAndMessages(null);
            }
        });

        /*** Camera management***/
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuddySDK.Vision.startCamera(new IVisionRsp.Stub() {
                    @Override
                    public void onSuccess(String s) throws RemoteException {
                        Log.i("Camera", "camera started successfully");
                    }

                    @Override
                    public void onFailed(String s) throws RemoteException {
                        Log.e("Camera", "camera started on error : "+ s);
                    }
                });
            } // end onclick
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuddySDK.Vision.stopCamera(new IVisionRsp.Stub() {
                    @Override
                    public void onSuccess(String s) throws RemoteException {
                        Log.i("Camera", "camera stopped successfully");
                    }

                    @Override
                    public void onFailed(String s) throws RemoteException {
                        Log.e("Camera", "camera stopped on error : "+ s);
                    }
                });
            } // end onclick
        });

        mGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CameraStatus cStatus = BuddySDK.Vision.getStatus(0);

                resultText.clearComposingText();
                resultText.setText("Camera status:\n" +
                        "started: " + cStatus.isStarted() + "\n" +
                        "tracking: " + cStatus.isTracking() + "\n" +
                        "motion detect: " + cStatus.isDetectingMotion());
            } // end onclick
        });


        return view;
    }


    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        displayBox.setChecked(false);
    }
}