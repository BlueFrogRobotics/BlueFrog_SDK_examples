package com.bfr.sdkv2_tts;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bfr.buddy.speech.shared.ITTSCallback;
import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

public class MainActivity extends BuddyActivity {

    final String  TAG = "SDKv2TTS";
    //initialize the different textView variable
    TextView mPositivityValue;
    EditText speed_text;  //initialization of Edit text to enter the values of speed, text and pitch
    EditText volume_text;
    EditText pitch_text;
    EditText to_say;
    Button setFR, setENG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize the different button to launch the different speech parameters of Buddy
        findViewById(R.id.speakBtn).setOnClickListener(v -> onButtonSpeak());
        findViewById(R.id.initReadspeakerBtn).setOnClickListener(v -> onButtonRead());
        findViewById(R.id.stopBtn).setOnClickListener(v -> onButtonStop());
        findViewById(R.id.speed).setOnClickListener(v -> onButtonSpeed());
        findViewById(R.id.volume).setOnClickListener(v -> onButtonVolume());
        findViewById(R.id.pitch).setOnClickListener(v -> onButtonPitch());
        findViewById(R.id.englishBtn).setOnClickListener(v -> onButtonEnglish());
        findViewById(R.id.frenchBtn).setOnClickListener(v -> onButtonFrench());
        //linking TextView variable to TextView widget
        speed_text= findViewById(R.id.speed_enter);
        volume_text= findViewById(R.id.volume_enter);
        pitch_text=findViewById(R.id.pitch_enter);
        to_say = findViewById(R.id.editTextToSay);

    }



    // Set English voice (Kate)
    private void onButtonEnglish() {
        BuddySDK.Speech.setSpeakerVoice("kate");

    }
    // Set French voice (Roxane)
    private void onButtonFrench() {
        BuddySDK.Speech.setSpeakerVoice("roxane");

    }


    // Button for choosing the pitch/tone of Buddy speech
    private void onButtonPitch() {
        Log.i(TAG, "Setting pitch to :" + Integer.parseInt(pitch_text.getText().toString()));
       BuddySDK.Speech.setSpeakerPitch(Integer.parseInt(pitch_text.getText().toString()));  // function for choosing the pitch/tone of Buddy speech

    }
    // Button for choosing the volume of Buddy speech

    private void onButtonVolume() {
        Log.i(TAG, "Setting Volume to :" + Integer.parseInt(volume_text.getText().toString()));
        BuddySDK.Speech.setSpeakerVolume(Integer.parseInt(volume_text.getText().toString()));  // function for choosing the volume of Buddy speech
    }
    // Button for choosing the speed of Buddy speech
    private void onButtonSpeed() {
        Log.i(TAG, "Setting Speed to :" + Integer.parseInt(speed_text.getText().toString()));
       BuddySDK.Speech.setSpeakerSpeed(Integer.parseInt(speed_text.getText().toString()));   // Function for choosing the speed of Buddy speech
    }


    // Button to stop Buddy speech
    private void onButtonStop() {
       BuddySDK.Speech.stopSpeaking(); // Function to stop Buddy speech
    }
    // Button for loading the speaker of Buddy
    private void onButtonRead() {
        BuddySDK.Speech.loadReadSpeaker();// Function to load the speaker
    }
  // Button for Buddy speaking
    private void onButtonSpeak() {
        // if ready to speak
        if(BuddySDK.Speech.isReadyToSpeak())
        {
            BuddySDK.Speech.startSpeaking(
//                    "Bonjour \\pause=1200\\ Je suis un robot qui parle",
                    to_say.getText().toString(),
                    new ITTSCallback.Stub() {
                        @Override
                        public void onSuccess(String s) throws RemoteException {
                            Log.i(TAG, "Message received : "+ s);
                        }

                        @Override
                        public void onPause() throws RemoteException {
                        }

                        @Override
                        public void onResume() throws RemoteException {
                        }

                        @Override
                        public void onError(String s) throws RemoteException {
                            Log.i(TAG, "Message received : "+ s);

                        }
                    });
        }
        else
        {
            Toast.makeText(getApplicationContext(), "TTS Not initialized", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    //This function is called when the SDK is ready
    public void onSDKReady() {
        BuddySDK.UI.setViewAsFace(findViewById(R.id.view_face));
        BuddySDK.UI.setFacialExpression(FacialExpression.NEUTRAL);
    }

    // Catches SPEAKING event.
    // Writes what has been spoken.
    @Override
    public void onEvent(EventItem iEvent) {

    }

}


