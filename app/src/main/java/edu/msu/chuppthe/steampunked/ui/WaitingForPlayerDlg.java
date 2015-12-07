package edu.msu.chuppthe.steampunked.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import edu.msu.chuppthe.steampunked.R;

/**
 * Created by evanhlavaty on 12/5/15.
 */
public class WaitingForPlayerDlg extends DialogFragment {

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.waiting_dlg);

        // Create the dialog box
        final AlertDialog dlg = builder.create();
        return dlg;
    }
}
