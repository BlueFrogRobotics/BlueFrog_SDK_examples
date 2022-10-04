package com.bfr.helloworld;

import android.os.Bundle;

import com.bfr.helloworld.R;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

public class MainActivity extends BuddyActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    // Will be called once all BFR services are initialized.
    @Override
    public void onSDKReady() {

        // transfer the touch information to BuddyCore in the background
        BuddySDK.UI.setViewAsFace(findViewById(R.id.view_face));

        // set the button click listener
        findViewById(R.id.buttonHello).setOnClickListener(view -> BuddySDK.Speech.startSpeaking("Hello"));
    }

}