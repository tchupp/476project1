package edu.msu.chuppthe.steampunked.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.msu.chuppthe.steampunked.R;
import edu.msu.chuppthe.steampunked.ui.GameLiveActivity;
import edu.msu.chuppthe.steampunked.ui.LobbyActivity;
import edu.msu.chuppthe.steampunked.ui.MainMenuActivity;

public class GCMIntentService extends IntentService {

    // Cases that match notification title
    public static final String TOAST_CASE = "show_toast";
    public static final String REFRESH_CASE = "refresh_board";
    public static final String MOVE_TO_GAME_LIVE_CASE = "lobby_to_game";
    public static final String ACTION_KEY = "action";


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
                Log.i("GCM_RECEIVED", extras.toString());

                switch (extras.getString("title")) {
                    case TOAST_CASE:
                        showToast(extras.getString("message"));
                        break;
                    case REFRESH_CASE:
                        Intent i = new Intent(GameLiveActivity.RECEIVE);
                        i.putExtra(ACTION_KEY, REFRESH_CASE);
                        sendBroadcast(i);
                        break;
                    case MOVE_TO_GAME_LIVE_CASE:
                        launchGameLiveActivity(extras);
                        break;
                    default:
                        showToast("Unrecognized Notification");
                        break;
                }
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void launchGameLiveActivity(Bundle extras) {
        Intent intent = new Intent(getBaseContext(), GameLiveActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtras(extras);
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