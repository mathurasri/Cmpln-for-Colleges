package com.instamour.mathu.cmpln;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

import static com.instamour.mathu.cmpln.CommonUtilites.SENDER_ID;

public class GCMIntentService extends GCMBaseIntentService{
    public GCMIntentService() {
        super(SENDER_ID);
    }


    @Override
    protected void onMessage(Context context, Intent intent) {
       // Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("price");

        //displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected void onError(Context context, String s) {

    //    Log.i(TAG, "Received error: " + s);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        ServerUtilities.register(context, Comment.name, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);

        String title = context.getString(R.string.app_name);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;

        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;

        notificationManager.notify(0, notification);

    }
}
