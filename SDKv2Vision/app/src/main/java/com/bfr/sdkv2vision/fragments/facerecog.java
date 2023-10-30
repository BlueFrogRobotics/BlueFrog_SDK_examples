package com.bfr.sdkv2vision.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bfr.buddy.vision.shared.Detections;
import com.bfr.buddy.vision.shared.FaceRecognition;
import com.bfr.buddy.vision.shared.IVisionRsp;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.sdkv2vision.R;

public class facerecog extends Fragment {

    String TAG = "Face recognition";
    private Button mSaveBtn, mRecogBtn, mLoadBtn, mGetAllBtn, mGetTopkBtn, mDeleteBtn;
    private TextView resultText;
    private EditText nameText;
    private EditText indexText;
    private ImageView mPreviewCamera;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_facerecog, container, false);

        //Link to UI
        mSaveBtn = view.findViewById(R.id.buttonStart);
        mRecogBtn = view.findViewById(R.id.buttonStop);
        mLoadBtn = view.findViewById(R.id.buttonLoad);
        mGetAllBtn = view.findViewById(R.id.buttonGetall);
        mGetTopkBtn = view.findViewById(R.id.buttonGetTopk);
        mDeleteBtn = view.findViewById(R.id.buttonDelete);
        resultText = view.findViewById(R.id.resultText);
        nameText = view.findViewById(R.id.editTextName);
        indexText = view.findViewById(R.id.editTextIndex);
        mPreviewCamera = view.findViewById(R.id.previewCam);

        /*** Face recognition*/
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // First, detect all the faces in front of the camera
                Detections listOfFaces = BuddySDK.Vision.detectFace();
                // index of the face to save for further recognition
                int idxOfFaceToSave = 0;

                // linking a name to the face for further recognition
                BuddySDK.Vision.saveFace(listOfFaces,
                        idxOfFaceToSave,
                        nameText.getText().toString(),
                        new IVisionRsp.Stub() {
                            @Override
                            public void onSuccess(String s) throws RemoteException {
                                Log.i(TAG, "Face saved successfully");
                            }

                            @Override
                            public void onFailed(String s) throws RemoteException {
                                Log.e(TAG, "Error saving face: " + s);
                            }
                        });
                // Display
                synchronized(this){
                    try {
                        this.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
            }
        });

        mRecogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // First, detect all the faces in front of the camera
                Detections listOfFaces = BuddySDK.Vision.detectFace();
                // index of the face to recognize
                int idxOfFaceToSave = 0;

                // Actual recognition
                String recognizedName =  BuddySDK.Vision.recognizeFace(listOfFaces, idxOfFaceToSave).getName();

                // Display
                resultText.setText("Face recognized:\n"+recognizedName);
                synchronized(this){
                    try {
                        this.wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mPreviewCamera.setImageBitmap(BuddySDK.Vision.getCVResultFrame());
            }
        });

        mLoadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // load saved faces from default file stored in the robot
                BuddySDK.Vision.loadFaces(new IVisionRsp.Stub() {
                    @Override
                    public void onSuccess(String s) throws RemoteException {
                        Log.i(TAG, "Faces loaded successfully");
                    }

                    @Override
                    public void onFailed(String s) throws RemoteException {
                        Log.e(TAG, "Faces loading failed: " + s);
                    }
                });
            }
        });

        mGetAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Once the previously saved faces have been loaded with BuddySDK.Vision.loadFaces()
                // get the list of the previoulsy registered names
                String[] listOfNames = BuddySDK.Vision.getSavedNames();

                String toDisplay = "";
                for (int i=0; i<listOfNames.length; i++){
                    toDisplay = toDisplay + listOfNames[i] + "\n";
                }

                //display
                resultText.setText(toDisplay);
            }
        });

        mGetTopkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // After a recognition with BuddySDK.Vision.recognizeFace()
                // get the list of the top k-candidates, from the most well recognized to the less
                FaceRecognition[] candidates = BuddySDK.Vision.getTopKResults(3);

                String toDisplay = "";
                for (int i=0; i<candidates.length; i++){
                    toDisplay = toDisplay + candidates[i].getName() + "\n";
                }
                //Display
                resultText.setText(toDisplay);
            }
        });

        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // remove a known face from the list of previously registered names at the specified index
                BuddySDK.Vision.removeFace(Integer.parseInt(indexText.getText().toString()),
                        new IVisionRsp.Stub() {
                            @Override
                            public void onSuccess(String s) throws RemoteException {
                                Log.i(TAG, "Removed saved face successfully");
                            }

                            @Override
                            public void onFailed(String s) throws RemoteException {
                                Log.e(TAG, "Removing saved face loading failed: " + s);
                            }
                        });
            }
        });

        return view;
    }
}