package com.bfr.sdkv2_head;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

public class MainActivity extends BuddyActivity implements View.OnClickListener {

    private final static String TAG = "Tuto Yes/No";

    //Button for each move
    Button startGTYes;
    Button stopGTYes;
    Button startYes;
    Button stopYes;
    Button startGTNo;
    Button stopGTNo;
    Button startNo;
    Button stopNo;

    //Edit Text for the input of speed, distance and angle
    EditText speedGTYes;
    EditText angleGTYes;
    EditText speedYes;
    EditText speedGTNo;
    EditText angleGTNo;
    EditText speedNo;

    //TextView for the Yes/No status
    TextView yesStatus;
    TextView noStatus;
    TextView yesPose;
    TextView noPose;

    //Switch enable yes/no motors
    Switch enableYesMotor;
    Switch enableNoMotor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Button linking
        startGTYes = (Button) findViewById(R.id.BstartGTYA);
        stopGTYes = (Button) findViewById(R.id.BstopGTYA);
        startYes = (Button) findViewById(R.id.BstartYM);
        stopYes = (Button) findViewById(R.id.BstopYM);
        startGTNo = (Button) findViewById(R.id.BstartGTNO);
        stopGTNo = (Button) findViewById(R.id.BstopGTNO);
        startNo = (Button) findViewById(R.id.BstartNM);
        stopNo = (Button) findViewById(R.id.BstopNM);

        startGTYes.setOnClickListener(this);
        stopGTYes.setOnClickListener(this);
        startYes.setOnClickListener(this);
        stopYes.setOnClickListener(this);
        startGTNo.setOnClickListener(this);
        stopGTNo.setOnClickListener(this);
        startNo.setOnClickListener(this);
        stopNo.setOnClickListener(this);

        //EditText linking
        speedGTYes = (EditText) findViewById(R.id.EDchooseSpeedGTYA);
        angleGTYes = (EditText) findViewById(R.id.EDchooseAngleGTYA);
        speedYes = (EditText) findViewById(R.id.EDchooseSpeedYM);
        speedGTNo = (EditText) findViewById(R.id.EDchooseSpeedGTNA);
        angleGTNo = (EditText) findViewById(R.id.EDchooseAngleGTNA);
        speedNo = (EditText) findViewById(R.id.EDchooseSpeedNM);

        //Text view linking
        yesStatus = (TextView) findViewById(R.id.TVenableStatusYES);
        noStatus = (TextView) findViewById(R.id.TVenableStatusNO);
        yesPose = (TextView) findViewById(R.id.TVYesPosition);
        noPose = (TextView) findViewById(R.id.TVNoPosition);

        //Switch linking
        enableYesMotor = (Switch) findViewById(R.id.SWenableYES);
        enableNoMotor = (Switch) findViewById(R.id.SWenableNO);

