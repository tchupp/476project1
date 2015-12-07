package edu.msu.chuppthe.steampunked.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import edu.msu.chuppthe.steampunked.R;

public class WaitingForMoveDlg extends DialogFragment {

    private GameLiveActivity gameLiveActivity;

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
                gameLiveActivity.onSurrender(null);
            }
        });

        // Create the dialog box
        return builder.create();
    }

    public void setGameLiveActivity(GameLiveActivity gameLiveActivity) {
        this.gameLiveActivity = gameLiveActivity;
    }
}
