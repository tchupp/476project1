package edu.msu.chuppthe.steampunked;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.app.AlertDialog;

public class MainMenuActivity extends AppCompatActivity {

    public static final String GRID_SELECTION = "GridSelection";
    public static final String PLAYER_NAME = "PlayerOne";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    public void onHowtoPlay(View view) {

        // Instantiate a dialog box builder
        AlertDialog.Builder builder =
                new AlertDialog.Builder(view.getContext());

        // Parameterize the builder
        builder.setTitle(R.string.instructions);
        builder.setMessage(R.string.gamerules);
        builder.setPositiveButton(android.R.string.ok, null);

        // Create the dialog box and show it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    public void onLogin(View view) {

        //TODO: VALIDATE USER CREDENTIALS
        //TODO: MOVE TO LOBBY OR MOVE TO ACTIVE GAME
        //TODO: SET REMEMBER ME IN DEVICE

//        Intent intent = new Intent(this, GameLiveActivity.class);
//        if (getTenRadioButton().isChecked()) {
//            intent.putExtra(GRID_SELECTION, 2);
//        } else if (getTwentyRadioButton().isChecked()) {
//            intent.putExtra(GRID_SELECTION, 4);
//        } else {
//            intent.putExtra(GRID_SELECTION, 1);
//        }
//
//        String playerOneName = String.valueOf(getUsernameTextField().getText());
//
//        intent.putExtra(PLAYER_NAME, playerOneName);
//
//        startActivity(intent);
    }

    public void onRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private RadioButton getFiveRadioButton() {
        return (RadioButton) findViewById(R.id.gridFiveRadio);
    }

    private RadioButton getTenRadioButton() {
        return (RadioButton) findViewById(R.id.gridTenRadio);
    }

    private RadioButton getTwentyRadioButton() {
        return (RadioButton) findViewById(R.id.gridTwentyRadio);
    }

    private EditText getUsernameTextField() {
        return (EditText) findViewById(R.id.usernameTextField);
    }

    private EditText getPasswordTextField() {
        return (EditText) findViewById(R.id.passwordTextField);
    }
}
