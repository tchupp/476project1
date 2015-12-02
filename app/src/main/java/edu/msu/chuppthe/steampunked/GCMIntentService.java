package edu.msu.chuppthe.steampunked;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by evanhlavaty on 11/24/15.
 */
public class GCMIntentService extends IntentService {

    public static final int MESSAGE_NOTIFICATION_ID = 435345;

    public GCMIntentService() {
        super("GCMIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (extras != null && !extras.isEmpty()) {
            // Since we're not using two way messaging, this is all we really to check for
            if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Logger.getLogger("GCM_RECEIVED").log(Level.INFO, extras.toString());

                // Creates notification in notification tray
                //createNotification(extras.getString("title"), extras.getString("message"));

                // Shows toast no matter where you are
                showToast(extras.getString("message"));

                // Moves to defined activity
                //launchSomeActivity(extras);
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void createNotification(String title, String message) {
        Context context = getBaseContext();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
                .setContentText(message);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(MESSAGE_NOTIFICATION_ID, mBuilder.build());
    }

    private void launchSomeActivity(Bundle extras) {
        Intent intent = new Intent(getBaseContext(), LobbyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        // Add any extras from notification
        //intent.putExtra("customdata", extras.getString(""));
        getBaseContext().getApplicationContext().startActivity(intent);
    }

    private void showToast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }
}