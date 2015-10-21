package edu.msu.chuppthe.steampunked;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class GameLiveActivity extends AppCompatActivity {

    private Player playerOne;

    private Player playerTwo;

    private Player activePlayer;

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

        //if (getPlayingAreaView().checkLeaks(pipe)) {
            //getPlayingAreaView().setLeaking(true);
      /*  } else {
            getPlayingAreaView().setLeaking(false);

        } */
    }

    public void onRotate(View view) {
        if (!getPlayingAreaView().rotateSelected()) {
            Toast.makeText(this, "Please Select A Pipe First.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSurrender(View view) {
        Intent intent = new Intent(this, GameOverActivity.class);
        startActivity(intent);
    }

    public void onPieceSelected(Pipe pipe) {
        getPlayingAreaView().notifyPieceSelected(pipe);
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    private void changeTurn() {
        if (this.activePlayer == this.playerOne) {
            this.activePlayer = this.playerTwo;

            this.playerOne.setActive(false);
            this.playerTwo.setActive(true);
        } else if (this.activePlayer == this.playerTwo) {
            this.activePlayer = this.playerOne;

            this.playerOne.setActive(true);
            this.playerTwo.setActive(false);
        }

        getSelectionAreaView().startTurn(this.activePlayer);
    }

    private PlayingAreaView getPlayingAreaView() {
        return (PlayingAreaView) findViewById(R.id.playingView);
    }

    private SelectionAreaView getSelectionAreaView() {
        return (SelectionAreaView) findViewById(R.id.selectionView);
    }
}
