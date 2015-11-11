package edu.msu.chuppthe.steampunked;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    private int gridSize;

    private String playerOneName;

    private String playerTwoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        this.gridSize = extras.getInt(MainMenuActivity.GRID_SELECTION);
        //this.playerOneName = extras.getString(MainMenuActivity.PLAYER_ONE);
        //this.playerTwoName = extras.getString(MainMenuActivity.PLAYER_TWO);
        String winningPlayerName = extras.getString(GameLiveActivity.WINNING_PLAYER);
        String winningText = "Player '" + winningPlayerName + "' Won!";
        getWinningPlayerText().setText(winningText);
    }

    public void onNewGame(View view) {
        Intent intent = new Intent(this, GameLiveActivity.class);
        //intent.putExtra(MainMenuActivity.PLAYER_ONE, this.playerOneName);
        //intent.putExtra(MainMenuActivity.PLAYER_TWO, this.playerTwoName);
        intent.putExtra(MainMenuActivity.GRID_SELECTION, this.gridSize);

        startActivity(intent);
    }

    public void onMainMenu(View view) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

    private TextView getWinningPlayerText() {
        return (TextView) findViewById(R.id.winningPlayerText);
    }
}
