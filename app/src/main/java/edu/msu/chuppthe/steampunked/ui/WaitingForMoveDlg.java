package edu.msu.chuppthe.steampunked.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;

import edu.msu.chuppthe.steampunked.R;

public class WaitingForMoveDlg extends DialogFragment {

    private static final int MAX_WAIT_TIME = 180;

    private GameLiveActivity gameLiveActivity;
    private boolean cancel;

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.waiting_for_move_dlg);

        builder.setNegativeButton(R.string.surrender_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                gameLiveActivity.onQuitWhileWaiting();
            }
        });

        startTimer();

        // Create the dialog box
        return builder.create();
    }

    @Override
    public void onStop() {
        super.onStop();
        cancel = true;
    }

    private void startTimer() {
        cancel = false;
        final long startTime = SystemClock.currentThreadTimeMillis();

        new Thread(new Runnable() {
            @Override
            public void run() {
                long delta;
                do {
                    if (cancel) {
                        return;
                    }

                    delta = SystemClock.currentThreadTimeMillis() - startTime;
                    Thread.yield();
                } while ((delta / 1000) < MAX_WAIT_TIME);
                gameLiveActivity.onSurrender(null);
            }
        }).start();
    }

    public void setGameLiveActivity(GameLiveActivity gameLiveActivity) {
        this.gameLiveActivity = gameLiveActivity;
    }
}
