package com.bfr.sdkv2_stt;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bfr.buddy.speech.shared.ISTTCallback;
import com.bfr.buddy.speech.shared.STTResult;
import com.bfr.buddy.speech.shared.STTResultsData;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.buddysdk.services.speech.STTTask;

import java.util.Locale;

/***
 *  Code template to demonstrate the Speech-to-text (STT)
 *  functionalities with the SDK
 *  We use two different API for ASR (automatic speech recognition):
 *  - Vocon (based on compiled grammar files)
 *  - Google free speech (online API call)
 */

public class MainActivity extends BuddyActivity {
    String TAG = "SDK_STT";
    private final MainActivity mainActivity = this;
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    boolean listenContinously = true;
    private Locale locale = Locale.FRENCH;
    final private String googleSttEngineLabel = "Google";
    final private String cerenceFreeSpeechSttEngineLabel = "Cerence free speech";
    final private String cerenceLocalFcfLabel = "Cerence local .fcf";
    final private String[] sttEngineItems = new String[]{googleSttEngineLabel, cerenceFreeSpeechSttEngineLabel, cerenceLocalFcfLabel};
    private String sttEngine = googleSttEngineLabel;
    private STTTask sttTask = null;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (String permission : REQUESTED_PERMISSIONS) {
            checkSelfPermission(permission, PERMISSION_REQ_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    //This function is called when the SDK is ready
    public void onSDKReady() {
        ((CheckBox) findViewById(R.id.listenContinuouslyCheckBox)).setOnCheckedChangeListener((buttonView, isChecked) -> listenContinously(isChecked));

        Spinner sttEngineSpinner = findViewById(R.id.sttEngineSpinner);
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        Locale[] languageItems = new Locale[]{Locale.FRENCH, Locale.ENGLISH};
        ArrayAdapter<Locale> languagesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languageItems);
        languageSpinner.setAdapter(languagesAdapter);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                locale = languageItems[position];
                createSTTTask(sttEngineSpinner.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> sttEngineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, sttEngineItems);
        sttEngineSpinner.setAdapter(sttEngineAdapter);
        sttEngineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createSTTTask(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        findViewById(R.id.initListenBtn).setOnClickListener(v -> initialize());
        findViewById(R.id.startListenBtn).setOnClickListener(v -> listen());
        findViewById(R.id.pauseListenBtn).setOnClickListener(v -> pause());
        findViewById(R.id.stopListenBtn).setOnClickListener(v -> stop());
    }

    private void listenContinously(boolean pistenContinously) {
        listenContinously = pistenContinously;
    }

    private void createSTTTask(int sttEnginePosition) {
        sttEngine = sttEngineItems[sttEnginePosition];
        Log.i(TAG, "Creating " + sttEngine + " STT task");
        switch (sttEngine) {
            case googleSttEngineLabel:
                sttTask = BuddySDK.Speech.createGoogleSTTTask(locale);
                break;
            case cerenceFreeSpeechSttEngineLabel:
                sttTask = BuddySDK.Speech.createCerenceFreeSpeechTask(locale);
                break;
            case cerenceLocalFcfLabel:
                String fcfFilename;
                if (locale == Locale.ENGLISH)
                    fcfFilename = "audio_en.fcf";
                else
                    fcfFilename = "audio_fr.fcf";
                sttTask = BuddySDK.Speech.createCerenceTaskFromAssets(locale, fcfFilename, getAssets());
                break;
            default:
                throw new RuntimeException(sttEngine + " is not supported");
        }
        TextView sttState = findViewById(R.id.sttState);
        sttState.setText("");
    }

    private void initialize() {
        TextView sttState = findViewById(R.id.sttState);
        sttState.setText("Initialiizing...");
        sttTask.initialize();
        sttState.setText("Initialiized...");
    }

    private void listen() {
        TextView sttState = findViewById(R.id.sttState);
        sttState.setText("Listening...");
        sttTask.start(listenContinously,
                new LocaleSTTCallback(this, sttEngine, listenContinously, sttState));
    }

    private void pause() {
        TextView sttState = findViewById(R.id.sttState);
        sttState.setText("Pausing...");
        sttTask.pause();
        sttState.setText("Paused...");
    }

    private void stop() {
        TextView sttState = findViewById(R.id.sttState);
        sttState.setText("Stopping...");
        sttTask.stop();
        sttState.setText("");
    }


    class LocaleSTTCallback extends ISTTCallback.Stub {

        private Activity activity;
        private String engineName;
        private boolean isContinous;
        private TextView sttState;

        LocaleSTTCallback(Activity pActivity,
                          String pEngineName,
                          boolean pIsContinous,
                          TextView pSttState) {
            activity = pActivity;
            engineName = pEngineName;
            isContinous = pIsContinous;
            sttState = pSttState;
        }

        @Override
        public void onSuccess(STTResultsData iResults) throws RemoteException {
            Log.i(TAG, engineName + " heard something!!");

            runOnUiThread(() -> {
                if (!iResults.getResults().isEmpty()) {
                    STTResult result = iResults.getResults().get(0);
                    String rule = result.getRule();
                    String log = engineName + " utterance: " + result.getUtterance() + "\n" +
                            "Confidence: " + result.getConfidence();
                    if (!rule.isEmpty())
                        log += "\nRule: " + result.getRule();
                    Log.i(TAG, log);
                    Toast.makeText(mainActivity, log, Toast.LENGTH_LONG).show();
                    if (!isContinous) {
                        sttState.setText("");
                    }
                }
            });
        }

        @Override
        public void onError(String iError) throws RemoteException {
            Log.e(TAG, engineName + " error: " + iError);
        }
    }

    private boolean checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
            return false;
        }
        return true;
    }

    private boolean checkPermission(@NonNull int[] grantResults) {
        if (grantResults.length == REQUESTED_PERMISSIONS.length) {
            for (int grantResult : grantResults)
                if (grantResult != PackageManager.PERMISSION_GRANTED)
                    return false;
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_ID && !checkPermission(grantResults)) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            getApplicationContext(),
                            "Please accept the permissions",
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
    }

}