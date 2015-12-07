package edu.msu.chuppthe.steampunked.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import edu.msu.chuppthe.steampunked.ui.GameLiveActivity;
import edu.msu.chuppthe.steampunked.ui.GameOverActivity;

public class GCMIntentService extends IntentService {

    // Cases that match notification title
    public static final String TOAST_CASE = "show_toast";
    public static final String PLAYER_JOINED_CASE = "player_joined";
    public static final String NEW_MOVE_CASE = "new_move";
    public static final String END_GAME_CASE = "end_game";
    public static final String PIPE_DISCARD_CASE = "pipe_discard";
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

                String title = extras.getString("title");
                if (title == null) {
                    return;
                }

                switch (title) {
                    case TOAST_CASE:
                        showToast(extras.getString("message"));
                        break;
                    case NEW_MOVE_CASE:
                        showToast(extras.getString("message"));
                        Intent moveIntent = new Intent(GameLiveActivity.RECEIVE);
                        moveIntent.putExtra(ACTION_KEY, NEW_MOVE_CASE);
                        moveIntent.putExtra(GameLiveActivity.PIPE_ID, extras.getString("data"));
                        sendBroadcast(moveIntent);
                        break;
                    case PLAYER_JOINED_CASE:
                        showToast(extras.getString("message"));
                        Intent joinIntent = new Intent(GameLiveActivity.RECEIVE);
                        joinIntent.putExtra(ACTION_KEY, PLAYER_JOINED_CASE);
                        joinIntent.putExtra(GameLiveActivity.PLAYER_TWO_NAME, extras.getString("data"));
                        sendBroadcast(joinIntent);
                        break;
                    case PIPE_DISCARD_CASE:
                        showToast(extras.getString("message"));
                        Intent discardIntent = new Intent(GameLiveActivity.RECEIVE);
                        discardIntent.putExtra(ACTION_KEY, PIPE_DISCARD_CASE);
                        discardIntent.putExtra(GameLiveActivity.DISCARD_ID, extras.getString("data"));
                        sendBroadcast(discardIntent);
                        break;
                    case END_GAME_CASE:
                        Intent endGameIntent = new Intent(getBaseContext(), GameOverActivity.class);
                        endGameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        endGameIntent.putExtra(GameLiveActivity.WINNING_PLAYER, extras.getString("data"));
                        startActivity(endGameIntent);
                        break;
                    default:
                        showToast("Unrecognized Notification");
                        break;
                }
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
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