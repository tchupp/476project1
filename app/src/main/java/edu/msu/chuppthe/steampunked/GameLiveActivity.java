package edu.msu.chuppthe.steampunked;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class GameLiveActivity extends AppCompatActivity {

    public static String WINNING_PLAYER = "WINNING_PLAYER";

    private Player playerOne;

    private Player playerTwo;

    private Player activePlayer;

    private Player inactivePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_live);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        int gridSize = extras.getInt(MainMenuActivity.GRID_SELECTION);
        String playerOneName = extras.getString(MainMenuActivity.PLAYER_ONE);
        String playerTwoName = extras.getString(MainMenuActivity.PLAYER_TWO);

        this.playerOne = new Player(playerOneName);
        this.playerTwo = new Player(playerTwoName);

        getPlayingAreaView().setupPlayArea(gridSize, this.playerOne, this.playerTwo);

        this.activePlayer = this.playerOne;
        this.inactivePlayer = this.playerTwo;

        this.playerOne.setActive(true);
        this.playerTwo.setActive(false);

        getSelectionAreaView().startTurn(this.activePlayer);
    }

    public void onInstall(View view) {
        if (getPlayingAreaView().installSelection()) {
            changeTurn();
        } else {
            Toast.makeText(this, "Install Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDiscard(View view) {
        if (getPlayingAreaView().discardSelection()) {
            changeTurn();
        } else {
            Toast.makeText(this, "Discard Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onOpenValve(View view) {
        boolean noLeaks = getPlayingAreaView().checkLeaks(activePlayer);
        gameOver(noLeaks);
    }

    public void onRotate(View view) {
        if (!getPlayingAreaView().rotateSelected()) {
            Toast.makeText(this, "Please Select A Pipe First.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSurrender(View view) {
        gameOver(false);
    }

    public void onPieceSelected(Pipe pipe) {
        getPlayingAreaView().notifyPieceSelected(pipe);
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    private void gameOver(boolean activeWon) {
        Intent intent = new Intent(this, GameOverActivity.class);
        intent.putExtra(MainMenuActivity.PLAYER_ONE, playerOne.getName());
        intent.putExtra(MainMenuActivity.PLAYER_TWO, playerTwo.getName());
        intent.putExtra(MainMenuActivity.GRID_SELECTION, getPlayingAreaView().getPlayingAreaSize());

        Player winner = activeWon ? activePlayer : inactivePlayer;
        intent.putExtra(WINNING_PLAYER, winner.getName());

        startActivity(intent);
    }

    private void changeTurn() {
        Player temp = this.activePlayer;
        this.activePlayer = this.inactivePlayer;
        this.inactivePlayer = temp;

        activePlayer.setActive(true);
        inactivePlayer.setActive(false);

        getSelectionAreaView().startTurn(this.activePlayer);
    }

    private PlayingAreaView getPlayingAreaView() {
        return (PlayingAreaView) findViewById(R.id.playingView);
    }

    private SelectionAreaView getSelectionAreaView() {
        return (SelectionAreaView) findViewById(R.id.selectionView);
    }
}
