package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfr.buddy.vision.shared.ArucoMarker;
import com.bfr.buddy.vision.shared.Detections;
import com.bfr.buddy.vision.shared.Pose;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;


public class detection extends Fragment {

    private Button mFaceDetectBtn, mPersonDetectBtn, mAprilTagBtn,mPoseBtn, mColorBtn;
    private TextView resultText;
    private ImageView mPreviewCamera;

    // elements to detect
    ArucoMarker[] mArucos;
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
        mPoseBtn = view.findViewById(R.id.buttonPose);
        mColorBtn = view.findViewById(R.id.buttonColor);
        resultText = view.findViewById(R.id.resultText);
        mPreviewCamera = view.findViewById(R.id.previewCam);

        /*** AprilTag detection*/
        mAprilTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mArucos = BuddySDK.Vision.getListOfArucos();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                // display
                if (mArucos.length > 0) {
                    getActivity().runOnUiThread(() -> {
                        //
                        resultText.clearComposingText();
                        resultText.setText("1st Aruco detected:\n" +
                                mArucos[0].getId() + " " +
                                mArucos[0].getCorners()[0][0] + " " +
                                mArucos[0].getCorners()[0][1]);
                        // Display image
                        mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                    });

                } // end if myArucos size >0
            }
        });

        // Pose estimation
        mPoseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (mArucos.length > 0)
                    {
                        int realArucoSize = 10; //put here the real marker size in cm or m
                        Pose arucoPose = BuddySDK.Vision.EstimateArucoPose(mArucos[0], realArucoSize);

                        getActivity().runOnUiThread(() -> {
                            //
                            resultText.clearComposingText();
                            resultText.setText("1st Aruco Pose estimation:\n" +
                                    arucoPose.getX() + " " +
                                    arucoPose.getY() + " " +
                                    arucoPose.getThetaY());
                            // Display image
                            mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
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