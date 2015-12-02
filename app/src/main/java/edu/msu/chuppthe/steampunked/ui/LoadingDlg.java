package edu.msu.chuppthe.steampunked.ui;
import edu.msu.chuppthe.steampunked.R ;

import android.app.DialogFragment;
import android.app.Dialog;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class LoadingDlg extends DialogFragment {

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.loading);

        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });


        // Create the dialog box
        final AlertDialog dlg = builder.create();
        return dlg;
    }


}
