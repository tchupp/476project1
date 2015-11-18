package edu.msu.chuppthe.steampunked;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class LoginDlg extends DialogFragment {

    private AlertDialog dlg;

    /**
     * Create the dialog box
     *
     * @param savedInstanceState The saved instance bundle
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.login);

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.login_dlg, null);
        builder.setView(view);

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Cancel just closes the dialog box
            }
        });

        // Add a login button
        builder.setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                login(getUsername(), getPassword());
            }
        });

        dlg = builder.create();

        return dlg;
    }

    private void login(String username, String password) {
        // TODO: login to server
        // If OK, head to lobby
        // If BAD, remove password
    }

    private String getUsername() {
        EditText usernameEdit = (EditText) dlg.findViewById(R.id.loginUsernameText);
        return usernameEdit.getText().toString();
    }

    private String getPassword() {
        EditText passwordEdit = (EditText) dlg.findViewById(R.id.loginPasswordText);
        return passwordEdit.getText().toString();
    }
}
