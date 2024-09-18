package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.bfr.buddy.vision.shared.Pose;
import com.bfr.buddy.vision.shared.QRCode;
import com.bfr.buddy.vision.shared.enums.QRDetectionMethod;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;

public class qrcode extends Fragment {

    private Button mDetectQRBtn, mPoseQRBtn;
    private TextView resultText;
    private ImageView mPreviewCamera;

    // elements to detect
    QRCode[] mQRCodes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_qrcode, container, false);

        //Link to UI
        mDetectQRBtn = view.findViewById(R.id.detectbtn);
        mPoseQRBtn = view.findViewById(R.id.posebtn);
        resultText = view.findViewById(R.id.resultText);
        mPreviewCamera = view.findViewById(R.id.previewCam);

        // Detection
        mDetectQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQRCodes = BuddySDK.Vision.getListOfQRCodes( QRDetectionMethod.NORMAL);

                // display
                if (mQRCodes.length > 0) {
                    getActivity().runOnUiThread(() -> {
                        //
                        resultText.clearComposingText();
                        resultText.setText("QRCode detected:\n" +
                                mQRCodes[0].getData() + "\nPosition:  " +
                                mQRCodes[0].getCorners()[0][0] + " " +
                                mQRCodes[0].getCorners()[0][1]);
                        // Display image
                        mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                    });

                } // end if myArucos size >0
                else {
                    Log.i("QRCode", "No QRCode detected");
                }
            }
        });

        // Pose estimation
        mPoseQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mQRCodes[0]==null)
                    return;

                Pose mPose = BuddySDK.Vision.EstimateQRCodePose(mQRCodes[0], 12.5 );

                // display
                if (mPose!=null) {
                    getActivity().runOnUiThread(() -> {
                        //
                        String textToDisplay = "QRCode Pose:\n" +
                                mPose.getX() + "  " +
                                mPose.getY() + " " +
                                mPose.getZ() + "\n" +
                                mPose.getThetaX() + " " +
                                mPose.getThetaY() + " " +
                                mPose.getThetaZ();
                        Log.i("QRCode", textToDisplay);

                        resultText.clearComposingText();
                        resultText.setText(textToDisplay);
                        // Display image
                        mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                    });

                } // end if myArucos size >0

            }
        });



        return view;

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}