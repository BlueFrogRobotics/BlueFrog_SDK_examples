package com.bfr.grafcetexample.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class bfr_Grafcet
{
    private final String TAG = "BFR_GRAFCET";

    // Default Params for scheduler
    // initial delay before start
    private final long INITIALDELAY = 0;
    // time period of execution
    private final long TIME_PERIOD = 10;

    // Scheduler for grafcet
    private ScheduledExecutorService myscheduler ;

    // Name
    public String name = "";

    // state
    public boolean started = false;
    public boolean changedStep = false;

    // Runnable containing the grafcet sequence
    public Runnable grafcet_runnable;

    // *********************  Constructor overloads
    public bfr_Grafcet(Runnable runnable, String Name)
    {
        name = Name;
        grafcet_runnable = runnable;
    }

    // without runnable
    public bfr_Grafcet(String Name)
    {
        name = Name;
    }

    //********************* Starting

    // start grafcet with default parameters
    public void start()
    {
        //if not started yet
        if (myscheduler == null || myscheduler.isShutdown())
        {
            //init thread
            myscheduler = Executors.newScheduledThreadPool(1);
            // start scheduled task
            myscheduler.scheduleWithFixedDelay(grafcet_runnable, INITIALDELAY, TIME_PERIOD, TimeUnit.MILLISECONDS);
        }
        else // grafcet already started
        {
            //Log.i(TAG, "Grafcet "+ name+ " already started. Skipping \n" + Log.getStackTraceString(new Exception()));
            Log.i(TAG, "\n********* GRAFCET **********\nGrafcet "+ name+ " already started. Skipping \n" );
        }

//        Thread displayCurrStep = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if( !(step_num == previous_step)) {
//                    // display current step
//                    Log.i(name, "current step: " + step_num + "  ");
//                    // update
//                    previous_step = step_num;
//                    changedStep = true;
//                } // end if step = same
//            }
//        });
//        displayCurrStep.start();

        // update state
        started = true;


        // Monitoring the step number
//        steNumber.setListener(new listenedVar.ChangeListener() {
//            @Override
//            public void onChange() {
//                Log.i(name, "Current step = " + steNumber.get());
//            } // end onchange
//        }); // end set listener

    } // end start

    // start grafcet and specify params (time period, ...)
    public void start(long mtime_period)
    {
        //if not started yet
        if (myscheduler == null || myscheduler.isShutdown())
        {
            Log.i(TAG, "\n********* GRAFCET **********\nGrafcet " + name + " starting \n");
            //init thread
            myscheduler = Executors.newScheduledThreadPool(1);
            // start scheduled task
            myscheduler.scheduleWithFixedDelay(grafcet_runnable, INITIALDELAY, mtime_period, TimeUnit.MILLISECONDS);
        }
        else // grafcet already started
        {
            Log.i(TAG, "\n********* GRAFCET **********\nGrafcet "+ name+ " already started. Skipping \n" );
        }

        // update state
        started = true;
    }


    public void start(long mtime_period, long m_init_delay )
    {
        //if not started yet
        if (myscheduler.isShutdown())
        {
            Log.i(TAG, "\n********* GRAFCET **********\nGrafcet " + name + " starting \n");
            // log the stack strace in verbose mode
            Log.v(TAG, "Grafcet " + name + " called at " + Log.getStackTraceString(new Exception()));
            //init thread
            myscheduler = Executors.newScheduledThreadPool(1);
            // start scheduled task
            myscheduler.scheduleWithFixedDelay(grafcet_runnable, m_init_delay, mtime_period, TimeUnit.MILLISECONDS);
        }
        else // grafcet already started
        {
            Log.i(TAG, "\n********* GRAFCET **********\nGrafcet "+ name+ " already started. Skipping \n");
        }

        // update state
        started = true;

    }

    // Stop the grafcet
    public void stop()
    {
        //if not started yet
        if (myscheduler.isShutdown())
        {
            Log.i(TAG, "\n********* GRAFCET **********\nGrafcet " + name + " not started yet \n");
        }
        else // grafcet already started
        {
            Log.i(TAG, "\n********* GRAFCET **********\nShutting down grafcet " + name + "\n");
            myscheduler.shutdown();
        }

        // update state
        started = false;
    }
} // end of class bfr_grafcet
