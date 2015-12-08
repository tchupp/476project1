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
    public static final String RECEIVE = "edu.msu.chuppthe.steampunked.ui.GameLiveActivity.receive";

    private static final String ACTIVE_PLAYER = "activePlayer";
    private static final String ACTIVE_MOVE_DLG = "active_move_dlg";
    private static final String ACTIVE_JOIN_DLG = "active_join_dlg";
    private static final String WAITING_FOR_MOVE_LABEL = "waitingForMove";
    private static final String WAITING_FOR_PLAYER_LABEL = "waiting";

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
    private Boolean waitingForPlayerShowing = false;

    private WaitingForMoveDlg waitingForMoveDlg;
    private Boolean waitingForMoveShowing = false;

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

        bundle.putBoolean(ACTIVE_MOVE_DLG, waitingForMoveShowing);
        bundle.putBoolean(ACTIVE_JOIN_DLG, waitingForPlayerShowing);

        getPlayingAreaView().saveToBundle(bundle);
        getSelectionAreaView().saveToBundle(bundle);

        bundle.putString(ACTIVE_PLAYER, this.activePlayer.getName());
        bundle.putString(PLAYER_ONE_NAME, this.playerOne.getName());
        bundle.putString(PLAYER_TWO_NAME, this.playerTwo.getName());
        bundle.putString(PLAYER_TWO_NAME, this.playerTwo.getName());
        bundle.putInt(GRID_SIZE, getPlayingAreaView().getPlayingAreaSize() / 5);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_game_live);

        this.cloud = new Cloud(this);
        this.preferences = new Preferences(this);

        this.waitingForPlayerDlg = new WaitingForPlayerDlg();
        this.waitingForPlayerDlg.setCancelable(false);

        this.waitingForMoveDlg = new WaitingForMoveDlg();
        this.waitingForMoveDlg.setCancelable(false);
        this.waitingForMoveDlg.setGameLiveActivity(this);

        Bundle savedState = (bundle == null) ? getIntent().getExtras() : bundle;

        int gridSize = savedState.getInt(GRID_SIZE);

        String playerOneName = savedState.getString(PLAYER_ONE_NAME);
        String playerTwoName = savedState.getString(PLAYER_TWO_NAME);

        if (playerOneName == null || playerTwoName == null) {
            return;
        }

        this.playerOne = new Player(playerOneName);
        this.playerTwo = new Player(playerTwoName);

        getPlayingAreaView().setupPlayArea(gridSize, this.playerOne, this.playerTwo);

        this.activePlayer = this.playerTwo;
        this.inactivePlayer = this.playerOne;

        if (bundle == null) {
            if (playerTwoName.isEmpty()) {
                this.waitingForPlayerDlg.show(getFragmentManager(), WAITING_FOR_PLAYER_LABEL);
            } else if (playerTwoName.equals(preferences.getAuthUsername())) {
                this.waitingForMoveDlg.show(getFragmentManager(), WAITING_FOR_MOVE_LABEL);
            }
        } else {
            String activePlayer = bundle.getString(ACTIVE_PLAYER);
            if (activePlayer == null) {
                return;
            }

            if (activePlayer.equals(this.playerTwo.getName())) {
                this.activePlayer = playerOne;
                this.inactivePlayer = this.playerTwo;
            }

            if (bundle.getBoolean(ACTIVE_JOIN_DLG)) {
                this.waitingForPlayerDlg.show(getFragmentManager(), WAITING_FOR_PLAYER_LABEL);
            } else if (bundle.getBoolean(ACTIVE_MOVE_DLG)) {
                this.waitingForMoveDlg.show(getFragmentManager(), WAITING_FOR_MOVE_LABEL);
            }

            getPlayingAreaView().getFromBundle(bundle, playerOne, this.playerTwo);
            getSelectionAreaView().getFromBundle(bundle, playerOne, this.playerTwo);
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
                String actionKey = intent.getExtras().getString(GCMIntentService.ACTION_KEY);
                if (actionKey == null) {
                    return;
                }

                switch (actionKey) {
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

        this.waitingForPlayerShowing = waitingForPlayerDlg.isAdded();
        if (this.waitingForPlayerShowing) {
            waitingForPlayerDlg.dismiss();
        }

        this.waitingForMoveShowing = waitingForMoveDlg.isAdded();
        if (this.waitingForMoveShowing) {
            waitingForMoveDlg.dismiss();
        }
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
        } else {
            changeTurn();
            waitingForMoveDlg.dismiss();
        }
    }

    public void onInstall(View view) {
        activePlayer.setLeak(false);

        if (getPlayingAreaView().installSelection(this.activePlayer)) {
            waitingForMoveDlg.show(getFragmentManager(), WAITING_FOR_MOVE_LABEL);
            changeTurn();
        } else {
            Toast.makeText(this, "Install Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onDiscard(final View view) {
        if (getPlayingAreaView().discardSelection()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (cloud.discardPipeFromCloud(preferences.getGameId())) {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                waitingForMoveDlg.show(getFragmentManager(), WAITING_FOR_MOVE_LABEL);
                                changeTurn();
                            }
                        });
                    } else {
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Failed to discard", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        } else {
            Toast.makeText(this, "Discard Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRotate(View view) {
        if (!getPlayingAreaView().rotateSelected()) {
            Toast.makeText(this, "Please Select A Pipe First.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onOpenValve(View view) {
        if (getPlayingAreaView().checkLeaks(activePlayer)) {
            gameOver(true, true);
        } else {
            Toast.makeText(this, "You Still Have Leaks!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onSurrender(View view) {
        gameOver(false, false);
    }

    public void onQuitWhileWaiting() {
        gameOver(true, false);
    }

    public void onPassiveQuit() {
        gameOver(false, true);
    }

    public void onPieceSelected(Pipe pipe, boolean isPortrait) {
        getPlayingAreaView().notifyPieceSelected(pipe, isPortrait);
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    private void gameOver(final boolean activeWon, final boolean thisPlayerWon) {
        final Intent intent = new Intent(this, GameOverActivity.class);

        Player winner = activeWon ? activePlayer : inactivePlayer;
        intent.putExtra(WINNING_PLAYER, winner.getName());

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (cloud.endGameFromCloud(preferences.getGameId(), thisPlayerWon)) {
                    startActivity(intent);
                } else {
                    getPlayingAreaView().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Failed Intent To Game Over", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
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
