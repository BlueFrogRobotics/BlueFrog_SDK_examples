package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfr.buddy.vision.shared.ArucoMarkers;
import com.bfr.buddy.vision.shared.Detections;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;


public class detection extends Fragment {

    private Button mFaceDetectBtn, mPersonDetectBtn, mAprilTagBtn, mColorBtn;
    private TextView resultText;
    private ImageView mPreviewCamera;

    // elements to detect
    ArucoMarkers mArucos;
    Detections mFaces;
    Detections mPersons;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_detection, container, false);

        //Link to UI
        mFaceDetectBtn = view.findViewById(R.id.buttonGetTopk);
        mPersonDetectBtn = view.findViewById(R.id.buttonStop);
        mAprilTagBtn = view.findViewById(R.id.buttonStart);
        mColorBtn = view.findViewById(R.id.buttonColor);
        resultText = view.findViewById(R.id.resultText);
        mPreviewCamera = view.findViewById(R.id.previewCam);

        /*** AprilTag detection*/
        mAprilTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mArucos = BuddySDK.Vision.detectArucoMarkers();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                // display
                if (mArucos.getNumOfArucoMarkers() > 0) {
                    getActivity().runOnUiThread(() -> {
                        //
                        resultText.clearComposingText();
                        resultText.setText("1st Aruco detected:\n" +
                                mArucos.getIds()[0] + " " +
                                mArucos.getX()[0] + " " +
                                mArucos.getY()[0]);
                        // Display image
                        mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                    });


                } // end if myArucos size >0
            }
        });


        /*** Face Detection */
        mFaceDetectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mFaces = BuddySDK.Vision.detectFace();
                    // display
                    if (mFaces.getNumOfDetections()>0)
                    {
                        resultText.clearComposingText();
                        resultText.setText("1st Face detected:\n" +
                                    mFaces.getScore()[0] + " " +
                                    mFaces.getLeftPos()[0] + " " +
                                    mFaces.getTopPos()[0]);
                        // Display image
                        mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                    } // end if size >0

                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
        });

        /*** Person Detection */
        mPersonDetectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mPersons = BuddySDK.Vision.detectPerson();
                    // display
                    if (mPersons.getNumOfDetections()>0)
                    {
                        resultText.clearComposingText();
                        resultText.setText("1st Person detected:\n" +
                                mPersons.getScore()[0] + " " +
                                mPersons.getLeftPos()[0] + " " +
                                mPersons.getTopPos()[0]);
                        // Display image
                        mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                    } // end if size >0

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /*** Color recognition ***/
        mColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultText.setText("Color :\n" +
                        BuddySDK.Vision.colorDetect());
            }
        });


        return view;
    }
}