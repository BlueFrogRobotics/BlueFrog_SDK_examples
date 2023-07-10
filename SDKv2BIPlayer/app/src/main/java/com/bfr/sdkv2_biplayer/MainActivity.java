package com.bfr.sdkv2_biplayer;

import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyCompatActivity;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.buddysdk.Interpreter.Interpreter.OnBehaviourAlgorithmListener;
import com.bfr.buddysdk.Interpreter.Interpreter.OnRunInstructionListener;
import com.bfr.buddysdk.Interpreter.Structures.Instructions.Abstract.ABehaviourInstruction;
import com.bfr.buddysdk.services.companion.Task;
import com.bfr.buddysdk.services.companion.TaskCallback;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BuddyCompatActivity implements OnRunInstructionListener, OnBehaviourAlgorithmListener {

    private static final String TAG = "BasicBiPlayer";

    private EditText editText;
    private EditText BItoPlayFrom;
    private EditText editCategory;

    private final int  STORAGE_PERMISSION_CODE = 101;

    // List of indexes
    List<String> BINames = new ArrayList<String>();
    // index to play
    private int BIidx =0;
    private final String _BI_FOLDER = "storage/emulated/0/BI/Behaviour";
    // to randomly choose BI
    String biToPlay ;
    String[] pathnames;
    File dir;
    File source;
    boolean isreadingBI = false;
    boolean playAll = false;
    Task biTask=null;

    TextView ongoingBI;


    ScheduledExecutorService myscheduler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=findViewById(R.id.editBIToPlay);
        BItoPlayFrom = findViewById(R.id.editBIToPlayFrom);
        editCategory = findViewById(R.id.editCategory);
        editText.setImeOptions(IME_ACTION_DONE);
        BItoPlayFrom.setImeOptions(IME_ACTION_DONE);
        editCategory.setImeOptions(IME_ACTION_DONE);

        ongoingBI = findViewById(R.id.ongoingBI);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

    }

    @Override
    public void onSDKReady() {
        Log.d(TAG, "SDK Ready");
        BuddySDK.UI.setViewAsFace(findViewById(R.id.view_face));


        IUsbCommadRsp rsp = new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.d(TAG, "command success "+s);
            }

            @Override
            public void onFailed(String s) throws RemoteException {
                Log.e(TAG, "command failed "+s);
            }
        };

        BuddySDK.USB.enableWheels(1, 1, rsp);
        BuddySDK.USB.enableYesMove(1, rsp);
        BuddySDK.USB.enableNoMove(1, rsp);

        // dir where the BI are
        dir = new File(_BI_FOLDER, "");
        // Get all comportemental in folder
        try {
            Files.list(Paths.get(_BI_FOLDER)).sorted().toArray();
//             String[] listOfBI =  Files.list(Paths.get(_BI_FOLDER)).sorted().toArray();
//             for (int i=0; i<listOfBI.length; i++)
//                 Log.i(TAG, "BI found on device: " + listOfBI[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pathnames = dir.list();
        // populate list
        int numOfBI =0;
        for (int i = 0; i < pathnames.length; i++)
        {
            // if the file is an xml (BI)
            if (pathnames[i].toUpperCase().contains(".XML"))
            {
                // add BI name
                BINames.add(pathnames[i]);
                BINames.sort(Collator.getInstance());
                // Init text field with first file
                BItoPlayFrom.setText(BINames.get(0));
            }
        } // next file


        /*** play all BI ***/
        findViewById(R.id.buttonPlayAll).setOnClickListener(view -> {
            String biName = editText.getText().toString();

            BIidx = 0;

            myscheduler = Executors.newScheduledThreadPool(1);
            myscheduler.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    if (isreadingBI == false) {

                        Log.w(TAG, "Run " + BINames.get(BIidx).toUpperCase());
                        // affect name of BI to read
                        biToPlay = BINames.get(BIidx);
                        // set
                        isreadingBI = true;
                        // read BI
                        readBI(biToPlay);
                        // Increment index
                        BIidx += 1;
                    }
                }
            }, 0, 500, TimeUnit.MILLISECONDS);

            if (BIidx >= BINames.size()) {
                Log.w(TAG, "BIidx = " + BIidx + " ->Shutting down scheduler");
                myscheduler.shutdown();
            }

        });

        findViewById(R.id.buttonCategory).setOnClickListener(view -> {
            ImageView imageView =findViewById(R.id.imageView);
            PlayerView videoView=findViewById(R.id.videoView);
            String category = editCategory.getText().toString();
            //interpreter.RunRandom(this, category, this, this, imageView, videoView);
            try{
            biTask = BuddySDK.Companion.createBICategoryTask(category, videoView, imageView);
            biTask.start(new TaskCallback() {
                @Override
                public void onStarted() {
                    Log.d(TAG, "bi has started ");
                }

                @Override
                public void onSuccess(@NonNull String s) {
                    Log.d(TAG, "BI success "+s);
                    isreadingBI = false;
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "BI cancelled");
                    isreadingBI = false;
                }

                @Override
                public void onError(@NonNull String s) {
                    Log.e(TAG, "BI error "+s);
                    isreadingBI = false;
                }

                @Override
                public void onIntermediateResult(@NonNull String s) {
                    Log.d(TAG, "[BI][TASK] le bi intermediate result "+s);
                }
            });
//            }
        } catch (Exception e) {
            Log.e(TAG, "Runbehaviour Probl with " + category + "\n" + Log.getStackTraceString(e));
            // reset
            isreadingBI = false;
        }
        });

        /*** Stop BI ***/
        findViewById(R.id.buttonStop).setOnClickListener(view -> {
            // reset
            //BIidx = 0;
            playAll = false;
            if (biTask != null)
                biTask.stop();
            if (myscheduler!=null && !myscheduler.isShutdown())
                myscheduler.shutdown();

        });

        /*** Play BI ***/
        findViewById(R.id.buttonPlay).setOnClickListener(view -> {
            String biName = editText.getText().toString();
            readBI(biName);
        });

        /*** Play Next ***/
        findViewById(R.id.buttonNext).setOnClickListener(view -> {
            if(BINames.size()<=BIidx)
                BIidx=0;
            biToPlay = BINames.get(BIidx);
            // set
            isreadingBI = true;
            // read BI
            readBI(biToPlay);
            // Increment index
            BIidx += 1;
        });

        /*** Play Previous ***/
        findViewById(R.id.buttonPrevious).setOnClickListener(view -> {
            BIidx -= 1;
            if(BIidx<0)
                BIidx=BINames.size()-1;
            biToPlay = BINames.get(BIidx);
            // set
            isreadingBI = true;
            // read BI
            readBI(biToPlay);
            // Increment index

        });

        /*** Relay ***/
        findViewById(R.id.buttonReplay).setOnClickListener(view -> {
            biToPlay = BINames.get(BIidx);
            // set
            isreadingBI = true;
            // read BI
            readBI(biToPlay);
        });


        /*** Play all BI from ***/

        findViewById(R.id.buttonPlayAllFrom).setOnClickListener(view -> {

            String biName = BItoPlayFrom.getText().toString().toUpperCase();
            BIidx = 0;
            // For each BI file
            for (int i=0; i<BINames.size(); i++)
            {
                Log.w(TAG, "Runbehaviour Attempting " + BINames.get(BIidx).toUpperCase());
                // if not the Bi to start from
                if(!BINames.get(BIidx).toUpperCase().contains(biName))
                {
                    BIidx+=1;
                }
                else // BI is the Bi to start from
                {
                    break;
                }
            }

            myscheduler = Executors.newScheduledThreadPool(1);
            myscheduler.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    if (isreadingBI == false) {

                        Log.w(TAG, "Run " + BINames.get(BIidx).toUpperCase());
                        // affect name of BI to read
                        biToPlay = BINames.get(BIidx);
                        // set
                        isreadingBI = true;
                        // read BI
                        readBI(biToPlay);
                        // Increment index
                        BIidx += 1;
                    }
                }
                }, 0, 500, TimeUnit.MILLISECONDS);

              if (BIidx >= BINames.size() ) {
                  Log.w(TAG, "BIidx = " + BIidx + " ->Shutting down scheduler");
                  myscheduler.shutdown();
              }

        });


    }

    @Override
    public void onEvent(EventItem eventItem) {
    }

    private void readBI(String biName) {
        ImageView imageView =findViewById(R.id.imageView);
        PlayerView videoView=findViewById(R.id.videoView);
        String docPath = "";

        String fileName =  biName;
        //File source = new File(fileName.replace(".xml", "")+".xml");
        Log.i(TAG, "Runbehaviour Attempting " + biName);

        try {

            biTask = BuddySDK.Companion.createBITask(fileName, videoView, imageView, true);
            biTask.start(new TaskCallback() {
                @Override
                public void onStarted() {
                    Log.d(TAG, "[BI][TASK] bi has started ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ongoingBI.setText("ongoing: " + biName);
                        }
                    });
                }

                @Override
                public void onSuccess(@NonNull String s) {
                    Log.d(TAG, "[BI][TASK] le bi success "+s);
                    ongoingBI.setText("finished: " + biName);
                    isreadingBI = false;
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "[BI][TASK] le bi cancelled");
                    ongoingBI.setText("finished on cancel: " + biName);
                    isreadingBI = false;
                }

                @Override
                public void onError(@NonNull String s) {
                    Log.e(TAG, "[BI][TASK] le bi error "+s);
                    ongoingBI.setText("finished with error: " + biName);
                    isreadingBI = false;
                }

                @Override
                public void onIntermediateResult(@NonNull String s) {
                    Log.d(TAG, "[BI][TASK] le bi intermediate result "+s);
                }
            });
//            }
        } catch (Exception e) {
            Log.e(TAG, "Runbehaviour Probl with " + biName + "\n" + Log.getStackTraceString(e));
            // reset
            isreadingBI = false;
        }
    } // end Read BI

    /**
     * Will be called when the bi has stopped playing
     * @param aborted true if the played bi has been aborted
     */
    @Override
    public void OnBehaviourAlgorithm(boolean aborted) {
        isreadingBI = false;
    }

    /**
     * Will be called for each instruction played
    * @param aBehaviourInstruction the currently played instruction
     * */
    @Override
    public void OnRunInstruction(ABehaviourInstruction aBehaviourInstruction) {

    }

    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }


}