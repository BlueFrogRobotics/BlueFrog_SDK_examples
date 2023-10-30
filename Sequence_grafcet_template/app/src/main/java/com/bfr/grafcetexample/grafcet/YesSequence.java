package com.bfr.grafcetexample.grafcet;

import android.os.RemoteException;
import android.util.Log;

import com.bfr.buddy.usb.shared.IUsbCommadRsp;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.grafcetexample.utils.bfr_Grafcet;

public class YesSequence extends bfr_Grafcet {

    public YesSequence(String Name) {
        super(Name);
        this.grafcet_runnable = mysequence;

    }

    private YesSequence grafcet=this;

    // grafcet
    private double time_in_curr_step = 0;
    private boolean bypass = false;
    public static int step_num, previous_step;
    // interface
    public static boolean go = false;
    public static boolean wait_for_other_seq = false;

    //Requested speed
    private int moveSpeed = 60;
    //motor response
    private String MvtAck = "";

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
   - wait for the first sequence to be at a specific step
   - Buddy looks down
   - Buddy looks up
   - repeat
    */
    private Runnable mysequence = new Runnable() {
        @Override
        public void run()
        {

            // if step changed
            if( !(step_num == previous_step)) {
                // display current step
                Log.i(name, "Current step : " + step_num );
                // update
                previous_step = step_num;

                // start counting time in current step
                time_in_curr_step = System.currentTimeMillis();
                //reset bypass
                bypass = false;
            }
            else
            {
                // if time > 2s
                if ((System.currentTimeMillis()-time_in_curr_step > 5000) && step_num >0)
                {
                    // activate bypass
                    bypass = true;
                }
            }
            // which grafcet step?
            switch (step_num) {
                case 0: // Wait for checkbox
                    if(go)
                    {
                        // next step
                        step_num = 1;
                    }
                    break;

                case 1: // enable Yes
                    // Enable wheels
                    BuddySDK.USB.enableYesMove(1,  MotorResponse);

                    // next step
                    step_num = 2;
                    break;

                case 2: // Wait Yes Motor to be enabled
                    //if wheel is not disable
                    if(!BuddySDK.Actuators.getYesStatus().toUpperCase().contains("DISABLE"))
                    {
                        // next step
                        step_num = 20;
                    }
                    break;

                case 20: // set flag to wait for other sequence
                    // set flag to wait for other sequence
                    wait_for_other_seq = true;
                    // next step
                    step_num = 3;
                    break;


                case 3: // sync with sequence 1
                    //wait for reset of flag by the other sequence
                    if(!wait_for_other_seq)
                    {
                        // next step
                        step_num = 4;
                    }
                    break;

                case 4: // Look up
                    MvtAck="";
                    BuddySDK.USB.buddySayYes(moveSpeed, 45, MotorResponse);
                        // next step
                        step_num = 5;

                    break;


                case 5: //
                   if(MvtAck.toUpperCase().contains("OK"))
                   {
                       // next step
                       step_num = 6;
                   }
                    if(MvtAck.toUpperCase().contains("TIMEOUT"))
                    {
                        Log.i(name, "YES:" + "Timout waiting for OK -> Retry");
                        // next step
                        step_num = 4;
                    }
                    break;


                case 6: // Wait for end of movement
                    if(MvtAck.toUpperCase().contains("_FINISHED"))
                    {
                        // next step
                        step_num = 7;
                    }
                    break;


                case 7: // Move the other way
                    MvtAck="";
                    BuddySDK.USB.buddySayYes(moveSpeed, 0, MotorResponse);

                    // next step
                    step_num = 8;
                    break;

                case 8: // Wait for Acknowledge of movement
                    if(MvtAck.toUpperCase().contains("OK"))
                    {
                        // next step
                        step_num = 9;
                    }
                    if(MvtAck.toUpperCase().contains("TIMEOUT"))
                    {
                        Log.i(name, "YES:" + "Timout waiting for OK -> Retry");
                        // next step
                        step_num = 7;
                    }
                    break;

                case 9: // Wait for Acknowledge of movement
                    if(MvtAck.toUpperCase().contains("FINISHED"))
                    {
                        // next step
                        step_num = 10;
                    }
                    break;

                case 10: // reset flag of other sequence
                        MoveSequence.wait_for_other_seq=false;
                        // next step
                        step_num = 11;
                    break;
                    

                case 11: // Check checkbox status
                    if(go) {
                        // next step
                        step_num = 2;
                    }
                    else // checkbox unchecked
                    {
                        // next_step
                        step_num = 0;
                    }
                    break;


                default:
                    //
                    step_num = 0;
            } //*end switch

        } // end of run

    }; // end of Runnable mysequence definition

}
