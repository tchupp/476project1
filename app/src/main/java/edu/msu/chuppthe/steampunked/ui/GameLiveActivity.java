package edu.msu.chuppthe.steampunked.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import edu.msu.chuppthe.steampunked.R;
import edu.msu.chuppthe.steampunked.game.Pipe;
import edu.msu.chuppthe.steampunked.game.Player;
import edu.msu.chuppthe.steampunked.gcm.GCMIntentService;
import edu.msu.chuppthe.steampunked.utility.Cloud;
import edu.msu.chuppthe.steampunked.utility.Preferences;

public class GameLiveActivity extends AppCompatActivity {

    public static final String WINNING_PLAYER = "WINNING_PLAYER";
    private static final String ACTIVE_PLAYER = "activePlayer";
    public static final String RECEIVE = "edu.msu.chuppthe.steampunked.ui.GameLiveActivity.receive";

    // Bundle keys
    public static final String PLAYER_ONE_NAME = "player_one_name";
    public static final String PLAYER_TWO_NAME = "player_two_name";
    public static final String GAME_ID = "game_identification";
    public static final String GRID_SIZE = "grid_size";
    public static final String PIPE_ID = "move_identification";
    public static final String DISCARD_ID = "discard_id";

    private Cloud cloud;
    private Preferences preferences;
    private WaitingForPlayerDlg waitingForPlayerDlg;
    private WaitingForMoveDlg waitingForMoveDlg;

    private BroadcastReceiver receiver;

    private Player playerOne;

    private Player playerTwo;

    private Player activePlayer;

    private Player inactivePlayer;

    /**
     * Save the instance state into a bundle
     *
     * @param bundle the bundle to save into
     */
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        getPlayingAreaView().saveToBundle(bundle);
        getSelectionAreaView().saveToBundle(bundle);
        bundle.putString(ACTIVE_PLAYER, this.activePlayer.getName());
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game_live);

        cloud = new Cloud(this);
        preferences = new Preferences(this);
        waitingForPlayerDlg = new WaitingForPlayerDlg();
        waitingForPlayerDlg.setCancelable(false);
        waitingForMoveDlg = new WaitingForMoveDlg();
        waitingForMoveDlg.setCancelable(false);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        String playerOneName = extras.getString(PLAYER_ONE_NAME);
        String playerTwoName = extras.getString(PLAYER_TWO_NAME);

        this.playerOne = new Player(playerOneName);
        this.playerTwo = new Player(playerTwoName);

        if (playerTwoName.equals("")) {
            waitingForPlayerDlg.show(getFragmentManager(), "waiting");
        }

        if (playerTwoName.equals(preferences.getAuthUsername())) {
            waitingForMoveDlg.show(getFragmentManager(), "waitingForMove");
        }

        getPlayingAreaView().setupPlayArea(extras.getInt(GRID_SIZE), this.playerOne, this.playerTwo);

        this.activePlayer = this.playerTwo;
        this.inactivePlayer = this.playerOne;

        if (bundle != null) {
            // We have saved state
            String activeName = bundle.getString(ACTIVE_PLAYER);
            if (activeName == null) {
                return;
            }

            if (activeName.equals(this.playerTwo.getName())) {
                this.activePlayer = this.playerOne;
                this.inactivePlayer = this.playerTwo;
            }

            getPlayingAreaView().getFromBundle(bundle, this.playerOne, this.playerTwo);
            getSelectionAreaView().getFromBundle(bundle, this.playerOne, this.playerTwo);
        }

        changeTurn();
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(RECEIVE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getExtras().getString(GCMIntentService.ACTION_KEY)) {
                    case GCMIntentService.NEW_MOVE_CASE:
                        addPipe(intent.getExtras().getString(PIPE_ID));
                        break;
                    case GCMIntentService.PLAYER_JOINED_CASE:
                        addPlayer(intent.getExtras().getString(PLAYER_TWO_NAME));
                        break;
                    case GCMIntentService.PIPE_DISCARD_CASE:
                        addPipe(intent.getExtras().getString(DISCARD_ID));
                        break;
                    default:
                        break;
                }
            }
        };
        //registering our receiver
        this.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister our receiver
        this.unregisterReceiver(this.receiver);
    }

    public void addPlayer(String name) {
        playerTwo.setName(name);
        waitingForPlayerDlg.dismiss();
        getPlayingAreaView().invalidate();
    }

    public void addPipe(final String pipeId) {
        if (!pipeId.equals("-1")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Pipe pipe = cloud.loadPipeFromCloud(preferences.getGameId(), pipeId);

                    getPlayingAreaView().post(new Runnable() {
                        @Override
                        public void run() {
                            getPlayingAreaView().addPipe(pipe);
                            changeTurn();
                            waitingForMoveDlg.dismiss();
                        }
                    });
                }
            }).start();
        }
        else {
            changeTurn();
            waitingForMoveDlg.dismiss();
        }
    }

    public void onInstall(View view) {
        activePlayer.setLeak(false);

        if (getPlayingAreaView().installSelection(this.activePlayer)) {
            waitingForMoveDlg.show(getFragmentManager(), "waitingForMove");
            changeTurn();
        } else {
            Toast.makeText(this, "Install Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDiscard(View view) {
        if (getPlayingAreaView().discardSelection()) {
            waitingForMoveDlg.show(getFragmentManager(), "waitingForMove");
            changeTurn();
        } else {
            Toast.makeText(this, "Discard Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onOpenValve(View view) {
        boolean noLeaks = getPlayingAreaView().checkLeaks(activePlayer);

        if (noLeaks) {
            gameOver(noLeaks);
        }
        else {
            Toast.makeText(this, "You Still Have Leaks!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRotate(View view) {
        if (!getPlayingAreaView().rotateSelected()) {
            Toast.makeText(this, "Please Select A Pipe First.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSurrender(View view) {
        gameOver(false);
    }

    public void onPieceSelected(Pipe pipe, boolean isPortrait) {
        getPlayingAreaView().notifyPieceSelected(pipe, isPortrait);
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    private void gameOver(boolean activeWon) {

        //TODO: server call to push surrender message

        Intent intent = new Intent(this, GameOverActivity.class);

        Player winner = activeWon ? activePlayer : inactivePlayer;
        intent.putExtra(WINNING_PLAYER, winner.getName());

        startActivity(intent);
    }

    private void changeTurn() {
        Player temp = this.activePlayer;
        this.activePlayer = this.inactivePlayer;
        this.inactivePlayer = temp;

        this.activePlayer.setActive(true);
        this.inactivePlayer.setActive(false);

        getSelectionAreaView().startTurn(this.activePlayer);
    }

    private PlayingAreaView getPlayingAreaView() {
        return (PlayingAreaView) findViewById(R.id.playingView);
    }

    private SelectionAreaView getSelectionAreaView() {
        return (SelectionAreaView) findViewById(R.id.selectionView);
    }

    public boolean passTouch(MotionEvent event) {
        return getPlayingAreaView().onTouchEvent(event);
    }
}
