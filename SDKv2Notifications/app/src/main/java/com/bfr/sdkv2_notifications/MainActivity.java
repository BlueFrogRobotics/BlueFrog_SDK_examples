package com.bfr.sdkv2_notifications;

import static java.util.Locale.FRENCH;

import android.os.Bundle;
import android.util.Log;

import com.bfr.buddy.companion.shared.TasksSignature;
import com.bfr.buddy.ui.shared.notifications.NotificationButton;
import com.bfr.buddy.ui.shared.notifications.NotificationButtonGroup;
import com.bfr.buddy.ui.shared.notifications.NotificationButtonText;
import com.bfr.buddy.ui.shared.notifications.NotificationCompanionTask;
import com.bfr.buddy.ui.shared.notifications.StringMultilang;
import com.bfr.buddy.utils.values.FloatingWidgetVisibility;
import com.bfr.buddysdk.BuddyActivity;
import com.bfr.buddysdk.BuddySDK;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kotlin.Pair;

public class MainActivity extends BuddyActivity {

    private static final String PACKAGE_NAME_CALCULATOR_APP = "com.android.calculator2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the different buttons to launch the different actions of Buddy
        findViewById(R.id.button_notification_show_simple).setOnClickListener(v -> onButtonNotificationShowSimple());
        findViewById(R.id.button_notification_show_resources).setOnClickListener(v -> onButtonNotificationShowResources());
        findViewById(R.id.button_notification_show_buttons).setOnClickListener(v -> onButtonNotificationShowButtons());
        findViewById(R.id.button_notification_show_image).setOnClickListener(v -> onButtonNotificationShowImage());
        findViewById(R.id.button_notification_hide).setOnClickListener(v -> onButtonNotificationHide());
    }

    // Show simple notification
    private void onButtonNotificationShowSimple() {
        BuddySDK.UI.showNotification("Title", "Text");
    }

    // Show notification with localised strings
    private void onButtonNotificationShowResources() {
        // Create texts
        StringMultilang title = new StringMultilang(R.string.notif_title);
        StringMultilang text = new StringMultilang(R.string.notif_text);

        BuddySDK.UI.showNotification(
                title, text, 5, false, null, null, null
        );
    }

    // Show notification with buttons with actions
    private void onButtonNotificationShowButtons() {
        // Create texts
        StringMultilang title = new StringMultilang(R.string.notif_title);
        StringMultilang text = new StringMultilang(R.string.notif_text);

        // Create action button

        // Create parameters for a button task
        Map<String, String> parameters = new HashMap<>();
        parameters.put(TasksSignature.RunActivity.packageParam, PACKAGE_NAME_CALCULATOR_APP);

        // Task to launch other application
        NotificationCompanionTask buttonTask = new NotificationCompanionTask(
                "runActivity",
                parameters,
                "USER_COMMAND",
                "USER_COMMAND"
        );

        NotificationButton button1 = new NotificationButton(
                new StringMultilang(new Pair<>(FRENCH, "Lancer Calculatrice")), // custom text
                NotificationButton.ColorType.YES, // predefined color
                buttonTask  //task from object
        );

        // Create cancel button
        NotificationButton button2 = new NotificationButton(
            NotificationButtonText.CANCEL,  // predefined text and color for a button
            null                            // no action for button, it will hide notificaiton
        );

        // Create group of two buttons (up to three max)
        NotificationButtonGroup buttons = new NotificationButtonGroup(button1, button2, null);

        BuddySDK.UI.showNotification(title, text, 5, false, null, buttons, null);
    }

    // Show notification with additional image
    private void onButtonNotificationShowImage() {
        StringMultilang title = new StringMultilang(R.string.notif_title);
        StringMultilang text = new StringMultilang(R.string.notif_text);

        BuddySDK.UI.showNotification(title, text, 5, false, R.drawable.flag_fr, null, null);
    }

    // Hide notification
    private void onButtonNotificationHide() {
        BuddySDK.UI.hideNotification();
    }

    //This function is called when the SDK is ready
    @Override
    public void onSDKReady() {
        // Make face active
        BuddySDK.UI.setViewAsFace(findViewById(R.id.view_face));

        // Always show notification widget (it is hidden by default)
        BuddySDK.UI.setNotificationsWidgetVisibility(FloatingWidgetVisibility.ALWAYS);
    }



}