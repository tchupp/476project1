package edu.msu.chuppthe.steampunked;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginDlg extends DialogFragment {

    private AlertDialog dlg;

    private SharedPreferences preferences;

    public static final String USERNAME_KEY = "LoginDlg_Username";
    public static final String PASSWORD_KEY = "LoginDlg_Password";
    public static final String SHARED_PREFERENCE_ID = "MyPreferences";

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

        // Get shared preferences for user login info

        preferences = view.getContext().getSharedPreferences(SHARED_PREFERENCE_ID, Context.MODE_PRIVATE);

        // Auto fill functions
        setUsername(view);
        setPassword(view);
        setRememberMeCheck(view);

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

        final MainMenuActivity activity = (MainMenuActivity) getActivity();
        final ImageView view = (ImageView) activity.findViewById(R.id.imageMainMenu);

        Runnable loginRunnable = new Runnable() {
            @Override
            public void run() {
                // Create a cloud object
                Cloud cloud = new Cloud();
                final String authToken = cloud.loginToCloud(username, password);
                if (authToken == null) {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            // If we fail to login, display a toast
                            Toast.makeText(view.getContext(), R.string.login_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    SharedPreferences.Editor editor = preferences.edit();

                    boolean rememberMe = isRememberMeChecked();

                    String storeUsername = rememberMe ? username : "";
                    String storePassword = rememberMe ? password : "";

                    editor.putString(USERNAME_KEY, storeUsername);
                    editor.putString(PASSWORD_KEY, storePassword);

                    editor.apply();

                    activity.moveToLobby(username, authToken);
                }
            }
        };

        new Thread(loginRunnable).start();
    }

    private String getUsername() {
        EditText usernameEdit = (EditText) dlg.findViewById(R.id.loginUsernameText);
        return usernameEdit.getText().toString();
    }

    public void setUsername(View view) {
        EditText usernameEdit = (EditText) view.findViewById(R.id.loginUsernameText);

        usernameEdit.setText(getUsernameFromPreferences());
    }

    private String getPassword() {
        EditText passwordEdit = (EditText) dlg.findViewById(R.id.loginPasswordText);
        return passwordEdit.getText().toString();
    }

    public void setPassword(View view) {
        EditText passwordEdit = (EditText) view.findViewById(R.id.loginPasswordText);

        passwordEdit.setText(getPasswordFromPreferences());
    }

    private boolean isRememberMeChecked() {
        CheckBox rememberMeCheck = (CheckBox) dlg.findViewById(R.id.rememberMeCheck);
        return rememberMeCheck.isChecked();
    }

    private void setRememberMeCheck(View view) {
        CheckBox rememberMeCheck = (CheckBox) view.findViewById(R.id.rememberMeCheck);

        boolean rememberMe = !getUsernameFromPreferences().isEmpty();
        rememberMeCheck.setChecked(rememberMe);
    }

    /**
     * @return username from shared preferences
     */
    private String getUsernameFromPreferences() {
        return preferences.getString(USERNAME_KEY, "");
    }

    /**
     * @return password from shared preferences
     */
    private String getPasswordFromPreferences() {
        return preferences.getString(PASSWORD_KEY, "");
    }
}
