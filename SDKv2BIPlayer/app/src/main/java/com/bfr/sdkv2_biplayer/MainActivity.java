package com.bfr.sdkv2_biplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;
/*import com.bfr.buddysdk.Interpreter.ABehaviourInstruction;
import com.bfr.buddysdk.Interpreter.BehaviourAlgorithmStorage;
import com.bfr.buddysdk.Interpreter.BehaviourInterpreter;
import com.bfr.buddysdk.Interpreter.OnBehaviourAlgorithmListener;
import com.bfr.buddysdk.Interpreter.OnRunInstructionListener;*/
import com.bfr.buddysdk.Interpreter.Interpreter.BehaviourInterpreter;
import com.bfr.buddysdk.Interpreter.Interpreter.OnBehaviourAlgorithmListener;
import com.bfr.buddysdk.Interpreter.Interpreter.OnRunInstructionListener;
import com.bfr.buddysdk.Interpreter.Structures.Algorithm.BehaviourAlgorithmStorage;
import com.bfr.buddysdk.Interpreter.Structures.Instructions.Abstract.ABehaviourInstruction;
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

public class MainActivity extends BuddyActivity implements OnRunInstructionListener, OnBehaviourAlgorithmListener {

    private static final String TAG = "BasicBiPlayer";

    private BehaviourInterpreter interpreter;
    private EditText editText;
    private EditText BItoPlayFrom;

    private final int  STORAGE_PERMISSION_CODE = 101;

