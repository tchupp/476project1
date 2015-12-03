package edu.msu.chuppthe.steampunked.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import edu.msu.chuppthe.steampunked.R;

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

        this.gridSize = extras.getInt(GameLiveActivity.GRID_SIZE);
        this.playerOneName = extras.getString(GameLiveActivity.PLAYER_ONE_NAME);
        this.playerTwoName = extras.getString(GameLiveActivity.PLAYER_TWO_NAME);
        String winningPlayerName = extras.getString(GameLiveActivity.WINNING_PLAYER);
        String winningText = "Player '" + winningPlayerName + "' Won!";
        getWinningPlayerText().setText(winningText);
    }

    public void onNewGame(View view) {
        Intent intent = new Intent(this, GameLiveActivity.class);
        intent.putExtra(GameLiveActivity.PLAYER_ONE_NAME, this.playerOneName);
        intent.putExtra(GameLiveActivity.PLAYER_TWO_NAME, this.playerTwoName);
        intent.putExtra(GameLiveActivity.GRID_SIZE, this.gridSize);

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
