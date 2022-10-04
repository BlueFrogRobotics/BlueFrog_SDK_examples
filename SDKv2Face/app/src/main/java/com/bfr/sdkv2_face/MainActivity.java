package com.bfr.sdkv2_face;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bfr.buddy.ui.shared.FaceTouchData;
import com.bfr.buddy.ui.shared.FacialEvent;
import com.bfr.buddy.ui.shared.FacialExpression;
import com.bfr.buddy.ui.shared.GazePosition;
import com.bfr.buddy.ui.shared.IUIFaceAnimationCallback;
import com.bfr.buddy.ui.shared.IUIFaceTouchCallback;
import com.bfr.buddy.ui.shared.LabialExpression;
import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddy.utils.events.EventState;
import com.bfr.buddy.utils.events.EventType;
import com.bfr.buddy.utils.values.FloatingWidgetVisibility;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

public class MainActivity extends BuddyActivity {

    final String TAG = "SDK_Face";
 //initialize the different textView variable
    TextView mPositivityValue;
    TextView mEnergyValue;

    CheckBox movingLipsCBx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize the different button to launch the different actions of Buddy
        findViewById(R.id.happyBtn).setOnClickListener(v -> onButtonHappy());
        findViewById(R.id.grumpyBtn).setOnClickListener(v -> onButtongrumpy());
        findViewById(R.id.neutralBtn).setOnClickListener(v -> onButtonneutral());
        findViewById(R.id.face_event).setOnClickListener(v -> onButtonfaceevent());
        findViewById(R.id.add_touch).setOnClickListener(v -> onButtonTouch());
        movingLipsCBx = findViewById(R.id.movingLipsChckBx);

        // Initialize the different seek bar
        SeekBar lEseek = findViewById(R.id.energySeekBar);
        SeekBar lPseek  = findViewById(R.id.positivitySeekBar);
        //linking Text Views varaible and text views widget in layout xml file
        mPositivityValue = findViewById(R.id.positivityValue);
        mEnergyValue = findViewById(R.id.energyValue);

        //When we move the cursor Buddy's face will be more or less energetic
        lEseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                BuddySDK.UI.setFaceEnergy(i/100F);
                mEnergyValue.setText(String.valueOf(i/100F));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //When we move the cursor Buddy's face will be more or less positive

        lPseek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        BuddySDK.UI.setFacePositivity(i/100F);
                        mPositivityValue.setText(String.valueOf(i/100F));

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        // Checkbox for LipSync
        movingLipsCBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // if checked
                if(b)
                   BuddySDK.UI.playFacialEvent(FacialEvent.FALL_ASLEEP);
                else // unchecked
                    BuddySDK.UI.playFacialEvent(FacialEvent.AWAKE);
            }
        });
    }

    private void onButtonTouch() {

        BuddySDK.UI.lookAt(GazePosition.TOP_RIGHT,true);
    }


    //Button to launch playFacialEvent
    private void onButtonfaceevent() {
        BuddySDK.UI.playFacialEvent(FacialEvent.WHAT);
    }


    //Button to have a  neutral face
    private void onButtonneutral() {
        BuddySDK.UI.setMood(FacialExpression.NEUTRAL);
    }
    //Button to have a grumpy face
    private void onButtongrumpy() {
        BuddySDK.UI.setMood(FacialExpression.GRUMPY);
    }
   //Button to have a happy face
    private void onButtonHappy() {
        BuddySDK.UI.setMood(FacialExpression.HAPPY);
    }

    @Override
    //This function is called when the SDK is ready
    public void onSDKReady() {
  BuddySDK.UI.setViewAsFace(findViewById(R.id.view_face));

        BuddySDK.UI.addFaceTouchListener(new IUIFaceTouchCallback.Stub() {
            @Override
            public void onTouch(FaceTouchData faceTouchData) throws RemoteException {
                switch(faceTouchData.getArea())
                {
                    case FACE:
                        break;
                    case RIGHT_EYE:
                        Log.i("FaceEvent", "Right Eye touched");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Right Eye touched", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case LEFT_EYE:
                        Log.i("FaceEvent", "Left Eye touched");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Left Eye touched", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case MOUTH:
                        Log.i("FaceEvent", "Mouth Eye touched");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Mouth touched", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRelease(FaceTouchData faceTouchData) throws RemoteException {

            }


        });


}

    // Catches SPEAKING event.
    // Writes what has been spoken.
    @Override
    public void onEvent(EventItem iEvent) {
        if (iEvent.getEventType() == EventType.LEFT_EYE_TOUCHED && iEvent.getEventState()==EventState.START) {

          // BuddySDK.UI.setLabialExpression(LabialExpression.SPEAK_ANGRY);
}
    }



}