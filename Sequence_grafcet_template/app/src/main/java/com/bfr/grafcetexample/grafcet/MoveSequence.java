package com.bfr.grafcetexample.grafcet;

import android.os.RemoteException;
import android.util.Log;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.grafcetexample.utils.bfr_Grafcet;

public class MoveSequence extends bfr_Grafcet {


    // grafcet
    private double time_in_curr_step = 0;
    private boolean timeout = false;
    public static int step_num, previous_step;
    // interface
    public static boolean go = false;
    public static boolean wait_for_other_seq = false;

    //Requested speed
    private int moveSpeed = 90;
    //motor response
    private String MvtAck = "";

    public MoveSequence(String Name) {
        super(Name);
        this.grafcet_runnable = mysequence;

    }


    /** Motor Response */
    private IUsbCommadRsp MotorResponse = new IUsbCommadRsp.Stub() {
        @Override
        public void onSuccess(String s) throws RemoteException {
            Log.i(name, "Motor Response : " + s);
            // save motor response
            MvtAck = s;
        }

        @Override
        public void onFailed(String s) throws RemoteException {
            // log the error
            Log.e(name, "Motor movement failed : " + s);
        }
    } //end of stub
            ;

    // Define the sequence/grafcet to be executed
   /* This provides a template for a grafcet.
   The sequence is as follows:
   - check the checkbox
   - Rotate to the Left
   - Rotate the other way
   - Wait for the Sequence 2
   - If the check box is unchecked then stop
   - if not, repeat
    */
    private Runnable mysequence = new Runnable() {
        @Override
        public void run()
        {

            try {

                // if step changed
                if (!(step_num == previous_step)) {
                    // display current step
                    Log.i(name, "Current step : " + step_num);
                    // update
                    previous_step = step_num;

                    // start counting time in current step
                    time_in_curr_step = System.currentTimeMillis();
                    //reset bypass
                    timeout = false;
                } else {
                    // if time > 5s
                    if ((System.currentTimeMillis() - time_in_curr_step > 5000) && step_num > 0) {
                        // activate bypass
                        timeout = true;
                    }
                }
                // which grafcet step?
                switch (step_num) {
                    case 0: // Wait for checkbox
                        if (go) {
                            // next step
                            step_num = 1;
                        }
                        break;


                    case 1: // enable wheels
                        // Enable wheels
                        BuddySDK.USB.enableWheels(1, 1, MotorResponse); // end Enablewheels
                        // next step
                        step_num = 2;
                        break;

                    case 2: // Wait Left wheel to be enabled
                        if (!BuddySDK.Actuators.getLeftWheelStatus().toUpperCase().contains("DISABLE")) {
                            // next step
                            step_num = 3;
                        }
                        break;

                    case 3: // Wait Right wheel to be enabled
                        //if wheel is not disable
                        if (!BuddySDK.Actuators.getRightWheelStatus().toUpperCase().contains("DISABLE")) {
                            // next step
                            step_num = 4;
                        }
                        break;


                    case 4: // Rotate to the left
                        MvtAck = "";
                        BuddySDK.USB.rotateBuddy(moveSpeed, 90, MotorResponse);
                        // next step
                        step_num = 5;
                        break;


                    case 5: // Wait for Acknowledge of movement
                        if (MvtAck.toUpperCase().contains("OK")) {
                            Log.i(name, "Rotate Left:" + MvtAck);
                            // next step
                            step_num = 6;
                        }
                        if (MvtAck.toUpperCase().contains("TIMEOUT")) {
                            Log.i(name, "Rotate Left:" + "Timout waiting for OK -> Retry");
                            // next step
                            step_num = 4;
                        }
                        break;


                    case 6: // Wait for end of movement
                        if (MvtAck.toUpperCase().contains("_FINISHED") || timeout) {
                            Log.i(name, "Rotate Left:" + MvtAck);

                            // next step
                            step_num = 7;
                        }
                        if (timeout) {
                            Log.i(name, "Rotate left:" + "Timout waiting for end of mvt");
                            // next step
                            step_num = 7;
                        }
                        break;


                    case 7: // Move the other way
                        Thread.sleep(500);
                        MvtAck = "";

                        BuddySDK.USB.rotateBuddy(moveSpeed, -90, MotorResponse);
                        // next step
                        step_num = 8;
                        break;

                    case 8: // Wait for Acknowledge of movement
                        if (MvtAck.toUpperCase().contains("OK")) {
                            Log.i(name, "Rotate Right:" + MvtAck);

                            // next step
                            step_num = 9;
                        }
                        if (MvtAck.toUpperCase().contains("TIMEOUT")) {
                            Log.i(name, "Rotate Right:" + "Timout waiting for OK -> Retry");
                            // next step
                            step_num = 7;
                        }
                        break;


                    case 9: // Wait for end of movement
                        if (MvtAck.toUpperCase().contains("_FINISHED") || timeout) {
                            Log.i(name, "Rotate Right:" + MvtAck);

                            // next step
                            step_num = 10;
                        }
                        if (timeout) {
                            Log.i(name, "Rotate Right:" + "Timout waiting for end of mvt");
                            // next step
                            step_num = 10;
                        }
                        break;



                    case 10: // reset wait of other sequence
                        YesSequence.wait_for_other_seq=false;
                        // next step
                        step_num = 11;
                        break;


                    case 11: // set wait of other sequence
                        wait_for_other_seq=true;
                        // next step
                        step_num = 12;
                        break;

                    case 12: // wait of other sequence
                        if(!wait_for_other_seq)
                            // next step
                            step_num = 13;
                        break;


                    case 13: // Check checkbox status
                        if (go) {
                            // next step
                            step_num = 4;
                        } else // checkbox unchecked
                        {
                            // next_step
                            step_num = 0;
                        }
                        break;


                    default:
                        //
                        step_num = 0;
                } //*end switch

            }
            catch (Exception e)
            {
                Log.e(name, e.toString());
            }
        } // end of run

    }; // end of Runnable mysequence definition

}
