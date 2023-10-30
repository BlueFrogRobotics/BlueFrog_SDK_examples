package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfr.buddy.vision.shared.Tracking;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;


public class tracking extends Fragment {

    private Button mStartBtn, mStopBtn, mGetBtn;
    private TextView resultText;
    private ImageView mPreviewCamera;

    //Element to display frame from Camera
    private Handler mHandler = new Handler();
    private  Runnable mRunnablePreviewFrame = new Runnable() {
        @Override
        public void run() {
            try {
                //display frame grand angle
                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
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
        View view =  inflater.inflate(R.layout.fragment_tracking, container, false);

        //Link to UI
        mStartBtn = view.findViewById(R.id.buttonStart);
        mStopBtn = view.findViewById(R.id.buttonStop);
        mGetBtn = view.findViewById(R.id.buttonGet);
        resultText = view.findViewById(R.id.resultText);
        mPreviewCamera = view.findViewById(R.id.previewCam);

        /*** Tracking */
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    BuddySDK.Vision.startTracking();
                    mHandler.post(mRunnablePreviewFrame);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuddySDK.Vision.stopTracking();
                mHandler.removeCallbacksAndMessages(null);
            }
        });

        mGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Tracking targetResult = BuddySDK.Vision.getTracking();

                resultText.setText("Target= " +   targetResult.isTrackingSuccessfull() + " "
                        + " " + targetResult.getLeftPos()
                        + " " + targetResult.getTopPos()  );
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        //reset
        mHandler.removeCallbacksAndMessages(null);
        BuddySDK.Vision.stopMotionDetection();
    }
}