package edu.msu.chuppthe.steampunked;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainMenuActivity extends AppCompatActivity {

    public static final String GRID_SELECTION = "Grid Selection";
    public static final String PLAYER_ONE = "Player One";
    public static final String PLAYER_TWO = "Player Two";

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

    public void onStart(View view) {
        Intent intent = new Intent(this, GameLiveActivity.class);
        if (getFiveRadioButton().isSelected()) {
            intent.putExtra(GRID_SELECTION, "5");
        } else if (getTenRadioButton().isSelected()) {
            intent.putExtra(GRID_SELECTION, "10");
        } else if (getTwentyRadioButton().isSelected()) {
            intent.putExtra(GRID_SELECTION, "20");
        }
        intent.putExtra(PLAYER_ONE, getPlayerOneTextField().getText());
        intent.putExtra(PLAYER_TWO, getPlayerTwoTextField().getText());

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
