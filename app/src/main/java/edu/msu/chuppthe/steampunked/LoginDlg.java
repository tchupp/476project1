package edu.msu.chuppthe.steampunked;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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
        @SuppressLint("InflateParams") final
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
                String username = getUsername();
                String password = getPassword();

                if (username.isEmpty() || password.isEmpty()) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), R.string.login_failed_empty,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    login(username, password, view);
                }
            }
        });

        dlg = builder.create();

        return dlg;
    }

    private void login(final String username, final String password, final View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Create a cloud object
                Cloud cloud = new Cloud();
                final boolean ok = cloud.loginToCloud(username, password);
                if (!ok) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            // If we fail to login, display a toast
                            Toast.makeText(view.getContext(), R.string.login, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (isRememberMeChecked()) {
                        // TODO: save username and pw to device
                    }
                    // TODO: Continue to lobby
                }
            }
        }).start();
    }

    private String getUsername() {
        EditText usernameEdit = (EditText) dlg.findViewById(R.id.loginUsernameText);
        return usernameEdit.getText().toString();
    }

    private String getPassword() {
        EditText passwordEdit = (EditText) dlg.findViewById(R.id.loginPasswordText);
        return passwordEdit.getText().toString();
    }

    private boolean isRememberMeChecked() {
        CheckBox rememberMeCheck = (CheckBox) dlg.findViewById(R.id.rememberMeCheck);
        return rememberMeCheck.isChecked();

    }
}
