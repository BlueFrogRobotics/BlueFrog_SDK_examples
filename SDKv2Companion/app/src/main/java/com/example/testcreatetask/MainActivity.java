package com.example.testcreatetask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import com.bfr.buddy.utils.events.EventItem;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;
import com.bfr.buddysdk.IBuddyActivity;
import com.bfr.buddysdk.services.companion.FollowMeMode;
import com.bfr.buddysdk.services.companion.Task;
import com.bfr.buddysdk.services.companion.TaskCallback;

public class MainActivity extends BuddyActivity {

    private final String TAG = "MainActivity";

    //Create the Task
    private Task task = null;
    private String PACKAGE_NAME;

    private Button buttonStartTask;

    private Button buttonStopTask;

    //example when you want to use other permissions than those in your manifest
    private String[] permission  = {
            "com.bfr.buddy.resource.FACE",
            "com.bfr.buddy.resource.SENSOR_MODULE",
            "com.bfr.buddy.resource.SPEECH",
            "com.bfr.buddy.resource.HEAD"
            ,"com.bfr.buddy.resource.WHEELS"
            ,"com.bfr.buddy.resource.VISION"
            };

    //Create the task callback you'll need to put in the start
    private final TaskCallback taskCallback = new TaskCallback() {

        @Override
        public void onStarted() {
            Log.i(TAG, "Task is started");
        }

        @Override
        public void onSuccess(@NonNull String value) {
            Log.i(TAG, "Task finished on success with message: " + value);
        }

        @Override
        public void onCancel() {
            Log.i(TAG, "Task finished on cancellation");
        }

        @Override
        public void onError(@NonNull String message) {
            Log.e(TAG, "Task finished on error with message: " + message);
        }

        @Override
        public void onIntermediateResult(@NonNull String s) {}
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get the package name for debugging
        PACKAGE_NAME = getApplicationContext().getPackageName();

        //Create two buttons, one to start the task and on to stop the task
        buttonStartTask = findViewById(R.id.btn_launch_task);
        buttonStopTask = findViewById(R.id.btn_stop_task);
    }

    @Override
    public void onSDKReady() {
        super.onSDKReady();

        buttonStartTask.setOnClickListener(view -> {

            //Avoid starting multiple time the task if you click many time on the button
            if (task != null)
                task.stop();

            try
            {
                //init the task
                //task = BuddySDK.Companion.createRandomStrollTask(null);
                task = BuddySDK.Companion.createFollowMeTask(FollowMeMode.WATCH_ME,  null);
                //start the task randomStrolleRandom
                task.start(taskCallback);
            }catch (RuntimeException e)
            {
                Log.e(PACKAGE_NAME, "error: " + e.getMessage());
                System.exit(0);
            }
        });

        buttonStopTask.setOnClickListener (view -> {
            //stop the task randomStrolleRandom
            if(task != null)
                task.stop();
        });
    }

    @Override
    public void onEvent(EventItem iEvent) {
        super.onEvent(iEvent);
    }

}
