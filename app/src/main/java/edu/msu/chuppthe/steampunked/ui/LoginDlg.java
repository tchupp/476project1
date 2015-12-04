package edu.msu.chuppthe.steampunked.ui;

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
import android.widget.ImageView;
import android.widget.Toast;

import edu.msu.chuppthe.steampunked.utility.Preferences;
import edu.msu.chuppthe.steampunked.R;
import edu.msu.chuppthe.steampunked.utility.Cloud;

public class LoginDlg extends DialogFragment {

    private AlertDialog dlg;

    private Preferences preferences;

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

        preferences = new Preferences(view.getContext());

        // Auto fill functions
        setUsername(view, preferences.getLoginUsername());
        setPassword(view, preferences.getLoginPassword());
        setRememberMeCheck(view, preferences.getRememberMe());

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
                String username = getUsernameString();
                String password = getPasswordString();

                if (username.isEmpty() || password.isEmpty()) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), R.string.login_failed_empty,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    login(username, password);
                }
            }
        });

        dlg = builder.create();

        return dlg;
    }

    private void login(final String username, final String password) {
        if (!(getActivity() instanceof MainMenuActivity)) {
            return;
        }
        final LoginLoadingDlg loginDlg = new LoginLoadingDlg();

        final MainMenuActivity activity = (MainMenuActivity) getActivity();
        final ImageView view = (ImageView) activity.findViewById(R.id.imageMainMenu);

        loginDlg.show(activity.getFragmentManager(), "login_loading");

        Runnable loginRunnable = new Runnable() {
            @Override
            public void run() {
                // Create a cloud object
                Cloud cloud = new Cloud(view.getContext());
                final String authToken = cloud.loginToCloud(username, password);
                if (authToken == null) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            // If we fail to login, display a toast
                            Toast.makeText(view.getContext(),
                                    R.string.login_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    boolean rememberMe = isRememberMeChecked();

                    String storeUsername = rememberMe ? username : "";
                    String storePassword = rememberMe ? password : "";

                    preferences.setLoginUsername(storeUsername);
                    preferences.setLoginPassword(storePassword);
                    preferences.setRememberMe(rememberMe);

                    preferences.setAuthUsername(username);
                    preferences.setAuthToken(authToken);


                    activity.moveToLobby();
                }

                loginDlg.dismiss();
            }
        };

        new Thread(loginRunnable).start();
    }

    public void setUsername(View view, String username) {
        EditText usernameEdit = (EditText) view.findViewById(R.id.loginUsernameText);
        usernameEdit.setText(username);
    }

    public void setPassword(View view, String password) {
        EditText passwordEdit = (EditText) view.findViewById(R.id.loginPasswordText);
        passwordEdit.setText(password);
    }

    private void setRememberMeCheck(View view, boolean rememberMe) {
        CheckBox rememberMeCheck = (CheckBox) view.findViewById(R.id.rememberMeCheck);
        rememberMeCheck.setChecked(rememberMe);
    }

    private String getUsernameString() {
        EditText usernameEdit = (EditText) dlg.findViewById(R.id.loginUsernameText);
        return usernameEdit.getText().toString();
    }

    private String getPasswordString() {
        EditText passwordEdit = (EditText) dlg.findViewById(R.id.loginPasswordText);
        return passwordEdit.getText().toString();
    }

    private boolean isRememberMeChecked() {
        CheckBox rememberMeCheck = (CheckBox) dlg.findViewById(R.id.rememberMeCheck);
        return rememberMeCheck.isChecked();
    }
}