        enableYesMotor.setOnClickListener(this);
        enableNoMotor.setOnClickListener(this);

    }


    //button for yes move
    private void OnButtonYes(float speed, float angle) {

        BuddySDK.USB.buddySayYes(speed, angle, new IUsbCommadRsp.Stub() { //function with speed, angle and stub callback
            @Override
            public void onSuccess(String success) {
                if (success.equals("YES_MOVE_FINISHED")) { //if function executed well YES_MOVE_FINISHED is sent
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"Yes move finished", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                }
            }
            @Override public void onFailed(String error) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

    }

    //button for no move
    private void OnButtonNo(float speed, float angle) {

        BuddySDK.USB.buddySayNo(speed, angle, new IUsbCommadRsp.Stub() { //function with speed, angle and stub callback
            @Override
            public void onSuccess(String success) {
                if (success.equals("NO_MOVE_FINISHED")) {  //if function executed well NO_MOVE_FINISHED is sent
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"No move finished", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                }
            }
            @Override public void onFailed(String error) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

    }

    //button for Straight yes move
    private void OnButtonStraightYes(float speed) {
     BuddySDK.USB.buddySayYesStraight(speed, new IUsbCommadRsp.Stub() { //function with speed, angle and stub callback
         @Override
         public void onSuccess(String s) throws RemoteException {
             if (s.equals("YES_MOVE_FINISHED")) {  //if function executed well YES_MOVE_FINISHED is sent
                 new Thread(new Runnable() {
                     @Override
                     public void run() {
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 Toast.makeText(MainActivity.this,"Success", Toast.LENGTH_SHORT).show();
                             }
                         });
                     }
                 }).start();
             }

         }

         @Override
         public void onFailed(String s) throws RemoteException {
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                         }
                     });
                 }
             }).start();
         }
     });

    }

    //button for no straight move move
    private void OnButtonStraightNo(float speed) {

        BuddySDK.USB.buddySayNoStraight(speed, new IUsbCommadRsp.Stub() { //function with speed, angle and stub callback
            @Override
            public void onSuccess(String s) throws RemoteException {
                if (s.equals("NO_MOVE_FINISHED")) {  //if function executed well NO_MOVE_FINISHED is sent
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"Success", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).start();
                }

            }

            @Override
            public void onFailed(String s) throws RemoteException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });


    }

    //button for stopping buddySayYesStraight
    private void OnButtonStopYes() {

        BuddySDK.USB.buddyStopYesMove(new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"Yes motor stopped", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onFailed(String s) throws RemoteException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });


    }

    //button for stopping buddySayNoStraight
    private void OnButtonStopNo() {
        BuddySDK.USB.buddyStopNoMove(new IUsbCommadRsp.Stub() {
            @Override
            public void onSuccess(String s) throws RemoteException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"No motor stoppes", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }



            @Override
            public void onFailed(String s) throws RemoteException {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();
            }
        });

    }

    //Enable Yes Motor
    //State : (0) disable, (1) enable
    private void EnableNoMotor(int state){
        Log.i(TAG,"State : " + state);
        // The motor for "no" move is enable
        BuddySDK.USB.enableNoMove(state, new IUsbCommadRsp.Stub() {
            @Override
            //if the motor succeeded to be enabled,we display motor is enabled
            public void onSuccess(String success) throws RemoteException {
                Log.i("TAG", "Motor Enabled");
            }

            @Override
            //if the motor did not succeed to be enabled,we display motor failed to be enabled
            public void onFailed(String error) throws RemoteException {
                Log.i("Motor No", "No motor Enabled Failed");
            }
        });
    }

    //Enable Yes Motor
    //State : (0) disable, (1) enable
    private void EnableYesMotor(int state){
        Log.i(TAG,"State : " + state);
        // The motor for "no" move is enable
        BuddySDK.USB.enableYesMove(state, new IUsbCommadRsp.Stub() {
            @Override
            //if the motor succeeded to be enabled,we display motor is enabled
            public void onSuccess(String success) throws RemoteException {
                Log.i("TAG", "Motor Enabled");
            }

            @Override
            //if the motor did not succeed to be enabled,we display motor failed to be enabled
            public void onFailed(String error) throws RemoteException {
                Log.i("Motor No", "No motor Enabled Failed");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        EnableYesMotor(0);
        EnableNoMotor(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EnableYesMotor(0);
        EnableNoMotor(0);
    }

    //This function is called when the SDK is ready
    @Override
    public void onSDKReady() {
        //Thread to update the Yes status
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            yesStatus.setText("Status : " + BuddySDK.Actuators.getYesStatus());
                            yesPose.setText("YES Motor position :  " + BuddySDK.Actuators.getYesPosition());
                            noStatus.setText("Status : " + BuddySDK.Actuators.getNoStatus());
                            noPose.setText("NO Motor position :  " + BuddySDK.Actuators.getNoPosition());
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    public void onEvent(EventItem iEvent) {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //Enable Yes motor
            case R.id.SWenableYES:
                EnableYesMotor(((Switch) v).isChecked() ? 1 : 0);
                Log.i(TAG,"Switch enable yes");
                break;

            //Enable No motor
            case R.id.SWenableNO:
                EnableNoMotor(((Switch) v).isChecked() ? 1 : 0);
                Log.i(TAG,"Switch enable no");
                break;

            //Go to an angle with the Yes motor
            case R.id.BstartGTYA:
                //Check if Yes motor is enable
                if(!BuddySDK.Actuators.getYesStatus().toUpperCase().contains("DISABLE")) {
                    //Check input of speed and angle
                    if (!speedGTYes.getText().toString().equals("") && !angleGTYes.getText().toString().equals("") &&
                            isInputValid(speedGTYes.getText().toString()) && isInputValid(angleGTYes.getText().toString())) {
                        OnButtonYes(Float.parseFloat(speedGTYes.getText().toString()), Float.parseFloat(angleGTYes.getText().toString()));
                    } else {
                        Log.i(TAG, "Speed or angle input error");
                        Toast.makeText(MainActivity.this, "Speed or angle input error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.i(TAG, "Yes motor disable");
                    Toast.makeText(MainActivity.this, "Enable Yes motor before", Toast.LENGTH_SHORT).show();
                }
                break;

            //Stop Yes motor
            case R.id.BstopGTYA:
                OnButtonStopYes();
                break;

            //Straight Yes
            case R.id.BstartYM:
                //Check if Yes motor is enable
                if(!BuddySDK.Actuators.getYesStatus().toUpperCase().contains("DISABLE")) {
                    //Check input of speed
                    if (!speedYes.getText().toString().equals("") && isInputValid(speedYes.getText().toString())) {
                        OnButtonStraightYes(Float.parseFloat(speedYes.getText().toString()));
                    } else {
                        Log.i(TAG, "Speed input error");
                        Toast.makeText(MainActivity.this, "Speed input error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.i(TAG, "Yes motor disable");
                    Toast.makeText(MainActivity.this, "Enable Yes motor before", Toast.LENGTH_SHORT).show();
                }
                break;

            //Stop Yes motor
            case R.id.BstopYM:
                OnButtonStopYes();
                break;

            //Go to an angle with the No motor
            case R.id.BstartGTNO:
                //Check if No motor is enable
                if(!BuddySDK.Actuators.getNoStatus().toUpperCase().contains("DISABLE")) {
                    //Check input of speed and angle
                    if (!speedGTNo.getText().toString().equals("") && !angleGTNo.getText().toString().equals("") &&
                            isInputValid(speedGTNo.getText().toString()) && isInputValid(angleGTNo.getText().toString())) {
                        OnButtonNo(Float.parseFloat(speedGTNo.getText().toString()), Float.parseFloat(angleGTNo.getText().toString()));
                    } else {
                        Log.i(TAG, "Speed or angle input error");
                        Toast.makeText(MainActivity.this, "Speed or angle input error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.i(TAG, "No motor disable");
                    Toast.makeText(MainActivity.this, "Enable No motor before", Toast.LENGTH_SHORT).show();
                }
                break;

            //Stop No motor
            case R.id.BstopGTNO:
                OnButtonStopNo();
                break;

            //Straight No
            case R.id.BstartNM:
                //Check if No motor is enable
                if(!BuddySDK.Actuators.getNoStatus().toUpperCase().contains("DISABLE")) {
                    //Check input of speed and angle
                    if (!speedNo.getText().toString().equals("") && isInputValid(speedNo.getText().toString())) {
                        OnButtonStraightNo(Float.parseFloat(speedNo.getText().toString()));
                    } else {
                        Log.i(TAG, "Speed input error");
                        Toast.makeText(MainActivity.this, "Speed input error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Log.i(TAG, "No motor disable");
                    Toast.makeText(MainActivity.this, "Enable No motor before", Toast.LENGTH_SHORT).show();
                }
                break;

            //Stop No motor
            case R.id.BstopNM:
                OnButtonStopNo();
                break;
        }
    }

    //Check if the Edit Text is a valid float input
    public boolean isInputValid(String inputText){
        try{
            float val = Float.parseFloat(inputText);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }
}