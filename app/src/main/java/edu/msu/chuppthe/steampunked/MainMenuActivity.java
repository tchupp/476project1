package edu.msu.chuppthe.steampunked;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.app.AlertDialog;

public class MainMenuActivity extends AppCompatActivity {

    public static final String GRID_SELECTION = "GridSelection";
    public static final String PLAYER_ONE = "PlayerOne";
    public static final String PLAYER_TWO = "PlayerTwo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void onStart(View view) {
        Intent intent = new Intent(this, GameLiveActivity.class);
        if (getTenRadioButton().isChecked()) {
            intent.putExtra(GRID_SELECTION, 2);
        } else if (getTwentyRadioButton().isChecked()) {
            intent.putExtra(GRID_SELECTION, 4);
        } else {
            intent.putExtra(GRID_SELECTION, 1);
        }

        String playerOneName = String.valueOf(getPlayerOneTextField().getText());
        String playerTwoName = String.valueOf(getPlayerTwoTextField().getText());

        playerOneName = playerOneName.isEmpty() ? "Player One" : playerOneName;
        playerTwoName = playerTwoName.isEmpty() ? "Player Two" : playerTwoName;

        intent.putExtra(PLAYER_ONE, playerOneName);
        intent.putExtra(PLAYER_TWO, playerTwoName);

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

    private EditText getPlayerOneTextField() {
        return (EditText) findViewById(R.id.playerOneTextField);
    }

    private EditText getPlayerTwoTextField() {
        return (EditText) findViewById(R.id.playerTwoTextField);
    }
}
