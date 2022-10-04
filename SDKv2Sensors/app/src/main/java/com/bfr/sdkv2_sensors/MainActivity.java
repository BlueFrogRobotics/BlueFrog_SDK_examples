package com.bfr.sdkv2_sensors;

import android.os.Bundle;
import android.os.RemoteException;
import android.provider.VoicemailContract;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

import android.os.Parcelable;
//import com.bfr.usbservice.IUsbAidlCbListner;
//import com.bfr.usbservice.MotorMotionData;
//import com.bfr.usbservice.BodySensorData;
//import com.bfr.usbservice.HeadSensorData;
//import com.bfr.usbservice.MotorHeadData;
//import com.bfr.usbservice.IUsbCommadRsp;


public class MainActivity extends BuddyActivity {
public Thread SensorsTh;
    TextView textTouchHeadCenter;//defining a text parameter so we show the text we want
    //USSensors Distance
    TextView SensorRightDist;//layout right USS sensor
    TextView SensorLeftDist ;//layout left USS sensor
    //USSensors Amplitude
    TextView USSensorLeftAmpl ;
    TextView USSensorRightAmpl;
    //BuddyHeadSensors layout state
    TextView TouchTopHead;
    TextView TouchLeftHead;
    TextView TouchRightHead ;
    //bodySensors layout state
    TextView TouchCenterBody;
    TextView TouchLeftBody;
    TextView TouchRightBody;
    //accelero & gyro
    TextView AccX;
    TextView AccY;
    TextView AccZ;
    TextView GyrX;
    TextView GyrY;
    TextView GyrZ;
    //TofSensors
   TextView frontCenter;
   TextView frontLeft;
   TextView frontRight;
   TextView back;
   //Microphone sensors
   TextView MicroAmbiant;
   TextView MicroTrigger;
   TextView MicroLocation;

    String TAG = "Messages : " ;
    //head sensors
    boolean touch_left_head = false;//Touching Head left
    boolean touch_top_head = false;//Touching Head top
    boolean touch_right_head = false;//Touching Head right
    //body sensors
    boolean touch_left_body = false;//Touching Head left
    boolean touch_center_body = false;//Touching Head top
    boolean touch_right_body = false;//Touching Head right
    //USSensors
    public int rightSensorDist=0;//distance USSensors
    public int leftSensorDist=0;//distance USSensors
    public int rightSensorAmpl=0;//amplitude USSensors
    public int leftSensorAmpl=0;
    //Gyro & Acc
    int ValGyrX ,ValGyrY , ValGyrZ;
    int ValAccX,ValAccY,ValAccZ;
    //TofSensors
    int TofCenter,TofRight ,TofLeft,TofBack ;
    int Object1, Object2;
    //Micro
    float TriggerScore = 0;
    float LocationAngle=0;
    float AmbiantNoise = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //external forward USSensors
        SensorRightDist= findViewById(R.id.USSensorRightDist);//link layout to value
        SensorLeftDist=findViewById(R.id.USSensorLeftDist);//link layout to value
        USSensorRightAmpl=findViewById(R.id.USSensorRightAmpl);
        USSensorLeftAmpl=findViewById(R.id.USSensorLeftAmpl);

        //Head Sensors
        TouchTopHead = findViewById(R.id.TouchTopHead);//top
        TouchRightHead=findViewById(R.id.TouchRightHead);//right
        TouchLeftHead = findViewById(R.id.TouchLeftHead);//left

        //Body sensors
        TouchCenterBody = findViewById(R.id.TouchCenterBody);//center
        TouchLeftBody = findViewById(R.id.TouchLeftBody);//Left
        TouchRightBody = findViewById(R.id.TouchRightBody);//right

        //ACC & Gyr
        AccX = findViewById(R.id.AccX);
        AccY = findViewById(R.id.AccY);
        AccZ = findViewById(R.id.AccZ);
        GyrX=findViewById(R.id.GyrX);
        GyrY=findViewById(R.id.GyrY);
        GyrZ=findViewById(R.id.GyrZ);

        //Microphone : Add in layout file textView
       //MicroAmbiant = findViewById(R.id.MicroAmbiant);
       //MicroTrigger = findViewById(R.id.MicroTrigger);
       //MicroLocation = findViewById(R.id.MicroLocation);

