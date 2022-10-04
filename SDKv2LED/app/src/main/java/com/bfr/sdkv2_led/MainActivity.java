package com.bfr.sdkv2_led;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddy.utils.events.EventState;
import com.bfr.buddy.utils.events.EventType;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

public class MainActivity extends BuddyActivity {
    EditText pattern;
    EditText period;
    EditText step;
    TextView mText1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Different button to launch the different functions with Buddy's leds
        findViewById(R.id.button_led2).setOnClickListener(v -> onButtonled2());
        findViewById(R.id.button_led3).setOnClickListener(v -> onButtonled3());
        findViewById(R.id.button_led4).setOnClickListener(v -> onButtonled4());//The button allowing Buddy to do a "yes" move
        findViewById(R.id.button_led5).setOnClickListener(v -> onButtonled5());
        findViewById(R.id.button_led6).setOnClickListener(v -> onButtonled6());
        findViewById(R.id.button_led7).setOnClickListener(v -> onButtonled7());
        findViewById(R.id.button_led8).setOnClickListener(v -> onButtonled8());
        findViewById(R.id.button_led9).setOnClickListener(v -> onButtonled9());
        findViewById(R.id.button_led10).setOnClickListener(v -> onButtonled10());
        findViewById(R.id.button_led11).setOnClickListener(v -> onButtonled11());
        mText1 = (TextView) findViewById(R.id.textView1);
        pattern = findViewById(R.id.pattern);
        period = findViewById(R.id.period);
        step = findViewById(R.id.step);

    }
    //Button to switch on continuously all leds with the color we choose

    private void onButtonled11() {
        BuddySDK.USB.updateAllLed("#C3C435",new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });
    }

    //Button to switch off LEDs in left shoulder
    private void onButtonled10() {
        BuddySDK.USB.stopLeftShoulderLed(new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });
    }

    //Button to switch off LEDs in right shoulder
    private void onButtonled9() {
        BuddySDK.USB.stopRightShoulderLed(new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });
    }
//Button to switch off LEDs in the heart
    private void onButtonled8() {

        BuddySDK.USB.stopHeartLed(new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });

    }
//Button to switch off all the Leds
    private void onButtonled7() {

        BuddySDK.USB.stopAllLed(new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });
    }
    //Button to switch on the part of Buddy we want periodically
    private void onButtonled6() {
        BuddySDK.USB.blinkLed(2,"#C3C435",1, new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });

    }

    //Button to blink all Leds with the color we choose.
    private void onButtonled5() {
        BuddySDK.USB.blinkAllLed("#00FF00",1, new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });

    }
    //Button to switch on gradually all leds periodically with the color and period we choose

    private void onButtonled4() {
        BuddySDK.USB.fadeAllLed("#FE2EC8",10, new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });


    }
    //Button to switch on continuously the Leds we want with the color we choose.

    private void onButtonled3() {
        BuddySDK.USB.updateLedColor(2,"#C43535", new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });

    }
    //Button to blink all Leds with the color we choose.

    private void onButtonled2() {
        BuddySDK.USB.updateAllLedWithPattern("#0037FF",
                Integer.parseInt(pattern.getText().toString()), // pattern number
                Integer.parseInt(period.getText().toString()), // Period
                Integer.parseInt(step.getText().toString()), // number of steps
                new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                Log.i("coucou", "Message received : "+ s);

            }

            @Override
            public void onFailed(String s) throws RemoteException {

            }
        });

    }




    @Override
    //This function is called when the SDK is ready
    public void onSDKReady() {


    }

    // Catches SPEAKING event.
    // Writes what has been spoken.
    @Override
    public void onEvent(EventItem iEvent) {
        if (iEvent.getEventType() == EventType.SPEAKING) {
            if (iEvent.getEventState() == EventState.START)
                mText1.setText("I am speaking\n\nText: " + iEvent.getMessage());
            else if (iEvent.getEventState() == EventState.END)
                mText1.setText("");
        }
    }



}