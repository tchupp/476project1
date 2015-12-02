package edu.msu.chuppthe.steampunked.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.msu.chuppthe.steampunked.R;
import edu.msu.chuppthe.steampunked.gcm.RegistrationTask;

public class MainMenuActivity extends AppCompatActivity {

    public static final String GRID_SELECTION = "GridSelection";
    public static final String PLAYER_NAME = "PlayerName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void onHowToPlay(View view) {
        // Instantiate a dialog box builder
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());

        // Parameterize the builder
        builder.setTitle(R.string.how_to_play_dlg_title);
        builder.setMessage(R.string.gamerules);
        builder.setPositiveButton(android.R.string.ok, null);

        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onLogin(View view) {
        LoginDlg loginDlg = new LoginDlg();
        loginDlg.show(getFragmentManager(), "login");
    }

    public void onRegister(View view) {
        RegisterDlg registerDlg = new RegisterDlg();
        registerDlg.show(getFragmentManager(), "register");
    }

    public void moveToLobby() {
        // Attempts to register device token for notifications
        new RegistrationTask(this).execute();


        Intent intent = new Intent(this, LobbyActivity.class);
        startActivity(intent);
    }
}