        //TofSensors
        frontCenter = findViewById(R.id.TofCenter);
        frontLeft   = findViewById(R.id.TofLeft);
        frontRight  = findViewById(R.id.TofRight);
        back        = findViewById(R.id.TofBack);
    }



    // called when the datas from the head sensor motor are received

    @Override
    public void onSDKReady() {
        Log.i(TAG, "onSDKReady: ");//launch sdk first

      //  after sdk launched we can set sensors
        BuddySDK.USB.enableSensorModule(true, new IUsbCommadRsp.Stub() {//called to enable sensors


            @Override
            public void onSuccess(String s) throws RemoteException {//in case of success
                Log.i(TAG, "Enabled Sensors");//show if it achieved
//buton enable onClickListner

                //USSensors launch
                BuddySDK.Sensors.USSensors().RightUS().getDistance();//launching the right sensor distance measurement
                BuddySDK.Sensors.USSensors().LeftUS().getDistance();//launching the left sensor distance measurement
                //BuddyHeadSensors Launch
                BuddySDK.Sensors.HeadTouchSensors().Top().isTouched();//Top head sensor
                BuddySDK.Sensors.HeadTouchSensors().Left().isTouched();//Left sensor touched
                BuddySDK.Sensors.HeadTouchSensors().Right().isTouched();//Right sensor touched

                //Body sensors
                BuddySDK.Sensors.BodyTouchSensors().Torso().isTouched();//center body sensor
                BuddySDK.Sensors.BodyTouchSensors().LeftShoulder().isTouched();//left body sensor
                BuddySDK.Sensors.BodyTouchSensors().RightShoulder().isTouched();//right body sensor

                //Accelero & giro Body
               BuddySDK.Sensors.BodyIMU().getAccX();
               BuddySDK.Sensors.BodyIMU().getAccY();
               BuddySDK.Sensors.BodyIMU().getAccZ();
               BuddySDK.Sensors.BodyIMU().getGyrX();
               BuddySDK.Sensors.BodyIMU().getGyrY();
               BuddySDK.Sensors.BodyIMU().getGyrZ();


                // Microphone functions
                BuddySDK.Sensors.Microphone().getAmbiantSound();//Sound volume in Decibel
                BuddySDK.Sensors.Microphone().getSoundLocalisation();//degree location of the sound
                BuddySDK.Sensors.Microphone().getTriggerScore();//OK Buddy more he recognizes it the highest score is


                //Tof Sensors
                BuddySDK.Sensors.TofSensors().FrontMiddle().getDistanceFirstObject();//Center
                BuddySDK.Sensors.TofSensors().FrontLeft().getDistanceFirstObject();
                BuddySDK.Sensors.TofSensors().FrontRight().getDistanceFirstObject();
                BuddySDK.Sensors.TofSensors().Back().getDistanceFirstObject();

                Thread SensorsTh = new Thread() {
                    public void run() {//function of the thread
                        while (true) {//do indefinitly

                            //USSensors working
                          rightSensorDist = BuddySDK.Sensors.USSensors().RightUS().getDistance();//distance calculated registered in a variable
                          SensorRightDist.setText("Right Sensor Distance = " + rightSensorDist);//show the distance in mm
                          leftSensorDist = BuddySDK.Sensors.USSensors().LeftUS().getDistance();//distance calculated registered in a variable
                          SensorLeftDist.setText("Left Sensor Distance = " + leftSensorDist);//show the distance in mm

                            //USSensors Amplitude working
                            rightSensorAmpl = BuddySDK.Sensors.USSensors().RightUS().getAmplitude();//Amplitude calculated registered in a variable
                            USSensorRightAmpl.setText("Right Sensor Amplitude = " + rightSensorAmpl);//show the Amplitude in mm
                            leftSensorAmpl = BuddySDK.Sensors.USSensors().LeftUS().getAmplitude();//Amplitude calculated registered in a variable
                            USSensorLeftAmpl.setText("Left Sensor Amplitude = " + leftSensorAmpl);//show the amplitude in mm

                            //Microphone Uncomment to see + add textView
                //      TriggerScore =   BuddySDK.Sensors.Microphone().getTriggerScore();//Sound volume in Decibel
                //         MicroTrigger.setText("Trigger = "+TriggerScore);//Score of pronounciation
                //      LocationAngle=  BuddySDK.Sensors.Microphone().getSoundLocalisation();//degree location of the sound
                //         MicroLocation.setText("Angle = "+LocationAngle);//location of the speaker
                //      AmbiantNoise =   BuddySDK.Sensors.Microphone().getAmbiantSound();//OK Buddy more he recognizes it the highest score is
                //         MicroAmbiant.setText("Noise : "+AmbiantNoise);

                            //Head Sensors touch working
                        touch_top_head= BuddySDK.Sensors.HeadTouchSensors().Top().isTouched();//boolean registered
                        TouchTopHead.setText("Head Top : "+touch_top_head);//layout of right sensor
                        touch_left_head = BuddySDK.Sensors.HeadTouchSensors().Left().isTouched();//boolean registered
                        TouchLeftHead.setText("Head Left : "+touch_left_head);//layout of left sensor
                        touch_right_head = BuddySDK.Sensors.HeadTouchSensors().Right().isTouched();//boolean registered
                        TouchRightHead.setText("Head Right : " +touch_right_head);//layout of right sensor

                            //Head Sensors InProximity //Not working
                       //     touch_top_head= BuddySDK.Sensors.HeadTouchSensors().Top().isInProximity();//boolean registered
                       //     TouchTopHead.setText("Head Top : "+touch_top_head);//layout of right sensor
                       //     touch_left_head = BuddySDK.Sensors.HeadTouchSensors().Left().isInProximity();//boolean registered
                       //     TouchLeftHead.setText("Head Left : "+touch_left_head);//layout of left sensor
                       //     touch_right_head = BuddySDK.Sensors.HeadTouchSensors().Right().isInProximity();//boolean registered
                       //     TouchRightHead.setText("Head Right : " +touch_right_head);//layout of right sensor


                            //Body sensors working
                        touch_center_body = BuddySDK.Sensors.BodyTouchSensors().Torso().isTouched();//center body sensor
                        TouchCenterBody.setText("Body Center : "+touch_center_body);//layout of the sensor's state
                        touch_left_body = BuddySDK.Sensors.BodyTouchSensors().LeftShoulder().isTouched();//left body sensor
                        TouchLeftBody.setText("Body left : "+touch_left_body);//layout of the sensor's state
                        touch_right_body = BuddySDK.Sensors.BodyTouchSensors().RightShoulder().isTouched();//tight body sensor
                        TouchRightBody.setText("Body right : "+touch_right_body);//layout of the sensor's state

                            //Body sensors inProximity 
                        //     touch_center_body = BuddySDK.Sensors.BodyTouchSensors().Torso().isInProximity();//center body sensor
                        //     TouchCenterBody.setText("Body Center : "+touch_center_body);//layout of the sensor's state
                        //     touch_left_body = BuddySDK.Sensors.BodyTouchSensors().LeftShoulder().isInProximity();//left body sensor
                        //     TouchLeftBody.setText("Body left : "+touch_left_body);//layout of the sensor's state
                        //     touch_right_body = BuddySDK.Sensors.BodyTouchSensors().RightShoulder().isInProximity();//tight body sensor
                        //     TouchRightBody.setText("Body right : "+touch_right_body);//layout of the sensor's state

                            //Gyr
                            ValGyrX=BuddySDK.Sensors.BodyIMU().getGyrX();
                            GyrX.setText("GyrX = "+ValGyrX);
                            ValGyrY=BuddySDK.Sensors.BodyIMU().getGyrY();
                            GyrY.setText("GyrY = "+ValGyrY);
                            ValGyrZ = BuddySDK.Sensors.BodyIMU().getGyrZ();
                            GyrZ.setText("GyrZ = "+ValGyrZ);
                            //Acc
                            ValAccX=BuddySDK.Sensors.BodyIMU().getAccX();
                            AccX.setText("AccX = "+ValAccX);
                            ValAccY=BuddySDK.Sensors.BodyIMU().getAccY();
                            AccY.setText("AccY = "+ValAccY);
                            ValAccZ = BuddySDK.Sensors.BodyIMU().getAccZ();
                            AccZ.setText("AccZ = "+ValAccZ);

                               //TofSensors Distance in mm
                            TofCenter = BuddySDK.Sensors.TofSensors().FrontMiddle().getDistanceFirstObject();//Center
                            frontCenter.setText("TofCenter Distance = "+TofCenter);// Distance in mm
                            TofRight = BuddySDK.Sensors.TofSensors().FrontRight().getDistanceFirstObject();
                            frontRight.setText("TofRight Distance = "+TofRight);//
                            TofLeft = BuddySDK.Sensors.TofSensors().FrontLeft().getDistanceFirstObject();//distance 3 objetc
                            frontLeft.setText("TofLeft Distance = "+TofLeft);//
                            TofBack = BuddySDK.Sensors.TofSensors().Back().getDistanceFirstObject();//distance 4 object
                            back.setText("TofBack Distance = "+TofBack);//
                               //2nd, 3rd, 4th object not detected
                               //nummber of objects non detected
                        }
                    }
                };
                SensorsTh.start();//start the thread
            }

            @Override
            public void onFailed(String s) throws RemoteException {//in case of failure
                Log.i(TAG, "Fail to Enable sensors :"+s);//if fail show why
            }
        });
        //linking the id of the buttons of Layout to buttons in the code


    }
    @Override
    public void onEvent(EventItem eventItem) {

    }
}