    // List of indexes
    List<String> BINames = new ArrayList<String>();
    // index to play
    private int BIidx =0;
    private final String _BI_FOLDER = "storage/emulated/0/Android/data/com.bfr.sdkv2_biplayer/files/BI";
    // to randomly choose BI
    String biToPlay ;
    String[] pathnames;
    File dir;
    File source;
    boolean isreadingBI = false;
    boolean playAll = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText=findViewById(R.id.editBIToPlay);
        BItoPlayFrom = findViewById(R.id.editBIToPlayFrom);

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        // Copy assets files
        copyAssets();
    }

    @Override
    public void onSDKReady() {
        Log.d(TAG, "SDK Ready");
        BuddySDK.UI.setViewAsFace(findViewById(R.id.view_face));

        //enable motors
        BuddySDK.USB.enableWheels(1, 1, new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException { Log.i(TAG, "Wheels enabled");}

            @Override
            public void onFailed(String s) throws RemoteException {  Log.e(TAG, "Wheels not enabled");}
        });
        BuddySDK.USB.enableYesMove(1, new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {Log.i(TAG, "Yes enabled");}

            @Override
            public void onFailed(String s) throws RemoteException {Log.e(TAG, "Wheels not enabled");}
        });
        BuddySDK.USB.enableNoMove(1, new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {Log.i(TAG, "No enabled");}

            @Override
            public void onFailed(String s) throws RemoteException {Log.e(TAG, "No not enabled");}
        });


        interpreter = new BehaviourInterpreter();

        // dir where the BI are
        dir = new File(_BI_FOLDER, "");
        // Get all comportemental in folder
        try {
            Files.list(Paths.get(_BI_FOLDER)).sorted().toArray();

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

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    //set
                    playAll = true;

                    // infinite while
                    while (true) {
                        // if ready to play
                        if (isreadingBI==false) {

                            // affect name of BI to read
                            biToPlay = BINames.get(BIidx);
                            // set
                            isreadingBI = true;
                            // read BI
                            readBI(biToPlay);
                            // Increment index
                            BIidx +=1;
                        }
                        else {
                            // wait
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } // end if ready to play

                        // if all of BIs read
                        if (BIidx >= BINames.size() || playAll == false)
                            break;

                    }   //end infinite while
                }
            });
            t.start();
        });


        findViewById(R.id.buttonStop).setOnClickListener(view -> {
            interpreter.Stop();
            // reset
            playAll = false;
        });

        /*** Play BI ***/
        findViewById(R.id.buttonPlay).setOnClickListener(view -> {
            String biName = editText.getText().toString();
            readBI(biName);
        });


        /*** Play all BI from ***/
        findViewById(R.id.buttonPlayAllFrom).setOnClickListener(view -> {
            String biName = BItoPlayFrom.getText().toString();

            // For each BI file
            for (int i=0; i<BINames.size(); i++)
            {
                // if not the Bi to start from
                if(!BINames.get(BIidx).contains(biName))
                {
                    BIidx+=1;
                }
                else // BI is the Bi to start from
                {
                    break;
                }
            }

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    //set
                    playAll = true;

                    // infinite while
                    while (true) {
                        // if ready to play
                        if (isreadingBI==false) {

                            // affect name of BI to read
                            biToPlay = BINames.get(BIidx);
                            // set
                            isreadingBI = true;
                            // read BI
                            readBI(biToPlay);
                            // Increment index
                            BIidx +=1;
                        }
                        else {
                            // wait
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } // end if ready to play

                        // if all of BIs read
                        if (BIidx >= BINames.size() || playAll == false)
                            break;

                    }   //end infinite while
                }
            });
            t.start();
        });


    }

    @Override
    public void onEvent(EventItem eventItem) {
    }





    private void readBI(String biName) {
        ImageView imageView =findViewById(R.id.imageView);
        PlayerView videoView=findViewById(R.id.videoView);
        String docPath = "storage/emulated/0/Android/data/com.bfr.sdkv2_biplayer/files/BI";

        String fileName = docPath + "/" + biName;
        //File source = new File(fileName.replace(".xml", "")+".xml");
        Log.i(TAG, "Runbehaviour Attempting " + biName);
        try {
            //BehaviourAlgorithmStorage storage = serializer.read(BehaviourAlgorithmStorage.class, source);
            File fileSave = new File(this.getExternalFilesDir (null) ,"BI/" + biName);
            BehaviourAlgorithmStorage storage = interpreter.Deserialize(this, fileSave);
//            if(storage==null) {
//                //BehaviourAlgorithm behaviourAlgo = interpreter.DeserializeAlgo(this, fileName);
//                //final boolean run = interpreter.Run(this, buddySDK, behaviourAlgo, this, this);
//                Log.d(TAG, "onReadBI storage null " + fileSave);
//            }
//            else {
//                Log.d(TAG, "onReadBI storage pas null");
                final boolean run = interpreter.Run(this, storage.getAlgorithm(), this, this, imageView, videoView);
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

        Log.i(TAG, "###########   BI FINISHED!!!");
        isreadingBI = false;
    }

    /**
    * @param aBehaviourInstruction
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


    /*** To copy files in the assets to the app Android folder
     *  (storage/emulated/0/Android/data/<name of the package>/files/<folder>)
     */

    private void copyAssets() {
    /*** copy a file */
    // get assets
        AssetManager assetManager = getAssets();

        // list of folders
        String[] folders = null;
        try {
            folders = assetManager.list("");
        } catch (IOException e) {
            Log.e("Assets", "Failed to get asset file list.", e);
        }

        // list of comportemental in folder
        String[] files = null;
        // for each folder
        if (folders != null) for (String foldername : folders) {
            Log.i("Assets", "Found folder: " + foldername  );
            // list of comportemental
            try {
                files = assetManager.list(foldername);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // for each file
            if (files != null) for (String filename : files) {
                // Files
                InputStream in = null;
                OutputStream out = null;
                //copy file
                try {
                    // open right asset
                    in = assetManager.open(foldername+"/"+filename);
                    // create folder if doesn't exist
                    File folder = new File(getExternalFilesDir(null), foldername);
                    if(!folder.exists())
                        folder.mkdirs();

                    // path in Android/data/<package>/
                    File outFile = new File(getExternalFilesDir(null), foldername+"/"+filename);
                    // destination file
                    out = new FileOutputStream(outFile);
                    // copy file
                    copyFile(in, out);
                    Log.i("Assets", "Copied " + foldername + "/" +filename );
                } catch(IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            }
        } // next folder
    }// end copyAssets


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
}